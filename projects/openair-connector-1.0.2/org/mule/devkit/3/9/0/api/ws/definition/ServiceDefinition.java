/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 */
package org.mule.devkit.3.9.0.api.ws.definition;

import com.google.common.base.Optional;
import java.net.URL;
import java.util.List;
import java.util.Map;

public interface ServiceDefinition {
    public String getId();

    public String getDisplayName();

    public URL getWsdlUrl();

    public Optional<String> getService();

    public Optional<String> getPort();

    public Map<String, Object> getProperties();

    public List<String> getExcludedOperations();
}
