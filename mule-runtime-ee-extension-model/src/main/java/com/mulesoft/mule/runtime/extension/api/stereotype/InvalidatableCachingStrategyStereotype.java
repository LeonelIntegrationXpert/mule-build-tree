/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.extension.api.stereotype.MuleStereotypeDefinition
 *  org.mule.runtime.extension.api.stereotype.StereotypeDefinition
 */
package com.mulesoft.mule.runtime.extension.api.stereotype;

import com.mulesoft.mule.runtime.extension.api.stereotype.MuleEEStereotypes;
import java.util.Optional;
import org.mule.runtime.extension.api.stereotype.MuleStereotypeDefinition;
import org.mule.runtime.extension.api.stereotype.StereotypeDefinition;

public final class InvalidatableCachingStrategyStereotype
extends MuleStereotypeDefinition {
    public String getName() {
        return "INVALIDATABLE_CACHING_STRATEGY";
    }

    public Optional<StereotypeDefinition> getParent() {
        return Optional.of(MuleEEStereotypes.CACHING_STRATEGY_DEFINITION);
    }
}
