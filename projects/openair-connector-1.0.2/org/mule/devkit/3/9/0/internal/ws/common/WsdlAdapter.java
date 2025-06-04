/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 */
package org.mule.devkit.3.9.0.internal.ws.common;

import com.google.common.base.Optional;
import java.util.List;
import org.mule.devkit.3.9.0.api.ws.authentication.WsdlSecurityStrategy;
import org.mule.devkit.3.9.0.api.ws.definition.ServiceDefinition;
import org.mule.devkit.3.9.0.api.ws.transport.WsdlTransport;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectorAdapter;
import org.mule.devkit.3.9.0.internal.ws.common.WSResolver;
import org.mule.devkit.3.9.0.internal.ws.model.SoapBodyEnricher;
import org.w3c.dom.Document;

public interface WsdlAdapter
extends ConnectionManagementConnectorAdapter {
    public List<ServiceDefinition> serviceDefinitions() throws Exception;

    public String endpoint(ServiceDefinition var1) throws Exception;

    public List<WsdlSecurityStrategy> security(ServiceDefinition var1) throws Exception;

    public WsdlTransport transport(ServiceDefinition var1) throws Exception;

    public Optional<List<Document>> headers(ServiceDefinition var1, String var2) throws Exception;

    public Optional<? extends SoapBodyEnricher> bodyEnricher();

    public WSResolver wsResolver() throws Exception;

    public Optional<String> singleServiceDefinitionId() throws Exception;

    public String wsdlSeparator();

    public void handleException(Exception var1) throws Exception;

    public List<Class<? extends Exception>> managedExceptions();
}
