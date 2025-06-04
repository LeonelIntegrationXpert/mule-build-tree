/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.jetbrains.annotations.NotNull
 */
package org.mule.modules.openair.client;

import com.google.common.base.Preconditions;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import org.jetbrains.annotations.NotNull;
import org.mule.modules.wsdl.openair.internal.runtime.ServiceDefinition;
import org.mule.modules.wsdl.openair.internal.runtime.header.HeaderBuilder;
import org.mule.modules.wsdl.openair.internal.runtime.header.SoapHeaderException;

public class OpenAirHeaderBuilder
implements HeaderBuilder {
    private final String sessionID;

    public OpenAirHeaderBuilder(String sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public void build(@NotNull SOAPHeader header, @NotNull ServiceDefinition serviceDefinition) throws SoapHeaderException {
        Preconditions.checkNotNull((Object)header);
        try {
            String headerPrefix = serviceDefinition.getHeaderPrefix();
            String namespace = serviceDefinition.getNamespace();
            QName qSessionHeader = new QName(namespace, "SessionHeader", headerPrefix);
            SOAPElement sessionHeader = header.addChildElement(qSessionHeader);
            SOAPElement sessionID = sessionHeader.addChildElement("sessionId", headerPrefix);
            sessionID.addTextNode(this.sessionID);
        }
        catch (SOAPException e) {
            throw new RuntimeException(e);
        }
    }
}
