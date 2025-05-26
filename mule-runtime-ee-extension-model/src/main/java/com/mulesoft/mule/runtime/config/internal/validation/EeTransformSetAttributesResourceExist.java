/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.ast.api.ArtifactAst
 *  org.mule.runtime.ast.api.ComponentAst
 *  org.mule.runtime.ast.api.ComponentParameterAst
 *  org.mule.runtime.ast.api.validation.ValidationResultItem
 */
package com.mulesoft.mule.runtime.config.internal.validation;

import com.mulesoft.mule.runtime.config.internal.validation.AbstractEeTransformResourceExists;
import java.util.Optional;
import org.mule.runtime.ast.api.ArtifactAst;
import org.mule.runtime.ast.api.ComponentAst;
import org.mule.runtime.ast.api.ComponentParameterAst;
import org.mule.runtime.ast.api.validation.ValidationResultItem;

public class EeTransformSetAttributesResourceExist
extends AbstractEeTransformResourceExists {
    public EeTransformSetAttributesResourceExist(ClassLoader artifactRegionClassLoader) {
        super(artifactRegionClassLoader);
    }

    @Override
    protected boolean applicableInner(ComponentAst component) {
        ComponentAst setAttributes = (ComponentAst)component.getParameter("Message", "setAttributes").getValue().getRight();
        return setAttributes != null && setAttributes.getParameter("SetAttributes", "resource").getValue().getRight() != null;
    }

    public Optional<ValidationResultItem> validate(ComponentAst component, ArtifactAst artifact) {
        ComponentAst setAttributes = (ComponentAst)component.getParameter("Message", "setAttributes").getValue().getRight();
        ComponentParameterAst resourceParam = setAttributes.getParameter("SetAttributes", "resource");
        String resource = (String)resourceParam.getValue().getRight();
        return this.validateResourceExists(setAttributes, resourceParam, resource);
    }
}
