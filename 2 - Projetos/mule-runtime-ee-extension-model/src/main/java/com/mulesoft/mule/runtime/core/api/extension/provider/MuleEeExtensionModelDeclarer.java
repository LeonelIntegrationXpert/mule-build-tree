/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.reflect.TypeToken
 *  org.mule.metadata.api.ClassTypeLoader
 *  org.mule.metadata.api.annotation.TypeAnnotation
 *  org.mule.metadata.api.builder.BaseTypeBuilder
 *  org.mule.metadata.api.builder.ObjectTypeBuilder
 *  org.mule.metadata.api.model.MetadataFormat
 *  org.mule.metadata.api.model.MetadataType
 *  org.mule.metadata.java.api.JavaTypeLoader
 *  org.mule.runtime.api.component.ComponentIdentifier
 *  org.mule.runtime.api.meta.Category
 *  org.mule.runtime.api.meta.ExpressionSupport
 *  org.mule.runtime.api.meta.model.ModelProperty
 *  org.mule.runtime.api.meta.model.ParameterDslConfiguration
 *  org.mule.runtime.api.meta.model.TypeAnnotationModelPropertyWrapper
 *  org.mule.runtime.api.meta.model.XmlDslModel
 *  org.mule.runtime.api.meta.model.declaration.fluent.ConstructDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.NestedComponentDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.OperationDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.OptionalParameterDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.ParameterizedDeclarer
 *  org.mule.runtime.api.meta.model.display.DisplayModel
 *  org.mule.runtime.api.meta.model.display.LayoutModel
 *  org.mule.runtime.api.meta.model.display.PathModel
 *  org.mule.runtime.api.meta.model.display.PathModel$Location
 *  org.mule.runtime.api.meta.model.display.PathModel$Type
 *  org.mule.runtime.api.meta.model.error.ErrorModel
 *  org.mule.runtime.api.meta.model.error.ErrorModelBuilder
 *  org.mule.runtime.api.meta.model.nested.ChainExecutionOccurrence
 *  org.mule.runtime.api.meta.model.operation.ExecutionType
 *  org.mule.runtime.api.meta.model.parameter.ParameterRole
 *  org.mule.runtime.api.metadata.TypedValue
 *  org.mule.runtime.config.internal.dsl.utils.DslConstants
 *  org.mule.runtime.core.api.error.Errors$ComponentIdentifiers$Handleable
 *  org.mule.runtime.core.api.extension.provider.MuleExtensionModelProvider
 *  org.mule.runtime.core.internal.extension.AllowsExpressionWithoutMarkersModelProperty
 *  org.mule.runtime.core.internal.extension.CustomBuildingDefinitionProviderModelProperty
 *  org.mule.runtime.extension.api.ExtensionConstants
 *  org.mule.runtime.extension.api.declaration.type.annotation.DisplayTypeAnnotation
 *  org.mule.runtime.extension.api.declaration.type.annotation.ExclusiveOptionalsTypeAnnotation
 *  org.mule.runtime.extension.api.declaration.type.annotation.ExpressionSupportAnnotation
 *  org.mule.runtime.extension.api.declaration.type.annotation.LayoutTypeAnnotation
 *  org.mule.runtime.extension.api.declaration.type.annotation.TypeDslAnnotation
 *  org.mule.runtime.extension.api.metadata.ComponentMetadataConfigurerFactory
 *  org.mule.runtime.extension.api.stereotype.MuleStereotypes
 *  org.mule.runtime.extension.api.util.XmlModelUtils
 *  org.mule.runtime.extension.privileged.util.ComponentDeclarationUtils
 */
package com.mulesoft.mule.runtime.core.api.extension.provider;

import com.google.common.reflect.TypeToken;
import com.mulesoft.mule.runtime.extension.api.stereotype.MuleEEStereotypes;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import org.mule.metadata.api.ClassTypeLoader;
import org.mule.metadata.api.annotation.TypeAnnotation;
import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.builder.ObjectTypeBuilder;
import org.mule.metadata.api.model.MetadataFormat;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.JavaTypeLoader;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.meta.Category;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.meta.model.ModelProperty;
import org.mule.runtime.api.meta.model.ParameterDslConfiguration;
import org.mule.runtime.api.meta.model.TypeAnnotationModelPropertyWrapper;
import org.mule.runtime.api.meta.model.XmlDslModel;
import org.mule.runtime.api.meta.model.declaration.fluent.ConstructDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.NestedComponentDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.OperationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.OptionalParameterDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterizedDeclarer;
import org.mule.runtime.api.meta.model.display.DisplayModel;
import org.mule.runtime.api.meta.model.display.LayoutModel;
import org.mule.runtime.api.meta.model.display.PathModel;
import org.mule.runtime.api.meta.model.error.ErrorModel;
import org.mule.runtime.api.meta.model.error.ErrorModelBuilder;
import org.mule.runtime.api.meta.model.nested.ChainExecutionOccurrence;
import org.mule.runtime.api.meta.model.operation.ExecutionType;
import org.mule.runtime.api.meta.model.parameter.ParameterRole;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.config.internal.dsl.utils.DslConstants;
import org.mule.runtime.core.api.error.Errors;
import org.mule.runtime.core.api.extension.provider.MuleExtensionModelProvider;
import org.mule.runtime.core.internal.extension.AllowsExpressionWithoutMarkersModelProperty;
import org.mule.runtime.core.internal.extension.CustomBuildingDefinitionProviderModelProperty;
import org.mule.runtime.extension.api.ExtensionConstants;
import org.mule.runtime.extension.api.declaration.type.annotation.DisplayTypeAnnotation;
import org.mule.runtime.extension.api.declaration.type.annotation.ExclusiveOptionalsTypeAnnotation;
import org.mule.runtime.extension.api.declaration.type.annotation.ExpressionSupportAnnotation;
import org.mule.runtime.extension.api.declaration.type.annotation.LayoutTypeAnnotation;
import org.mule.runtime.extension.api.declaration.type.annotation.TypeDslAnnotation;
import org.mule.runtime.extension.api.metadata.ComponentMetadataConfigurerFactory;
import org.mule.runtime.extension.api.stereotype.MuleStereotypes;
import org.mule.runtime.extension.api.util.XmlModelUtils;
import org.mule.runtime.extension.privileged.util.ComponentDeclarationUtils;

class MuleEeExtensionModelDeclarer {
    private static final String SCRIPT_PARAM_NAME = "script";
    private static final String RESOURCE_PARAM_NAME = "resource";
    private static final ExclusiveOptionalsTypeAnnotation SCRIPT_RESOURCE_EXCLUSIVENESS = new ExclusiveOptionalsTypeAnnotation(new HashSet<String>(Arrays.asList("script", "resource")), true);
    private final ComponentMetadataConfigurerFactory configurerFactory;

    public MuleEeExtensionModelDeclarer(ComponentMetadataConfigurerFactory configurerFactory) {
        this.configurerFactory = configurerFactory;
    }

    ExtensionDeclarer createExtensionModel() {
        BaseTypeBuilder typeBuilder = BaseTypeBuilder.create((MetadataFormat)JavaTypeLoader.JAVA);
        ExtensionDeclarer extensionDeclarer = new ExtensionDeclarer().named("ee").describedAs("Mule Runtime and Integration Platform: Core EE components").onVersion(MuleExtensionModelProvider.MULE_VERSION).fromVendor("MuleSoft, Inc.").withCategory(Category.SELECT).supportingJavaVersions(ExtensionConstants.ALL_SUPPORTED_JAVA_VERSIONS).withModelProperty((ModelProperty)new CustomBuildingDefinitionProviderModelProperty()).withXmlDsl(XmlDslModel.builder().setPrefix("ee").setNamespace(DslConstants.EE_NAMESPACE).setSchemaVersion(MuleExtensionModelProvider.MULE_VERSION).setXsdFileName("mule-ee.xsd").setSchemaLocation(XmlModelUtils.buildSchemaLocation((String)"ee", (String)DslConstants.EE_NAMESPACE)).build());
        ErrorModel anyError = ErrorModelBuilder.newError((ComponentIdentifier)Errors.ComponentIdentifiers.Handleable.ANY).build();
        ErrorModel expressionError = ErrorModelBuilder.newError((ComponentIdentifier)Errors.ComponentIdentifiers.Handleable.EXPRESSION).withParent(anyError).build();
        extensionDeclarer.withErrorModel(anyError).withErrorModel(expressionError);
        this.declareTransform(extensionDeclarer, expressionError);
        this.declareDynamicEvaluate(extensionDeclarer, MuleExtensionModelProvider.TYPE_LOADER, expressionError);
        this.declareInvalidateCache(extensionDeclarer);
        this.declareInvalidateKey(extensionDeclarer);
        this.declareCache(extensionDeclarer);
        this.declareObjectStoreCachingStrategy(extensionDeclarer);
        this.declareSchedulerPools(extensionDeclarer, typeBuilder);
        return extensionDeclarer;
    }

    private void declareTransform(ExtensionDeclarer extensionDeclarer, ErrorModel expressionError) {
        OperationDeclarer transform = (OperationDeclarer)((OperationDeclarer)extensionDeclarer.withOperation("transform").withExecutionType(ExecutionType.CPU_INTENSIVE).supportsStreaming(false)).withErrorModel(expressionError);
        ComponentDeclarationUtils.withNoErrorMapping((OperationDeclarer)transform);
        transform.withOutput().ofType(MuleExtensionModelProvider.ANY_TYPE);
        transform.withOutputAttributes().ofType(MuleExtensionModelProvider.ANY_TYPE);
        this.createMessageGroup(transform);
        this.createSetVariablesGroup(transform);
    }

    private void createMessageGroup(OperationDeclarer transform) {
        ParameterGroupDeclarer message = transform.onParameterGroup("Message").withDslInlineRepresentation(true).withLayout(LayoutModel.builder().order(1).build());
        ObjectTypeBuilder setPayloadType = MuleExtensionModelProvider.BASE_TYPE_BUILDER.objectType().id("SetPayload").with((TypeAnnotation)new TypeDslAnnotation(true, false, null, null));
        setPayloadType.addField().key(SCRIPT_PARAM_NAME).description("Modifies the payload of the message according to the provided value.").value((MetadataType)BaseTypeBuilder.create((MetadataFormat)MetadataFormat.JAVA).anyType().build()).required(false).with((TypeAnnotation)new LayoutTypeAnnotation(LayoutModel.builder().asText().build())).with((TypeAnnotation)new ExpressionSupportAnnotation(ExpressionSupport.REQUIRED)).with((TypeAnnotation)TypeAnnotationModelPropertyWrapper.defaultTypeAnnotationModelPropertyWrapper((ModelProperty)new AllowsExpressionWithoutMarkersModelProperty())).with((TypeAnnotation)SCRIPT_RESOURCE_EXCLUSIVENESS).build();
        setPayloadType.addField().key(RESOURCE_PARAM_NAME).description("Modifies the payload of the message according to the value referenced as a resource.").value(MuleExtensionModelProvider.STRING_TYPE).required(false).with((TypeAnnotation)SCRIPT_RESOURCE_EXCLUSIVENESS).with((TypeAnnotation)new DisplayTypeAnnotation(DisplayModel.builder().path(new PathModel(PathModel.Type.FILE, false, PathModel.Location.ANY, new String[]{"dw", "dwl"})).build())).build();
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)message.withOptionalParameter("setPayload").ofType((MetadataType)setPayloadType.build())).withRole(ParameterRole.BEHAVIOUR)).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).withLayout(LayoutModel.builder().asText().build())).withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(true).allowsReferences(false).allowTopLevelDefinition(false).build());
        ObjectTypeBuilder setAttributesType = MuleExtensionModelProvider.BASE_TYPE_BUILDER.objectType().id("SetAttributes").with((TypeAnnotation)new TypeDslAnnotation(true, false, null, null));
        setAttributesType.addField().key(SCRIPT_PARAM_NAME).description("Modifies the attributes of the message according to the provided value.").value((MetadataType)BaseTypeBuilder.create((MetadataFormat)MetadataFormat.JAVA).anyType().build()).required(false).with((TypeAnnotation)new LayoutTypeAnnotation(LayoutModel.builder().asText().build())).with((TypeAnnotation)new ExpressionSupportAnnotation(ExpressionSupport.REQUIRED)).with((TypeAnnotation)TypeAnnotationModelPropertyWrapper.defaultTypeAnnotationModelPropertyWrapper((ModelProperty)new AllowsExpressionWithoutMarkersModelProperty())).with((TypeAnnotation)SCRIPT_RESOURCE_EXCLUSIVENESS).build();
        setAttributesType.addField().key(RESOURCE_PARAM_NAME).description("Modifies the attributes of the message according to the value referenced as a resource.").value(MuleExtensionModelProvider.STRING_TYPE).required(false).with((TypeAnnotation)SCRIPT_RESOURCE_EXCLUSIVENESS).with((TypeAnnotation)new DisplayTypeAnnotation(DisplayModel.builder().path(new PathModel(PathModel.Type.FILE, false, PathModel.Location.ANY, new String[]{"dw", "dwl"})).build())).build();
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)message.withOptionalParameter("setAttributes").ofType((MetadataType)setAttributesType.build())).withRole(ParameterRole.BEHAVIOUR)).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(true).allowsReferences(false).allowTopLevelDefinition(false).build());
    }

    private void createSetVariablesGroup(OperationDeclarer transform) {
        ParameterGroupDeclarer setVariablesGroup = transform.onParameterGroup("Set Variables").withLayout(LayoutModel.builder().order(2).build());
        ObjectTypeBuilder setVariableType = MuleExtensionModelProvider.BASE_TYPE_BUILDER.objectType().id("SetVariable").with((TypeAnnotation)new TypeDslAnnotation(true, false, null, null));
        setVariableType.addField().key("variableName").description("Declares the name of the target variable for this transformation.").value(MuleExtensionModelProvider.STRING_TYPE).required(false).build();
        setVariableType.addField().key(SCRIPT_PARAM_NAME).description("Creates or modifies the variable according to the provided value.").value((MetadataType)BaseTypeBuilder.create((MetadataFormat)MetadataFormat.JAVA).anyType().build()).required(false).with((TypeAnnotation)new LayoutTypeAnnotation(LayoutModel.builder().asText().build())).with((TypeAnnotation)new ExpressionSupportAnnotation(ExpressionSupport.REQUIRED)).with((TypeAnnotation)TypeAnnotationModelPropertyWrapper.defaultTypeAnnotationModelPropertyWrapper((ModelProperty)new AllowsExpressionWithoutMarkersModelProperty())).with((TypeAnnotation)SCRIPT_RESOURCE_EXCLUSIVENESS).build();
        setVariableType.addField().key(RESOURCE_PARAM_NAME).description("Creates or modifies the variable according to the value referenced as a resource.").value(MuleExtensionModelProvider.STRING_TYPE).required(false).with((TypeAnnotation)SCRIPT_RESOURCE_EXCLUSIVENESS).with((TypeAnnotation)new DisplayTypeAnnotation(DisplayModel.builder().path(new PathModel(PathModel.Type.FILE, false, PathModel.Location.ANY, new String[]{"dw", "dwl"})).build())).build();
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)setVariablesGroup.withOptionalParameter("variables").ofType((MetadataType)MuleExtensionModelProvider.BASE_TYPE_BUILDER.arrayType().of((MetadataType)setVariableType.build()).build())).withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(true).allowsReferences(false).allowTopLevelDefinition(false).build())).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).withRole(ParameterRole.BEHAVIOUR);
    }

    private void declareDynamicEvaluate(ExtensionDeclarer extensionDeclarer, ClassTypeLoader typeLoader, ErrorModel expressionError) {
        OperationDeclarer evaluate = (OperationDeclarer)((OperationDeclarer)((OperationDeclarer)extensionDeclarer.withOperation("dynamicEvaluate").withExecutionType(ExecutionType.CPU_INTENSIVE).supportsStreaming(false)).describedAs("Evaluates an expression that should result in a script, then evaluates that script for a final result.")).withErrorModel(expressionError);
        ComponentDeclarationUtils.withNoErrorMapping((OperationDeclarer)evaluate);
        evaluate.withOutput().ofType(MuleExtensionModelProvider.ANY_TYPE);
        evaluate.withOutputAttributes().ofType(MuleExtensionModelProvider.VOID_TYPE);
        evaluate.onDefaultParameterGroup().withRequiredParameter("expression").ofType(MuleExtensionModelProvider.ANY_TYPE).withExpressionSupport(ExpressionSupport.REQUIRED).describedAs("An expression referencing the dynamic script to evaluate.");
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)evaluate.onDefaultParameterGroup().withOptionalParameter("parameters").withDisplayModel(DisplayModel.builder().displayName("Additional Bindings").build())).ofType(typeLoader.load(new TypeToken<Map<String, TypedValue<Object>>>(){}.getType()))).withRole(ParameterRole.BEHAVIOUR)).withExpressionSupport(ExpressionSupport.REQUIRED)).withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(true).allowsReferences(false).allowTopLevelDefinition(false).build())).withLayout(LayoutModel.builder().asText().build())).describedAs("Additional bindings to use during evaluation.");
    }

    private void declareInvalidateCache(ExtensionDeclarer extensionDeclarer) {
        OperationDeclarer invalidateCache = ((OperationDeclarer)extensionDeclarer.withOperation("invalidateCache").describedAs("Invalidates all entries within a cache.")).withExecutionType(ExecutionType.CPU_LITE);
        ComponentDeclarationUtils.withNoErrorMapping((OperationDeclarer)invalidateCache);
        invalidateCache.onDefaultParameterGroup().withRequiredParameter("cachingStrategy-ref").withAllowedStereotypes(Collections.singletonList(MuleEEStereotypes.INVALIDATABLE_CACHING_STRATEGY)).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED).ofType(MuleExtensionModelProvider.STRING_TYPE).describedAs("Reference to the caching strategy object that will be invalidated.");
        invalidateCache.withOutput().ofType(MuleExtensionModelProvider.VOID_TYPE);
        invalidateCache.withOutputAttributes().ofType(MuleExtensionModelProvider.VOID_TYPE);
    }

    private void declareInvalidateKey(ExtensionDeclarer extensionDeclarer) {
        OperationDeclarer invalidateKey = ((OperationDeclarer)extensionDeclarer.withOperation("invalidateKey").describedAs("Invalidates a single entry within a cache.")).withExecutionType(ExecutionType.CPU_LITE);
        ComponentDeclarationUtils.withNoErrorMapping((OperationDeclarer)invalidateKey);
        invalidateKey.onDefaultParameterGroup().withRequiredParameter("cachingStrategy-ref").withAllowedStereotypes(Collections.singletonList(MuleEEStereotypes.INVALIDATABLE_CACHING_STRATEGY)).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED).ofType(MuleExtensionModelProvider.STRING_TYPE).describedAs("Reference to the caching strategy object whose key will be invalidated.").withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(false).allowsReferences(true).allowTopLevelDefinition(false).build());
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)invalidateKey.onDefaultParameterGroup().withOptionalParameter("keyGenerationExpression").withExpressionSupport(ExpressionSupport.SUPPORTED)).ofType(MuleExtensionModelProvider.STRING_TYPE)).describedAs("The expression to generate the object's key to store them in the caching strategy. Do not use this property if keyGenerator-ref is used.");
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)invalidateKey.onDefaultParameterGroup().withOptionalParameter("keyGenerator-ref").withAllowedStereotypes(Collections.singletonList(MuleEEStereotypes.CACHING_KEY_GENERATOR))).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).ofType(MuleExtensionModelProvider.STRING_TYPE)).describedAs("Reference to the key generator object used to create the object's key to store them in the caching strategy. Do not use this property if keyGenerationExpression is used.")).withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(false).allowsReferences(true).allowTopLevelDefinition(false).build());
        invalidateKey.withOutput().ofType(MuleExtensionModelProvider.VOID_TYPE);
        invalidateKey.withOutputAttributes().ofType(MuleExtensionModelProvider.VOID_TYPE);
    }

    private void declareObjectStoreCachingStrategy(ExtensionDeclarer extensionDeclarer) {
        ConstructDeclarer objectStoreCachingStrategy = (ConstructDeclarer)extensionDeclarer.withConstruct("objectStoreCachingStrategy").allowingTopLevelDefinition().withStereotype(MuleEEStereotypes.INVALIDATABLE_CACHING_STRATEGY);
        objectStoreCachingStrategy.onDefaultParameterGroup().withRequiredParameter("name").asComponentId().ofType(MuleExtensionModelProvider.STRING_TYPE).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED);
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)objectStoreCachingStrategy.onDefaultParameterGroup().withOptionalParameter("keyGenerationExpression").withExpressionSupport(ExpressionSupport.SUPPORTED)).ofType(MuleExtensionModelProvider.STRING_TYPE)).describedAs("The expression to generate the object's key to store them in the caching strategy. Do not use this property if keyGenerator-ref is used.");
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)objectStoreCachingStrategy.onDefaultParameterGroup().withOptionalParameter("keyGenerator-ref").withAllowedStereotypes(Collections.singletonList(MuleEEStereotypes.CACHING_KEY_GENERATOR))).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).ofType(MuleExtensionModelProvider.STRING_TYPE)).describedAs("Reference to the key generator object used to create the object's key to store them in the caching strategy. Do not use this property if keyGenerationExpression is used.")).withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(false).allowsReferences(true).allowTopLevelDefinition(false).build());
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)objectStoreCachingStrategy.onDefaultParameterGroup().withOptionalParameter("responseGenerator-ref").withAllowedStereotypes(Collections.singletonList(MuleEEStereotypes.CACHING_RESPONSE_GENERATOR))).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).ofType(MuleExtensionModelProvider.STRING_TYPE)).describedAs("Reference to the response generator object used to create the responses returned by the caching strategy.")).withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(false).allowsReferences(true).allowTopLevelDefinition(false).build());
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)objectStoreCachingStrategy.onDefaultParameterGroup().withOptionalParameter("objectStore").withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(true).allowsReferences(true).build())).ofType(MuleExtensionModelProvider.OBJECT_STORE_TYPE)).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).withAllowedStereotypes(Collections.singletonList(MuleStereotypes.OBJECT_STORE));
        ParameterGroupDeclarer store = objectStoreCachingStrategy.onParameterGroup("Object Store");
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)store.withOptionalParameter("persistent").describedAs("When no store is specified, defines if this store should be persistent or not.")).ofType(MuleExtensionModelProvider.BOOLEAN_TYPE)).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).defaultingTo((Object)false);
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)store.withOptionalParameter("maxEntries").describedAs("When no store is specified, defines the maximum number of entries that this store keeps around. Specify '-1' if the store is supposed to be `unbounded`.")).ofType(MuleExtensionModelProvider.INTEGER_TYPE)).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).defaultingTo((Object)4000);
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)store.withOptionalParameter("entryTTL").describedAs("When no store is specified, defines the time-to-live for each message ID when a default object store is created, specified in milliseconds. Use '-1' for entries that should never expire. DO NOT combine this with an unbounded store!")).ofType(MuleExtensionModelProvider.INTEGER_TYPE)).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).defaultingTo((Object)300000);
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)store.withOptionalParameter("expirationInterval").describedAs("When no store is specified, defines the interval for periodic bounded size enforcement and entry expiration, specified in milliseconds. Arbitrary positive values between one second and several hours or days are possible, but should be chosen carefully according to the expected message rate to prevent OutOfMemory conditions.")).ofType(MuleExtensionModelProvider.INTEGER_TYPE)).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).defaultingTo((Object)30000);
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)objectStoreCachingStrategy.onParameterGroup("Advanced").withOptionalParameter("synchronized").describedAs("Indicates that `ee:cache` will synchronize cache access. When cache is synchronized, each thread accessing a given key will have to acquire a lock on it. This provides a way to obtain cache coherence as there will be only a value for each cache key. Note that cache synchronization cannot be enforced when the underlying cache implementation is used outside the caching strategy. When there is no need to get cache coherence, setting this attribute to `false` will improve performance as no locking is involved. NOTE: cache keys must be Strings in order to be locked.")).ofType(MuleExtensionModelProvider.BOOLEAN_TYPE)).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).defaultingTo((Object)true);
        ParameterGroupDeclarer eventCopyStrategy = objectStoreCachingStrategy.onParameterGroup("Event copy strategy");
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)eventCopyStrategy.withOptionalParameter("serializableEventCopyStrategy").withRole(ParameterRole.BEHAVIOUR)).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).ofType((MetadataType)BaseTypeBuilder.create((MetadataFormat)MetadataFormat.JAVA).objectType().id("SerializableEventCopyStrategy").with((TypeAnnotation)new TypeDslAnnotation(true, false, null, null)).build())).withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(true).allowsReferences(false).allowTopLevelDefinition(false).build())).describedAs("Creates copies of a mule event and clones the payload using serialization. Requires a Serializable payload.");
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)eventCopyStrategy.withOptionalParameter("simpleEventCopyStrategy").withRole(ParameterRole.BEHAVIOUR)).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).ofType((MetadataType)BaseTypeBuilder.create((MetadataFormat)MetadataFormat.JAVA).objectType().id("SimpleEventCopyStrategy").with((TypeAnnotation)new TypeDslAnnotation(true, false, null, null)).build())).withDsl(ParameterDslConfiguration.builder().allowsInlineDefinition(true).allowsReferences(false).allowTopLevelDefinition(false).build())).describedAs("Creates copies of a mule event");
        eventCopyStrategy.withExclusiveOptionals(new HashSet<String>(Arrays.asList("serializableEventCopyStrategy", "simpleEventCopyStrategy")), false);
    }

    private void declareCache(ExtensionDeclarer extensionDeclarer) {
        OperationDeclarer cache = ((OperationDeclarer)extensionDeclarer.withOperation("cache").describedAs("Caching scope that will return a cached value if present.")).blocking(false);
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)cache.onDefaultParameterGroup().withOptionalParameter("cachingStrategy-ref").withAllowedStereotypes(Collections.singletonList(MuleEEStereotypes.CACHING_STRATEGY))).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).ofType(MuleExtensionModelProvider.STRING_TYPE)).describedAs("Reference to the caching strategy object.");
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)cache.onDefaultParameterGroup().withOptionalParameter("filterExpression").ofType(MuleExtensionModelProvider.BOOLEAN_TYPE)).withExpressionSupport(ExpressionSupport.REQUIRED)).defaultingTo((Object)true).describedAs("The expression used to filter which messages should be processed using the caching strategy.");
        cache.withChain().setExecutionOccurrence(ChainExecutionOccurrence.ONCE_OR_NONE);
        cache.withOutput().ofDynamicType(MuleExtensionModelProvider.ANY_TYPE);
        cache.withOutputAttributes().ofDynamicType(MuleExtensionModelProvider.VOID_TYPE);
        this.configurerFactory.create().asPassthroughScope().configure((ParameterizedDeclarer)cache);
    }

    private void declareSchedulerPools(ExtensionDeclarer extensionDeclarer, BaseTypeBuilder typeBuilder) {
        ConstructDeclarer schedulerPools = ((ConstructDeclarer)extensionDeclarer.withConstruct("schedulerPools").describedAs("The configured artifact will create and use its own schedulers based on the configuration provided here, rather than the container level schedulers.")).allowingTopLevelDefinition();
        this.declareIoScheduler(schedulerPools.withOptionalComponent("uber"));
        this.declareCpuLightScheduler(schedulerPools.withOptionalComponent("cpu-light"));
        this.declareIoScheduler(schedulerPools.withOptionalComponent("io"));
        this.declareCpuIntensiveScheduler(schedulerPools.withOptionalComponent("cpu-intensive"));
        ParameterGroupDeclarer parameterGroupDeclarer = schedulerPools.onDefaultParameterGroup();
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)parameterGroupDeclarer.withOptionalParameter("poolStrategy").ofType((MetadataType)typeBuilder.stringType().enumOf(new String[]{"UBER", "DEDICATED"}).build())).defaultingTo((Object)"DEDICATED").withExpressionSupport(ExpressionSupport.NOT_SUPPORTED)).describedAs("The strategy to be used for managing the thread pools that back the 3 types of schedulers in the Mule Runtime (cpu_light, cpu_intensive and I/O).");
        parameterGroupDeclarer.withRequiredParameter("gracefulShutdownTimeout").ofType(MuleExtensionModelProvider.LONG_TYPE).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED).describedAs("The maximum time (in milliseconds) to wait until all tasks in all the artifact thread pools have completed execution when stopping the scheduler service.");
    }

    private void declareCpuIntensiveScheduler(NestedComponentDeclarer schedulerTypeDeclarer) {
        ParameterGroupDeclarer parameterGroupDeclarer = schedulerTypeDeclarer.onDefaultParameterGroup();
        parameterGroupDeclarer.withRequiredParameter("poolSize").ofType(MuleExtensionModelProvider.INTEGER_TYPE).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED).describedAs("The number of threads to keep in the cpu_intensive pool, even if they are idle.");
        parameterGroupDeclarer.withRequiredParameter("queueSize").ofType(MuleExtensionModelProvider.INTEGER_TYPE).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED).describedAs("The size of the queue to use for holding cpu_intensive tasks before they are executed.");
    }

    private void declareCpuLightScheduler(NestedComponentDeclarer schedulerTypeDeclarer) {
        ParameterGroupDeclarer parameterGroupDeclarer = schedulerTypeDeclarer.onDefaultParameterGroup();
        parameterGroupDeclarer.withRequiredParameter("poolSize").ofType(MuleExtensionModelProvider.INTEGER_TYPE).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED).describedAs("The number of threads to keep in the cpu_lite pool, even if they are idle.");
        parameterGroupDeclarer.withRequiredParameter("queueSize").ofType(MuleExtensionModelProvider.INTEGER_TYPE).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED).describedAs("The size of the queue to use for holding cpu_lite tasks before they are executed.");
    }

    private void declareIoScheduler(NestedComponentDeclarer schedulerTypeDeclarer) {
        ParameterGroupDeclarer parameterGroupDeclarer = schedulerTypeDeclarer.onDefaultParameterGroup();
        parameterGroupDeclarer.withRequiredParameter("corePoolSize").ofType(MuleExtensionModelProvider.INTEGER_TYPE).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED).describedAs("The number of threads to keep in the I/O pool.");
        parameterGroupDeclarer.withRequiredParameter("maxPoolSize").ofType(MuleExtensionModelProvider.INTEGER_TYPE).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED).describedAs("The maximum number of threads to allow in the I/O pool.");
        parameterGroupDeclarer.withRequiredParameter("queueSize").ofType(MuleExtensionModelProvider.INTEGER_TYPE).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED).describedAs("The size of the queue to use for holding I/O tasks before they are executed.");
        parameterGroupDeclarer.withRequiredParameter("keepAlive").ofType(MuleExtensionModelProvider.LONG_TYPE).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED).describedAs("When the number of threads in the I/O pool is greater than ioThreadPoolCoreSize, this is the maximum time (in milliseconds) that excess idle threads will wait for new tasks before terminating.");
    }
}
