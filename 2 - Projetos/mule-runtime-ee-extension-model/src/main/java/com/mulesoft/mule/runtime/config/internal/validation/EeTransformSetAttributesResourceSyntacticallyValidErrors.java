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
import java.util.List;
import java.util.function.Supplier;
import org.mule.runtime.api.el.ExpressionLanguage;
import org.mule.runtime.api.el.validation.Severity;
import org.mule.runtime.ast.api.ArtifactAst;
import org.mule.runtime.ast.api.ComponentAst;
import org.mule.runtime.ast.api.ComponentParameterAst;
import org.mule.runtime.ast.api.validation.Validation;
import org.mule.runtime.ast.api.validation.ValidationResultItem;

public class EeTransformSetAttributesResourceSyntacticallyValidErrors
extends EeTransformResourceSyntacticallyValid {
    public EeTransformSetAttributesResourceSyntacticallyValidErrors(ClassLoader artifactRegionClassLoader, ExpressionLanguage expressionLanguage, Supplier<Validation.Level> level, Severity severity) {
        super(artifactRegionClassLoader, expressionLanguage, level, severity);
    }

    @Override
    protected boolean applicableInner(ComponentAst component) {
        ComponentAst setAttributes = (ComponentAst)component.getParameter("Message", "setAttributes").getValue().getRight();
        return setAttributes != null && setAttributes.getParameter("SetAttributes", "resource").getValue().getRight() != null;
    }

    public List<ValidationResultItem> validateMany(ComponentAst component, ArtifactAst artifact) {
        ComponentAst setPayload = (ComponentAst)component.getParameter("Message", "setAttributes").getValue().getRight();
        ComponentParameterAst resourceParam = setPayload.getParameter("SetAttributes", "resource");
        String resource = (String)resourceParam.getValue().getRight();
        return this.validateResourceExpressionValid(component, resourceParam, resource);
    }
}
