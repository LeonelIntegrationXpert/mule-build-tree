/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyResponseAttributesFactory
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyResponseAttributesHandlerFactory
 *  com.mulesoft.extension.policies.attributes.tools.CrossClassFieldCopier
 *  org.mule.extension.http.api.HttpResponseAttributes
 *  org.mule.extension.http.api.policy.HttpPolicyResponseAttributes
 *  org.mule.runtime.api.util.MultiMap
 */
package com.mulesoft.extension.policies.attributes.handler.factory.impl;

import com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyResponseAttributesFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyResponseAttributesHandlerFactory;
import com.mulesoft.extension.policies.attributes.tools.CrossClassFieldCopier;
import java.util.Collection;
import org.mule.extension.http.api.HttpResponseAttributes;
import org.mule.extension.http.api.policy.HttpPolicyResponseAttributes;
import org.mule.runtime.api.util.MultiMap;

public class HttpPolicyResponseAttributesReflectiveFactory
implements HttpPolicyResponseAttributesFactory {
    private final CrossClassFieldCopier fieldCopier = new CrossClassFieldCopier(HttpResponseAttributes.class, HttpPolicyResponseAttributes.class, (Collection)HttpPolicyResponseAttributesHandlerFactory.HTTP_POLICY_RESPONSE_ATTRIBUTES_KNOWN_FIELDS);

    public HttpResponseAttributes from(HttpPolicyResponseAttributes attributes, MultiMap<String, String> headers) {
        HttpResponseAttributes copy = new HttpResponseAttributes(attributes.getStatusCode().intValue(), attributes.getReasonPhrase(), headers);
        this.fieldCopier.copyFields((Object)attributes, (Object)copy);
        return copy;
    }
}
