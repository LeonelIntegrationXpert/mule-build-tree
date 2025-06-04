/*
 * Decompiled with CFR 0.152.
 */
package org.mule.modules.openair.client;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.mule.modules.wsdl.openair.internal.runtime.CallDefinition;
import org.mule.modules.wsdl.openair.internal.runtime.SoapCallException;
import org.xml.sax.SAXException;

public interface ProgramClient {
    public XMLStreamReader invokeOperation(String var1);

    public XMLStreamReader invokeOperation(String var1, XMLStreamReader var2);

    public String invokeLoginOperation(XMLStreamReader var1) throws SoapCallException, ParserConfigurationException, TransformerConfigurationException, XPathExpressionException, IOException, SAXException;

    public void invokeLogoutOperation(XMLStreamReader var1) throws SoapCallException;

    public XMLStreamReader invoke(CallDefinition var1, XMLStreamReader var2) throws SoapCallException;
}
