/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.RemovalListener
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.mule.api.MuleContext
 *  org.mule.api.MuleException
 *  org.mule.module.ws.consumer.WSConsumer
 *  org.mule.module.ws.consumer.WSConsumerConfig
 */
package org.mule.devkit.3.9.0.internal.ws.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.devkit.3.9.0.api.lifecycle.LifeCycleManager;
import org.mule.devkit.3.9.0.api.ws.definition.ServiceDefinition;
import org.mule.devkit.3.9.0.api.ws.exception.WrongParametrizationWsdlException;
import org.mule.devkit.3.9.0.internal.ws.common.EnhancedServiceDefinition;
import org.mule.devkit.3.9.0.internal.ws.common.WSResolver;
import org.mule.devkit.3.9.0.internal.ws.common.WsdlAdapter;
import org.mule.devkit.3.9.0.internal.ws.model.cache.EnhancedServiceDefinitionCallable;
import org.mule.devkit.3.9.0.internal.ws.model.cache.EnhancedServiceDefinitionKey;
import org.mule.devkit.3.9.0.internal.ws.model.cache.WSConsumerCallable;
import org.mule.devkit.3.9.0.internal.ws.model.cache.WSConsumerConfigCallable;
import org.mule.devkit.3.9.0.internal.ws.model.cache.WSConsumerConfigKey;
import org.mule.devkit.3.9.0.internal.ws.model.cache.WSConsumerKey;
import org.mule.devkit.3.9.0.internal.ws.model.cache.exception.WSLifecycleException;
import org.mule.module.ws.consumer.WSConsumer;
import org.mule.module.ws.consumer.WSConsumerConfig;

public class DefaultWSResolver
implements WSResolver {
    private ImmutableMap<String, ServiceDefinition> definitionsCache;
    private Cache<WSConsumerConfigKey, WSConsumerConfig> wsConfigCache;
    private Cache<WSConsumerKey, WSConsumer> wsConsumerCache;
    private Cache<EnhancedServiceDefinitionKey, EnhancedServiceDefinition> enhancedServiceDefinitionCache;

    public DefaultWSResolver(WsdlAdapter wsdlAdapter) throws Exception {
        this.initialize(wsdlAdapter);
    }

    @Override
    public ServiceDefinition serviceDefinition(String id) {
        if (!this.definitionsCache.containsKey((Object)id)) {
            throw new WrongParametrizationWsdlException("The connector is being invoked with an id ([" + id + "]) that cannot be resolved.");
        }
        return (ServiceDefinition)this.definitionsCache.get((Object)id);
    }

    @Override
    public EnhancedServiceDefinition enhancedServiceDefinition(String id, WsdlAdapter wsdlAdapter, String operation) throws Exception {
        ServiceDefinition serviceDefinition = this.serviceDefinition(id);
        EnhancedServiceDefinitionKey enhancedServiceDefinitionKey = new EnhancedServiceDefinitionKey(id, operation);
        return (EnhancedServiceDefinition)this.enhancedServiceDefinitionCache.get((Object)enhancedServiceDefinitionKey, (Callable)new EnhancedServiceDefinitionCallable(wsdlAdapter, serviceDefinition, operation));
    }

    @Override
    public WSConsumer wsConsumer(EnhancedServiceDefinition enhancedServiceDefinition, MuleContext muleContext) throws ExecutionException {
        WSConsumerConfigKey wsConsumerConfigKey = new WSConsumerConfigKey(enhancedServiceDefinition.getId(), enhancedServiceDefinition.getServiceAddress(), enhancedServiceDefinition.getService(), enhancedServiceDefinition.getPort());
        WSConsumerConfig wsConsumerConfig = this.getWsConsumerConfig(wsConsumerConfigKey, enhancedServiceDefinition, muleContext);
        WSConsumerKey wsConsumerKey = new WSConsumerKey(wsConsumerConfigKey, enhancedServiceDefinition.getOperation());
        return (WSConsumer)this.wsConsumerCache.get((Object)wsConsumerKey, (Callable)new WSConsumerCallable(muleContext, wsConsumerConfig, enhancedServiceDefinition.getOperation()));
    }

    @Override
    public void dispose() {
        this.wsConfigCache.invalidateAll();
        this.wsConsumerCache.invalidateAll();
        this.definitionsCache = null;
        this.enhancedServiceDefinitionCache.invalidateAll();
    }

    @Override
    public ImmutableMap<String, ServiceDefinition> serviceDefinitions() {
        return this.definitionsCache;
    }

    private void initialize(WsdlAdapter wsdlAdapter) throws Exception {
        this.wsConfigCache = CacheBuilder.newBuilder().removalListener(this.getRemovalListener()).build();
        this.wsConsumerCache = CacheBuilder.newBuilder().removalListener(this.getRemovalListener()).build();
        this.enhancedServiceDefinitionCache = CacheBuilder.newBuilder().removalListener(this.getRemovalListener()).build();
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (ServiceDefinition serviceDefinition : wsdlAdapter.serviceDefinitions()) {
            builder.put((Object)serviceDefinition.getId(), (Object)serviceDefinition);
        }
        this.definitionsCache = builder.build();
    }

    private WSConsumerConfig getWsConsumerConfig(WSConsumerConfigKey wsConsumerConfigKey, EnhancedServiceDefinition enhancedServiceDefinition, MuleContext muleContext) throws ExecutionException {
        return (WSConsumerConfig)this.wsConfigCache.get((Object)wsConsumerConfigKey, (Callable)new WSConsumerConfigCallable(muleContext, enhancedServiceDefinition));
    }

    private <T, K> RemovalListener<T, K> getRemovalListener() {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    private void executeLifecycleEnding(Object key, Object value) {
        try {
            LifeCycleManager.executeStopAndDispose(value);
        }
        catch (MuleException e) {
            throw new WSLifecycleException("There was an issue while trying to close the config referenced by " + key.toString(), e);
        }
    }

    static /* synthetic */ void access$000(DefaultWSResolver x0, Object x1, Object x2) {
        x0.executeLifecycleEnding(x1, x2);
    }
}
