/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.dsl.api.xml.XmlNamespaceInfo
 *  org.mule.runtime.dsl.api.xml.XmlNamespaceInfoProvider
 */
package com.mulesoft.mule.runtime.internal.dsl.processor.xml.provider;

import java.util.Collection;
import java.util.Collections;
import org.mule.runtime.dsl.api.xml.XmlNamespaceInfo;
import org.mule.runtime.dsl.api.xml.XmlNamespaceInfoProvider;

public class CoreEeXmlNamespaceInfoProvider
implements XmlNamespaceInfoProvider {
    public static final String EE_NAMESPACE = "ee";

    public Collection<XmlNamespaceInfo> getXmlNamespacesInfo() {
        return Collections.singleton(new XmlNamespaceInfo(){

            public String getNamespaceUriPrefix() {
                return "http://www.mulesoft.org/schema/mule/ee/core";
            }

            public String getNamespace() {
                return CoreEeXmlNamespaceInfoProvider.EE_NAMESPACE;
            }
        });
    }
}
