/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 */
package org.mule.modules.openair.metadata.wsdlparser.node.predicate;

import com.google.common.base.Predicate;
import org.w3c.dom.Node;

public class NodeHasAttributesPredicate
implements Predicate<Node> {
    public boolean apply(Node input) {
        return input.hasAttributes();
    }
}
