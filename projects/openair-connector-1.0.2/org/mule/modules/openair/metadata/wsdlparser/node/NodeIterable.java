/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.modules.openair.metadata.wsdlparser.node.NodeIterable$NodeIterator
 */
package org.mule.modules.openair.metadata.wsdlparser.node;

import java.util.Iterator;
import org.mule.modules.openair.metadata.wsdlparser.node.NodeIterable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeIterable
implements Iterable<Node> {
    private final NodeList nodeList;

    public NodeIterable(NodeList nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    public Iterator<Node> iterator() {
        return new NodeIterator(this, null);
    }

    static /* synthetic */ NodeList access$000(NodeIterable x0) {
        return x0.nodeList;
    }
}
