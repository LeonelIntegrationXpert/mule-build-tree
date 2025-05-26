/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.api.meta.Category
 *  org.mule.runtime.api.meta.model.ModelProperty
 *  org.mule.runtime.api.meta.model.XmlDslModel
 *  org.mule.runtime.api.meta.model.declaration.fluent.ConstructDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.NestedRouteDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.OperationDeclarer
 *  org.mule.runtime.api.meta.model.declaration.fluent.OptionalParameterDeclarer
 *  org.mule.runtime.core.api.extension.provider.MuleExtensionModelProvider
 *  org.mule.runtime.core.internal.extension.CustomBuildingDefinitionProviderModelProperty
 *  org.mule.runtime.core.internal.extension.CustomLocationPartModelProperty
 *  org.mule.runtime.extension.api.ExtensionConstants
 *  org.mule.runtime.extension.api.util.XmlModelUtils
 *  org.mule.runtime.extension.privileged.util.ComponentDeclarationUtils
 */
package com.mulesoft.mule.runtime.http.policy.api.extension.provider;

import org.mule.runtime.api.meta.Category;
import org.mule.runtime.api.meta.model.ModelProperty;
import org.mule.runtime.api.meta.model.XmlDslModel;
import org.mule.runtime.api.meta.model.declaration.fluent.ConstructDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.NestedRouteDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.OperationDeclarer;
import org.mule.runtime.api.meta.model.declaration.fluent.OptionalParameterDeclarer;
import org.mule.runtime.core.api.extension.provider.MuleExtensionModelProvider;
import org.mule.runtime.core.internal.extension.CustomBuildingDefinitionProviderModelProperty;
import org.mule.runtime.core.internal.extension.CustomLocationPartModelProperty;
import org.mule.runtime.extension.api.ExtensionConstants;
import org.mule.runtime.extension.api.util.XmlModelUtils;
import org.mule.runtime.extension.privileged.util.ComponentDeclarationUtils;

class HttpPolicyEeExtensionModelDeclarer {
    public static final String EXECUTE_NEXT = "execute-next";

    HttpPolicyEeExtensionModelDeclarer() {
    }

    public ExtensionDeclarer createExtensionModel() {
        ExtensionDeclarer extensionDeclarer = new ExtensionDeclarer().named("http-policy").describedAs("Mule Runtime and Integration Platform: HTTP Policy EE components").onVersion(MuleExtensionModelProvider.MULE_VERSION).fromVendor("MuleSoft, Inc.").withCategory(Category.SELECT).supportingJavaVersions(ExtensionConstants.ALL_SUPPORTED_JAVA_VERSIONS).withModelProperty((ModelProperty)new CustomBuildingDefinitionProviderModelProperty()).withXmlDsl(XmlDslModel.builder().setPrefix("http-policy").setNamespace("http://www.mulesoft.org/schema/mule/mule-http").setSchemaVersion(MuleExtensionModelProvider.MULE_VERSION).setXsdFileName("mule-http-policy.xsd").setSchemaLocation(XmlModelUtils.buildSchemaLocation((String)"http-policy", (String)"http://www.mulesoft.org/schema/mule/mule-http")).build());
        this.declareProxy(extensionDeclarer);
        this.declareExecuteNext(extensionDeclarer);
        return extensionDeclarer;
    }

    private void declareProxy(ExtensionDeclarer extensionDeclarer) {
        ConstructDeclarer proxyDeclarer = extensionDeclarer.withConstruct("proxy").allowingTopLevelDefinition();
        proxyDeclarer.onDefaultParameterGroup().withRequiredParameter("name").describedAs("The name used to identify this policy.").asComponentId().ofType(MuleExtensionModelProvider.STRING_TYPE);
        NestedRouteDeclarer sourceDeclarer = proxyDeclarer.withRoute("source").withModelProperty((ModelProperty)new CustomLocationPartModelProperty("source", false));
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)sourceDeclarer.onDefaultParameterGroup().withOptionalParameter("propagateMessageTransformations").ofType(MuleExtensionModelProvider.BOOLEAN_TYPE)).describedAs("Whether changes made by the policy to the message before returning to the next policy or flow should be propagated to it.")).defaultingTo((Object)false);
        sourceDeclarer.withChain();
        NestedRouteDeclarer operationDeclarer = proxyDeclarer.withRoute("operation").withModelProperty((ModelProperty)new CustomLocationPartModelProperty("operation", false));
        ((OptionalParameterDeclarer)((OptionalParameterDeclarer)operationDeclarer.onDefaultParameterGroup().withOptionalParameter("propagateMessageTransformations").ofType(MuleExtensionModelProvider.BOOLEAN_TYPE)).describedAs("Whether changes made by the policy to the message before returning to the next policy or flow should be propagated to it.")).defaultingTo((Object)false);
        operationDeclarer.withChain();
    }

    private void declareExecuteNext(ExtensionDeclarer extensionDeclarer) {
        OperationDeclarer executeNext = extensionDeclarer.withOperation(EXECUTE_NEXT);
        ComponentDeclarationUtils.withNoErrorMapping((OperationDeclarer)executeNext);
        executeNext.withOutput().ofType(MuleExtensionModelProvider.ANY_TYPE);
        executeNext.withOutputAttributes().ofType(MuleExtensionModelProvider.ANY_TYPE);
    }
}
