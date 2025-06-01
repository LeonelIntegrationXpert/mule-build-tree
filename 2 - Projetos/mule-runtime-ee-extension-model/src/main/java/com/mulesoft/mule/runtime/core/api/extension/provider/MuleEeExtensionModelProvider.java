/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.annotation.NoInstantiate
 *  org.mule.runtime.api.dsl.DslResolvingContext
 *  org.mule.runtime.api.meta.model.ExtensionModel
 *  org.mule.runtime.api.util.LazyValue
 *  org.mule.runtime.extension.api.loader.ExtensionLoadingContext
 *  org.mule.runtime.extension.api.loader.ExtensionModelLoader
 *  org.mule.runtime.extension.api.loader.ExtensionModelLoadingRequest
 *  org.mule.runtime.extension.api.metadata.ComponentMetadataConfigurerFactory
 */
package com.mulesoft.mule.runtime.core.api.extension.provider;

import com.mulesoft.mule.runtime.core.api.extension.provider.MuleEeExtensionModelDeclarer;
import org.mule.api.annotation.NoInstantiate;
import org.mule.runtime.api.dsl.DslResolvingContext;
import org.mule.runtime.api.meta.model.ExtensionModel;
import org.mule.runtime.api.util.LazyValue;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionModelLoader;
import org.mule.runtime.extension.api.loader.ExtensionModelLoadingRequest;
import org.mule.runtime.extension.api.metadata.ComponentMetadataConfigurerFactory;

@NoInstantiate
public final class MuleEeExtensionModelProvider
extends ExtensionModelLoader {
    public static final String TRANSFORM_ELEMENT_NAME = "transform";
    private static ComponentMetadataConfigurerFactory configurerFactory = ComponentMetadataConfigurerFactory.getDefault();
    private static final LazyValue<ExtensionModel> EXTENSION_MODEL = new LazyValue(() -> new MuleEeExtensionModelProvider().loadExtensionModel(new MuleEeExtensionModelDeclarer(configurerFactory).createExtensionModel(), ExtensionModelLoadingRequest.builder((ClassLoader)MuleEeExtensionModelProvider.class.getClassLoader(), (DslResolvingContext)DslResolvingContext.nullDslResolvingContext()).build()));

    protected void declareExtension(ExtensionLoadingContext context) {
    }

    public String getId() {
        return "EE";
    }

    public static ExtensionModel getExtensionModel() {
        return (ExtensionModel)EXTENSION_MODEL.get();
    }

    public static void setConfigurerFactory(ComponentMetadataConfigurerFactory componentMetadataConfigurerFactory) {
        configurerFactory = componentMetadataConfigurerFactory;
    }
}
