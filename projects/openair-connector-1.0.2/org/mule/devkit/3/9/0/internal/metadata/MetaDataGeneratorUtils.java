/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.mule.common.metadata.DefaultMetaData
 *  org.mule.common.metadata.DefaultMetaDataKey
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataField
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.common.metadata.MetaDataModel
 *  org.mule.common.metadata.MetaDataProperties
 *  org.mule.common.metadata.MetaDataPropertyScope
 *  org.mule.common.metadata.field.property.MetaDataFieldProperty
 */
package org.mule.devkit.3.9.0.internal.metadata;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataField;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.MetaDataProperties;
import org.mule.common.metadata.MetaDataPropertyScope;
import org.mule.common.metadata.field.property.MetaDataFieldProperty;
import org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKey;
import org.mule.devkit.3.9.0.internal.metadata.InternalComposedMetaDataKeyBuilder;

public class MetaDataGeneratorUtils {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getMD5(Object ... data) {
        String string;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream out = null;
        try {
            bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(data);
            string = new String(MessageDigest.getInstance("MD5").digest(bos.toByteArray()));
        }
        catch (Exception e) {
            String string2;
            try {
                e.printStackTrace();
                string2 = "";
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly((OutputStream)bos);
                throw throwable;
            }
            IOUtils.closeQuietly((OutputStream)out);
            IOUtils.closeQuietly((OutputStream)bos);
            return string2;
        }
        IOUtils.closeQuietly((OutputStream)out);
        IOUtils.closeQuietly((OutputStream)bos);
        return string;
    }

    public static MetaData extractPropertiesToMetaData(MetaDataModel wrappedMetaDataModel, MetaData userDefinedMetaData) {
        DefaultMetaData generatedMetaData = new DefaultMetaData(wrappedMetaDataModel);
        for (MetaDataPropertyScope metaDataPropertyScope : MetaDataPropertyScope.values()) {
            MetaDataProperties properties = userDefinedMetaData.getProperties(metaDataPropertyScope);
            for (MetaDataField metaDataField : properties.getFields()) {
                List userDefinedProperties = metaDataField.getProperties();
                MetaDataFieldProperty[] arrayOfUserDefinedProperties = userDefinedProperties.toArray(new MetaDataFieldProperty[userDefinedProperties.size()]);
                generatedMetaData.addProperty(metaDataPropertyScope, metaDataField.getName(), metaDataField.getMetaDataModel(), arrayOfUserDefinedProperties);
            }
        }
        return generatedMetaData;
    }

    public static List<MetaDataKey> toSimpleKeyWithCategory(List<ComposedMetaDataKey> metadataKeys, String keySeparator, String category) {
        List<MetaDataKey> retrievedKeys = MetaDataGeneratorUtils.composedToDefaultMetaDataKeys(metadataKeys, keySeparator);
        return MetaDataGeneratorUtils.fillCategory(retrievedKeys, category);
    }

    public static List<MetaDataKey> fillCategory(List<MetaDataKey> metadataKeys, String categoryClassName) {
        for (MetaDataKey metaDataKey : metadataKeys) {
            ((DefaultMetaDataKey)metaDataKey).setCategory(categoryClassName);
        }
        return metadataKeys;
    }

    private static List<MetaDataKey> composedToDefaultMetaDataKeys(List<ComposedMetaDataKey> metadataKeys, String keySeparator) {
        return InternalComposedMetaDataKeyBuilder.toSimpleKey(metadataKeys, keySeparator);
    }

    public static String getMetaDataException(MetaDataKey key) {
        if (key != null && key.getId() != null) {
            return "There was an error retrieving metadata from key: " + key.getId() + " after acquiring the connection, for more detailed information please read the provided stacktrace";
        }
        return "There was an error retrieving metadata after acquiring the connection, MetaDataKey is null or its id is null, for more detailed information please read the provided stacktrace";
    }
}
