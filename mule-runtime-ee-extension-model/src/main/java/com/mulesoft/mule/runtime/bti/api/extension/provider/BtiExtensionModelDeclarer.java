/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.api.meta.Category
 *  org.mule.runtime.api.meta.model.ModelProperty
 *  org.mule.runtime.api.meta.model.XmlDslModel
 *  org.mule.runtime.api.meta.model.declaration.fluent.ConstructDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.OptionalParameterDeclarer
 *  org.mule.runtime.core.api.extension.provider.MuleExtensionModelProvider
 *  org.mule.runtime.core.internal.extension.CustomBuildingDefinitionProviderModelProperty
 *  org.mule.runtime.extension.api.ExtensionConstants
 *  org.mule.runtime.extension.api.stereotype.MuleStereotypes
 *  org.mule.runtime.extension.api.util.XmlModelUtils
 */
package com.mulesoft.mule.runtime.bti.api.extension.provider;

import com.mulesoft.mule.runtime.extension.api.stereotype.MuleEEStereotypes;
import org.mule.runtime.api.meta.Category;
import org.mule.runtime.api.meta.model.ModelProperty;
import org.mule.runtime.api.meta.model.XmlDslModel;
import org.mule.runtime.api.meta.model.declaration.fluent.ConstructDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.OptionalParameterDeclarer;
import org.mule.runtime.core.api.extension.provider.MuleExtensionModelProvider;
import org.mule.runtime.core.internal.extension.CustomBuildingDefinitionProviderModelProperty;
import org.mule.runtime.extension.api.ExtensionConstants;
import org.mule.runtime.extension.api.stereotype.MuleStereotypes;
import org.mule.runtime.extension.api.util.XmlModelUtils;

public class BtiExtensionModelDeclarer {
    public ExtensionDeclarer createExtensionModel() {
        ExtensionDeclarer extensionDeclarer = new ExtensionDeclarer().named("bti").describedAs("Mule Runtime and Integration Platform: BTI transaction manager").onVersion(MuleExtensionModelProvider.MULE_VERSION).fromVendor("MuleSoft, Inc.").withCategory(Category.SELECT).supportingJavaVersions(ExtensionConstants.ALL_SUPPORTED_JAVA_VERSIONS).withModelProperty((ModelProperty)new CustomBuildingDefinitionProviderModelProperty()).withXmlDsl(XmlDslModel.builder().setPrefix("bti").setNamespace("http://www.mulesoft.org/schema/mule/ee/bti").setSchemaVersion(MuleExtensionModelProvider.MULE_VERSION).setXsdFileName("mule-bti-ee.xsd").setSchemaLocation(XmlModelUtils.buildSchemaLocation((String)"bti", (String)"http://www.mulesoft.org/schema/mule/ee/bti")).build());
        this.declareTxManager(extensionDeclarer);
        this.declareXaDataSourcePool(extensionDeclarer);
        return extensionDeclarer;
    }

    private void declareTxManager(ExtensionDeclarer extensionDeclarer) {
        ((ConstructDeclarer)extensionDeclarer.withConstruct("transactionManager").allowingTopLevelDefinition().withStereotype(MuleEEStereotypes.TX_MANAGER)).describedAs("To configure an instance of the bitronix transaction manager within Mule, add this element to your Mule application.\nYou can then declare XA transactions on endpoints supporting XA transactions, and all those transactions will be managed by the bitronix transaction manager.");
    }

    private void declareXaDataSourcePool(ExtensionDeclarer extensionDeclarer) {
        ConstructDeclarer xaDataSourcePoolDeclarer = (ConstructDeclarer)((ConstructDeclarer)extensionDeclarer.withConstruct("xaDataSourcePool").allowingTopLevelDefinition().withStereotype(MuleStereotypes.APP_CONFIG)).describedAs("This element allows to configure a Bitronix data source pool. You must reference a data source though the 'dataSource-ref' parameter, and then use this element as data source in the DB connector config.");
        xaDataSourcePoolDeclarer.onDefaultParameterGroup().withRequiredParameter("name").ofType(MuleExtensionModelProvider.STRING_TYPE).asComponentId().describedAs("Identifies the pool so that the connector can reference it.");
        ((OptionalParameterDeclarer)xaDataSourcePoolDeclarer.onDefaultParameterGroup().withOptionalParameter("minPoolSize").ofType(MuleExtensionModelProvider.INTEGER_TYPE)).describedAs("Defines the minimal amount of connections that can be in the pool.");
        ((OptionalParameterDeclarer)xaDataSourcePoolDeclarer.onDefaultParameterGroup().withOptionalParameter("maxPoolSize").ofType(MuleExtensionModelProvider.INTEGER_TYPE)).describedAs("Defines the maximum amount of connections that can be in the pool.");
        ((OptionalParameterDeclarer)xaDataSourcePoolDeclarer.onDefaultParameterGroup().withOptionalParameter("maxIdleTime").ofType(MuleExtensionModelProvider.INTEGER_TYPE)).describedAs("Defines the amount of seconds an idle connection can stay in the pool before getting closed.");
        xaDataSourcePoolDeclarer.onDefaultParameterGroup().withRequiredParameter("dataSource-ref").ofType(MuleExtensionModelProvider.STRING_TYPE).describedAs("Reference to the JDBC data source object. An XADataSource object must be provided.");
        ((OptionalParameterDeclarer)xaDataSourcePoolDeclarer.onDefaultParameterGroup().withOptionalParameter("acquireIncrement").ofType(MuleExtensionModelProvider.INTEGER_TYPE)).describedAs("Determines how many connections at a time will try to acquire when the pool is exhausted.");
        ((OptionalParameterDeclarer)xaDataSourcePoolDeclarer.onDefaultParameterGroup().withOptionalParameter("preparedStatementCacheSize").ofType(MuleExtensionModelProvider.INTEGER_TYPE)).describedAs("Determines how many statements are cached per pooled connection. Leaving this empty means statement caching is disabled.");
        ((OptionalParameterDeclarer)xaDataSourcePoolDeclarer.onDefaultParameterGroup().withOptionalParameter("acquireTimeoutSeconds").ofType(MuleExtensionModelProvider.INTEGER_TYPE)).describedAs("The number of seconds a client calling getConnection() will wait for a Connection to be checked-in or acquired when the pool is exhausted. Zero means wait indefinitely.");
    }
}
