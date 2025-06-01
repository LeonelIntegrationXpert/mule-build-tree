/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.metadata.api.model.MetadataType
 *  org.mule.runtime.api.meta.Category
 *  org.mule.runtime.api.meta.model.ModelProperty
 *  org.mule.runtime.api.meta.model.XmlDslModel
 *  org.mule.runtime.api.meta.model.declaration.fluent.ConstructDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.OptionalParameterDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer
 *  org.mule.runtime.core.api.extension.provider.MuleExtensionModelProvider
 *  org.mule.runtime.core.internal.extension.CustomBuildingDefinitionProviderModelProperty
 *  org.mule.runtime.extension.api.ExtensionConstants
 *  org.mule.runtime.extension.api.stereotype.MuleStereotypes
 *  org.mule.runtime.extension.api.util.XmlModelUtils
 */
package com.mulesoft.mule.runtime.module.serialization.kryo.api.extension.provider;

import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.meta.Category;
import org.mule.runtime.api.meta.model.ModelProperty;
import org.mule.runtime.api.meta.model.XmlDslModel;
import org.mule.runtime.api.meta.model.declaration.fluent.ConstructDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.OptionalParameterDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.core.api.extension.provider.MuleExtensionModelProvider;
import org.mule.runtime.core.internal.extension.CustomBuildingDefinitionProviderModelProperty;
import org.mule.runtime.extension.api.ExtensionConstants;
import org.mule.runtime.extension.api.stereotype.MuleStereotypes;
import org.mule.runtime.extension.api.util.XmlModelUtils;

class KryoSerializerEeExtensionModelDeclarer {
    KryoSerializerEeExtensionModelDeclarer() {
    }

    public ExtensionDeclarer createExtensionModel() {
        ExtensionDeclarer extensionDeclarer = new ExtensionDeclarer().named("kryo").describedAs("Mule Runtime and Integration Platform: An implementation of the ObjectSerializer API which uses Kryo").onVersion(MuleExtensionModelProvider.MULE_VERSION).fromVendor("MuleSoft, Inc.").withCategory(Category.SELECT).supportingJavaVersions(ExtensionConstants.ALL_SUPPORTED_JAVA_VERSIONS).withModelProperty((ModelProperty)new CustomBuildingDefinitionProviderModelProperty()).withXmlDsl(XmlDslModel.builder().setPrefix("kryo").setNamespace("http://www.mulesoft.org/schema/mule/kryo").setSchemaVersion(MuleExtensionModelProvider.MULE_VERSION).setXsdFileName("mule-kryo.xsd").setSchemaLocation(XmlModelUtils.buildSchemaLocation((String)"kryo", (String)"http://www.mulesoft.org/schema/mule/kryo")).build());
        this.declareSerializer(extensionDeclarer);
        return extensionDeclarer;
    }

    private void declareSerializer(ExtensionDeclarer extensionDeclarer) {
        ConstructDeclarer proxyDeclarer = ((ConstructDeclarer)extensionDeclarer.withConstruct("serializer").withStereotype(MuleStereotypes.SERIALIZER)).allowingTopLevelDefinition();
        ParameterGroupDeclarer defaultParamGroup = proxyDeclarer.onDefaultParameterGroup();
        defaultParamGroup.withRequiredParameter("name").describedAs("The name used to identify this serializer.").asComponentId().ofType(MuleExtensionModelProvider.STRING_TYPE);
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)defaultParamGroup.withOptionalParameter("compressionMode").describedAs("The type of compression to use when serializing")).ofType((MetadataType)MuleExtensionModelProvider.BASE_TYPE_BUILDER.stringType().enumOf(new String[]{"NONE", "DEFLATE", "GZIP"}).build())).defaultingTo((Object)"NONE");
    }
}
