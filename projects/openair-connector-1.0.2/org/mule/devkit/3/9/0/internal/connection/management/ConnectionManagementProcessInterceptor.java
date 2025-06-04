/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleContext
 *  org.mule.api.MuleEvent
 *  org.mule.api.MuleMessage
 *  org.mule.api.devkit.ProcessInterceptor
 *  org.mule.api.processor.MessageProcessor
 *  org.mule.api.routing.filter.Filter
 *  org.mule.devkit.processor.ExpressionEvaluatorSupport
 *  org.mule.security.oauth.callback.ProcessCallback
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.mule.devkit.3.9.0.internal.connection.management;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.devkit.ProcessInterceptor;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.filter.Filter;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionKey;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionManager;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectorAdapter;
import org.mule.devkit.3.9.0.internal.connection.management.UnableToAcquireConnectionException;
import org.mule.devkit.3.9.0.internal.connection.management.UnableToReleaseConnectionException;
import org.mule.devkit.processor.ExpressionEvaluatorSupport;
import org.mule.security.oauth.callback.ProcessCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManagementProcessInterceptor<P, Adapter extends ConnectionManagementConnectorAdapter, Key extends ConnectionManagementConnectionKey, Strategy>
extends ExpressionEvaluatorSupport
implements ProcessInterceptor<P, Adapter> {
    private static Logger logger = LoggerFactory.getLogger(ConnectionManagementProcessInterceptor.class);
    private final ConnectionManagementConnectionManager<Key, Adapter, Strategy> connManagementBasicConnectionManager;
    private final MuleContext muleContext;
    private final ProcessInterceptor<P, Adapter> next;

    public ConnectionManagementProcessInterceptor(ProcessInterceptor<P, Adapter> next, ConnectionManagementConnectionManager<Key, Adapter, Strategy> connManagementBasicConnectionManager, MuleContext muleContext) {
        this.next = next;
        this.connManagementBasicConnectionManager = connManagementBasicConnectionManager;
        this.muleContext = muleContext;
    }

    public P execute(ProcessCallback<P, Adapter> processCallback, Adapter object, MessageProcessor messageProcessor, MuleEvent event) throws Exception {
        ConnectionManagementConnectorAdapter adapter = null;
        Object key = null;
        key = this.connManagementBasicConnectionManager.getConnectionKey(messageProcessor, event);
        try {
            Object connection;
            if (logger.isDebugEnabled()) {
                logger.debug("Attempting to acquire connection using " + key.toString());
            }
            if ((adapter = (ConnectionManagementConnectorAdapter)this.connManagementBasicConnectionManager.acquireConnection(key)) == null) {
                throw new UnableToAcquireConnectionException();
            }
            if (logger.isDebugEnabled()) {
                connection = this.connManagementBasicConnectionManager.getConnectionAdapter(adapter);
                logger.debug("Connection has been acquired with [id=" + connection.connectionId() + "]");
            }
            connection = this.next.execute(processCallback, adapter, messageProcessor, event);
            return (P)connection;
        }
        catch (Exception e) {
            if (processCallback.getManagedExceptions() != null) {
                for (Class exceptionClass : processCallback.getManagedExceptions()) {
                    if (!exceptionClass.isInstance(e)) continue;
                    if (logger.isDebugEnabled()) {
                        logger.debug("An exception ( " + exceptionClass.getName() + ") has been thrown. Destroying the connection with [id=" + this.connManagementBasicConnectionManager.getConnectionAdapter(adapter).connectionId() + "]");
                    }
                    try {
                        if (adapter == null) continue;
                        this.connManagementBasicConnectionManager.destroyConnection(key, adapter);
                        adapter = null;
                    }
                    catch (Exception innerException) {
                        logger.error(innerException.getMessage(), (Throwable)innerException);
                    }
                }
            }
            throw e;
        }
        finally {
            try {
                if (adapter != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Releasing the connection back into the pool [id=" + this.connManagementBasicConnectionManager.getConnectionAdapter(adapter).connectionId() + "]");
                    }
                    this.connManagementBasicConnectionManager.releaseConnection(key, adapter);
                }
            }
            catch (Exception e) {
                throw new UnableToReleaseConnectionException(e);
            }
        }
    }

    public P execute(ProcessCallback<P, Adapter> processCallback, Adapter object, Filter filter, MuleMessage message) throws Exception {
        throw new UnsupportedOperationException();
    }
}
