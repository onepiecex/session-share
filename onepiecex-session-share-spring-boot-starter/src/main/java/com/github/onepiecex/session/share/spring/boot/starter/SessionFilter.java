package com.github.onepiecex.session.share.spring.boot.starter;


import com.github.onepiecex.session.share.core.wrapper.SessionShareRequestWrapper;
import com.github.onepiecex.session.share.core.wrapper.SessionShareResponseWrapper;

import javax.servlet.*;
import java.io.IOException;
/**
 * Created by wangziqing on 17/6/28.
 */
public class SessionFilter implements Filter {

    private final SpringSessionShareConfig springSessionConfig;

    public SessionFilter(SpringSessionShareConfig springSessionConfig){
        this.springSessionConfig = springSessionConfig;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        SessionShareRequestWrapper requestWrapper = new SessionShareRequestWrapper(request,springSessionConfig);
        SessionShareResponseWrapper responseWrapper = new SessionShareResponseWrapper(response,requestWrapper);
        chain.doFilter(requestWrapper,responseWrapper);
    }

    @Override
    public void destroy() {

    }
}
