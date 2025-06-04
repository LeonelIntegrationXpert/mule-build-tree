/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.config.MuleManifest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.FatalBeanException
 *  org.springframework.beans.factory.xml.BeanDefinitionParser
 *  org.springframework.beans.factory.xml.NamespaceHandlerSupport
 */
package org.mule.modules.openair.generated.config;

import org.mule.config.MuleManifest;
import org.mule.modules.openair.generated.config.AddDefinitionParser;
import org.mule.modules.openair.generated.config.CreateAccountDefinitionParser;
import org.mule.modules.openair.generated.config.CreateUserDefinitionParser;
import org.mule.modules.openair.generated.config.DeleteDefinitionParser;
import org.mule.modules.openair.generated.config.GetCrystalInfoDefinitionParser;
import org.mule.modules.openair.generated.config.MakeurlDefinitionParser;
import org.mule.modules.openair.generated.config.ModifyDefinitionParser;
import org.mule.modules.openair.generated.config.OpenAirConnectorConfigConfigDefinitionParser;
import org.mule.modules.openair.generated.config.ReadDefinitionParser;
import org.mule.modules.openair.generated.config.RunReportDefinitionParser;
import org.mule.modules.openair.generated.config.ServerTimeDefinitionParser;
import org.mule.modules.openair.generated.config.ServerTimeWithTimezoneDefinitionParser;
import org.mule.modules.openair.generated.config.SubmitDefinitionParser;
import org.mule.modules.openair.generated.config.UpsertDefinitionParser;
import org.mule.modules.openair.generated.config.WhoamiDefinitionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class OpenairNamespaceHandler
extends NamespaceHandlerSupport {
    private static Logger logger = LoggerFactory.getLogger(OpenairNamespaceHandler.class);

    private void handleException(String beanName, String beanScope, NoClassDefFoundError noClassDefFoundError) {
        String muleVersion = "";
        try {
            muleVersion = MuleManifest.getProductVersion();
        }
        catch (Exception _x) {
            logger.error("Problem while reading mule version");
        }
        logger.error("Cannot launch the mule app, the  " + beanScope + " [" + beanName + "] within the connector [openair] is not supported in mule " + muleVersion);
        throw new FatalBeanException("Cannot launch the mule app, the  " + beanScope + " [" + beanName + "] within the connector [openair] is not supported in mule " + muleVersion, (Throwable)noClassDefFoundError);
    }

    public void init() {
        try {
            this.registerBeanDefinitionParser("config", (BeanDefinitionParser)new OpenAirConnectorConfigConfigDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("config", "@Config", ex);
        }
        try {
            this.registerBeanDefinitionParser("read", (BeanDefinitionParser)new ReadDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("read", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("add", (BeanDefinitionParser)new AddDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("add", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("makeurl", (BeanDefinitionParser)new MakeurlDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("makeurl", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("delete", (BeanDefinitionParser)new DeleteDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("delete", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("server-time", (BeanDefinitionParser)new ServerTimeDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("server-time", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("server-time-with-timezone", (BeanDefinitionParser)new ServerTimeWithTimezoneDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("server-time-with-timezone", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("whoami", (BeanDefinitionParser)new WhoamiDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("whoami", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("create-user", (BeanDefinitionParser)new CreateUserDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("create-user", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("create-account", (BeanDefinitionParser)new CreateAccountDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("create-account", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("upsert", (BeanDefinitionParser)new UpsertDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("upsert", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("modify", (BeanDefinitionParser)new ModifyDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("modify", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("submit", (BeanDefinitionParser)new SubmitDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("submit", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("get-crystal-info", (BeanDefinitionParser)new GetCrystalInfoDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("get-crystal-info", "@Processor", ex);
        }
        try {
            this.registerBeanDefinitionParser("run-report", (BeanDefinitionParser)new RunReportDefinitionParser());
        }
        catch (NoClassDefFoundError ex) {
            this.handleException("run-report", "@Processor", ex);
        }
    }
}
