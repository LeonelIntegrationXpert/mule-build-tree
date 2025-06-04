/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleContext
 *  org.mule.api.MuleEvent
 *  org.mule.api.processor.MessageProcessor
 *  org.mule.api.retry.RetryPolicyTemplate
 */
package org.mule.devkit.3.9.0.internal.connection.management;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.retry.RetryPolicyTemplate;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionAdapter;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionKey;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectorAdapter;

public interface ConnectionManagementConnectionManager<Key extends ConnectionManagementConnectionKey, Adapter extends ConnectionManagementConnectorAdapter, Strategy> {
    public Adapter acquireConnection(Key var1) throws Exception;

    public void releaseConnection(Key var1, Adapter var2) throws Exception;

    public void destroyConnection(Key var1, Adapter var2) throws Exception;

    public Key getDefaultConnectionKey();

    public Key getEvaluatedConnectionKey(MuleEvent var1) throws Exception;

    public RetryPolicyTemplate getRetryPolicyTemplate();

    public Key getConnectionKey(MessageProcessor var1, MuleEvent var2) throws Exception;

    public ConnectionManagementConnectionAdapter newConnection();

    public MuleContext getMuleContext();

    public ConnectionManagementConnectorAdapter newConnector(ConnectionManagementConnectionAdapter<Strategy, Key> var1);

    public ConnectionManagementConnectionAdapter getConnectionAdapter(ConnectionManagementConnectorAdapter var1);
}
