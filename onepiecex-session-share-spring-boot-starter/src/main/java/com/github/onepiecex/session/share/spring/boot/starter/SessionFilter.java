package com.github.onepiecex.session.share.spring.boot.starter;


import com.github.onepiecex.session.share.core.wrapper.SessionShareRequestWrapper;
import com.github.onepiecex.session.share.core.wrapper.SessionShareResponseWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * Created by wangziqing on 17/6/28.
 */
public class SessionFilter extends OncePerRequestFilter {

    private final SpringSessionShareConfig springSessionConfig;

    public SessionFilter(SpringSessionShareConfig springSessionConfig){
        this.springSessionConfig = springSessionConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        SessionShareRequestWrapper sessionShareRequestWrapper = new SessionShareRequestWrapper(httpServletRequest,springSessionConfig);
        SessionShareResponseWrapper sessionShareResponseWrapper = new SessionShareResponseWrapper(httpServletResponse,sessionShareRequestWrapper);
        filterChain.doFilter(sessionShareRequestWrapper,sessionShareResponseWrapper);
    }
}
