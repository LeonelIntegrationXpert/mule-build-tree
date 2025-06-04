/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package org.mule.modules.wsdl.openair.internal.runtime;

import org.jetbrains.annotations.NotNull;

public class ServiceDefinition {
    private final String namespace;
    private final String serviceName;
    private final String portName;
    private final String baseEndpoint;
    private final String headerPrefix;

    private ServiceDefinition(@NotNull String namespace, @NotNull String serviceName, @NotNull String portName, @NotNull String baseEndpoint, @NotNull String headerPrefix) {
        this.namespace = namespace;
        this.serviceName = serviceName;
        this.portName = portName;
        this.baseEndpoint = baseEndpoint;
        this.headerPrefix = headerPrefix;
    }

    public static ServiceDefinition create(@NotNull String namespace, @NotNull String serviceName, @NotNull String portName, @NotNull String baseEndpoint, @NotNull String headerPrefix) {
        return new ServiceDefinition(namespace, serviceName, portName, baseEndpoint, headerPrefix);
    }

    @NotNull
    public String getNamespace() {
        return this.namespace;
    }

    @NotNull
    public String getServiceName() {
        return this.serviceName;
    }

    @NotNull
    public String getPortName() {
        return this.portName;
    }

    @NotNull
    public String getBaseEndpoint() {
        return this.baseEndpoint;
    }

    @NotNull
    public String getHeaderPrefix() {
        return this.headerPrefix;
    }

    public String toString() {
        return "ServiceDefinition{namespace='" + this.namespace + '\'' + ", serviceName='" + this.serviceName + '\'' + ", portName='" + this.portName + '\'' + ", baseEndpoint='" + this.baseEndpoint + '\'' + ", headerPrefix='" + this.headerPrefix + '\'' + '}';
    }
}
