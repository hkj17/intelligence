package com.is.gson;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** 
 * @author zhuolin(zl@nbicc.com) 
 * @date 2014年12月24日
 * 类说明 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonPolicyDef {

    Policy[] value();

    public static enum Policy {
    	APP_LOGIN,
        UNBIND_LIST,
        FIRMWARE_VERSION_LIST,
        BINDED_LIST,
        BINDED_LIST_WEB,
        QUERY_PARA,
        ENTERPRISE_DEVICE
    }
}

