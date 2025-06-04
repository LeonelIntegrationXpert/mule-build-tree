/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleEvent
 *  org.mule.api.debug.DebugInfoProvider
 *  org.mule.api.debug.FieldDebugInfo
 *  org.mule.api.debug.FieldDebugInfoFactory
 *  org.mule.api.transformer.DataType
 *  org.mule.api.transformer.TransformerException
 *  org.mule.api.transformer.TransformerMessagingException
 *  org.mule.api.transport.PropertyScope
 *  org.mule.transport.NullPayload
 */
package org.mule.devkit.3.9.0.internal.ws.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.mule.api.MuleEvent;
import org.mule.api.debug.DebugInfoProvider;
import org.mule.api.debug.FieldDebugInfo;
import org.mule.api.debug.FieldDebugInfoFactory;
import org.mule.api.transformer.DataType;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transformer.TransformerMessagingException;
import org.mule.api.transport.PropertyScope;
import org.mule.devkit.3.9.0.internal.ws.model.InvokeSoapMessageProcessor;
import org.mule.transport.NullPayload;

public class InvokeSoapMessageProcessorDebuggable
extends InvokeSoapMessageProcessor
implements DebugInfoProvider {
    public InvokeSoapMessageProcessorDebuggable(String operationName) {
        super(operationName);
    }

    public List<FieldDebugInfo<?>> getDebugInfo(MuleEvent event) {
        ArrayList infoList = new ArrayList();
        try {
            if (event.getMessage().getPayload() instanceof NullPayload) {
                infoList.add((FieldDebugInfo<?>)FieldDebugInfoFactory.createFieldDebugInfo((String)"Body", NullPayload.class, (Object)"{NullPayload}"));
            } else {
                String xml = (String)this.evaluateAndTransform(this.muleContext, event, (Type)((Object)String.class), null, event.getMessage().getPayload());
                event.getMessage().setPayload((Object)xml, event.getMessage().getDataType());
                infoList.add((FieldDebugInfo<?>)FieldDebugInfoFactory.createFieldDebugInfo((String)"Body", String.class, (Object)this.removeXMLHeader(xml)));
            }
        }
        catch (Exception e) {
            infoList.add((FieldDebugInfo<?>)FieldDebugInfoFactory.createFieldDebugInfo((String)"Header", String.class, (Exception)e));
        }
        try {
            List<String> soapHeaders = this.soapHeaders(event);
            if (!soapHeaders.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String soapHeader : soapHeaders) {
                    sb.append(this.removeXMLHeader(soapHeader));
                    sb.append("\n");
                }
                infoList.add((FieldDebugInfo<?>)FieldDebugInfoFactory.createFieldDebugInfo((String)"Headers", String.class, (Object)sb.toString()));
            }
        }
        catch (Exception e) {
            infoList.add((FieldDebugInfo<?>)FieldDebugInfoFactory.createFieldDebugInfo((String)"Header", String.class, (Exception)e));
        }
        return infoList;
    }

    private String removeXMLHeader(String xml) {
        return xml.replaceAll("<\\?xml(.+?)\\?>", "").trim();
    }

    private List<String> soapHeaders(MuleEvent event) throws TransformerException, TransformerMessagingException {
        ArrayList<String> headers = new ArrayList<String>();
        for (String outboundPropertyName : event.getMessage().getOutboundPropertyNames()) {
            if (!outboundPropertyName.startsWith("soap.")) continue;
            String headerXml = (String)this.evaluateAndTransform(this.muleContext, event, (Type)((Object)String.class), null, event.getMessage().getOutboundProperty(outboundPropertyName));
            DataType propertyDataType = event.getMessage().getPropertyDataType(outboundPropertyName, PropertyScope.OUTBOUND);
            event.getMessage().setOutboundProperty(outboundPropertyName, (Object)headerXml, propertyDataType);
            headers.add(headerXml);
        }
        return headers;
    }
}
