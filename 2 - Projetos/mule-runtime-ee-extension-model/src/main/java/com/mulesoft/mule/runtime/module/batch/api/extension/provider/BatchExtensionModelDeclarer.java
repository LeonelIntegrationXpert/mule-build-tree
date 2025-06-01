/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.metadata.api.ClassTypeLoader
 *  org.mule.metadata.api.builder.BaseTypeBuilder
 *  org.mule.metadata.api.model.MetadataFormat
 *  org.mule.metadata.api.model.MetadataType
 *  org.mule.runtime.api.meta.Category
 *  org.mule.runtime.api.meta.ExpressionSupport
 *  org.mule.runtime.api.meta.model.ModelProperty
 *  org.mule.runtime.api.meta.model.XmlDslModel
 *  org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.FunctionDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.NestedChainDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.NestedComponentDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.NestedRouteDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.OperationDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.OptionalParameterDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.ParameterizedDeclarer
 *  org.mule.runtime.api.meta.model.nested.ChainExecutionOccurrence
 *  org.mule.runtime.core.api.extension.provider.MuleExtensionModelProvider
 *  org.mule.runtime.core.internal.extension.AllowsExpressionWithoutMarkersModelProperty
 *  org.mule.runtime.core.internal.extension.CustomBuildingDefinitionProviderModelProperty
 *  org.mule.runtime.core.internal.extension.CustomLocationPartModelProperty
 *  org.mule.runtime.extension.api.ExtensionConstants
 *  org.mule.runtime.extension.api.metadata.ComponentMetadataConfigurerFactory
 *  org.mule.runtime.extension.api.property.NoWrapperModelProperty
 *  org.mule.runtime.extension.api.util.XmlModelUtils
 *  org.mule.runtime.extension.privileged.util.ComponentDeclarationUtils
 *  org.mule.sdk.api.metadata.resolving.ChainInputTypeResolver
 */
package com.mulesoft.mule.runtime.module.batch.api.extension.provider;

import com.mulesoft.mule.runtime.module.batch.api.extension.record.AcceptRecordPolicy;
import com.mulesoft.mule.runtime.module.batch.api.extension.stereotype.MuleBatchStereotypes;
import com.mulesoft.mule.runtime.module.batch.api.extension.structure.BatchJobInstance;
import com.mulesoft.mule.runtime.module.batch.internal.extension.BatchAggregatorChainInputTypeResolver;
import java.util.concurrent.TimeUnit;
import org.mule.metadata.api.ClassTypeLoader;
import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.model.MetadataFormat;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.meta.Category;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.meta.model.ModelProperty;
import org.mule.runtime.api.meta.model.XmlDslModel;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.FunctionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.NestedChainDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.NestedComponentDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.NestedRouteDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.OperationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.OptionalParameterDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterGroupDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterizedDeclarer;
import org.mule.runtime.api.meta.model.nested.ChainExecutionOccurrence;
import org.mule.runtime.core.api.extension.provider.MuleExtensionModelProvider;
import org.mule.runtime.core.internal.extension.AllowsExpressionWithoutMarkersModelProperty;
import org.mule.runtime.core.internal.extension.CustomBuildingDefinitionProviderModelProperty;
import org.mule.runtime.core.internal.extension.CustomLocationPartModelProperty;
import org.mule.runtime.extension.api.ExtensionConstants;
import org.mule.runtime.extension.api.metadata.ComponentMetadataConfigurerFactory;
import org.mule.runtime.extension.api.property.NoWrapperModelProperty;
import org.mule.runtime.extension.api.util.XmlModelUtils;
import org.mule.runtime.extension.privileged.util.ComponentDeclarationUtils;
import org.mule.sdk.api.metadata.resolving.ChainInputTypeResolver;

public class BatchExtensionModelDeclarer {
    public static final int DEFAULT_MAX_CONCURRENCY = 2 * Runtime.getRuntime().availableProcessors();
    private final ComponentMetadataConfigurerFactory configurerFactory = ComponentMetadataConfigurerFactory.getDefault();

    public ExtensionDeclarer createExtensionModel() {
        ExtensionDeclarer extensionDeclarer = new ExtensionDeclarer().named("batch").describedAs("Mule Runtime and Integration Platform: Batch components").onVersion(MuleExtensionModelProvider.MULE_VERSION).fromVendor("MuleSoft, Inc.").withCategory(Category.SELECT).supportingJavaVersions(ExtensionConstants.ALL_SUPPORTED_JAVA_VERSIONS).withModelProperty((ModelProperty)new CustomBuildingDefinitionProviderModelProperty()).withXmlDsl(XmlDslModel.builder().setPrefix("batch").setNamespace("http://www.mulesoft.org/schema/mule/ee/batch").setSchemaVersion(MuleExtensionModelProvider.MULE_VERSION).setXsdFileName("mule-batch.xsd").setSchemaLocation(XmlModelUtils.buildSchemaLocation((String)"batch", (String)"http://www.mulesoft.org/schema/mule/ee/batch")).build());
        OperationDeclarer jobDeclaration = this.declareJob(extensionDeclarer, MuleExtensionModelProvider.BASE_TYPE_BUILDER, MuleExtensionModelProvider.TYPE_LOADER);
        this.declareProcessRecords(jobDeclaration, MuleExtensionModelProvider.TYPE_LOADER);
        this.declareAggregator(extensionDeclarer);
        this.declareOnComplete(jobDeclaration);
        this.declareFunctions(extensionDeclarer, MuleExtensionModelProvider.TYPE_LOADER);
        return extensionDeclarer;
    }

    private void declareProcessRecords(OperationDeclarer jobDeclaration, ClassTypeLoader typeLoader) {
        NestedComponentDeclarer processRecords = jobDeclaration.withComponent("processRecords").withModelProperty((ModelProperty)new CustomLocationPartModelProperty("route"));
        processRecords.withStereotype(MuleBatchStereotypes.PROCESS_RECORDS);
        this.declareStep(processRecords, typeLoader);
    }

    private void declareOnComplete(OperationDeclarer jobDeclaration) {
        ((NestedChainDeclarer)jobDeclaration.withOptionalComponent("onComplete").describedAs("This block contains a message processor chain that receives a message which payload is a BatchJobResult object").withStereotype(MuleBatchStereotypes.ON_COMPLETE).withModelProperty((ModelProperty)new CustomLocationPartModelProperty("route")).withChain().withModelProperty((ModelProperty)NoWrapperModelProperty.INSTANCE)).setExecutionOccurrence(ChainExecutionOccurrence.AT_LEAST_ONCE);
    }

    private void declareStep(NestedComponentDeclarer processRecordsDeclaration, ClassTypeLoader typeLoader) {
        NestedRouteDeclarer stepDeclaration = processRecordsDeclaration.withRoute("step").describedAs("A processing unit between a batch job").withMinOccurs(1).withModelProperty((ModelProperty)new CustomLocationPartModelProperty("route"));
        ((NestedChainDeclarer)stepDeclaration.withChain().withModelProperty((ModelProperty)NoWrapperModelProperty.INSTANCE)).setExecutionOccurrence(ChainExecutionOccurrence.MULTIPLE_OR_NONE);
        ParameterGroupDeclarer params = stepDeclaration.onDefaultParameterGroup();
        params.withRequiredParameter("name").describedAs("The name of the step. There cannot be two steps with the same name in the same job").ofType(MuleExtensionModelProvider.STRING_TYPE).asComponentId();
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)((OptionalParameterDeclarer)params.withOptionalParameter("acceptExpression").describedAs("An expression that if evaluated to false, filters the incoming record")).ofType(MuleExtensionModelProvider.BOOLEAN_TYPE)).withModelProperty((ModelProperty)new AllowsExpressionWithoutMarkersModelProperty()).withExpressionSupport(ExpressionSupport.REQUIRED)).defaultingTo((Object)true);
        ((OptionalParameterDeclarer)params.withOptionalParameter("acceptPolicy").ofType(typeLoader.load(AcceptRecordPolicy.class))).defaultingTo((Object)AcceptRecordPolicy.NO_FAILURES.name());
    }

    private void declareAggregator(ExtensionDeclarer batchDeclarer) {
        OperationDeclarer aggregator = (OperationDeclarer)((OperationDeclarer)((OperationDeclarer)batchDeclarer.withOperation("aggregator").describedAs("Aggregates records so that they can be processed in bulk before being passed over to the next step")).withModelProperty((ModelProperty)new CustomLocationPartModelProperty("aggregator", false))).withStereotype(MuleBatchStereotypes.STEP_AGGREGATOR);
        aggregator.withOutput().ofType(MuleExtensionModelProvider.VOID_TYPE);
        aggregator.withOutputAttributes().ofType(MuleExtensionModelProvider.VOID_TYPE);
        aggregator.onDefaultParameterGroup().withOptionalParameter("size").ofType(MuleExtensionModelProvider.INTEGER_TYPE);
        ((OptionalParameterDeclarer)aggregator.onDefaultParameterGroup().withOptionalParameter("streaming").ofType(MuleExtensionModelProvider.BOOLEAN_TYPE)).defaultingTo((Object)false);
        ((OptionalParameterDeclarer)aggregator.onDefaultParameterGroup().withOptionalParameter("preserveMimeTypes").ofType(MuleExtensionModelProvider.BOOLEAN_TYPE)).defaultingTo((Object)false);
        ((NestedChainDeclarer)aggregator.withChain().withModelProperty((ModelProperty)NoWrapperModelProperty.INSTANCE)).setExecutionOccurrence(ChainExecutionOccurrence.MULTIPLE_OR_NONE);
        this.configurerFactory.create().setChainInputTypeResolver((ChainInputTypeResolver)new BatchAggregatorChainInputTypeResolver()).configure((ParameterizedDeclarer)aggregator);
    }

    private OperationDeclarer declareJob(ExtensionDeclarer extensionDeclarer, BaseTypeBuilder typeBuilder, ClassTypeLoader typeLoader) {
        OperationDeclarer jobDeclaration = (OperationDeclarer)extensionDeclarer.withOperation("job").describedAs("Defines a BatchJob and creates an instance per each event that runs through this processor");
        ComponentDeclarationUtils.withNoErrorMapping((OperationDeclarer)jobDeclaration);
        jobDeclaration.withOutput().ofType(typeLoader.load(BatchJobInstance.class));
        jobDeclaration.withOutputAttributes().ofType(MuleExtensionModelProvider.VOID_TYPE);
        jobDeclaration.onDefaultParameterGroup().withRequiredParameter("jobName").describedAs("The name of the batch job").ofType(MuleExtensionModelProvider.STRING_TYPE).asComponentId();
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)jobDeclaration.onDefaultParameterGroup().withOptionalParameter("maxFailedRecords").describedAs("The number of max records allowed to fail before failing the job")).ofType(MuleExtensionModelProvider.INTEGER_TYPE)).defaultingTo((Object)0);
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)jobDeclaration.onDefaultParameterGroup().withOptionalParameter("blockSize").describedAs("For performance reasons, batch records are queued and scheduled in blocks. This attribute sets size of the block that should be used for instances of this job.")).ofType(MuleExtensionModelProvider.INTEGER_TYPE)).defaultingTo((Object)100);
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)jobDeclaration.onDefaultParameterGroup().withOptionalParameter("maxConcurrency").defaultingTo((Object)DEFAULT_MAX_CONCURRENCY).describedAs("The maximum concurrency. This value determines the maximum level of parallelism that the Job can use to optimize for performance when processing blocks. If no value is provided, the default is twice the number of available cores in the CPU(s)")).ofType((MetadataType)MuleExtensionModelProvider.BASE_TYPE_BUILDER.numberType().integer().range((Number)1, null).build())).withExpressionSupport(ExpressionSupport.NOT_SUPPORTED);
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)jobDeclaration.onDefaultParameterGroup().withOptionalParameter("jobInstanceId").describedAs("An optional expression which allows giving each spawned JobInstance a friendly name. If provided, this attribute is required to have an expression which provides unique values. A RuntimeException will be thrown when this expression returns a value previously seen.")).ofType(MuleExtensionModelProvider.STRING_TYPE)).withExpressionSupport(ExpressionSupport.REQUIRED);
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)jobDeclaration.onDefaultParameterGroup().withOptionalParameter("schedulingStrategy").describedAs("In the event of two or more instances of the same job being in executable state, this scheduling strategy specifies how the job's scheduler should be shared across those instances. This is specific to each batch job. If your application has several jobs, each can have a different strategy and each job instance will behave according to the strategy configured in its job.")).ofType((MetadataType)typeBuilder.stringType().enumOf(new String[]{"ORDERED_SEQUENTIAL", "ROUND_ROBIN"}).build())).defaultingTo((Object)"ORDERED_SEQUENTIAL");
        NestedComponentDeclarer history = jobDeclaration.withOptionalComponent("history").describedAs("Configures historic record keeping about executed job instances").withStereotype(MuleBatchStereotypes.HISTORY);
        ParameterGroupDeclarer expirationParamGroup = history.onParameterGroup("expiration").withDslInlineRepresentation(true);
        expirationParamGroup.withRequiredParameter("maxAge").ofType(MuleExtensionModelProvider.INTEGER_TYPE);
        expirationParamGroup.withRequiredParameter("ageUnit").ofType(typeLoader.load(TimeUnit.class));
        return jobDeclaration;
    }

    private void declareFunctions(ExtensionDeclarer extensionDeclarer, ClassTypeLoader typeLoader) {
        FunctionDeclarer functionDeclarer = extensionDeclarer.withFunction("isSuccessfulRecord");
        functionDeclarer.withOutput().describedAs("Returns whether the current record has not failed on any step").ofType(MuleExtensionModelProvider.BOOLEAN_TYPE);
        functionDeclarer = extensionDeclarer.withFunction("isFailedRecord");
        functionDeclarer.withOutput().describedAs("Returns whether the current record has failed on any step").ofType(MuleExtensionModelProvider.BOOLEAN_TYPE);
        MetadataType exceptionType = typeLoader.load(Exception.class);
        functionDeclarer = extensionDeclarer.withFunction("failureExceptionForStep");
        functionDeclarer.onDefaultParameterGroup().withRequiredParameter("stepName").ofType(MuleExtensionModelProvider.STRING_TYPE);
        functionDeclarer.withOutput().describedAs("Returns the exception that the current record experienced in a given step, or null if the record didn't fail on that step").ofType(exceptionType);
        functionDeclarer = extensionDeclarer.withFunction("getStepExceptions");
        functionDeclarer.withOutput().describedAs("Returns a map which keys are the names of the steps in which the current record has failed. The values of such map are the corresponding exceptions").ofType((MetadataType)this.getTypeBuilder().objectType().openWith(exceptionType).build());
        functionDeclarer = extensionDeclarer.withFunction("getFirstException");
        functionDeclarer.withOutput().describedAs("Returns the first exception that the current record experienced or null if the record hasn't failed so far").ofType(exceptionType);
        functionDeclarer = extensionDeclarer.withFunction("getLastException");
        functionDeclarer.withOutput().describedAs("Returns the last exception that the current record experienced or null if the record hasn't failed so far").ofType(exceptionType);
    }

    private BaseTypeBuilder getTypeBuilder() {
        return BaseTypeBuilder.create((MetadataFormat)MetadataFormat.JAVA);
    }
}
