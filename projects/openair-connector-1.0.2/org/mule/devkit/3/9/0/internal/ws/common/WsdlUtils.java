/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ibm.wsdl.extensions.schema.SchemaSerializer
 *  javax.wsdl.BindingInput
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.Definition
 *  javax.wsdl.Operation
 *  javax.wsdl.Port
 *  javax.wsdl.PortType
 *  javax.wsdl.Service
 *  javax.wsdl.Types
 *  javax.wsdl.WSDLException
 *  javax.wsdl.extensions.ExtensionRegistry
 *  javax.wsdl.extensions.ExtensionSerializer
 *  javax.wsdl.extensions.mime.MIMEPart
 *  javax.wsdl.factory.WSDLFactory
 *  javax.wsdl.xml.WSDLReader
 *  org.mule.util.StringUtils
 */
package org.mule.devkit.3.9.0.internal.ws.common;

import com.ibm.wsdl.extensions.schema.SchemaSerializer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.ExtensionSerializer;
import javax.wsdl.extensions.mime.MIMEPart;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import org.mule.devkit.3.9.0.api.ws.exception.WrongParametrizationWsdlException;
import org.mule.util.StringUtils;

public class WsdlUtils {
    public static String[] getServiceNames(Definition wsdlDefinition) {
        Map services;
        ArrayList<String> serviceNames = new ArrayList<String>();
        if (wsdlDefinition != null && (services = wsdlDefinition.getServices()) != null) {
            for (QName name : services.keySet()) {
                serviceNames.add(name.getLocalPart());
            }
        }
        return serviceNames.toArray(new String[serviceNames.size()]);
    }

    public static String[] getPortNames(Service service) {
        ArrayList names = new ArrayList();
        if (service != null && service.getPorts() != null) {
            names.addAll(service.getPorts().keySet());
        }
        return names.toArray(new String[names.size()]);
    }

    public static String[] getOperationNames(Port port) {
        ArrayList<String> operationNames = new ArrayList<String>();
        List bindingOperations = port.getBinding().getBindingOperations();
        for (BindingOperation operation : bindingOperations) {
            operationNames.add(operation.getName());
        }
        return operationNames.toArray(new String[operationNames.size()]);
    }

    public static Service getService(Definition def, String serviceName) {
        WsdlUtils.validateBlankString(serviceName, "service name");
        Service service = def.getService(new QName(def.getTargetNamespace(), serviceName));
        WsdlUtils.validateNotNull(service, "The service name [" + serviceName + "] was not found in the current wsdl file.");
        return service;
    }

    public static Port getPort(Service service, String portName) {
        WsdlUtils.validateBlankString(portName, "port name");
        Port port = service.getPort(portName.trim());
        WsdlUtils.validateNotNull(port, "The port name [" + portName + "] was not found in the current wsdl file.");
        return port;
    }

    public static Operation getOperation(PortType portType, String operationName) {
        WsdlUtils.validateBlankString(operationName, "operation name");
        Operation operation = portType.getOperation(operationName, null, null);
        WsdlUtils.validateNotNull(operation, "The operation name [" + operationName + "] was not found in the current wsdl file.");
        return operation;
    }

    public static Definition parseWSDL(String wsdlLocation) {
        try {
            WsdlUtils.validateBlankString(wsdlLocation, "wsdl location");
            WSDLFactory factory = WSDLFactory.newInstance();
            ExtensionRegistry registry = factory.newPopulatedExtensionRegistry();
            registry.registerSerializer(Types.class, new QName("http://www.w3.org/2001/XMLSchema", "schema"), (ExtensionSerializer)new SchemaSerializer());
            QName header = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "header");
            registry.registerDeserializer(MIMEPart.class, header, registry.queryDeserializer(BindingInput.class, header));
            registry.registerSerializer(MIMEPart.class, header, registry.querySerializer(BindingInput.class, header));
            Class<?> clazz = registry.createExtension(BindingInput.class, header).getClass();
            registry.mapExtensionTypes(MIMEPart.class, header, clazz);
            WSDLReader wsdlReader = factory.newWSDLReader();
            wsdlReader.setFeature("javax.wsdl.verbose", false);
            wsdlReader.setFeature("javax.wsdl.importDocuments", true);
            wsdlReader.setExtensionRegistry(registry);
            Definition definition = wsdlReader.readWSDL(wsdlLocation);
            WsdlUtils.validateNotNull(definition, "There was an issue while parsing the wsdl file for [" + wsdlLocation + "].");
            return definition;
        }
        catch (WSDLException e) {
            throw new WrongParametrizationWsdlException("Something went wrong when parsing the wsdl file for [" + wsdlLocation + "].", e);
        }
    }

    public static void validateNotNull(Object paramValue, String errorMessage) {
        if (paramValue == null) {
            throw new WrongParametrizationWsdlException(errorMessage);
        }
    }

    public static void validateBlankString(String paramValue, String paramName) {
        if (StringUtils.isBlank((String)paramValue)) {
            throw new WrongParametrizationWsdlException("The " + paramName + " can not be blank nor null.");
        }
    }
}
