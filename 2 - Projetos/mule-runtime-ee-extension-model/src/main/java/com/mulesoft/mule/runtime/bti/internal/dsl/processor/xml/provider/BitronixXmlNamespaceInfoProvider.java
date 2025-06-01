/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.dsl.api.xml.XmlNamespaceInfo
 *  org.mule.runtime.dsl.api.xml.XmlNamespaceInfoProvider
 */
package com.mulesoft.mule.runtime.bti.internal.dsl.processor.xml.provider;

import java.util.Collection;
import java.util.Collections;
import org.mule.runtime.dsl.api.xml.XmlNamespaceInfo;
import org.mule.runtime.dsl.api.xml.XmlNamespaceInfoProvider;

public class BitronixXmlNamespaceInfoProvider implements XmlNamespaceInfoProvider {
	public static final String BTI_NAMESPACE = "bti";
	public static final String BTI_NAMESPACE_URI = "http://www.mulesoft.org/schema/mule/ee/bti";

	public Collection<XmlNamespaceInfo> getXmlNamespacesInfo() {
		return Collections.singleton(new XmlNamespaceInfo() {

			public String getNamespaceUriPrefix() {
				return BitronixXmlNamespaceInfoProvider.BTI_NAMESPACE_URI;
			}

			public String getNamespace() {
				return BitronixXmlNamespaceInfoProvider.BTI_NAMESPACE;
			}
		});
	}
}
