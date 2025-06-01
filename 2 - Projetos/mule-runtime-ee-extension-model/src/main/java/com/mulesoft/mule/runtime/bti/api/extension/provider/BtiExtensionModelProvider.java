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
 */
package com.mulesoft.mule.runtime.bti.api.extension.provider;

import com.mulesoft.mule.runtime.bti.api.extension.provider.BtiExtensionModelDeclarer;
import org.mule.api.annotation.NoInstantiate;
import org.mule.runtime.api.dsl.DslResolvingContext;
import org.mule.runtime.api.meta.model.ExtensionModel;
import org.mule.runtime.api.util.LazyValue;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionModelLoader;
import org.mule.runtime.extension.api.loader.ExtensionModelLoadingRequest;

@NoInstantiate
public class BtiExtensionModelProvider
extends ExtensionModelLoader {
    private static final LazyValue<ExtensionModel> EXTENSION_MODEL = new LazyValue(() -> new BtiExtensionModelProvider().loadExtensionModel(new BtiExtensionModelDeclarer().createExtensionModel(), ExtensionModelLoadingRequest.builder((ClassLoader)BtiExtensionModelProvider.class.getClassLoader(), (DslResolvingContext)DslResolvingContext.nullDslResolvingContext()).build()));

    protected void declareExtension(ExtensionLoadingContext context) {
    }

    public String getId() {
        return "bti";
    }

    public static ExtensionModel getExtensionModel() {
        return (ExtensionModel)EXTENSION_MODEL.get();
    }
}
