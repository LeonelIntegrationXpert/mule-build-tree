/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.DefaultMuleException
 *  org.mule.api.MuleContext
 *  org.mule.api.MuleException
 *  org.mule.config.i18n.CoreMessages
 *  org.mule.module.http.api.requester.HttpRequesterConfig
 *  org.mule.module.ws.consumer.WSConsumerConfig
 *  org.mule.module.ws.security.SecurityStrategy
 *  org.mule.module.ws.security.WSSecurity
 *  org.mule.module.ws.security.WssTimestampSecurityStrategy
 *  org.mule.module.ws.security.WssUsernameTokenSecurityStrategy
 */
package org.mule.devkit.3.9.0.internal.ws.model.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.mule.api.DefaultMuleException;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.config.i18n.CoreMessages;
import org.mule.devkit.3.9.0.api.lifecycle.LifeCycleManager;
import org.mule.devkit.3.9.0.api.ws.authentication.WsdlSecurityStrategy;
import org.mule.devkit.3.9.0.api.ws.authentication.WsdlTimestamp;
import org.mule.devkit.3.9.0.api.ws.authentication.WsdlUsernameToken;
import org.mule.devkit.3.9.0.api.ws.transport.WsdlTransport;
import org.mule.devkit.3.9.0.internal.ws.common.EnhancedServiceDefinition;
import org.mule.devkit.3.9.0.internal.ws.transport.WsdlTransportException;
import org.mule.devkit.3.9.0.internal.ws.transport.WsdlTransportFactoryBuilder;
import org.mule.module.http.api.requester.HttpRequesterConfig;
import org.mule.module.ws.consumer.WSConsumerConfig;
import org.mule.module.ws.security.SecurityStrategy;
import org.mule.module.ws.security.WSSecurity;
import org.mule.module.ws.security.WssTimestampSecurityStrategy;
import org.mule.module.ws.security.WssUsernameTokenSecurityStrategy;

public class WSConsumerConfigCallable
implements Callable<WSConsumerConfig> {
    private MuleContext muleContext;
    private EnhancedServiceDefinition enhancedServiceDefinition;

    public WSConsumerConfigCallable(MuleContext muleContext, EnhancedServiceDefinition enhancedServiceDefinition) {
        this.muleContext = muleContext;
        this.enhancedServiceDefinition = enhancedServiceDefinition;
    }

    @Override
    public WSConsumerConfig call() throws Exception {
        WSConsumerConfig wsConsumerConfig = new WSConsumerConfig();
        wsConsumerConfig.setMuleContext(this.muleContext);
        wsConsumerConfig.setWsdlLocation(this.enhancedServiceDefinition.getWsdlUrl().toString());
        wsConsumerConfig.setService(this.enhancedServiceDefinition.getService());
        wsConsumerConfig.setPort(this.enhancedServiceDefinition.getPort());
        wsConsumerConfig.setServiceAddress(this.enhancedServiceDefinition.getServiceAddress());
        this.initialiseSecurity(wsConsumerConfig);
        this.initialiseConnectorConfig(wsConsumerConfig);
        LifeCycleManager.executeInitialiseAndStart(wsConsumerConfig);
        return wsConsumerConfig;
    }

    private void initialiseConnectorConfig(WSConsumerConfig wsConsumerConfig) throws MuleException {
        if (!this.enhancedServiceDefinition.getTransport().isPresent()) {
            return;
        }
        WsdlTransport wsdlTransport = (WsdlTransport)this.enhancedServiceDefinition.getTransport().get();
        try {
            HttpRequesterConfig httpRequesterConfig = WsdlTransportFactoryBuilder.build(wsdlTransport, this.muleContext);
            wsConsumerConfig.setConnectorConfig(httpRequesterConfig);
        }
        catch (WsdlTransportException e) {
            throw new DefaultMuleException(CoreMessages.createStaticMessage((String)"There was an error when trying to resolve the underlying transport, please refer to the logs to see more details."), (Throwable)e);
        }
    }

    private void initialiseSecurity(WSConsumerConfig wsConsumerConfig) {
        List<SecurityStrategy> securityStrategyList = this.getSecurityStrategies();
        if (!securityStrategyList.isEmpty()) {
            WSSecurity wsSecurity = new WSSecurity();
            wsSecurity.setStrategies(securityStrategyList);
            wsConsumerConfig.setSecurity(wsSecurity);
        }
    }

    private List<SecurityStrategy> getSecurityStrategies() {
        ArrayList<SecurityStrategy> securityStrategyList = new ArrayList<SecurityStrategy>();
        for (WsdlSecurityStrategy wsdlSecurityStrategy : this.enhancedServiceDefinition.getStrategies()) {
            if (wsdlSecurityStrategy instanceof WsdlUsernameToken) {
                WsdlUsernameToken wsdlUsernameToken = (WsdlUsernameToken)wsdlSecurityStrategy;
                WssUsernameTokenSecurityStrategy wssUsernameTokenSecurityStrategy = new WssUsernameTokenSecurityStrategy();
                wssUsernameTokenSecurityStrategy.setUsername(wsdlUsernameToken.getUsername());
                wssUsernameTokenSecurityStrategy.setPassword(wsdlUsernameToken.getPassword());
                wssUsernameTokenSecurityStrategy.setPasswordType(wsdlUsernameToken.getPasswordType());
                wssUsernameTokenSecurityStrategy.setAddCreated(wsdlUsernameToken.isAddCreated());
                wssUsernameTokenSecurityStrategy.setAddNonce(wsdlUsernameToken.isAddNonce());
                securityStrategyList.add((SecurityStrategy)wssUsernameTokenSecurityStrategy);
            }
            if (!(wsdlSecurityStrategy instanceof WsdlTimestamp)) continue;
            WsdlTimestamp wsdlTimestamp = (WsdlTimestamp)wsdlSecurityStrategy;
            WssTimestampSecurityStrategy wssTimestampSecurityStrategy = new WssTimestampSecurityStrategy();
            wssTimestampSecurityStrategy.setExpires(wsdlTimestamp.getExpires());
            securityStrategyList.add((SecurityStrategy)wssTimestampSecurityStrategy);
        }
        return securityStrategyList;
    }
}
