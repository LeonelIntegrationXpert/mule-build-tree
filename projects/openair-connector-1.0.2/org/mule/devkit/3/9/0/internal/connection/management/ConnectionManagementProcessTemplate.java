/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleContext
 *  org.mule.api.MuleEvent
 *  org.mule.api.MuleMessage
 *  org.mule.api.devkit.ProcessInterceptor
 *  org.mule.api.devkit.ProcessTemplate
 *  org.mule.api.processor.MessageProcessor
 *  org.mule.api.routing.filter.Filter
 *  org.mule.security.oauth.callback.ProcessCallback
 *  org.mule.security.oauth.process.ProcessCallbackProcessInterceptor
 *  org.mule.security.oauth.process.RetryProcessInterceptor
 */
package org.mule.devkit.3.9.0.internal.connection.management;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.devkit.ProcessInterceptor;
import org.mule.api.devkit.ProcessTemplate;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.filter.Filter;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionKey;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionManager;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectorAdapter;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementProcessInterceptor;
import org.mule.security.oauth.callback.ProcessCallback;
import org.mule.security.oauth.process.ProcessCallbackProcessInterceptor;
import org.mule.security.oauth.process.RetryProcessInterceptor;

public class ConnectionManagementProcessTemplate<P, Adapter extends ConnectionManagementConnectorAdapter, Key extends ConnectionManagementConnectionKey, Strategy>
implements ProcessTemplate<P, Adapter> {
    private final ProcessInterceptor<P, Adapter> processInterceptor;

    public ConnectionManagementProcessTemplate(ConnectionManagementConnectionManager<Key, Adapter, Strategy> connManagementBasicConnectionManager, MuleContext muleContext) {
        RetryProcessInterceptor retryProcessInterceptor;
        ProcessCallbackProcessInterceptor processCallbackProcessInterceptor = new ProcessCallbackProcessInterceptor();
        ConnectionManagementProcessInterceptor managedConnectionProcessInterceptor = new ConnectionManagementProcessInterceptor(processCallbackProcessInterceptor, connManagementBasicConnectionManager, muleContext);
        this.processInterceptor = retryProcessInterceptor = new RetryProcessInterceptor(managedConnectionProcessInterceptor, muleContext, connManagementBasicConnectionManager.getRetryPolicyTemplate());
    }

    public P execute(ProcessCallback<P, Adapter> processCallback, MessageProcessor messageProcessor, MuleEvent event) throws Exception {
        return (P)this.processInterceptor.execute(processCallback, null, messageProcessor, event);
    }

    public P execute(ProcessCallback<P, Adapter> processCallback, Filter filter, MuleMessage message) throws Exception {
        return (P)this.processInterceptor.execute(processCallback, null, filter, message);
    }
}
