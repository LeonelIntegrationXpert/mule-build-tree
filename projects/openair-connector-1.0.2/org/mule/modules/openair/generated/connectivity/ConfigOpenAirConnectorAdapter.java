/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.ConnectionException
 */
package org.mule.modules.openair.generated.connectivity;

import org.mule.api.ConnectionException;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionAdapter;
import org.mule.devkit.3.9.0.internal.connection.management.TestableConnection;
import org.mule.modules.openair.config.Config;
import org.mule.modules.openair.generated.connectivity.ConnectionManagementConfigOpenAirConnectorConnectionKey;

public class ConfigOpenAirConnectorAdapter
extends Config
implements ConnectionManagementConnectionAdapter<Config, ConnectionManagementConfigOpenAirConnectorConnectionKey>,
TestableConnection<ConnectionManagementConfigOpenAirConnectorConnectionKey> {
    @Override
    public Config getStrategy() {
        return this;
    }

    @Override
    public void test(ConnectionManagementConfigOpenAirConnectorConnectionKey connectionKey) throws ConnectionException {
        super.connect(connectionKey.getCompany(), connectionKey.getUsername(), connectionKey.getPassword(), connectionKey.getApiNamespace(), connectionKey.getApiKey());
    }

    @Override
    public void connect(ConnectionManagementConfigOpenAirConnectorConnectionKey connectionKey) throws ConnectionException {
        super.connect(connectionKey.getCompany(), connectionKey.getUsername(), connectionKey.getPassword(), connectionKey.getApiNamespace(), connectionKey.getApiKey());
    }

    @Override
    public void disconnect() {
        super.disconnect();
    }

    @Override
    public String connectionId() {
        return super.getSessionID();
    }

    @Override
    public boolean isConnected() {
        return super.isConnected();
    }
}
