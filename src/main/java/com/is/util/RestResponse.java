package com.is.util;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;
import com.is.model.CloudEntity;

/** 
 * @author zhuolin(zl@nbicc.com) 
 * @date 2015年10月29日
 * restful service response
 */
@XmlRootElement
public class RestResponse implements CloudEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8704155819454848570L;

	@SerializedName("result_code")
	private int resultCode;
	
	@SerializedName("data")
	private Object resultObject;
	
	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public Object getResultObject() {
		return resultObject;
	}

	public void setResultObject(Object resultObject) {
		this.resultObject = resultObject;
	}

	public RestResponse(int resultCode,Object resultObj){
		this.resultCode = resultCode;
		this.resultObject = resultObj;
	}
}
