/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package org.mule.modules.wsdl.openair.internal.runtime;

import java.io.StringReader;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXSource;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class XmlConverterUtils {
    private static XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    private XmlConverterUtils() {
    }

    @NotNull
    static XMLStreamReader soapResponseToXmlStream(@NotNull SOAPMessage soapResponse, @NotNull SOAPBodyElement sourceContent, @NotNull SOAPEnvelope soapEnvelope) throws SOAPException, XMLStreamException {
        SOAPPart responsePart = soapResponse.getSOAPPart();
        NodeList childNodes = responsePart.getChildNodes();
        NamedNodeMap envelopeAttributes = childNodes.item(0).getAttributes();
        for (int i = 0; i < envelopeAttributes.getLength(); ++i) {
            Node node = envelopeAttributes.item(i);
            String nodeName = node.getNodeName();
            Name name = soapEnvelope.createName(nodeName);
            sourceContent.addAttribute(name, node.getNodeValue());
        }
        DOMSource source = new DOMSource(sourceContent);
        XMLStreamReader reader = inputFactory.createXMLStreamReader(source);
        StAXSource stAXSource = new StAXSource(reader);
        return stAXSource.getXMLStreamReader();
    }

    @NotNull
    static XMLStreamReader computeCallsPayloadForMethodWithNoParameter(@NotNull String operationName, @NotNull String namespace) throws XMLStreamException {
        String callsPayloadAsString = "<ns0:" + operationName + " xmlns:ns0=\"" + namespace + "\"/>";
        return inputFactory.createXMLStreamReader(new StringReader(callsPayloadAsString));
    }
}
