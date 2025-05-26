/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.handler.HttpPolicyResponseAttributesHandler
 *  com.mulesoft.extension.policies.attributes.handler.factory.impl.HttpPolicyResponseAttributesDefaultFactory
 *  com.mulesoft.extension.policies.attributes.handler.factory.impl.HttpPolicyResponseAttributesReflectiveFactory
 *  org.mule.extension.http.api.policy.HttpPolicyResponseAttributes
 */
package com.mulesoft.extension.policies.attributes.handler.factory;

import com.mulesoft.extension.policies.attributes.handler.HttpPolicyResponseAttributesHandler;
import com.mulesoft.extension.policies.attributes.handler.factory.AttributesHandlerFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyResponseAttributesFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.impl.HttpPolicyResponseAttributesDefaultFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.impl.HttpPolicyResponseAttributesReflectiveFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.mule.extension.http.api.policy.HttpPolicyResponseAttributes;

public class HttpPolicyResponseAttributesHandlerFactory
extends AttributesHandlerFactory {
    public static final Set<String> HTTP_POLICY_RESPONSE_ATTRIBUTES_KNOWN_FIELDS = new HashSet<String>(Arrays.asList("statusCode", "reasonPhrase", "headers"));

    public HttpPolicyResponseAttributesHandlerFactory() {
        super(HttpPolicyResponseAttributes.class, HTTP_POLICY_RESPONSE_ATTRIBUTES_KNOWN_FIELDS);
    }

    protected HttpPolicyResponseAttributesHandler defaultImplementation() {
        return new HttpPolicyResponseAttributesHandler((HttpPolicyResponseAttributesFactory)new HttpPolicyResponseAttributesDefaultFactory());
    }

    protected HttpPolicyResponseAttributesHandler reflectiveImplementation() {
        return new HttpPolicyResponseAttributesHandler((HttpPolicyResponseAttributesFactory)new HttpPolicyResponseAttributesReflectiveFactory());
    }
}
