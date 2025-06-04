/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.mule.api.MuleContext
 *  org.mule.module.ws.consumer.WSConsumer
 */
package org.mule.devkit.3.9.0.internal.ws.common;

import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;
import org.mule.api.MuleContext;
import org.mule.devkit.3.9.0.api.ws.definition.ServiceDefinition;
import org.mule.devkit.3.9.0.internal.ws.common.EnhancedServiceDefinition;
import org.mule.devkit.3.9.0.internal.ws.common.WsdlAdapter;
import org.mule.module.ws.consumer.WSConsumer;

public interface WSResolver {
    public ServiceDefinition serviceDefinition(String var1);

    public EnhancedServiceDefinition enhancedServiceDefinition(String var1, WsdlAdapter var2, String var3) throws Exception;

    public WSConsumer wsConsumer(EnhancedServiceDefinition var1, MuleContext var2) throws ExecutionException;

    public void dispose();

    public ImmutableMap<String, ServiceDefinition> serviceDefinitions();
}
