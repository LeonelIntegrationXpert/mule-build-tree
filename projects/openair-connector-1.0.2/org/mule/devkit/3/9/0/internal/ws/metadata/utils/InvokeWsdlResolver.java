/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  javax.wsdl.Binding
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.Definition
 *  javax.wsdl.Message
 *  javax.wsdl.Operation
 *  javax.wsdl.Part
 *  javax.wsdl.Port
 *  javax.wsdl.PortType
 *  javax.wsdl.Service
 *  javax.wsdl.extensions.soap.SOAPHeader
 *  org.mule.devkit.3.9.0.internal.ws.metadata.utils.InvokeWsdlResolver$OperationMode
 */
package org.mule.devkit.3.9.0.internal.ws.metadata.utils;

import com.google.common.base.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.xml.transform.TransformerException;
import org.mule.devkit.3.9.0.internal.ws.common.WsdlUtils;
import org.mule.devkit.3.9.0.internal.ws.metadata.WsdlSchemaUtils;
import org.mule.devkit.3.9.0.internal.ws.metadata.utils.InputOperationResolver;
import org.mule.devkit.3.9.0.internal.ws.metadata.utils.InvokeWsdlResolver;
import org.mule.devkit.3.9.0.internal.ws.metadata.utils.OperationIOResolver;
import org.mule.devkit.3.9.0.internal.ws.metadata.utils.OutputOperationResolver;

public class InvokeWsdlResolver {
    List<SOAPHeader> operationHeaders;
    private OperationIOResolver operationIOResolver;
    private Definition definition;
    private List<String> schemas;
    private Service service;
    private Port port;
    private Optional<Part> messagePart;
    private Operation operation;

    public InvokeWsdlResolver(OperationMode operationMode, String wsdlLocation, String serviceName, String portName, String operationName) throws TransformerException {
        this.initialize(operationMode, wsdlLocation, serviceName, portName, operationName);
    }

    private void initialize(OperationMode operationMode, String wsdlLocation, String serviceName, String portName, String operationName) throws TransformerException {
        this.operationIOResolver = operationMode == OperationMode.INPUT ? new InputOperationResolver() : new OutputOperationResolver();
        this.definition = WsdlUtils.parseWSDL(wsdlLocation);
        this.schemas = WsdlSchemaUtils.getSchemas(this.definition);
        this.service = WsdlUtils.getService(this.definition, serviceName);
        this.port = WsdlUtils.getPort(this.service, portName);
        Binding binding = this.getPort().getBinding();
        PortType portType = binding.getPortType();
        this.operation = WsdlUtils.getOperation(portType, operationName);
        Optional<Message> optionalMessage = this.operationIOResolver.getMessage(this.operation);
        BindingOperation bindingOperation = binding.getBindingOperation(operationName, null, null);
        this.operationHeaders = this.operationIOResolver.getHeaders(bindingOperation);
        Map parts = optionalMessage.isPresent() ? ((Message)optionalMessage.get()).getParts() : new HashMap();
        this.messagePart = this.resolveMessagePart(bindingOperation, parts);
    }

    private Optional<Part> resolveMessagePart(BindingOperation bindingOperation, Map<?, ?> parts) {
        if (!parts.isEmpty()) {
            if (parts.size() == 1) {
                Object firstValueKey = parts.keySet().toArray()[0];
                return Optional.of((Object)((Part)parts.get(firstValueKey)));
            }
            Optional<String> bodyPartNameOptional = this.operationIOResolver.getBodyPartName(bindingOperation);
            if (bodyPartNameOptional.isPresent()) {
                return Optional.of((Object)((Part)parts.get(bodyPartNameOptional.get())));
            }
            return Optional.absent();
        }
        return Optional.absent();
    }

    public Definition getDefinition() {
        return this.definition;
    }

    public List<String> getSchemas() {
        return this.schemas;
    }

    public Service getService() {
        return this.service;
    }

    public Port getPort() {
        return this.port;
    }

    public Optional<Part> getMessagePart() {
        return this.messagePart;
    }

    public Operation getOperation() {
        return this.operation;
    }

    public List<SOAPHeader> getOperationHeaders() {
        return this.operationHeaders;
    }
}
