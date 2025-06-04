/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool.KeyedPoolableObjectFactory
 *  org.mule.api.context.MuleContextAware
 *  org.mule.api.lifecycle.Disposable
 *  org.mule.api.lifecycle.Initialisable
 *  org.mule.api.lifecycle.Startable
 *  org.mule.api.lifecycle.Stoppable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.mule.devkit.3.9.0.internal.connection.management;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionAdapter;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionKey;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionManager;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManagementConnectorFactory
implements KeyedPoolableObjectFactory<ConnectionManagementConnectionKey, ConnectionManagementConnectorAdapter> {
    private static Logger logger = LoggerFactory.getLogger(ConnectionManagementConnectorFactory.class);
    private ConnectionManagementConnectionManager connManagementBasicConnectionManager;

    public ConnectionManagementConnectorFactory(ConnectionManagementConnectionManager connManagementBasicConnectionManager) {
        this.connManagementBasicConnectionManager = connManagementBasicConnectionManager;
    }

    public ConnectionManagementConnectorAdapter makeObject(ConnectionManagementConnectionKey key) throws Exception {
        ConnectionManagementConnectorAdapter connector;
        if (key == null) {
            logger.warn("Connection key is null");
            throw new RuntimeException("Invalid key type ".concat(key.getClass().getName()));
        }
        ConnectionManagementConnectionAdapter connection = this.connManagementBasicConnectionManager.newConnection();
        if (connection instanceof MuleContextAware) {
            ((MuleContextAware)connection).setMuleContext(this.connManagementBasicConnectionManager.getMuleContext());
        }
        if (connection instanceof Initialisable) {
            ((Initialisable)connection).initialise();
        }
        if (connection instanceof Startable) {
            ((Startable)connection).start();
        }
        if (connection != null && !connection.isConnected()) {
            connection.connect(key);
        }
        if ((connector = this.connManagementBasicConnectionManager.newConnector(connection)) instanceof MuleContextAware && connector != connection) {
            ((MuleContextAware)connector).setMuleContext(this.connManagementBasicConnectionManager.getMuleContext());
        }
        if (connector instanceof Initialisable && connector != connection) {
            ((Initialisable)connector).initialise();
        }
        if (connector instanceof Startable && connector != connection) {
            ((Startable)connector).start();
        }
        return connector;
    }

    public void destroyObject(ConnectionManagementConnectionKey key, ConnectionManagementConnectorAdapter adapter) throws Exception {
        try {
            ConnectionManagementConnectionAdapter connection = this.connManagementBasicConnectionManager.getConnectionAdapter(adapter);
            connection.disconnect();
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            if (adapter instanceof Stoppable) {
                ((Stoppable)adapter).stop();
            }
            if (adapter instanceof Disposable) {
                ((Disposable)adapter).dispose();
            }
        }
    }

    public boolean validateObject(ConnectionManagementConnectionKey key, ConnectionManagementConnectorAdapter adapter) {
        try {
            ConnectionManagementConnectionAdapter connection = this.connManagementBasicConnectionManager.getConnectionAdapter(adapter);
            return connection.isConnected();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), (Throwable)e);
            return false;
        }
    }

    public void activateObject(ConnectionManagementConnectionKey key, ConnectionManagementConnectorAdapter adapter) throws Exception {
        ConnectionManagementConnectionAdapter connection = this.connManagementBasicConnectionManager.getConnectionAdapter(adapter);
        if (!connection.isConnected()) {
            connection.connect(key);
        }
    }

    public void passivateObject(ConnectionManagementConnectionKey key, ConnectionManagementConnectorAdapter adapter) throws Exception {
    }
}
