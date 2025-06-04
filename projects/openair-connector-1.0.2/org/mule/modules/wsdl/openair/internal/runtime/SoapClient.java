/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.cxf.staxutils.StaxUtils
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.mule.modules.wsdl.openair.internal.runtime;

import java.util.Collections;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import org.apache.cxf.staxutils.StaxUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.modules.wsdl.openair.internal.runtime.CallDefinition;
import org.mule.modules.wsdl.openair.internal.runtime.ServiceDefinition;
import org.mule.modules.wsdl.openair.internal.runtime.SoapCallException;
import org.mule.modules.wsdl.openair.internal.runtime.XmlConverterUtils;
import org.mule.modules.wsdl.openair.internal.runtime.header.HeaderBuilder;
import org.mule.modules.wsdl.openair.internal.runtime.header.SoapHeaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class SoapClient {
    public static final String XMLSOAP_ORG_SOAP_ENCODING_NAMESPACE = "http://schemas.xmlsoap.org/soap/encoding/";
    private static final Logger logger = LoggerFactory.getLogger(SoapClient.class);
    private static final String HEADER_PREFIX = "headerPrefix";
    private static final String BODY_PREFIX = "bodyPrefix";
    private static final String CONNECTION_TIMEOUT = "com.sun.xml.internal.ws.connect.timeout";
    private static final String READ_TIMEOUT = "com.sun.xml.internal.ws.request.timeout";
    private final ServiceDefinition serviceDefinition;
    private HeaderBuilder soapHeaderBuilder;
    private Map<String, String> headerParams;
    private Integer connectionTimeout;
    private Integer readTimeout;

    private SoapClient(@NotNull ServiceDefinition service) {
        this.serviceDefinition = service;
    }

    @NotNull
    public static SoapClient create(@NotNull ServiceDefinition service) {
        return new SoapClient(service);
    }

    @Nullable
    public XMLStreamReader invoke(@NotNull CallDefinition callDefinition, @Nullable XMLStreamReader payload) throws SoapCallException {
        logger.debug("Service Definition for Invocation: {}", (Object)this.serviceDefinition);
        logger.debug("SOAP call to endpoint: {}", (Object)callDefinition);
        XMLStreamReader result = null;
        try {
            String operationName = callDefinition.getOperationName();
            String endpointPath = callDefinition.getEndpointPath();
            Dispatch<SOAPMessage> dispatch = this.buildMessageDispatch(endpointPath, operationName);
            SOAPMessage soapRequest = this.buildSoapRequest(payload, operationName, dispatch);
            SOAPMessage soapResponse = dispatch.invoke(soapRequest);
            logger.debug("Client call successful.");
            if (soapResponse != null) {
                SOAPBodyElement sourceContent = (SOAPBodyElement)soapResponse.getSOAPBody().getChildElements().next();
                SOAPPart soapPart = soapRequest.getSOAPPart();
                SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
                result = XmlConverterUtils.soapResponseToXmlStream(soapResponse, sourceContent, soapEnvelope);
            }
        }
        catch (Exception e) {
            logger.warn("Error during web serviceDefinition invocation", (Throwable)e);
            throw SoapCallException.createCallException(e);
        }
        return result;
    }

    @NotNull
    private Dispatch<SOAPMessage> buildMessageDispatch(@NotNull String endpointPath, @NotNull String operationName) {
        Integer rTimeout;
        String namespace = this.serviceDefinition.getNamespace();
        String operation = this.serviceDefinition.getServiceName();
        QName serviceQName = new QName(namespace, operation);
        String portName = this.serviceDefinition.getPortName();
        QName portQName = new QName(namespace, portName);
        String baseEndpoint = this.serviceDefinition.getBaseEndpoint();
        String endpointAddress = baseEndpoint + endpointPath;
        Service service = Service.create(serviceQName);
        service.addPort(portQName, "http://schemas.xmlsoap.org/wsdl/soap/http", endpointAddress);
        Dispatch<SOAPMessage> dispatch = service.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);
        Map<String, Object> rc = dispatch.getRequestContext();
        rc.put("javax.xml.ws.soap.http.soapaction.use", Boolean.TRUE);
        rc.put("javax.xml.ws.soap.http.soapaction.uri", operationName);
        Integer ctxTimeout = this.getConnectionTimeout();
        if (ctxTimeout != null) {
            rc.put(CONNECTION_TIMEOUT, ctxTimeout);
            logger.debug("Setting timeout to {}", (Object)ctxTimeout);
        }
        if ((rTimeout = this.getReadTimeout()) != null) {
            rc.put(READ_TIMEOUT, rTimeout);
            logger.debug("Setting readTime to {}", (Object)rTimeout);
        }
        return dispatch;
    }

    @NotNull
    private SOAPMessage buildSoapRequest(@Nullable XMLStreamReader payload, @NotNull String operationName, @NotNull Dispatch<SOAPMessage> dispatch) throws SOAPException, SoapHeaderException, XMLStreamException {
        SOAPBinding binding = (SOAPBinding)dispatch.getBinding();
        MessageFactory msgFactory = binding.getMessageFactory();
        SOAPMessage result = msgFactory.createMessage();
        SOAPPart part = result.getSOAPPart();
        SOAPEnvelope env = part.getEnvelope();
        env.setEncodingStyle(XMLSOAP_ORG_SOAP_ENCODING_NAMESPACE);
        String namespace = this.serviceDefinition.getNamespace();
        env.addNamespaceDeclaration(BODY_PREFIX, namespace + operationName + "/");
        env.addNamespaceDeclaration(HEADER_PREFIX, namespace);
        SOAPHeader header = env.getHeader();
        if (this.soapHeaderBuilder != null) {
            this.soapHeaderBuilder.build(header, this.serviceDefinition);
        }
        Map<String, String> params = this.getHeaderParams();
        String headerPrefix = this.serviceDefinition.getHeaderPrefix();
        for (String key : params.keySet()) {
            QName qname = new QName(namespace, key, headerPrefix);
            SOAPElement soapElement = header.addChildElement(qname);
            String headerName = params.get(key);
            soapElement.addTextNode(headerName);
        }
        XMLStreamReader callsPayload = payload;
        if (callsPayload == null) {
            String soapMethodsCallNamespace = namespace.endsWith("/") ? namespace.substring(0, namespace.length() - 1) : namespace;
            callsPayload = XmlConverterUtils.computeCallsPayloadForMethodWithNoParameter(operationName, soapMethodsCallNamespace);
        }
        Document document = StaxUtils.read((XMLStreamReader)callsPayload);
        SOAPBody body = env.getBody();
        body.addDocument(document);
        result.saveChanges();
        return result;
    }

    public Integer getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    @NotNull
    public Map<String, String> getHeaderParams() {
        return this.headerParams != null ? this.headerParams : Collections.emptyMap();
    }

    public void setHeaderParams(@NotNull Map<String, String> headerParams) {
        this.headerParams = headerParams;
    }

    public void setSoapHeaderBuilder(HeaderBuilder soapHeaderBuilder) {
        this.soapHeaderBuilder = soapHeaderBuilder;
    }
}
