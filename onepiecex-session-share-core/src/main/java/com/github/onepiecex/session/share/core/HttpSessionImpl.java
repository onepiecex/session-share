package com.github.onepiecex.session.share.core;

import com.github.onepiecex.session.share.core.util.CookieDataCodec;
import com.github.onepiecex.session.share.core.util.CookieEncryption;
import com.github.onepiecex.session.share.core.util.Crypto;
import com.github.onepiecex.session.share.core.util.JackJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.*;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangziqing on 17/6/22.
 */
public class HttpSessionImpl implements HttpSession {

    private final static Logger logger = LoggerFactory.getLogger(HttpSessionImpl.class);

    final String LAST_TIME_KEY = "___LT";
    final String ID_KEY = "___ID";
    final String TIMESTAMP_KEY = "___TS";
    final String CREATE_TIME_KEY = "___CT";
    final String EXPIRY_TIME_KEY = "___EP";
    final String NEW_KEY = "___NEW";

    private HttpServletRequest httpServletRequest;
    private SessionShareConfig sessionShareConfig;

    private Long sessionExpireTimeInMs;

    private final String sessionCookieName;
    private final Boolean sessionTransferredOverHttpsOnly;
    private final Boolean sessionHttpOnly;
    private final String applicationCookieDomain;

    private String secret;

    private boolean sessionDataHasBeenChanged = false;


    private volatile Map<String, String> data = new ConcurrentHashMap<>();

    public HttpSessionImpl(HttpServletRequest httpServletRequest, SessionShareConfig sessionShareConfig) {
        this.httpServletRequest = httpServletRequest;
        this.sessionShareConfig = sessionShareConfig;

        String prefix = sessionShareConfig.getPrefix();
        if (null == prefix || prefix.isEmpty()) {
            prefix = "ONEPIECEX";
        }
        sessionCookieName = prefix + "_SESSION";

        Integer seconds = sessionShareConfig.getExpire_time_in_seconds();
        if (null == seconds) {
            seconds = 1800;
        }
        sessionExpireTimeInMs = seconds * 1000l;

        sessionTransferredOverHttpsOnly = sessionShareConfig.isTransferred_over_https_only();
        sessionHttpOnly = sessionShareConfig.isHttp_only();
        applicationCookieDomain = sessionShareConfig.getDomain();
        secret = sessionShareConfig.getSecret();
        if (null == secret || secret.isEmpty()) {
            throw new RuntimeException("session.secret 密匙不能为空");
        } else if (secret.length() < 32) {
            throw new RuntimeException("session.secret 密匙长度不能小于32");
        }
        init();
    }

    private void init() {
        Cookie cookie = getCookie();
        try {
            if (cookie != null && cookie.getValue() != null && !cookie.getValue().trim().isEmpty()) {
                String value = cookie.getValue();
                String sign = value.substring(0, value.indexOf("-"));
                String payload = value.substring(value.indexOf("-") + 1);
                if (CookieDataCodec.safeEquals(sign, Crypto.signHmacSha1(payload, sessionShareConfig.getSecret()))) {
                    payload = CookieEncryption.getInstance(sessionShareConfig.getSecret()).decrypt(payload);
                    CookieDataCodec.decode(data, payload);
                }

                if (data.containsKey(EXPIRY_TIME_KEY)) {
                    Long expiryTime = Long.parseLong(data.get(EXPIRY_TIME_KEY));
                    if (expiryTime >= 0) {
                        sessionExpireTimeInMs = expiryTime;
                    }
                }
                checkExpire();
            }
        } catch (UnsupportedEncodingException u) {
            u.printStackTrace();
        }

        String currentTime = Long.toString(System.currentTimeMillis());
        if (!data.containsKey(CREATE_TIME_KEY)) {
            data.put(CREATE_TIME_KEY, currentTime);
        }
        data.put(LAST_TIME_KEY, currentTime);
    }

    public void save(HttpServletResponse httpServletResponse) {
        if (!sessionDataHasBeenChanged && sessionExpireTimeInMs == null) {
            return;
        }
        sessionDataHasBeenChanged = false;
        if (isEmpty()) {
            Cookie cookie = getCookie();
            if (null != cookie) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
            }
            return;
        }
        if (sessionExpireTimeInMs != null && !data.containsKey(TIMESTAMP_KEY)) {
            data.put(TIMESTAMP_KEY, Long.toString(System.currentTimeMillis()));
        }
        try {
            String sessionData = CookieDataCodec.encode(data);
            sessionData = CookieEncryption.getInstance(secret).encrypt(sessionData);
            String sign = Crypto.signHmacSha1(sessionData, secret);

            Cookie cookie = createCookie(sessionCookieName, sign + "-" + sessionData);

            if (sessionExpireTimeInMs != null) {
                cookie.setMaxAge((int) (sessionExpireTimeInMs / 1000L));
            }
            httpServletResponse.addCookie(cookie);

        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            logger.error("Encoding exception - this must not happen", unsupportedEncodingException);
            throw new RuntimeException(unsupportedEncodingException);
        }
    }

    private Cookie createCookie(
            String sessionCookieName,
            String value) {
        Cookie cookie = new Cookie(sessionCookieName, value);
        if (applicationCookieDomain != null && !applicationCookieDomain.isEmpty()) {
            cookie.setDomain(applicationCookieDomain);
        }
        if (sessionTransferredOverHttpsOnly != null) {
            cookie.setSecure(sessionTransferredOverHttpsOnly);
        }
        if (sessionHttpOnly != null) {
            cookie.setHttpOnly(sessionHttpOnly);
        }
        return cookie;
    }


    private Cookie getCookie() {
        Cookie[] cookies = this.httpServletRequest.getCookies();
        if (null != cookies) {
            for (Cookie cookie_ : cookies) {
                if (cookie_.getName().equals(sessionCookieName)) {
                    return cookie_;
                }
            }
        }
        return null;
    }


    private boolean isEmpty() {
        int itemsToIgnore = 0;
        if (data.containsKey(TIMESTAMP_KEY)) {
            itemsToIgnore++;
        }
        if (data.containsKey(EXPIRY_TIME_KEY)) {
            itemsToIgnore++;
        }
        return (data.isEmpty() || data.size() == itemsToIgnore);
    }

    private boolean shouldExpire() {
        if (sessionExpireTimeInMs != null) {
            if (!data.containsKey(TIMESTAMP_KEY)) {
                return true;
            }
            Long timestamp = Long.parseLong(data.get(TIMESTAMP_KEY));
            return (timestamp + sessionExpireTimeInMs < System.currentTimeMillis());
        }
        return false;
    }

    private void checkExpire() {
        if (sessionExpireTimeInMs != null) {
            if (shouldExpire()) {
                sessionDataHasBeenChanged = true;
                data.clear();
            } else {
                data.put(TIMESTAMP_KEY, "" + System.currentTimeMillis());
            }
        }
    }

    private void setExpiryTime(Long expiryTimeMs) {
        if (expiryTimeMs == null) {
            data.remove(EXPIRY_TIME_KEY);
            sessionDataHasBeenChanged = true;
        } else {
            data.put(EXPIRY_TIME_KEY, "" + expiryTimeMs);

            sessionExpireTimeInMs = expiryTimeMs;
        }

        if (sessionExpireTimeInMs != null) {
            if (!data.containsKey(TIMESTAMP_KEY)) {
                data.put(TIMESTAMP_KEY, "" + System.currentTimeMillis());
            }
            checkExpire();
            sessionDataHasBeenChanged = true;
        }
    }

    @Override
    public long getCreationTime() {
        return Long.valueOf(data.get(CREATE_TIME_KEY));
    }

    @Override
    public String getId() {
        if (!data.containsKey(ID_KEY)) {
            data.put(ID_KEY, UUID.randomUUID().toString());
        }
        return data.get(ID_KEY);
    }

    @Override
    public long getLastAccessedTime() {
        return Long.valueOf(data.get(LAST_TIME_KEY));
    }

    @Override
    public ServletContext getServletContext() {
        return httpServletRequest.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        setExpiryTime(interval * 1000l);
    }

    @Override
    public int getMaxInactiveInterval() {
        return (int) (sessionExpireTimeInMs / 1000L);
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return JackJson.parseObject(data.get(name), Object.class);
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        final Iterator it = data.keySet().iterator();
        return new Enumeration() {
            @Override
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            @Override
            public Object nextElement() {
                return it.next();
            }
        };
    }

    @Override
    public String[] getValueNames() {
        return data.keySet().toArray(new String[data.size()]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (name.contains(":")) {
            throw new IllegalArgumentException(
                    "Character ':' is invalid in a session key.");
        }

        sessionDataHasBeenChanged = true;

        if (value == null) {
            removeAttribute(name);
        } else {
            data.put(name, JackJson.toJSONString(value));
        }
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        sessionDataHasBeenChanged = true;
        data.remove(name);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
    }

    @Override
    public void invalidate() {
        sessionDataHasBeenChanged = true;
        data.clear();
    }

    @Override
    public boolean isNew() {
        if (!data.containsKey(NEW_KEY)) {
            data.put(NEW_KEY, "1");
            return true;
        }
        return false;
    }
}
