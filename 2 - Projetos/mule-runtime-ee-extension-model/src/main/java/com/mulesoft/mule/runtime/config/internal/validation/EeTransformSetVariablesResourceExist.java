package com.mulesoft.mule.runtime.config.internal.validation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mule.runtime.ast.api.ArtifactAst;
import org.mule.runtime.ast.api.ComponentAst;
import org.mule.runtime.ast.api.ComponentParameterAst;
import org.mule.runtime.ast.api.validation.ValidationResultItem;

public class EeTransformSetVariablesResourceExist extends AbstractEeTransformResourceExists {

    public EeTransformSetVariablesResourceExist(ClassLoader artifactRegionClassLoader) {
        super(artifactRegionClassLoader);
    }

    @Override
    protected boolean applicableInner(ComponentAst component) {
        List<?> variables = (List<?>) component.getParameter("Set Variables", "variables").getValue().getRight();

        return variables != null && variables.stream().anyMatch(obj -> {
            if (!(obj instanceof ComponentAst)) return false;
            ComponentAst setVar = (ComponentAst) obj;
            ComponentParameterAst resourceParam = setVar.getParameter("SetVariable", "resource");
            return resourceParam != null && resourceParam.getValue().getRight() != null;
        });
    }

    @Override
    public List<ValidationResultItem> validateMany(ComponentAst component, ArtifactAst artifact) {
        List<?> variables = (List<?>) component.getParameter("Set Variables", "variables").getValue().getRight();

        return variables.stream()
                .filter(obj -> {
                    if (!(obj instanceof ComponentAst)) return false;
                    ComponentAst setVar = (ComponentAst) obj;
                    ComponentParameterAst resourceParam = setVar.getParameter("SetVariable", "resource");
                    if (resourceParam == null) return false;
                    String resource = (String) resourceParam.getValue().getRight();
                    return resource != null;
                })
                .map(obj -> {
                    ComponentAst setVar = (ComponentAst) obj;
                    ComponentParameterAst resourceParam = setVar.getParameter("SetVariable", "resource");
                    String resource = (String) resourceParam.getValue().getRight();
                    return this.validateResourceExists(component, resourceParam, resource);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
