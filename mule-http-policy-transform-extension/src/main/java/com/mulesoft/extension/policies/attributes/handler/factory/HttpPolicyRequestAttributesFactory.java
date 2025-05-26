/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.extension.http.api.policy.HttpPolicyRequestAttributes
 *  org.mule.runtime.api.util.MultiMap
 */
package com.mulesoft.extension.policies.attributes.handler.factory;

import org.mule.extension.http.api.policy.HttpPolicyRequestAttributes;
import org.mule.runtime.api.util.MultiMap;

public interface HttpPolicyRequestAttributesFactory {
    public HttpPolicyRequestAttributes from(HttpPolicyRequestAttributes var1, MultiMap<String, String> var2);
}
