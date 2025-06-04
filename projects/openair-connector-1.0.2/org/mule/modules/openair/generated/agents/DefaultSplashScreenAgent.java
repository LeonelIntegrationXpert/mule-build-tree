/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.mule.api.MetadataAware
 *  org.mule.api.MuleContext
 *  org.mule.api.agent.Agent
 *  org.mule.api.context.MuleContextAware
 *  org.mule.api.registry.MuleRegistry
 *  org.mule.util.StringMessageUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.mule.modules.openair.generated.agents;

import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import org.mule.api.MetadataAware;
import org.mule.api.MuleContext;
import org.mule.api.agent.Agent;
import org.mule.api.context.MuleContextAware;
import org.mule.api.registry.MuleRegistry;
import org.mule.modules.openair.generated.devkit.SplashScreenAgent;
import org.mule.util.StringMessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSplashScreenAgent
implements Agent,
MuleContextAware,
SplashScreenAgent {
    private int extensionsCount;
    private MuleContext muleContext;
    private static Logger logger = LoggerFactory.getLogger(DefaultSplashScreenAgent.class);

    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return "DevKitSplashScreenAgent";
    }

    public String getDescription() {
        return "DevKit Extension Information";
    }

    @Override
    public int getExtensionsCount() {
        return this.extensionsCount;
    }

    public MuleContext getMuleContext() {
        return this.muleContext;
    }

    public void setMuleContext(MuleContext value) {
        this.muleContext = value;
    }

    public void initialise() {
    }

    @Override
    public void splash() {
        MuleRegistry registry = this.muleContext.getRegistry();
        Collection metadataAwares = registry.lookupObjects(MetadataAware.class);
        HashMap metadataAwaresByClass = new HashMap();
        for (MetadataAware connectorMetadata : metadataAwares) {
            metadataAwaresByClass.put(metadataAwares.getClass(), connectorMetadata);
        }
        this.extensionsCount = metadataAwaresByClass.size();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DevKit Extensions (" + Integer.toString(this.extensionsCount) + ") used in this application \n");
        if (this.extensionsCount > 0) {
            for (MetadataAware connectorMetadata : metadataAwaresByClass.values()) {
                stringBuilder.append(StringUtils.capitalise((String)connectorMetadata.getModuleName()));
                stringBuilder.append(" ");
                stringBuilder.append(connectorMetadata.getModuleVersion());
                stringBuilder.append(" (DevKit ");
                stringBuilder.append(connectorMetadata.getDevkitVersion());
                stringBuilder.append(" Build ");
                stringBuilder.append(connectorMetadata.getDevkitBuild());
                stringBuilder.append(")+\n");
            }
        }
        logger.info(StringMessageUtils.getBoilerPlate((String)stringBuilder.toString(), (char)'+', (int)80));
    }

    public void start() {
        this.splash();
    }

    public void stop() {
    }

    public void dispose() {
    }
}
