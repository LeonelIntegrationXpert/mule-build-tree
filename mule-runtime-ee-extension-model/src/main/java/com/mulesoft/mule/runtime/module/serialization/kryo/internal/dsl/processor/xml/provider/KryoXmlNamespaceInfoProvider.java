/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.dsl.api.xml.XmlNamespaceInfo
 *  org.mule.runtime.dsl.api.xml.XmlNamespaceInfoProvider
 */
package com.mulesoft.mule.runtime.module.serialization.kryo.internal.dsl.processor.xml.provider;

import java.util.Collection;
import java.util.Collections;
import org.mule.runtime.dsl.api.xml.XmlNamespaceInfo;
import org.mule.runtime.dsl.api.xml.XmlNamespaceInfoProvider;

public final class KryoXmlNamespaceInfoProvider
implements XmlNamespaceInfoProvider {
    public static final String KRYO_NAMESPACE_URI = "http://www.mulesoft.org/schema/mule/kryo";
    public static final String KRYO_NAMESPACE = "kryo";

    public Collection<XmlNamespaceInfo> getXmlNamespacesInfo() {
        return Collections.singleton(new XmlNamespaceInfo(){

            public String getNamespaceUriPrefix() {
                return KryoXmlNamespaceInfoProvider.KRYO_NAMESPACE_URI;
            }

            public String getNamespace() {
                return KryoXmlNamespaceInfoProvider.KRYO_NAMESPACE;
            }
        });
    }
}
