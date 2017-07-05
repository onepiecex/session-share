package com.jbg.extra.web.auths;

import com.jbg.extra.models.SysUser;

/**
 * Created by wangziqing on 17/7/4.
 */
public class SysUserContext {

    private SysUser sysUser;

    private String remoteIp;

    private String userAgent;

    public SysUser getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
