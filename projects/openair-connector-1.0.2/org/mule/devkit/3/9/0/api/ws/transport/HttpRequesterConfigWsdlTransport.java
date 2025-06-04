/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.module.http.api.requester.HttpRequesterConfig
 */
package org.mule.devkit.3.9.0.api.ws.transport;

import org.mule.devkit.3.9.0.api.ws.transport.WsdlTransport;
import org.mule.module.http.api.requester.HttpRequesterConfig;

public class HttpRequesterConfigWsdlTransport
implements WsdlTransport {
    private HttpRequesterConfig connectorConfig;

    public HttpRequesterConfigWsdlTransport(HttpRequesterConfig connectorConfig) {
        this.connectorConfig = connectorConfig;
    }

    public HttpRequesterConfig getConnectorConfig() {
        return this.connectorConfig;
    }
}
