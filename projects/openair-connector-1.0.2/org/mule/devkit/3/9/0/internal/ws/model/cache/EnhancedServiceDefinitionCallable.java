/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Definition
 *  javax.wsdl.Service
 *  org.mule.util.StringUtils
 */
package org.mule.devkit.3.9.0.internal.ws.model.cache;

import java.util.List;
import java.util.concurrent.Callable;
import javax.wsdl.Definition;
import javax.wsdl.Service;
import org.mule.devkit.3.9.0.api.ws.authentication.WsdlSecurityStrategy;
import org.mule.devkit.3.9.0.api.ws.definition.ServiceDefinition;
import org.mule.devkit.3.9.0.api.ws.exception.WrongParametrizationWsdlException;
import org.mule.devkit.3.9.0.api.ws.transport.WsdlTransport;
import org.mule.devkit.3.9.0.internal.ws.common.DefaultEnhancedServiceDefinition;
import org.mule.devkit.3.9.0.internal.ws.common.EnhancedServiceDefinition;
import org.mule.devkit.3.9.0.internal.ws.common.WsdlAdapter;
import org.mule.devkit.3.9.0.internal.ws.common.WsdlUtils;
import org.mule.devkit.3.9.0.internal.ws.model.WsdlIntrospecterUtils;
import org.mule.util.StringUtils;

public class EnhancedServiceDefinitionCallable
implements Callable<EnhancedServiceDefinition> {
    private WsdlAdapter wsdlAdapter;
    private ServiceDefinition serviceDefinition;
    private String operation;

    public EnhancedServiceDefinitionCallable(WsdlAdapter wsdlAdapter, ServiceDefinition serviceDefinition, String operation) {
        this.wsdlAdapter = wsdlAdapter;
        this.serviceDefinition = serviceDefinition;
        this.operation = operation;
    }

    @Override
    public EnhancedServiceDefinition call() throws Exception {
        String serviceAddress = this.endpoint();
        WsdlTransport transport = this.wsdlAdapter.transport(this.serviceDefinition);
        List<WsdlSecurityStrategy> strategies = this.wsdlAdapter.security(this.serviceDefinition);
        return this.enhanceServiceDefinition(this.serviceDefinition, serviceAddress, strategies, transport, this.operation);
    }

    private String endpoint() throws Exception {
        String serviceAddress = this.wsdlAdapter.endpoint(this.serviceDefinition);
        if (StringUtils.isBlank((String)serviceAddress)) {
            throw new WrongParametrizationWsdlException("Service address obtained from @WsdlServiceEndpoint must not be null, nor empty.");
        }
        if (!StringUtils.startsWith((String)serviceAddress, (String)"http") && !StringUtils.startsWith((String)serviceAddress, (String)"https")) {
            throw new WrongParametrizationWsdlException(String.format("Service address obtained from @WsdlServiceEndpoint must start with 'http'/'https', but [%s] was found.", serviceAddress));
        }
        return serviceAddress;
    }

    private EnhancedServiceDefinition enhanceServiceDefinition(ServiceDefinition serviceDefinition, String serviceAddress, List<WsdlSecurityStrategy> strategies, WsdlTransport transport, String operation) {
        Definition definition;
        String serviceName = (String)serviceDefinition.getService().orNull();
        String portName = (String)serviceDefinition.getPort().orNull();
        if (!serviceDefinition.getService().isPresent()) {
            definition = WsdlUtils.parseWSDL(serviceDefinition.getWsdlUrl().toString());
            serviceName = WsdlIntrospecterUtils.resolveServiceName(serviceDefinition, definition);
        }
        if (!serviceDefinition.getPort().isPresent()) {
            definition = WsdlUtils.parseWSDL(serviceDefinition.getWsdlUrl().toString());
            Service service = WsdlIntrospecterUtils.resolveService(serviceDefinition, definition);
            portName = WsdlIntrospecterUtils.resolvePortName(serviceDefinition, service);
        }
        return new DefaultEnhancedServiceDefinition(serviceDefinition.getId(), serviceDefinition.getWsdlUrl(), serviceName, portName, serviceAddress, strategies, transport, operation);
    }
}
