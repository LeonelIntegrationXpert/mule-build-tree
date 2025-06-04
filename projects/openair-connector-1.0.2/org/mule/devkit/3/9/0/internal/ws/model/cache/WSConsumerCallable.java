/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleContext
 *  org.mule.module.ws.consumer.WSConsumer
 *  org.mule.module.ws.consumer.WSConsumerConfig
 */
package org.mule.devkit.3.9.0.internal.ws.model.cache;

import java.util.concurrent.Callable;
import org.mule.api.MuleContext;
import org.mule.devkit.3.9.0.api.lifecycle.LifeCycleManager;
import org.mule.module.ws.consumer.WSConsumer;
import org.mule.module.ws.consumer.WSConsumerConfig;

public class WSConsumerCallable
implements Callable<WSConsumer> {
    private MuleContext muleContext;
    private WSConsumerConfig wsConsumerConfig;
    private String operation;

    public WSConsumerCallable(MuleContext muleContext, WSConsumerConfig wsConsumerConfig, String operation) {
        this.muleContext = muleContext;
        this.wsConsumerConfig = wsConsumerConfig;
        this.operation = operation;
    }

    @Override
    public WSConsumer call() throws Exception {
        WSConsumer wsConsumer = new WSConsumer();
        wsConsumer.setMuleContext(this.muleContext);
        wsConsumer.setConfig(this.wsConsumerConfig);
        wsConsumer.setOperation(this.operation);
        LifeCycleManager.executeInitialiseAndStart(wsConsumer);
        return wsConsumer;
    }
}
