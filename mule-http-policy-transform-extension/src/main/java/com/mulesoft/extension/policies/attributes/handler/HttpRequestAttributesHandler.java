/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.extension.http.api.HttpRequestAttributes
 *  org.mule.extension.http.api.HttpRequestAttributesBuilder
 *  org.mule.extension.http.api.policy.HttpPolicyResponseAttributes
 *  org.mule.runtime.api.util.MultiMap
 */
package com.mulesoft.extension.policies.attributes.handler;

import com.mulesoft.extension.policies.attributes.handler.HttpAttributesHandler;
import java.util.List;
import org.mule.extension.http.api.HttpRequestAttributes;
import org.mule.extension.http.api.HttpRequestAttributesBuilder;
import org.mule.extension.http.api.policy.HttpPolicyResponseAttributes;
import org.mule.runtime.api.util.MultiMap;

public class HttpRequestAttributesHandler
extends HttpAttributesHandler<HttpRequestAttributes> {
    @Override
    public Object addResponseHeaders(HttpRequestAttributes attributes, MultiMap<String, String> headers) {
        HttpPolicyResponseAttributes updatedAttributes = new HttpPolicyResponseAttributes();
        updatedAttributes.setHeaders(this.enforceHeaders(headers));
        return updatedAttributes;
    }

    @Override
    public Object addRequestHeaders(HttpRequestAttributes attributes, MultiMap<String, String> headers) {
        return new HttpRequestAttributesBuilder(attributes).headers(this.mergeHeaders((MultiMap<String, String>)attributes.getHeaders(), headers)).build();
    }

    @Override
    public Object removeHeaders(HttpRequestAttributes attributes, List<String> headerNames) {
        MultiMap originalHeaders = attributes.getHeaders();
        return new HttpRequestAttributesBuilder(attributes).headers(this.removeHeaders((MultiMap<String, String>)originalHeaders, headerNames)).build();
    }
}
