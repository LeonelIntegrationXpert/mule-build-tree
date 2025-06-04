/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.common.metadata.property.StructureIdentifierMetaDataModelProperty
 */
package org.mule.devkit.3.9.0.internal.metadata.fixes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.property.StructureIdentifierMetaDataModelProperty;

public class STUDIO7157 {
    public static StructureIdentifierMetaDataModelProperty getStructureIdentifierMetaDataModelProperty(MetaDataKey key, boolean derivedStructure, boolean generatedStructure) {
        try {
            return STUDIO7157.initializeWith3Parameters(key, derivedStructure, generatedStructure);
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (InstantiationException instantiationException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
        try {
            return STUDIO7157.initializeWith2Parameters(key, derivedStructure);
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (InstantiationException instantiationException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
        throw new RuntimeException("There was an issue while trying to look for the a constructor in mule-common (StructureIdentifierMetaDataModelProperty#StructureIdentifierMetaDataModelProperty(MetaDataKey, boolean, boolean) neither StructureIdentifierMetaDataModelProperty#StructureIdentifierMetaDataModelProperty(MetaDataKey, boolean)). Try upgrading Studio to the latest version.");
    }

    private static StructureIdentifierMetaDataModelProperty initializeWith3Parameters(MetaDataKey key, boolean derivedStructure, boolean generatedStructure) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor constructor = StructureIdentifierMetaDataModelProperty.class.getConstructor(MetaDataKey.class, Boolean.TYPE, Boolean.TYPE);
        return (StructureIdentifierMetaDataModelProperty)constructor.newInstance(key, derivedStructure, generatedStructure);
    }

    private static StructureIdentifierMetaDataModelProperty initializeWith2Parameters(MetaDataKey key, boolean derivedStructure) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor constructor = StructureIdentifierMetaDataModelProperty.class.getConstructor(MetaDataKey.class, Boolean.TYPE);
        return (StructureIdentifierMetaDataModelProperty)constructor.newInstance(key, derivedStructure);
    }
}
