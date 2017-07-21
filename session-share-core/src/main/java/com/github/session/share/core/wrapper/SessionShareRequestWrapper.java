package com.github.session.share.core.wrapper;

import com.github.session.share.core.SessionImpl;
import com.github.session.share.core.SessionShareConfig;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;


/**
 * Created by wangziqing on 17/6/22.
 */
public class SessionShareRequestWrapper extends HttpServletRequestWrapper {

    private final SessionShareConfig sessionShareConfig;

    private SessionImpl sessionImpl;

    public SessionShareRequestWrapper(ServletRequest request, SessionShareConfig sessionShareConfig) {
        super((HttpServletRequest) request);
        this.sessionShareConfig = sessionShareConfig;
    }

    @Override
    public HttpSession getSession() {
        return getShareSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {
        return getShareSession(create);
    }

    private HttpSession getShareSession(boolean create) {
        if(create){
            if (null == sessionImpl) {
                synchronized (this) {
                    if (null == sessionImpl) {
                        sessionImpl = new SessionImpl(this, sessionShareConfig);
                    }
                }
            }
        }
        return sessionImpl;
    }
}
