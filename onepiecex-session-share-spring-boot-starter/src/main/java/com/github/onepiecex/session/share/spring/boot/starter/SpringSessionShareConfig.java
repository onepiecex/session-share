package com.github.onepiecex.session.share.spring.boot.starter;

import com.github.onepiecex.session.share.core.SessionShareConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Created by wangziqing on 17/6/23.
 */
@Configuration
@ConfigurationProperties(prefix="session")
public class SpringSessionShareConfig implements SessionShareConfig {
    private String prefix;
    private boolean http_only;
    private boolean transferred_over_https_only;
    private Integer expire_time_in_seconds;
    private String domain;
    private String secret;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new SessionFilter(this));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isHttp_only() {
        return http_only;
    }

    public void setHttp_only(boolean http_only) {
        this.http_only = http_only;
    }

    public boolean isTransferred_over_https_only() {
        return transferred_over_https_only;
    }

    public void setTransferred_over_https_only(boolean transferred_over_https_only) {
        this.transferred_over_https_only = transferred_over_https_only;
    }

    @Override
    public Integer getExpire_time_in_seconds() {
        return expire_time_in_seconds;
    }

    public void setExpire_time_in_seconds(Integer expire_time_in_seconds) {
        this.expire_time_in_seconds = expire_time_in_seconds;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
