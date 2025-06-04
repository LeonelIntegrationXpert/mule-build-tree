/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleContext
 *  org.mule.api.MuleException
 *  org.mule.api.context.MuleContextAware
 *  org.mule.api.lifecycle.Disposable
 *  org.mule.api.lifecycle.Initialisable
 *  org.mule.api.lifecycle.InitialisationException
 *  org.mule.api.lifecycle.Startable
 *  org.mule.api.lifecycle.Stoppable
 *  org.mule.common.MuleVersion
 *  org.mule.config.MuleManifest
 *  org.mule.config.i18n.CoreMessages
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.mule.modules.openair.generated.adapters;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.common.MuleVersion;
import org.mule.config.MuleManifest;
import org.mule.config.i18n.CoreMessages;
import org.mule.modules.openair.generated.adapters.OpenAirConnectorMetadataAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAirConnectorLifecycleInjectionAdapter
extends OpenAirConnectorMetadataAdapter
implements MuleContextAware,
Disposable,
Initialisable,
Startable,
Stoppable {
    public void start() throws MuleException {
    }

    public void stop() throws MuleException {
    }

    public void initialise() throws InitialisationException {
        Logger log = LoggerFactory.getLogger(OpenAirConnectorLifecycleInjectionAdapter.class);
        MuleVersion connectorVersion = new MuleVersion("3.7");
        MuleVersion muleVersion = new MuleVersion(MuleManifest.getProductVersion());
        if (!muleVersion.atLeastBase(connectorVersion)) {
            throw new InitialisationException(CoreMessages.minMuleVersionNotMet((String)this.getMinMuleVersion()), (Initialisable)this);
        }
    }

    public void dispose() {
    }

    public void setMuleContext(MuleContext context) {
    }
}
