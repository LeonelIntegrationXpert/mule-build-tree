/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.common.MuleVersion
 *  org.mule.config.MuleManifest
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
 */
package org.mule.modules.openair.generated.config;

import org.mule.common.MuleVersion;
import org.mule.config.MuleManifest;
import org.mule.modules.openair.generated.processors.UpsertMessageProcessor;
import org.mule.modules.openair.generated.processors.UpsertMessageProcessorDebuggable;
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
import org.w3c.dom.Element;

public class UpsertDefinitionParser
extends AbstractDevkitBasedDefinitionParser {
    private static Logger logger = LoggerFactory.getLogger(UpsertDefinitionParser.class);

    private BeanDefinitionBuilder getBeanDefinitionBuilder(ParserContext parserContext) {
        try {
            MuleVersion muleVersion = new MuleVersion(MuleManifest.getProductVersion());
            BeanDefinitionBuilder beanDefinitionBuilder = muleVersion.atLeastBase("3.8.0") ? BeanDefinitionBuilder.rootBeanDefinition((String)UpsertMessageProcessorDebuggable.class.getName()) : BeanDefinitionBuilder.rootBeanDefinition((String)UpsertMessageProcessor.class.getName());
            return beanDefinitionBuilder;
        }
        catch (NoClassDefFoundError noClassDefFoundError) {
            String muleVersion = "";
            try {
                muleVersion = MuleManifest.getProductVersion();
            }
            catch (Exception _x) {
                logger.error("Problem while reading mule version");
            }
            logger.error("Cannot launch the mule app, the @Processor [upsert] within the connector [openair] is not supported in mule " + muleVersion);
            throw new BeanDefinitionParsingException(new Problem("Cannot launch the mule app, the @Processor [upsert] within the connector [openair] is not supported in mule " + muleVersion, new Location(parserContext.getReaderContext().getResource()), null, (Throwable)noClassDefFoundError));
        }
    }

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = this.getBeanDefinitionBuilder(parserContext);
        builder.addConstructorArgValue((Object)"upsert");
        builder.setScope("prototype");
        if (!this.hasAttribute(element, "config-ref")) {
            throw new BeanDefinitionParsingException(new Problem("It seems that the config-ref for @Processor [upsert] within the connector [openair] is null or missing. Please, fill the value with the correct global element.", new Location(parserContext.getReaderContext().getResource()), null));
        }
        this.parseConfigRef(element, builder);
        this.parseProperty(builder, element, "oaObject", "oaObject");
        if (this.hasAttribute(element, "request-ref")) {
            if (element.getAttribute("request-ref").startsWith("#")) {
                builder.addPropertyValue("request", (Object)element.getAttribute("request-ref"));
            } else {
                builder.addPropertyValue("request", (Object)("#[registry:" + element.getAttribute("request-ref") + "]"));
            }
        }
        AbstractBeanDefinition definition = builder.getBeanDefinition();
        this.setNoRecurseOnDefinition((BeanDefinition)definition);
        this.attachProcessorDefinition(parserContext, (BeanDefinition)definition);
        return definition;
    }
}
