/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.api.component.ComponentIdentifier
 *  org.mule.runtime.ast.api.ComponentAst
 *  org.mule.runtime.ast.api.ComponentParameterAst
 *  org.mule.runtime.ast.api.util.ComponentAstPredicatesFactory
 *  org.mule.runtime.ast.api.validation.Validation$Level
 *  org.mule.runtime.config.internal.dsl.utils.DslConstants
 *  org.mule.runtime.config.internal.validation.ResourceExistsAndAccessible
 */
package com.mulesoft.mule.runtime.config.internal.validation;

import java.util.List;
import java.util.function.Predicate;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.ast.api.ComponentAst;
import org.mule.runtime.ast.api.ComponentParameterAst;
import org.mule.runtime.ast.api.util.ComponentAstPredicatesFactory;
import org.mule.runtime.ast.api.validation.Validation;
import org.mule.runtime.config.internal.dsl.utils.DslConstants;
import org.mule.runtime.config.internal.validation.ResourceExistsAndAccessible;

public abstract class AbstractEeTransformResourceExists
extends ResourceExistsAndAccessible {
    private static final ComponentIdentifier EE_TRANSFORM_IDENTIFIER = ComponentIdentifier.builder().namespace("ee").namespaceUri(DslConstants.EE_NAMESPACE).name("transform").build();

    public AbstractEeTransformResourceExists(ClassLoader artifactRegionClassLoader) {
        super(artifactRegionClassLoader);
    }

    public final String getName() {
        return "'ee:transform' resources exist";
    }

    public final String getDescription() {
        return "DataWeave script files referenced in 'ee:transform' exist and are accessible.";
    }

    public final Validation.Level getLevel() {
        return Validation.Level.ERROR;
    }

    public final Predicate<List<ComponentAst>> applicable() {
        return ComponentAstPredicatesFactory.currentElemement(comp -> comp.getIdentifier().equals(EE_TRANSFORM_IDENTIFIER) && this.applicableInner((ComponentAst)comp) && comp.getParameters().stream().anyMatch(p -> this.fixedValuePresent((ComponentParameterAst)p) && this.resourceParameter((ComponentParameterAst)p)));
    }

    protected abstract boolean applicableInner(ComponentAst var1);
}
