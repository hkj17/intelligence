package com.is.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/** 
 * @author zhuolin(zl@nbicc.com) 
 * @date 2015年10月30日
 * 类说明 
 */
public class PropertyConfig extends PropertyPlaceholderConfigurer{
	private static Map<String, String> ctxPropertiesMap = Collections.synchronizedMap( new HashMap<String, String>() );
	
	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactory, Properties props )
			throws BeansException{

		super.processProperties(beanFactory, props);
		for(Object key : props.keySet()){
			String keyStr = key.toString();
			String value = props.getProperty(keyStr);
			ctxPropertiesMap.put(keyStr, value);
		}
	}

	public static String getContextProperty( String name ){
		return ctxPropertiesMap.get(name);
	}
}
