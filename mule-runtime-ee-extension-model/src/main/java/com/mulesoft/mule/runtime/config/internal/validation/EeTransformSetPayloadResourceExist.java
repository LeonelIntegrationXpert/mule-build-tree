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

public class EeTransformSetPayloadResourceExist
extends AbstractEeTransformResourceExists {
    public EeTransformSetPayloadResourceExist(ClassLoader artifactRegionClassLoader) {
        super(artifactRegionClassLoader);
    }

    @Override
    protected boolean applicableInner(ComponentAst component) {
        ComponentAst setPayload = (ComponentAst)component.getParameter("Message", "setPayload").getValue().getRight();
        return setPayload != null && setPayload.getParameter("SetPayload", "resource").getValue().getRight() != null;
    }

    public Optional<ValidationResultItem> validate(ComponentAst component, ArtifactAst artifact) {
        ComponentAst setPayload = (ComponentAst)component.getParameter("Message", "setPayload").getValue().getRight();
        ComponentParameterAst resourceParam = setPayload.getParameter("SetPayload", "resource");
        String resource = (String)resourceParam.getValue().getRight();
        return this.validateResourceExists(setPayload, resourceParam, resource);
    }
}
