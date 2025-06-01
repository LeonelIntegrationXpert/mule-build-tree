/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.dsl.api.xml.XmlNamespaceInfo
 *  org.mule.runtime.dsl.api.xml.XmlNamespaceInfoProvider
 */
package com.mulesoft.mule.runtime.module.batch.internal.dsl.processor.xml.provider;

import java.util.Collection;
import java.util.Collections;
import org.mule.runtime.dsl.api.xml.XmlNamespaceInfo;
import org.mule.runtime.dsl.api.xml.XmlNamespaceInfoProvider;

public class BatchXmlNamespaceInfoProvider
implements XmlNamespaceInfoProvider {
    public static final String BATCH_NAMESPACE = "batch";
    public static final String BATCH_NAMESPACE_URI = "http://www.mulesoft.org/schema/mule/ee/batch";

    public Collection<XmlNamespaceInfo> getXmlNamespacesInfo() {
        return Collections.singleton(new XmlNamespaceInfo(){

            public String getNamespaceUriPrefix() {
                return BatchXmlNamespaceInfoProvider.BATCH_NAMESPACE_URI;
            }

            public String getNamespace() {
                return BatchXmlNamespaceInfoProvider.BATCH_NAMESPACE;
            }
        });
    }
}
