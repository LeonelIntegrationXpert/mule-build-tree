/*
 * Decompiled with CFR 0.152.
 */
package org.mule.modules.openair.generated.connectivity;

import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionKey;

public class ConnectionManagementConfigOpenAirConnectorConnectionKey
implements ConnectionManagementConnectionKey {
    private String company;
    private String username;
    private String password;
    private String apiNamespace;
    private String apiKey;

    public ConnectionManagementConfigOpenAirConnectorConnectionKey(String company, String username, String password, String apiNamespace, String apiKey) {
        this.company = company;
        this.username = username;
        this.password = password;
        this.apiNamespace = apiNamespace;
        this.apiKey = apiKey;
    }

    public void setUsername(String value) {
        this.username = value;
    }

    public String getUsername() {
        return this.username;
    }

    public void setApiNamespace(String value) {
        this.apiNamespace = value;
    }

    public String getApiNamespace() {
        return this.apiNamespace;
    }

    public void setCompany(String value) {
        this.company = value;
    }

    public String getCompany() {
        return this.company;
    }

    public void setPassword(String value) {
        this.password = value;
    }

    public String getPassword() {
        return this.password;
    }

    public void setApiKey(String value) {
        this.apiKey = value;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public int hashCode() {
        int result = this.username != null ? this.username.hashCode() : 0;
        return result;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConnectionManagementConfigOpenAirConnectorConnectionKey)) {
            return false;
        }
        ConnectionManagementConfigOpenAirConnectorConnectionKey that = (ConnectionManagementConfigOpenAirConnectorConnectionKey)o;
        return !(this.username != null ? !this.username.equals(that.username) : that.username != null);
    }
}
