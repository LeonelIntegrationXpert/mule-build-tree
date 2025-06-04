/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  javax.wsdl.Definition
 *  javax.wsdl.WSDLException
 *  javax.wsdl.extensions.schema.Schema
 *  javax.wsdl.factory.WSDLFactory
 *  javax.wsdl.xml.WSDLReader
 *  org.dom4j.dom.DOMElement
 *  org.mule.common.metadata.datatype.DataType
 */
package org.mule.modules.openair.metadata.wsdlparser;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.dom4j.dom.DOMElement;
import org.mule.common.metadata.datatype.DataType;
import org.mule.modules.openair.config.Config;
import org.mule.modules.openair.metadata.wsdlparser.node.NodeIterable;
import org.mule.modules.openair.metadata.wsdlparser.node.predicate.NodeHasAttributesPredicate;
import org.mule.modules.openair.metadata.wsdlparser.node.predicate.NodeNamePredicate;
import org.mule.modules.openair.utils.XmlParserUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class WsdlConverter {
    private static final TransformerFactory TRANSFORMER_FACTORY = XmlParserUtils.getTransformerFactory();
    private static final Map<String, DataType> types = ImmutableMap.builder().put((Object)"string", (Object)DataType.STRING).put((Object)"boolean", (Object)DataType.BOOLEAN).put((Object)"date", (Object)DataType.DATE).put((Object)"decimal", (Object)DataType.DECIMAL).put((Object)"byte", (Object)DataType.BYTE).put((Object)"unsignedByte", (Object)DataType.BYTE).put((Object)"dateTime", (Object)DataType.DATE_TIME).put((Object)"int", (Object)DataType.INTEGER).put((Object)"integer", (Object)DataType.INTEGER).put((Object)"unsignedInt", (Object)DataType.INTEGER).put((Object)"short", (Object)DataType.INTEGER).put((Object)"unsignedShort", (Object)DataType.INTEGER).put((Object)"long", (Object)DataType.LONG).put((Object)"unsignedLong", (Object)DataType.LONG).put((Object)"double", (Object)DataType.DOUBLE).build();
    private static final Predicate<Node> HAS_ATTRIBUTES_PREDICATE = new NodeHasAttributesPredicate();

    public Definition convert(String location) throws WSDLException, TransformerException, MalformedURLException {
        WSDLFactory wsdlFactory = WSDLFactory.newInstance();
        WSDLReader wsdlReader = wsdlFactory.newWSDLReader();
        Definition definition = wsdlReader.readWSDL(location);
        Element schemaNode = ((Schema)Schema.class.cast(definition.getTypes().getExtensibilityElements().get(0))).getElement();
        Node startNode = schemaNode.getParentNode().getParentNode();
        Document document = startNode.getOwnerDocument();
        Node bindingNode = this.getChild(startNode, "wsdl:binding");
        this.updateAttribute(this.getChild(bindingNode, "wsdlsoap:binding"), "style", "document");
        for (Node operationNode : this.toIterable(bindingNode, "wsdl:operation")) {
            for (Node ioNode : this.toIterable(operationNode, "wsdl:output", "wsdl:input")) {
                for (Node componentNode : this.toFilteredIterable(ioNode, "wsdlsoap:body", "wsdlsoap:header")) {
                    this.updateAttribute(componentNode, "use", "literal");
                    this.removeAttribute(componentNode, "encodingStyle");
                    this.removeAttribute(componentNode, "namespace");
                }
            }
        }
        for (Node messageNode : this.toIterable(startNode, "wsdl:message")) {
            for (Node partNode : this.toFilteredIterable(messageNode, "wsdl:part")) {
                if (partNode.getAttributes().getNamedItem("type") == null) continue;
                Attr attr = document.createAttribute("element");
                attr.setNodeValue("tns1:" + partNode.getAttributes().getNamedItem("type").getNodeValue().split(":")[1]);
                partNode.getAttributes().setNamedItem(attr);
                this.removeAttribute(partNode, "type");
            }
        }
        Node firstComplexTypeNode = this.getChild(schemaNode, "complexType");
        ArrayList<Element> globalElementNodes = new ArrayList<Element>();
        for (Node complexTypeNode : this.toIterable((Node)schemaNode, "complexType")) {
            if (complexTypeNode.getNodeType() == 3) continue;
            Node node = this.getChild(complexTypeNode, "complexContent");
            for (Node attributeNode : this.toFilteredIterable(this.getChild(node, "restriction"), "attribute")) {
                if (attributeNode.getNodeType() == 3) continue;
                Element tempNode = null;
                if (attributeNode.getAttributes().getNamedItem("ref").getNodeValue().equals("soapenc:arrayType")) {
                    Element elementElement = document.createElementNS(attributeNode.getNamespaceURI(), "element");
                    String elementName = attributeNode.getAttributes().getNamedItem("wsdl:arrayType").getNodeValue().replace("[]", "").split(":")[1];
                    String elementType = (types.containsKey(elementName) ? "xsd:" : "tns1:") + elementName;
                    elementElement.setAttribute("name", Character.toLowerCase(elementName.charAt(0)) + elementName.substring(1));
                    elementElement.setAttribute("type", elementType);
                    elementElement.setAttribute("minOccurs", "0");
                    elementElement.setAttribute("maxOccurs", "unbounded");
                    tempNode = document.createElementNS(attributeNode.getNamespaceURI(), "sequence");
                    tempNode.appendChild(elementElement);
                }
                complexTypeNode.replaceChild(tempNode, node);
            }
            Element elementNode = document.createElementNS(complexTypeNode.getNamespaceURI(), "element");
            elementNode.setAttribute("name", complexTypeNode.getAttributes().getNamedItem("name").getNodeValue());
            elementNode.setAttribute("type", "tns1:" + complexTypeNode.getAttributes().getNamedItem("name").getNodeValue());
            globalElementNodes.add(elementNode);
        }
        Element elementNode = document.createElementNS(schemaNode.getNamespaceURI(), "element");
        elementNode.setAttribute("name", "string");
        elementNode.setAttribute("type", "xsd:string");
        globalElementNodes.add(elementNode);
        for (Node node : globalElementNodes) {
            schemaNode.insertBefore(node, firstComplexTypeNode);
        }
        schemaNode.removeChild(this.getChild(schemaNode, "import"));
        if (startNode.hasAttributes()) {
            for (int i = 0; i < startNode.getAttributes().getLength(); ++i) {
                if (!startNode.getAttributes().item(i).getNodeValue().equals("http://schemas.xmlsoap.org/soap/encoding/")) continue;
                startNode.getAttributes().removeNamedItem(startNode.getAttributes().item(i).getNodeName());
            }
        }
        StringWriter writer = new StringWriter();
        Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        DOMSource domSource = new DOMSource(startNode);
        transformer.transform(domSource, new StreamResult(writer));
        new File(URI.create(Config.TEMPORARY_WSDL_DIRECTORY_PATH)).mkdirs();
        transformer.transform(domSource, new StreamResult(new File(URI.create(Config.TEMPORARY_WSDL_PATH))));
        return wsdlFactory.newWSDLReader().readWSDL(startNode.getBaseURI(), new InputSource(new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8))));
    }

    private Node getChild(Node parentNode, String nodeName) {
        return (Node)Iterables.find(this.toIterable(parentNode), (Predicate)new NodeNamePredicate(nodeName), (Object)new DOMElement(nodeName));
    }

    private Iterable<Node> toIterable(Node node) {
        return node.hasChildNodes() ? new NodeIterable(node.getChildNodes()) : new ArrayList();
    }

    private Iterable<Node> toIterable(Node node, String ... nameFilters) {
        ArrayList<NodeNamePredicate> predicates = new ArrayList<NodeNamePredicate>();
        for (String nameFilter : nameFilters) {
            predicates.add(new NodeNamePredicate(nameFilter));
        }
        return this.toIterable(node, (Predicate<Node>)Predicates.or(predicates));
    }

    private Iterable<Node> toFilteredIterable(Node node, String ... nameFilters) {
        ArrayList<NodeNamePredicate> predicates = new ArrayList<NodeNamePredicate>();
        for (String nameFilter : nameFilters) {
            predicates.add(new NodeNamePredicate(nameFilter));
        }
        return this.toIterable(node, (Predicate<Node>)Predicates.and(HAS_ATTRIBUTES_PREDICATE, (Predicate)Predicates.or(predicates)));
    }

    private Iterable<Node> toIterable(Node node, Predicate<Node> predicate) {
        return Iterables.filter(this.toIterable(node), predicate);
    }

    private void updateAttribute(Node node, String attributeName, String newValue) {
        if (node.hasAttributes()) {
            node.getAttributes().getNamedItem(attributeName).setNodeValue(newValue);
        }
    }

    private void removeAttribute(Node node, String attributeName) {
        node.getAttributes().removeNamedItem(attributeName);
    }
}
