/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 */
package org.mule.devkit.3.9.0.internal.ws.common;

import com.google.common.base.Optional;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.mule.devkit.3.9.0.api.ws.authentication.WsdlSecurityStrategy;
import org.mule.devkit.3.9.0.api.ws.transport.WsdlTransport;
import org.mule.devkit.3.9.0.internal.ws.common.EnhancedServiceDefinition;

public class DefaultEnhancedServiceDefinition
implements EnhancedServiceDefinition {
    List<WsdlSecurityStrategy> strategies;
    private String id;
    private URL wsdlUrl;
    private String service;
    private String port;
    private String serviceAddress;
    private Optional<WsdlTransport> transport;
    private String operation;

    public DefaultEnhancedServiceDefinition(String id, URL wsdlUrl, String service, String port, String serviceAddress, List<WsdlSecurityStrategy> strategies, WsdlTransport transport, String operation) {
        this.id = id;
        this.wsdlUrl = wsdlUrl;
        this.service = service;
        this.port = port;
        this.serviceAddress = serviceAddress;
        this.strategies = strategies == null ? Collections.EMPTY_LIST : strategies;
        this.transport = Optional.fromNullable((Object)transport);
        this.operation = operation;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public URL getWsdlUrl() {
        return this.wsdlUrl;
    }

    @Override
    public String getService() {
        return this.service;
    }

    @Override
    public String getPort() {
        return this.port;
    }

    @Override
    public String getServiceAddress() {
        return this.serviceAddress;
    }

    @Override
    public List<WsdlSecurityStrategy> getStrategies() {
        return this.strategies;
    }

    @Override
    public Optional<WsdlTransport> getTransport() {
        return this.transport;
    }

    @Override
    public String getOperation() {
        return this.operation;
    }
}
