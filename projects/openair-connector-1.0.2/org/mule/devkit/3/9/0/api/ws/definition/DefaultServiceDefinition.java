/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  org.mule.util.IOUtils
 *  org.mule.util.StringUtils
 */
package org.mule.devkit.3.9.0.api.ws.definition;

import com.google.common.base.Optional;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mule.devkit.3.9.0.api.ws.definition.ServiceDefinition;
import org.mule.devkit.3.9.0.internal.ws.common.ServiceDefinitionInitializationException;
import org.mule.util.IOUtils;
import org.mule.util.StringUtils;

public class DefaultServiceDefinition
implements ServiceDefinition {
    private String id;
    private String displayName;
    private URL wsdlUrl;
    private Optional<String> service;
    private Optional<String> port;
    private List<String> excludedOperations = new ArrayList<String>();
    private Map<String, Object> properties = new HashMap<String, Object>();

    public DefaultServiceDefinition(String id, String displayName, String wsdlLocation) {
        this(id, displayName, wsdlLocation, null, null);
    }

    public DefaultServiceDefinition(String id, String displayName, String wsdlLocation, String service, String port) {
        URL wsdlUrl = this.resolveWsdlUrl(id, wsdlLocation);
        this.initialize(id, displayName, wsdlUrl, service, port);
    }

    private void initialize(String id, String displayName, URL wsdlUrl, String service, String port) {
        this.verifyParameters(id, wsdlUrl);
        this.setId(id);
        this.setDisplayName(displayName);
        this.setWsdlUrl(wsdlUrl);
        this.setService(service);
        this.setPort(port);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public URL getWsdlUrl() {
        return this.wsdlUrl;
    }

    @Override
    public Optional<String> getService() {
        return this.service;
    }

    @Override
    public Optional<String> getPort() {
        return this.port;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public List<String> getExcludedOperations() {
        return this.excludedOperations;
    }

    private URL resolveWsdlUrl(String id, String wsdlLocation) {
        if (StringUtils.isBlank((String)wsdlLocation)) {
            throw new ServiceDefinitionInitializationException(String.format("The given 'wsdlLocation' for [%s] ServiceDefinition must not be null (nor empty).", id));
        }
        return IOUtils.getResourceAsUrl((String)wsdlLocation, this.getClass());
    }

    private void verifyParameters(String id, URL wsdlUrl) {
        if (StringUtils.isBlank((String)id)) {
            throw new ServiceDefinitionInitializationException(String.format("The given 'id' for a ServiceDefinition must not be null (nor empty). Received [%s]", id));
        }
        if (wsdlUrl == null) {
            throw new ServiceDefinitionInitializationException(String.format("The given 'wsdlUrl' for [%s] ServiceDefinition must not be null (the provided wsdl file is probably unreachable).", id));
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setWsdlUrl(URL wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public void setService(String service) {
        this.service = Optional.fromNullable((Object)service);
    }

    public void setPort(String port) {
        this.port = Optional.fromNullable((Object)port);
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void addProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    public void setExcludedOperations(List<String> excludedOperations) {
        this.excludedOperations = excludedOperations;
    }

    public void excludeOperation(String operationName) {
        if (StringUtils.isBlank((String)operationName)) {
            throw new IllegalArgumentException("Operation name cannot be blank");
        }
        this.excludedOperations.add(operationName);
    }
}
