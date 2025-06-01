/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.dsl.api.xml.XmlNamespaceInfo
 *  org.mule.runtime.dsl.api.xml.XmlNamespaceInfoProvider
 */
package com.mulesoft.mule.runtime.tracking.internal.dsl.processor.xml.provider;

import java.util.Arrays;
import java.util.Collection;
import org.mule.runtime.dsl.api.xml.XmlNamespaceInfo;
import org.mule.runtime.dsl.api.xml.XmlNamespaceInfoProvider;

public class TrackingXmlNamespaceInfoProvider
implements XmlNamespaceInfoProvider {
    public static final String TRACKING_NAMESPACE = "tracking";
    public static final String TRACKING_NAMESPACE_URI = "http://www.mulesoft.org/schema/mule/ee/tracking";

    public Collection<XmlNamespaceInfo> getXmlNamespacesInfo() {
        return Arrays.asList(new XmlNamespaceInfo(){

            public String getNamespaceUriPrefix() {
                return TrackingXmlNamespaceInfoProvider.TRACKING_NAMESPACE_URI;
            }

            public String getNamespace() {
                return TrackingXmlNamespaceInfoProvider.TRACKING_NAMESPACE;
            }
        });
    }
}
