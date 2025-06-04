/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.config.MuleManifest
 *  org.mule.config.PoolingProfile
 *  org.mule.security.oauth.config.AbstractDevkitBasedDefinitionParser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.parsing.BeanDefinitionParsingException
 *  org.springframework.beans.factory.parsing.Location
 *  org.springframework.beans.factory.parsing.Problem
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.ParserContext
 *  org.springframework.util.xml.DomUtils
 */
package org.mule.modules.openair.generated.config;

import org.mule.config.MuleManifest;
import org.mule.config.PoolingProfile;
import org.mule.modules.openair.generated.connectivity.OpenAirConnectorConfigConnectionManagementConnectionManager;
import org.mule.security.oauth.config.AbstractDevkitBasedDefinitionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class OpenAirConnectorConfigConfigDefinitionParser
extends AbstractDevkitBasedDefinitionParser {
    private static Logger logger = LoggerFactory.getLogger(OpenAirConnectorConfigConfigDefinitionParser.class);

    public String moduleName() {
        return "OpenAir";
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        this.parseConfigName(element);
        BeanDefinitionBuilder builder = this.getBeanDefinitionBuilder(parserContext);
        builder.setScope("singleton");
        this.setInitMethodIfNeeded(builder, OpenAirConnectorConfigConnectionManagementConnectionManager.class);
        this.setDestroyMethodIfNeeded(builder, OpenAirConnectorConfigConnectionManagementConnectionManager.class);
        this.parseProperty(builder, element, "endpoint", "endpoint");
        this.parseProperty(builder, element, "connectionTimeout", "connectionTimeout");
        this.parseProperty(builder, element, "readTimeout", "readTimeout");
        this.parseProperty(builder, element, "company", "company");
        this.parseProperty(builder, element, "username", "username");
        this.parseProperty(builder, element, "password", "password");
        this.parseProperty(builder, element, "apiNamespace", "apiNamespace");
        this.parseProperty(builder, element, "apiKey", "apiKey");
        BeanDefinitionBuilder poolingProfileBuilder = BeanDefinitionBuilder.rootBeanDefinition((String)PoolingProfile.class.getName());
        Element poolingProfileElement = DomUtils.getChildElementByTagName((Element)element, (String)"connection-pooling-profile");
        if (poolingProfileElement != null) {
            this.parseProperty(poolingProfileBuilder, poolingProfileElement, "maxActive");
            this.parseProperty(poolingProfileBuilder, poolingProfileElement, "maxIdle");
            this.parseProperty(poolingProfileBuilder, poolingProfileElement, "maxWait");
            if (this.hasAttribute(poolingProfileElement, "exhaustedAction")) {
                poolingProfileBuilder.addPropertyValue("exhaustedAction", PoolingProfile.POOL_EXHAUSTED_ACTIONS.get(poolingProfileElement.getAttribute("exhaustedAction")));
            }
            if (this.hasAttribute(poolingProfileElement, "initialisationPolicy")) {
                poolingProfileBuilder.addPropertyValue("initialisationPolicy", PoolingProfile.POOL_INITIALISATION_POLICIES.get(poolingProfileElement.getAttribute("initialisationPolicy")));
            }
            if (this.hasAttribute(poolingProfileElement, "evictionCheckIntervalMillis")) {
                this.parseProperty(poolingProfileBuilder, poolingProfileElement, "evictionCheckIntervalMillis");
            }
            if (this.hasAttribute(poolingProfileElement, "minEvictionMillis")) {
                this.parseProperty(poolingProfileBuilder, poolingProfileElement, "minEvictionMillis");
            }
            builder.addPropertyValue("poolingProfile", (Object)poolingProfileBuilder.getBeanDefinition());
        }
        AbstractBeanDefinition definition = builder.getBeanDefinition();
        this.setNoRecurseOnDefinition((BeanDefinition)definition);
        this.parseRetryPolicyTemplate("reconnect", element, parserContext, builder, (BeanDefinition)definition);
        this.parseRetryPolicyTemplate("reconnect-forever", element, parserContext, builder, (BeanDefinition)definition);
        this.parseRetryPolicyTemplate("reconnect-custom-strategy", element, parserContext, builder, (BeanDefinition)definition);
        return definition;
    }

    private BeanDefinitionBuilder getBeanDefinitionBuilder(ParserContext parserContext) {
        try {
            return BeanDefinitionBuilder.rootBeanDefinition((String)OpenAirConnectorConfigConnectionManagementConnectionManager.class.getName());
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            String muleVersion = "";
            try {
                muleVersion = MuleManifest.getProductVersion();
            }
            catch (Exception _x) {
                logger.error("Problem while reading mule version");
            }
            logger.error("Cannot launch the mule app, the configuration [config] within the connector [openair] is not supported in mule " + muleVersion);
            throw new BeanDefinitionParsingException(new Problem("Cannot launch the mule app, the configuration [config] within the connector [openair] is not supported in mule " + muleVersion, new Location(parserContext.getReaderContext().getResource()), null, (Throwable)noClassDefFoundError));
        }
    }
}
