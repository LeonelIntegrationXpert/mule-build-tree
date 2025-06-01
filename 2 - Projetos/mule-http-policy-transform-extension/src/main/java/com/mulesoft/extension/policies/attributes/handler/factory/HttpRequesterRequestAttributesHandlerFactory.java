/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.handler.HttpRequesterRequestAttributesHandler
 *  com.mulesoft.extension.policies.attributes.handler.factory.impl.HttpPolicyRequestAttributesDefaultFactory
 *  com.mulesoft.extension.policies.attributes.handler.factory.impl.HttpPolicyRequestAttributesReflectiveFactory
 *  org.mule.extension.http.api.policy.HttpPolicyRequestAttributes
 */
package com.mulesoft.extension.policies.attributes.handler.factory;

import com.mulesoft.extension.policies.attributes.handler.HttpRequesterRequestAttributesHandler;
import com.mulesoft.extension.policies.attributes.handler.factory.AttributesHandlerFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyRequestAttributesFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.impl.HttpPolicyRequestAttributesDefaultFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.impl.HttpPolicyRequestAttributesReflectiveFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.mule.extension.http.api.policy.HttpPolicyRequestAttributes;

public class HttpRequesterRequestAttributesHandlerFactory
extends AttributesHandlerFactory {
    public static final Set<String> HTTP_POLICY_REQUEST_ATTRIBUTES_KNOWN_FIELDS = new HashSet<String>(Arrays.asList("headers", "queryParams", "uriParams", "requestPath"));

    public HttpRequesterRequestAttributesHandlerFactory() {
        super(HttpPolicyRequestAttributes.class, HTTP_POLICY_REQUEST_ATTRIBUTES_KNOWN_FIELDS);
    }

    protected HttpRequesterRequestAttributesHandler defaultImplementation() {
        return new HttpRequesterRequestAttributesHandler((HttpPolicyRequestAttributesFactory)new HttpPolicyRequestAttributesDefaultFactory());
    }

    protected HttpRequesterRequestAttributesHandler reflectiveImplementation() {
        return new HttpRequesterRequestAttributesHandler((HttpPolicyRequestAttributesFactory)new HttpPolicyRequestAttributesReflectiveFactory());
    }
}
