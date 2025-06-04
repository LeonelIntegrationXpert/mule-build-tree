/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool2.KeyedPooledObjectFactory
 *  org.apache.commons.pool2.impl.GenericKeyedObjectPool
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.mule.modules.openair.pool;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.mule.modules.openair.utils.XmlParserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XsltTransformerPool
extends GenericKeyedObjectPool<String, Transformer> {
    private static final Logger logger = LoggerFactory.getLogger(XsltTransformerPool.class);
    private static final TransformerFactory TRANSFORMER_FACTORY = XmlParserUtils.getTransformerFactory();

    public XsltTransformerPool() {
        super((KeyedPooledObjectFactory)new /* Unavailable Anonymous Inner Class!! */);
    }

    static /* synthetic */ TransformerFactory access$000() {
        return TRANSFORMER_FACTORY;
    }

    static /* synthetic */ Logger access$100() {
        return logger;
    }
}
