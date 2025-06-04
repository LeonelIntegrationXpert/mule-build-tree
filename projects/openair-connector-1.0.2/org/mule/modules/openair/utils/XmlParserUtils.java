/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.mule.modules.openair.utils;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.TransformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlParserUtils {
    private static final Logger logger = LoggerFactory.getLogger(XmlParserUtils.class);

    private XmlParserUtils() {
    }

    public static TransformerFactory getTransformerFactory() {
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            factory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
            factory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
        }
        catch (IllegalArgumentException e) {
            logger.trace("Unable to disable XXE on TransformerFactory", (Throwable)e);
        }
        return factory;
    }

    public static XMLInputFactory getXmlInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            factory.setProperty("javax.xml.stream.supportDTD", false);
            factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        }
        catch (FactoryConfigurationError e) {
            logger.trace("Unable to disable XXE on XMLInputFactory", (Throwable)e);
        }
        return factory;
    }
}
