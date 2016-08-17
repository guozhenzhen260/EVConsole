package com.example.alizhifu;

import java.util.Map;

public class RequestParametersHolder {
	private Map protocalMustParams;
	private Map protocalOptParams;
	private Map applicationParams;

	public Map getProtocalMustParams() {
		return protocalMustParams;
	}
	public void setProtocalMustParams(Map protocalMustParams) {
		this.protocalMustParams = protocalMustParams;
	}
	public Map getProtocalOptParams() {
		return protocalOptParams;
	}
	public void setProtocalOptParams(Map protocalOptParams) {
		this.protocalOptParams = protocalOptParams;
	}
	public Map getApplicationParams() {
		return applicationParams;
	}
	public void setApplicationParams(Map applicationParams) {
		this.applicationParams = applicationParams;
	}
	
}
