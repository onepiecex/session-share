package com.github.onepiecex.session.share.core.wrapper;

import com.github.onepiecex.session.share.core.HttpSessionImpl;
import com.github.onepiecex.session.share.core.SessionShareConfig;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;


/**
 * Created by wangziqing on 17/6/22.
 */
public class SessionShareRequestWrapper extends HttpServletRequestWrapper {

    private final SessionShareConfig sessionShareConfig;

    private HttpSessionImpl httpSessionImpl;

    public SessionShareRequestWrapper(ServletRequest request, SessionShareConfig sessionShareConfig) {
        super((HttpServletRequest) request);
        this.sessionShareConfig = sessionShareConfig;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public HttpSession getSession(boolean create) {
        if(create){
            if (null == httpSessionImpl) {
                synchronized (this) {
                    if (null == httpSessionImpl) {
                        httpSessionImpl = new HttpSessionImpl(this, sessionShareConfig);
                    }
                }
            }
        }
        return httpSessionImpl;
    }
}
