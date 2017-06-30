package com.github.onepiecex.session.share.core.util;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by wangziqing on 17/2/23.
 */
/**
 * Created by wangziqing on 17/6/22.
 */
public abstract class JackJson {
    static private final JsonFactory jsonFactory = new JsonFactory();
    static private final ObjectMapper mapper = new ObjectMapper();

    public static String toJSONString(Object object){
        try (StringWriter sw = new StringWriter();
             JsonGenerator gen = jsonFactory.createGenerator(sw)) {
            mapper.writeValue(gen, object);
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static<T> T parseObject(String json,Class<T> type){
        T t = null;
        try {
            t = mapper.readValue(json,type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

}
