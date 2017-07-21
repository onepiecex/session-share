package com.github.session.share.core;

/**
 * Created by wangziqing on 17/6/28.
 */
public interface SessionShareConfig {
    String getPrefix();
    boolean isHttp_only();
    boolean isTransferred_over_https_only();
    Integer getExpire_time_in_seconds();
    String getDomain();
    String getSecret();
}
