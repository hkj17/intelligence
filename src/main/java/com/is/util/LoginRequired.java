package com.is.util;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/** 
 * @author lishuhuan 
 * @date 2016�?3�?23�?
 * 自定义annotation，进入方法前�?要登录验�? 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {

}
