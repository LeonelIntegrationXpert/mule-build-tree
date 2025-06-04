/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 */
package org.mule.devkit.3.9.0.internal.ws.common;

import com.google.common.base.Optional;
import java.net.URL;
import java.util.List;
import org.mule.devkit.3.9.0.api.ws.authentication.WsdlSecurityStrategy;
import org.mule.devkit.3.9.0.api.ws.transport.WsdlTransport;

public interface EnhancedServiceDefinition {
    public String getId();

    public URL getWsdlUrl();

    public String getService();

    public String getPort();

    public String getServiceAddress();

    public List<WsdlSecurityStrategy> getStrategies();

    public Optional<WsdlTransport> getTransport();

    public String getOperation();
}
