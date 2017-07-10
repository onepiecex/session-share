package com.github.onepiecex.session.share.core;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by wangziqing on 17/7/10.
 */
public interface Session extends HttpSession {

    String getString(String name);
    
    <T> T getAttribute(String name,Class<T> cls);

    <T> T getValue(String name,Class<T> cls);

    Map<String,String> getData();
}
