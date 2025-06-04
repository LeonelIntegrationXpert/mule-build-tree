/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.wsdl.Binding
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.Definition
 *  javax.wsdl.Message
 *  javax.wsdl.Operation
 *  javax.wsdl.Output
 *  javax.wsdl.Part
 *  javax.wsdl.WSDLException
 *  javax.wsdl.factory.WSDLFactory
 *  javax.wsdl.xml.WSDLLocator
 *  javax.wsdl.xml.WSDLReader
 *  org.apache.commons.httpclient.util.URIUtil
 *  org.apache.commons.io.FilenameUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.text.WordUtils
 *  org.apache.cxf.catalog.CatalogWSDLLocator
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.mule.common.metadata.DefaultMetaData
 *  org.mule.common.metadata.DefaultSimpleMetaDataModel
 *  org.mule.common.metadata.DefaultUnknownMetaDataModel
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataModel
 *  org.mule.common.metadata.XmlMetaDataModel
 *  org.mule.common.metadata.builder.DefaultMetaDataBuilder
 *  org.mule.common.metadata.builder.XmlMetaDataBuilder
 *  org.mule.common.metadata.datatype.DataType
 */
package org.mule.modules.wsdl.openair.internal.datasense;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLLocator;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.cxf.catalog.CatalogWSDLLocator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultSimpleMetaDataModel;
import org.mule.common.metadata.DefaultUnknownMetaDataModel;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.XmlMetaDataModel;
import org.mule.common.metadata.builder.DefaultMetaDataBuilder;
import org.mule.common.metadata.builder.XmlMetaDataBuilder;
import org.mule.common.metadata.datatype.DataType;
import org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKey;
import org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKeyBuilder;
import org.mule.modules.wsdl.openair.internal.datasense.SchemaUtils;
import org.mule.modules.wsdl.openair.internal.datasense.WsdlDatasenseException;
import org.mule.modules.wsdl.openair.internal.runtime.CallDefinition;

public class WsdlDatasenseParser {
    private static final Predicate<String> NON_EMPTY_STRING = new /* Unavailable Anonymous Inner Class!! */;
    private static final Map<String, DataType> types;
    private final List<ComposedMetaDataKey> metaDataKeys = new ArrayList<ComposedMetaDataKey>();
    private final Map<ComposedMetaDataKey, URL> metaDataKeysUrls = new HashMap<ComposedMetaDataKey, URL>();

    public void addMetadata(@NotNull Iterable<URL> wsdlURLs, @NotNull String category, @Nullable String suffix) throws WsdlDatasenseException {
        for (URL wsdlURL : wsdlURLs) {
            try {
                URI wsdlURI = new URI(wsdlURL.toString());
                String name = URIUtil.getName((String)wsdlURI.toString());
                if (StringUtils.isBlank((CharSequence)name)) {
                    String urlPath = URIUtil.getPath((String)wsdlURI.toString());
                    String[] path = urlPath.split("/");
                    name = path[path.length - 1];
                }
                String wsdlName = FilenameUtils.getBaseName((String)name);
                WSDLFactory factory = WSDLFactory.newInstance();
                WSDLReader wsdlReader = factory.newWSDLReader();
                Definition wsdlDefinition = wsdlReader.readWSDL((WSDLLocator)new CatalogWSDLLocator(wsdlURI.toString()));
                Map<QName, Binding> bindings = this.getBindingsFromWsdlDefinition(wsdlDefinition);
                for (QName qName : bindings.keySet()) {
                    Binding binding = bindings.get(qName);
                    for (Object bindingOperation : binding.getBindingOperations()) {
                        Operation operation = ((BindingOperation)bindingOperation).getOperation();
                        String operationName = operation.getName();
                        String nonNullSuffix = StringUtils.isNotBlank((CharSequence)suffix) ? " " + suffix : "";
                        CallDefinition callDefinition = new CallDefinition(wsdlName, operationName);
                        String firstLevelLabel = this.makeReadable(wsdlName) + nonNullSuffix;
                        String secondLevelLabel = this.makeReadable(operationName);
                        List<ComposedMetaDataKey> composedMetaDataKeys = ComposedMetaDataKeyBuilder.getInstance().newKeyCombination().newLevel().addId(callDefinition.getEndpointPath(), firstLevelLabel).endLevel().newLevel().addId(callDefinition.getOperationName(), secondLevelLabel).endLevel().endKeyCombination().build();
                        ComposedMetaDataKey composedMetaDataKey = composedMetaDataKeys.get(0);
                        this.metaDataKeys.add(composedMetaDataKey);
                        this.metaDataKeysUrls.put(composedMetaDataKey, wsdlURL);
                    }
                }
            }
            catch (URISyntaxException e) {
                throw new WsdlDatasenseException(e);
            }
            catch (WSDLException e) {
                throw new WsdlDatasenseException(e);
            }
        }
    }

    private String makeReadable(String original) {
        String temporary = original.replace('_', ' ');
        Object[] strings = StringUtils.splitByCharacterTypeCamelCase((String)temporary);
        return WordUtils.capitalizeFully((String)StringUtils.join((Iterable)Iterables.filter((Iterable)Lists.newArrayList((Object[])strings), NON_EMPTY_STRING), (String)" "));
    }

    @NotNull
    public List<ComposedMetaDataKey> getMetaDataKeys() {
        return this.metaDataKeys;
    }

    @NotNull
    public Map<String, MetaData> getInputMetaData(ComposedMetaDataKey key) throws WsdlDatasenseException {
        URL wsdlUrl = this.metaDataKeysUrls.get(key);
        String[] wsdlAndOperation = StringUtils.split((String)key.getId(), (String)"||");
        String operationName = wsdlAndOperation[1];
        try {
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader wsdlReader = factory.newWSDLReader();
            Definition wsdlDefinition = wsdlReader.readWSDL(wsdlUrl.toString());
            List<String> schemas = SchemaUtils.getSchemas(wsdlDefinition);
            Operation operation = this.getOperationFromWsdl(wsdlDefinition, operationName);
            Message message = this.getInputMessage(operation);
            Map parts = message.getParts();
            return Maps.transformValues((Map)((Map)MoreObjects.firstNonNull((Object)parts, (Object)ImmutableMap.of())), (Function)new /* Unavailable Anonymous Inner Class!! */);
        }
        catch (TransformerException e) {
            throw new WsdlDatasenseException("Problem reading schemas from wsdl definition", e);
        }
        catch (WSDLException e) {
            throw new WsdlDatasenseException("Problem reading schemas from wsdl definition", e);
        }
    }

    @NotNull
    public Map<String, MetaData> getOutputMetaData(ComposedMetaDataKey key) throws WsdlDatasenseException {
        URL wsdlUrl = this.metaDataKeysUrls.get(key);
        String[] wsdlAndOperation = StringUtils.split((String)key.getId(), (String)"||");
        String operationName = wsdlAndOperation[1];
        try {
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader wsdlReader = factory.newWSDLReader();
            Definition wsdlDefinition = wsdlReader.readWSDL(wsdlUrl.toString());
            List<String> schemas = SchemaUtils.getSchemas(wsdlDefinition);
            Operation operation = this.getOperationFromWsdl(wsdlDefinition, operationName);
            Message message = this.getOutputMessage(operation);
            if (message != null && message.getParts() != null) {
                Map parts = message.getParts();
                return Maps.transformValues((Map)((Map)MoreObjects.firstNonNull((Object)parts, (Object)ImmutableMap.of())), (Function)new /* Unavailable Anonymous Inner Class!! */);
            }
            return ImmutableMap.of();
        }
        catch (TransformerException e) {
            throw new WsdlDatasenseException("Problem reading schemas from wsdl definition", e);
        }
        catch (WSDLException e) {
            throw new WsdlDatasenseException("Problem reading schemas from wsdl definition", e);
        }
    }

    private Message getInputMessage(Operation operation) {
        return operation.getInput().getMessage();
    }

    @Nullable
    private Message getOutputMessage(Operation operation) {
        Output output = operation.getOutput();
        return output != null ? output.getMessage() : null;
    }

    private Operation getOperationFromWsdl(Definition wsdlDefinition, String operationName) {
        Map<QName, Binding> bindings = this.getBindingsFromWsdlDefinition(wsdlDefinition);
        Set<QName> qNames = bindings.keySet();
        Binding binding = bindings.get(qNames.iterator().next());
        BindingOperation bindingOperation = binding.getBindingOperation(operationName, null, null);
        return bindingOperation.getOperation();
    }

    private Map<QName, Binding> getBindingsFromWsdlDefinition(Definition wsdlDefinition) {
        return wsdlDefinition.getBindings();
    }

    @NotNull
    private MetaData createMetaData(@NotNull List<String> schemas, @Nullable Part part) {
        if (part != null) {
            if (part.getElementName() != null) {
                QName elementName = part.getElementName();
                XmlMetaDataBuilder createXmlObject = new DefaultMetaDataBuilder().createXmlObject(elementName);
                for (String schema : schemas) {
                    createXmlObject.addSchemaStringList(new String[]{schema});
                }
                createXmlObject.setEncoding(Charset.defaultCharset());
                createXmlObject.setExample("");
                XmlMetaDataModel model = (XmlMetaDataModel)createXmlObject.build();
                if (!model.getFields().isEmpty()) {
                    return new DefaultMetaData((MetaDataModel)model);
                }
            } else if (part.getTypeName() != null) {
                DataType dataType = this.getDataTypeFromTypeName(part);
                DefaultSimpleMetaDataModel defaultSimpleMetaDataModel = new DefaultSimpleMetaDataModel(dataType);
                return new DefaultMetaData((MetaDataModel)defaultSimpleMetaDataModel);
            }
        }
        return new DefaultMetaData((MetaDataModel)new DefaultUnknownMetaDataModel());
    }

    private DataType getDataTypeFromTypeName(Part part) {
        String localPart = part.getTypeName().getLocalPart();
        DataType dataType = types.get(localPart);
        return dataType != null ? dataType : DataType.STRING;
    }

    static /* synthetic */ MetaData access$000(WsdlDatasenseParser x0, List x1, Part x2) {
        return x0.createMetaData(x1, x2);
    }

    static {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put((Object)"string", (Object)DataType.STRING);
        builder.put((Object)"boolean", (Object)DataType.BOOLEAN);
        builder.put((Object)"date", (Object)DataType.DATE);
        builder.put((Object)"decimal", (Object)DataType.DECIMAL);
        builder.put((Object)"byte", (Object)DataType.BYTE);
        builder.put((Object)"unsignedByte", (Object)DataType.BYTE);
        builder.put((Object)"dateTime", (Object)DataType.DATE_TIME);
        builder.put((Object)"int", (Object)DataType.INTEGER);
        builder.put((Object)"integer", (Object)DataType.INTEGER);
        builder.put((Object)"unsignedInt", (Object)DataType.INTEGER);
        builder.put((Object)"short", (Object)DataType.INTEGER);
        builder.put((Object)"unsignedShort", (Object)DataType.INTEGER);
        builder.put((Object)"long", (Object)DataType.LONG);
        builder.put((Object)"unsignedLong", (Object)DataType.LONG);
        builder.put((Object)"double", (Object)DataType.DOUBLE);
        types = builder.build();
    }
}
