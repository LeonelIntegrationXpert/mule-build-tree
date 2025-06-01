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
package com.mulesoft.mule.runtime.tracking.api.extension.provider;

import com.mulesoft.mule.runtime.tracking.api.extension.provider.TrackingEeExtensionModelDeclarer;
import org.mule.api.annotation.NoInstantiate;
import org.mule.runtime.api.dsl.DslResolvingContext;
import org.mule.runtime.api.meta.model.ExtensionModel;
import org.mule.runtime.api.util.LazyValue;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.extension.api.loader.ExtensionModelLoader;
import org.mule.runtime.extension.api.loader.ExtensionModelLoadingRequest;

@NoInstantiate
public final class TrackingEeExtensionModelProvider
extends ExtensionModelLoader {
    private static final LazyValue<ExtensionModel> EXTENSION_MODEL = new LazyValue(() -> new TrackingEeExtensionModelProvider().loadExtensionModel(new TrackingEeExtensionModelDeclarer().createExtensionModel(), ExtensionModelLoadingRequest.builder((ClassLoader)TrackingEeExtensionModelProvider.class.getClassLoader(), (DslResolvingContext)DslResolvingContext.nullDslResolvingContext()).build()));

    protected void declareExtension(ExtensionLoadingContext context) {
    }

    public String getId() {
        return "tracking";
    }

    public static ExtensionModel getExtensionModel() {
        return (ExtensionModel)EXTENSION_MODEL.get();
    }
}
