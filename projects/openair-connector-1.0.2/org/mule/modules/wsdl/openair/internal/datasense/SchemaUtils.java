/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Definition
 *  javax.wsdl.Import
 *  javax.wsdl.Types
 *  javax.wsdl.extensions.schema.Schema
 *  javax.wsdl.extensions.schema.SchemaImport
 *  org.apache.commons.lang.StringUtils
 *  org.jetbrains.annotations.NotNull
 */
package org.mule.modules.wsdl.openair.internal.datasense;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaImport;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SchemaUtils {
    private SchemaUtils() {
    }

    @NotNull
    public static List<String> getSchemas(@NotNull Definition wsdlDefinition) throws TransformerException {
        Map wsdlNamespaces = wsdlDefinition.getNamespaces();
        ArrayList<String> schemas = new ArrayList<String>();
        ArrayList<Types> typesList = new ArrayList<Types>();
        SchemaUtils.extractWsdlTypes(wsdlDefinition, typesList);
        for (Types types : typesList) {
            for (Object o : types.getExtensibilityElements()) {
                if (!(o instanceof Schema)) continue;
                schemas.addAll(SchemaUtils.resolveSchema(wsdlNamespaces, (Schema)o));
            }
        }
        for (Object wsdlImportList : wsdlDefinition.getImports().values()) {
            List importList = (List)wsdlImportList;
            for (Import wsdlImport : importList) {
                schemas.addAll(SchemaUtils.getSchemas(wsdlImport.getDefinition()));
            }
        }
        return schemas;
    }

    @NotNull
    public static List<String> resolveSchema(Map<String, String> wsdlNamespaces, Schema schema) throws TransformerException {
        ArrayList<String> schemas = new ArrayList<String>();
        SchemaUtils.fixPrefix(wsdlNamespaces, schema);
        SchemaUtils.fixSchemaLocations(schema);
        String flatSchema = SchemaUtils.schemaToString(schema);
        schemas.add(flatSchema);
        return schemas;
    }

    private static void extractWsdlTypes(@NotNull Definition wsdlDefinition, @NotNull List<Types> typesList) {
        if (wsdlDefinition.getTypes() != null) {
            typesList.add(wsdlDefinition.getTypes());
        }
    }

    private static void fixPrefix(Map<String, String> wsdlNamespaces, Schema schema) {
        for (Map.Entry<String, String> entry : wsdlNamespaces.entrySet()) {
            boolean isDefault = StringUtils.isEmpty((String)entry.getKey());
            boolean containNamespace = schema.getElement().hasAttribute("xmlns:" + entry.getKey());
            if (isDefault || containNamespace) continue;
            schema.getElement().setAttribute("xmlns:" + entry.getKey(), entry.getValue());
        }
    }

    private static void fixSchemaLocations(Schema schema) {
        String basePath = SchemaUtils.getBasePath(schema.getDocumentBaseURI());
        Map oldImports = schema.getImports();
        Collection values = oldImports.values();
        if (!values.isEmpty()) {
            SchemaUtils.setSchemaLocationUris(basePath, values);
            SchemaUtils.fixImportSchemaLocationInDom(schema, basePath);
        }
    }

    private static void setSchemaLocationUris(String basePath, Collection<? extends List<SchemaImport>> values) {
        for (List<SchemaImport> list : values) {
            for (SchemaImport schemaImport : list) {
                String schemaLocationURI = schemaImport.getSchemaLocationURI();
                if (schemaLocationURI == null || schemaLocationURI.startsWith(basePath) || schemaLocationURI.startsWith("http")) continue;
                schemaImport.setSchemaLocationURI(basePath + schemaLocationURI);
            }
        }
    }

    private static void fixImportSchemaLocationInDom(@NotNull Schema schema, @NotNull String basePath) {
        NodeList children = schema.getElement().getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            String schemaLocation;
            NamedNodeMap attributes;
            Node namedItem;
            Node item = children.item(i);
            if (!"import".equals(item.getLocalName()) || (namedItem = (attributes = item.getAttributes()).getNamedItem("schemaLocation")) == null || (schemaLocation = namedItem.getNodeValue()).startsWith(basePath) || schemaLocation.startsWith("http")) continue;
            namedItem.setNodeValue(basePath + schemaLocation);
        }
    }

    private static String getBasePath(String documentURI) {
        File document = new File(documentURI);
        if (document.isDirectory()) {
            return documentURI;
        }
        String fileName = document.getName();
        int fileNameIndex = documentURI.lastIndexOf(fileName);
        if (fileNameIndex == -1) {
            return documentURI;
        }
        return documentURI.substring(0, fileNameIndex);
    }

    private static String schemaToString(Schema schema) throws TransformerException {
        Element element = schema.getElement();
        return SchemaUtils.elementToString(element);
    }

    private static String elementToString(Element element) throws TransformerException {
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(element), new StreamResult(writer));
        return writer.toString();
    }
}
