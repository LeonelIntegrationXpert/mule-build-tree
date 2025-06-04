/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleContext
 *  org.mule.api.MuleException
 *  org.mule.module.http.api.HttpAuthentication
 *  org.mule.module.http.api.requester.HttpRequesterConfig
 *  org.mule.module.http.api.requester.HttpRequesterConfigBuilder
 *  org.mule.module.http.api.requester.authentication.BasicAuthenticationBuilder
 */
package org.mule.devkit.3.9.0.internal.ws.transport;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.devkit.3.9.0.api.lifecycle.LifeCycleManager;
import org.mule.devkit.3.9.0.api.ws.transport.HttpBasicWsdlTransport;
import org.mule.devkit.3.9.0.api.ws.transport.HttpRequesterConfigWsdlTransport;
import org.mule.devkit.3.9.0.api.ws.transport.WsdlTransport;
import org.mule.devkit.3.9.0.internal.ws.transport.WsdlTransportException;
import org.mule.module.http.api.HttpAuthentication;
import org.mule.module.http.api.requester.HttpRequesterConfig;
import org.mule.module.http.api.requester.HttpRequesterConfigBuilder;
import org.mule.module.http.api.requester.authentication.BasicAuthenticationBuilder;

public abstract class WsdlTransportFactoryBuilder {
    public static HttpRequesterConfig build(WsdlTransport wsdlTransport, MuleContext muleContext) throws MuleException, WsdlTransportException {
        HttpRequesterConfig httpRequesterConfig;
        if (wsdlTransport instanceof HttpBasicWsdlTransport) {
            httpRequesterConfig = WsdlTransportFactoryBuilder.build((HttpBasicWsdlTransport)wsdlTransport, muleContext);
        } else if (wsdlTransport instanceof HttpRequesterConfigWsdlTransport) {
            httpRequesterConfig = WsdlTransportFactoryBuilder.build((HttpRequesterConfigWsdlTransport)wsdlTransport);
        } else {
            throw new WsdlTransportException("The current implementation only supports HttpBasicWsdlTransport and HttpRequesterConfigWsdlTransport as types of transports");
        }
        LifeCycleManager.executeInitialiseAndStart(httpRequesterConfig);
        return httpRequesterConfig;
    }

    private static HttpRequesterConfig build(HttpBasicWsdlTransport httpBasicWsdlTransport, MuleContext muleContext) throws MuleException {
        BasicAuthenticationBuilder basicAuthenticationBuilder = new BasicAuthenticationBuilder(muleContext);
        basicAuthenticationBuilder.setUsername(httpBasicWsdlTransport.getUser());
        basicAuthenticationBuilder.setPassword(httpBasicWsdlTransport.getPass());
        basicAuthenticationBuilder.setPreemptive(httpBasicWsdlTransport.isPreemptive());
        HttpAuthentication httpAuthentication = basicAuthenticationBuilder.build();
        HttpRequesterConfigBuilder httpRequesterConfigBuilder = new HttpRequesterConfigBuilder(muleContext);
        httpRequesterConfigBuilder.setAuthentication(httpAuthentication);
        HttpRequesterConfig httpRequesterConfig = httpRequesterConfigBuilder.build();
        return httpRequesterConfig;
    }

    private static HttpRequesterConfig build(HttpRequesterConfigWsdlTransport httpRequesterConfigWsdlTransport) throws MuleException {
        return httpRequesterConfigWsdlTransport.getConnectorConfig();
    }
}
