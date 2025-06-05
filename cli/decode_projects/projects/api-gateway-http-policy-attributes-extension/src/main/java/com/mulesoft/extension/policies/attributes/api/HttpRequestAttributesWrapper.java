package com.mulesoft.extension.policies.attributes.api;

import java.security.cert.Certificate;
import java.util.Map;
import org.mule.extension.http.api.HttpRequestAttributes;
import org.mule.runtime.api.util.MultiMap;

public class HttpRequestAttributesWrapper extends HttpRequestAttributes {
	private static final long serialVersionUID = -2149085335509752700L;

	public HttpRequestAttributesWrapper() {
		this((MultiMap<String, String>) new MultiMap(), null, null, null, null, null, null, null, null, null, null,
				null, null);
	}

	public HttpRequestAttributesWrapper(MultiMap<String, String> headers, String listenerPath, String relativePath,
			String version, String scheme, String method, String requestPath, String requestUri, String queryString,
			MultiMap<String, String> queryParams, Map<String, String> uriParams, String remoteAddress,
			Certificate clientCertificate) {
		super(headers, listenerPath, relativePath, version, scheme, method, requestPath, requestUri, queryString,
				queryParams, uriParams, remoteAddress, clientCertificate);
	}
}