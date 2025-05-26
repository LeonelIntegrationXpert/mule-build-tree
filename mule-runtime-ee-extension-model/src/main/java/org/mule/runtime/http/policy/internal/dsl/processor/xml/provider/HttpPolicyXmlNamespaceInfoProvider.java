/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.dsl.api.xml.XmlNamespaceInfo
 *  org.mule.runtime.dsl.api.xml.XmlNamespaceInfoProvider
 */
package org.mule.runtime.http.policy.internal.dsl.processor.xml.provider;

import java.util.Collection;
import java.util.Collections;
import org.mule.runtime.dsl.api.xml.XmlNamespaceInfo;
import org.mule.runtime.dsl.api.xml.XmlNamespaceInfoProvider;

public class HttpPolicyXmlNamespaceInfoProvider
implements XmlNamespaceInfoProvider {
    public static final String HTTP_POLICY_NAMESPACE_PREFIX = "http-policy";
    public static final String HTTP_POLICY_NAMESPACE_URI = "http://www.mulesoft.org/schema/mule/mule-http";

    public Collection<XmlNamespaceInfo> getXmlNamespacesInfo() {
        return Collections.singleton(new XmlNamespaceInfo(){

            public String getNamespaceUriPrefix() {
                return HttpPolicyXmlNamespaceInfoProvider.HTTP_POLICY_NAMESPACE_URI;
            }

            public String getNamespace() {
                return HttpPolicyXmlNamespaceInfoProvider.HTTP_POLICY_NAMESPACE_PREFIX;
            }
        });
    }
}
