/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.mule.runtime.api.el.ExpressionLanguage
 *  org.mule.runtime.api.el.validation.Severity
 *  org.mule.runtime.ast.api.ArtifactAst
 *  org.mule.runtime.ast.api.ComponentAst
 *  org.mule.runtime.ast.api.ComponentParameterAst
 *  org.mule.runtime.ast.api.validation.Validation$Level
 *  org.mule.runtime.ast.api.validation.ValidationResultItem
 */
package com.mulesoft.mule.runtime.config.internal.validation;

import com.mulesoft.mule.runtime.config.internal.validation.EeTransformResourceSyntacticallyValid;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.mule.runtime.api.el.ExpressionLanguage;
import org.mule.runtime.api.el.validation.Severity;
import org.mule.runtime.ast.api.ArtifactAst;
import org.mule.runtime.ast.api.ComponentAst;
import org.mule.runtime.ast.api.ComponentParameterAst;
import org.mule.runtime.ast.api.validation.Validation;
import org.mule.runtime.ast.api.validation.ValidationResultItem;

public class EeTransformSetVariablesResourceSyntacticallyValidErrors
extends EeTransformResourceSyntacticallyValid {
    public EeTransformSetVariablesResourceSyntacticallyValidErrors(ClassLoader artifactRegionClassLoader, ExpressionLanguage expressionLanguage, Supplier<Validation.Level> level, Severity severity) {
        super(artifactRegionClassLoader, expressionLanguage, level, severity);
    }

    @Override
    protected boolean applicableInner(ComponentAst component) {
        List<?> variables = (List<?>) component
                .getParameter("Set Variables", "variables")
                .getValue()
                .getRight();

        return variables != null && variables.stream().anyMatch(obj -> {
            if (!(obj instanceof ComponentAst)) return false;
            ComponentAst setVar = (ComponentAst) obj;
            ComponentParameterAst resourceParam = setVar.getParameter("SetVariable", "resource");
            return resourceParam != null && resourceParam.getValue().getRight() != null;
        });
    }

    public List<ValidationResultItem> validateMany(ComponentAst component, ArtifactAst artifact) {
        List<?> variables = (List<?>) component
                .getParameter("Set Variables", "variables")
                .getValue()
                .getRight();

        return variables.stream()
                .filter(obj -> {
                    if (!(obj instanceof ComponentAst)) return false;
                    ComponentAst setVar = (ComponentAst) obj;
                    ComponentParameterAst resourceParam = setVar.getParameter("SetVariable", "resource");
                    if (resourceParam == null) return false;
                    return resourceParam.getValue().getRight() != null;
                })
                .flatMap(obj -> {
                    ComponentAst setVar = (ComponentAst) obj;
                    ComponentParameterAst resourceParam = setVar.getParameter("SetVariable", "resource");
                    String resource = (String) resourceParam.getValue().getRight();
                    return this.validateResourceExpressionValid(component, resourceParam, resource).stream();
                })
                .collect(Collectors.toList());
    }
}
