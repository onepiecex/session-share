package com.jbg.extra.web.auths;

import com.github.onepiecex.session.share.core.wrapper.SessionShareRequestWrapper;
import com.github.onepiecex.session.share.core.wrapper.SessionShareResponseWrapper;
import com.jbg.extra.models.SysUser;
import com.jbg.extra.services.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by wangziqing on 17/7/4.
 */
@Configuration
public class SysUserAuth extends WebMvcConfigurerAdapter {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SysUserContextArgumentResolver());
        super.addArgumentResolvers(argumentResolvers);
    }

    private class SysUserContextArgumentResolver implements HandlerMethodArgumentResolver {

        @Override
        public boolean supportsParameter(MethodParameter methodParameter) {
            return methodParameter.getParameterType().equals(SysUserContext.class);
        }

        @Override
        public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
            SessionShareRequestWrapper httpServletRequest = (SessionShareRequestWrapper)nativeWebRequest.getNativeRequest();
            SessionShareResponseWrapper httpServletResponse = (SessionShareResponseWrapper)nativeWebRequest.getNativeResponse();
            HttpSession httpSession = httpServletRequest.getSession();
            Object uid = httpSession.getAttribute("uid");
            SysUserContext sysUserContext = null;
            if (null == uid) {
                httpServletResponse.sendRedirect("/login");
            } else {
                SysUser sysUser = sysUserService.getById(Long.valueOf(uid.toString()));
                if (null == sysUser) {
                    httpServletResponse.sendRedirect("/login");
                } else {
                    sysUserContext = new SysUserContext();
                    sysUserContext.setRemoteIp(httpServletRequest.getAttribute("remoteIp").toString());
                    sysUserContext.setUserAgent(httpServletRequest.getHeader("User-Agent"));
                    sysUserContext.setSysUser(sysUser);
                }
            }
            return sysUserContext;
        }
    }
}
