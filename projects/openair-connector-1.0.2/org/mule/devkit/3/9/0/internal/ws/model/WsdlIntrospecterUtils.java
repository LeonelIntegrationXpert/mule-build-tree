/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Definition
 *  javax.wsdl.Port
 *  javax.wsdl.Service
 */
package org.mule.devkit.3.9.0.internal.ws.model;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import org.mule.devkit.3.9.0.api.ws.definition.ServiceDefinition;
import org.mule.devkit.3.9.0.api.ws.exception.InvalidWsdlFileException;
import org.mule.devkit.3.9.0.internal.ws.common.WsdlUtils;

public class WsdlIntrospecterUtils {
    public static Service resolveService(ServiceDefinition serviceDefinition, Definition definition) {
        return serviceDefinition.getService().isPresent() ? WsdlUtils.getService(definition, (String)serviceDefinition.getService().get()) : WsdlIntrospecterUtils.lookUpServiceOn(serviceDefinition, definition);
    }

    public static Port resolvePort(ServiceDefinition serviceDefinition, Service service, Definition definition) {
        return serviceDefinition.getPort().isPresent() ? WsdlUtils.getPort(service, (String)serviceDefinition.getPort().get()) : WsdlIntrospecterUtils.lookUpPortOn(serviceDefinition, service);
    }

    public static String resolveServiceName(ServiceDefinition serviceDefinition, Definition definition) {
        return WsdlIntrospecterUtils.lookUpServiceNameOn(serviceDefinition, definition);
    }

    public static String resolvePortName(ServiceDefinition serviceDefinition, Service service) {
        return WsdlIntrospecterUtils.lookUpPortNameOn(serviceDefinition, service);
    }

    private static String lookUpServiceNameOn(ServiceDefinition serviceDefinition, Definition definition) {
        String[] serviceNames = WsdlUtils.getServiceNames(definition);
        if (serviceNames.length > 1) {
            String errorMessage = String.format("WSDL file [%s] has [%d] services in it. When providing more than 1 service, the connector should specify which one is the intended to be used.", serviceDefinition.getId(), serviceNames.length);
            throw new InvalidWsdlFileException(errorMessage);
        }
        if (serviceNames.length == 0) {
            String errorMessage = String.format("WSDL file [%s] does not have any services at all. Check if the WSDL file was properly generated.", serviceDefinition.getId());
            throw new InvalidWsdlFileException(errorMessage);
        }
        return serviceNames[0];
    }

    private static Service lookUpServiceOn(ServiceDefinition serviceDefinition, Definition definition) {
        return WsdlUtils.getService(definition, WsdlIntrospecterUtils.lookUpServiceNameOn(serviceDefinition, definition));
    }

    private static String lookUpPortNameOn(ServiceDefinition serviceDefinition, Service service) {
        String[] portNames = WsdlUtils.getPortNames(service);
        if (portNames.length > 1) {
            String errorMessage = String.format("WSDL file [%s] has [%d] ports in it. When providing more than 1 port, the connector should specify which one is the intended to be used.", serviceDefinition.getId(), portNames.length);
            throw new InvalidWsdlFileException(errorMessage);
        }
        if (portNames.length == 0) {
            String errorMessage = String.format("WSDL file [%s] does not have any port at all. Check if the WSDL file was properly generated.", serviceDefinition.getId());
            throw new InvalidWsdlFileException(errorMessage);
        }
        return portNames[0];
    }

    private static Port lookUpPortOn(ServiceDefinition serviceDefinition, Service service) {
        return WsdlUtils.getPort(service, WsdlIntrospecterUtils.lookUpPortNameOn(serviceDefinition, service));
    }
}
