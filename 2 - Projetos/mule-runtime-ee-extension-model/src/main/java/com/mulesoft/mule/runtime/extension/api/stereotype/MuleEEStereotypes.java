/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.api.meta.model.stereotype.StereotypeModel
 *  org.mule.runtime.api.meta.model.stereotype.StereotypeModelBuilder
 *  org.mule.runtime.extension.api.stereotype.MuleStereotypes
 *  org.mule.runtime.extension.api.stereotype.StereotypeDefinition
 */
package com.mulesoft.mule.runtime.extension.api.stereotype;

import com.mulesoft.mule.runtime.extension.api.stereotype.CachingKeyGeneratorStereotype;
import com.mulesoft.mule.runtime.extension.api.stereotype.CachingResponseGeneratorStereotype;
import com.mulesoft.mule.runtime.extension.api.stereotype.CachingStrategyStereotype;
import com.mulesoft.mule.runtime.extension.api.stereotype.InvalidatableCachingStrategyStereotype;
import com.mulesoft.mule.runtime.extension.api.stereotype.TransactionManagerStereotype;
import org.mule.runtime.api.meta.model.stereotype.StereotypeModel;
import org.mule.runtime.api.meta.model.stereotype.StereotypeModelBuilder;
import org.mule.runtime.extension.api.stereotype.MuleStereotypes;
import org.mule.runtime.extension.api.stereotype.StereotypeDefinition;

public class MuleEEStereotypes {
    private static final String CORE_PREFIX = "mule";
    private static final String STEREOTYPE_NAMESPACE = "mule".toUpperCase();
    public static final StereotypeDefinition CACHING_STRATEGY_DEFINITION = new CachingStrategyStereotype();
    public static final StereotypeDefinition INVALIDATABLE_CACHING_STRATEGY_DEFINITION = new InvalidatableCachingStrategyStereotype();
    public static final StereotypeDefinition CACHING_KEY_GENERATOR_DEFINITION = new CachingKeyGeneratorStereotype();
    public static final StereotypeDefinition CACHING_RESPONSE_GENERATOR_DEFINITION = new CachingResponseGeneratorStereotype();
    public static final StereotypeDefinition TX_MANAGER_DEFINITION = new TransactionManagerStereotype();
    public static final StereotypeModel CACHING_STRATEGY = StereotypeModelBuilder.newStereotype((String)CACHING_STRATEGY_DEFINITION.getName(), (String)STEREOTYPE_NAMESPACE).withParent(MuleStereotypes.CONFIG).build();
    public static final StereotypeModel INVALIDATABLE_CACHING_STRATEGY = StereotypeModelBuilder.newStereotype((String)INVALIDATABLE_CACHING_STRATEGY_DEFINITION.getName(), (String)STEREOTYPE_NAMESPACE).withParent(CACHING_STRATEGY).build();
    public static final StereotypeModel CACHING_KEY_GENERATOR = StereotypeModelBuilder.newStereotype((String)CACHING_KEY_GENERATOR_DEFINITION.getName(), (String)STEREOTYPE_NAMESPACE).build();
    public static final StereotypeModel CACHING_RESPONSE_GENERATOR = StereotypeModelBuilder.newStereotype((String)CACHING_RESPONSE_GENERATOR_DEFINITION.getName(), (String)STEREOTYPE_NAMESPACE).build();
    public static final StereotypeModel TX_MANAGER = StereotypeModelBuilder.newStereotype((String)TX_MANAGER_DEFINITION.getName(), (String)STEREOTYPE_NAMESPACE).withParent(MuleStereotypes.APP_CONFIG).build();
}
