/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.metadata.api.annotation.TypeAnnotation
 *  org.mule.metadata.api.builder.BaseTypeBuilder
 *  org.mule.metadata.api.model.MetadataFormat
 *  org.mule.metadata.api.model.MetadataType
 *  org.mule.metadata.api.model.ObjectType
 *  org.mule.metadata.java.api.annotation.ClassInformationAnnotation
 *  org.mule.runtime.api.meta.Category
 *  org.mule.runtime.api.meta.ExpressionSupport
 *  org.mule.runtime.api.meta.model.ModelProperty
 *  org.mule.runtime.api.meta.model.ParameterDslConfiguration
 *  org.mule.runtime.api.meta.model.XmlDslModel
 *  org.mule.runtime.api.meta.model.declaration.fluent.ConstructDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.OperationDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.OptionalParameterDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer
 *  org.mule.runtime.api.meta.model.display.DisplayModel
 *  org.mule.runtime.api.meta.model.stereotype.StereotypeModel
 *  org.mule.runtime.api.meta.model.stereotype.StereotypeModelBuilder
 *  org.mule.runtime.core.api.extension.provider.MuleExtensionModelProvider
 *  org.mule.runtime.core.internal.extension.CustomBuildingDefinitionProviderModelProperty
 *  org.mule.runtime.extension.api.ExtensionConstants
 *  org.mule.runtime.extension.api.declaration.type.annotation.ExpressionSupportAnnotation
 *  org.mule.runtime.extension.api.property.NoWrapperModelProperty
 *  org.mule.runtime.extension.api.stereotype.MuleStereotypes
 *  org.mule.runtime.extension.api.util.XmlModelUtils
 *  org.mule.runtime.extension.privileged.util.ComponentDeclarationUtils
 */
package com.mulesoft.mule.runtime.tracking.api.extension.provider;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.mule.metadata.api.annotation.TypeAnnotation;
import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.model.MetadataFormat;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.api.model.ObjectType;
import org.mule.metadata.java.api.annotation.ClassInformationAnnotation;
import org.mule.runtime.api.meta.Category;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.meta.model.ModelProperty;
import org.mule.runtime.api.meta.model.ParameterDslConfiguration;
import org.mule.runtime.api.meta.model.XmlDslModel;
import org.mule.runtime.api.meta.model.declaration.fluent.ConstructDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.OperationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.OptionalParameterDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.api.meta.model.display.DisplayModel;
import org.mule.runtime.api.meta.model.stereotype.StereotypeModel;
import org.mule.runtime.api.meta.model.stereotype.StereotypeModelBuilder;
import org.mule.runtime.core.api.extension.provider.MuleExtensionModelProvider;
import org.mule.runtime.core.internal.extension.CustomBuildingDefinitionProviderModelProperty;
import org.mule.runtime.extension.api.ExtensionConstants;
import org.mule.runtime.extension.api.declaration.type.annotation.ExpressionSupportAnnotation;
import org.mule.runtime.extension.api.property.NoWrapperModelProperty;
import org.mule.runtime.extension.api.stereotype.MuleStereotypes;
import org.mule.runtime.extension.api.util.XmlModelUtils;
import org.mule.runtime.extension.privileged.util.ComponentDeclarationUtils;

class TrackingEeExtensionModelDeclarer {
    public static final String TRACKING_NAMESPACE = "tracking";
    public static final String TRANSACTION = "transaction";
    public static final String CUSTOM_EVENT = "custom-event";
    public static final String CUSTOM_EVENT_TEMPLATE = "custom-event-template";

    TrackingEeExtensionModelDeclarer() {
    }

    ExtensionDeclarer createExtensionModel() {
        ExtensionDeclarer extensionDeclarer = new ExtensionDeclarer().named(TRACKING_NAMESPACE).describedAs("Mule Runtime and Integration Platform: Tracking EE components").onVersion(MuleExtensionModelProvider.MULE_VERSION).fromVendor("MuleSoft, Inc.").withCategory(Category.SELECT).supportingJavaVersions(ExtensionConstants.ALL_SUPPORTED_JAVA_VERSIONS).withModelProperty((ModelProperty)new CustomBuildingDefinitionProviderModelProperty()).withXmlDsl(XmlDslModel.builder().setPrefix(TRACKING_NAMESPACE).setNamespace("http://www.mulesoft.org/schema/mule/ee/tracking").setSchemaVersion(MuleExtensionModelProvider.MULE_VERSION).setXsdFileName("mule-tracking-ee.xsd").setSchemaLocation(XmlModelUtils.buildSchemaLocation((String)TRACKING_NAMESPACE, (String)"http://www.mulesoft.org/schema/mule/ee/tracking")).build());
        StereotypeModel templateStereotype = StereotypeModelBuilder.newStereotype((String)"TRACKING", (String)"CUSTOM_EVENT_TEMPLATE").withParent(MuleStereotypes.CONFIG).build();
        ObjectType trackingMetadataType = BaseTypeBuilder.create((MetadataFormat)MetadataFormat.JAVA).objectType().with((TypeAnnotation)new ClassInformationAnnotation(Map.class, Arrays.asList(String.class, String.class))).openWith((MetadataType)BaseTypeBuilder.create((MetadataFormat)MetadataFormat.JAVA).stringType().with((TypeAnnotation)new ExpressionSupportAnnotation(ExpressionSupport.SUPPORTED)).build()).build();
        this.declareCustomEvent(extensionDeclarer, templateStereotype, trackingMetadataType);
        this.declareCustomEventTemplate(extensionDeclarer, templateStereotype, trackingMetadataType);
        this.declareTransaction(extensionDeclarer);
        return extensionDeclarer;
    }

    private void declareCustomEvent(ExtensionDeclarer extensionDeclarer, StereotypeModel templateStereotype, ObjectType trackingMetadataType) {
        OperationDeclarer customEvent = (OperationDeclarer)extensionDeclarer.withOperation(CUSTOM_EVENT).supportsStreaming(false);
        ComponentDeclarationUtils.withNoErrorMapping((OperationDeclarer)customEvent);
        customEvent.withOutput().ofType(MuleExtensionModelProvider.VOID_TYPE);
        customEvent.withOutputAttributes().ofType(MuleExtensionModelProvider.VOID_TYPE);
        customEvent.onDefaultParameterGroup().withRequiredParameter("event-name").ofType(MuleExtensionModelProvider.STRING_TYPE);
        ((OptionalParameterDeclarer)customEvent.onDefaultParameterGroup().withOptionalParameter("inherits").ofType(MuleExtensionModelProvider.STRING_TYPE)).withAllowedStereotypes(Collections.singletonList(templateStereotype));
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)customEvent.onDefaultParameterGroup().withOptionalParameter("metaData").ofType((MetadataType)trackingMetadataType)).withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(true).build())).withModelProperty((ModelProperty)new NoWrapperModelProperty());
    }

    private void declareCustomEventTemplate(ExtensionDeclarer extensionDeclarer, StereotypeModel templateStereotype, ObjectType trackingMetadataType) {
        ParameterGroupDeclarer defaultParamGroup = ((ConstructDeclarer)extensionDeclarer.withConstruct(CUSTOM_EVENT_TEMPLATE).allowingTopLevelDefinition().withStereotype(templateStereotype)).onDefaultParameterGroup();
        defaultParamGroup.withRequiredParameter("name").asComponentId().ofType(MuleExtensionModelProvider.STRING_TYPE);
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)defaultParamGroup.withOptionalParameter("metaData").ofType((MetadataType)trackingMetadataType)).withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(true).build())).withModelProperty((ModelProperty)new NoWrapperModelProperty());
    }

    private void declareTransaction(ExtensionDeclarer extensionDeclarer) {
        OperationDeclarer transaction = (OperationDeclarer)extensionDeclarer.withOperation(TRANSACTION).supportsStreaming(false);
        ComponentDeclarationUtils.withNoErrorMapping((OperationDeclarer)transaction);
        transaction.withOutput().ofType(MuleExtensionModelProvider.VOID_TYPE);
        transaction.withOutputAttributes().ofType(MuleExtensionModelProvider.VOID_TYPE);
        OptionalParameterDeclarer txIdParam = transaction.onDefaultParameterGroup().withOptionalParameter("id");
        txIdParam.ofType(MuleExtensionModelProvider.STRING_TYPE);
        txIdParam.withExpressionSupport(ExpressionSupport.SUPPORTED);
        txIdParam.withDisplayModel(DisplayModel.builder().displayName("Transaction ID").build());
        txIdParam.describedAs("The value of the identifier. It can be a literal or an expression. By default, a numeric value is assigned.");
    }
}
