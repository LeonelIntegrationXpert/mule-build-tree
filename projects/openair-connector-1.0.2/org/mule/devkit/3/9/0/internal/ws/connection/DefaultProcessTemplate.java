/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleEvent
 *  org.mule.api.MuleMessage
 *  org.mule.api.devkit.ProcessTemplate
 *  org.mule.api.processor.MessageProcessor
 *  org.mule.api.routing.filter.Filter
 *  org.mule.security.oauth.callback.ProcessCallback
 */
package org.mule.devkit.3.9.0.internal.ws.connection;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.devkit.ProcessTemplate;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.filter.Filter;
import org.mule.security.oauth.callback.ProcessCallback;

public class DefaultProcessTemplate<P, Adapter>
implements ProcessTemplate<P, Adapter> {
    private final Adapter processObject;

    public DefaultProcessTemplate(Adapter adapter) {
        this.processObject = adapter;
    }

    public P execute(ProcessCallback<P, Adapter> processCallback, MessageProcessor messageProcessor, MuleEvent event) throws Exception {
        return (P)processCallback.process(this.processObject);
    }

    public P execute(ProcessCallback<P, Adapter> processCallback, Filter filter, MuleMessage message) throws Exception {
        return (P)processCallback.process(this.processObject);
    }
}
