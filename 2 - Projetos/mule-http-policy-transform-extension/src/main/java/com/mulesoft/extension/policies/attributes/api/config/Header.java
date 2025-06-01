package com.mulesoft.extension.policies.attributes.api.config;

import org.mule.runtime.extension.api.annotation.param.Parameter;

public class Header {
	@Parameter
	private String headerName;
	@Parameter
	private String headerValue;

	public String getHeaderName() {
		return this.headerName;
	}

	public String getHeaderValue() {
		return this.headerValue;
	}
}