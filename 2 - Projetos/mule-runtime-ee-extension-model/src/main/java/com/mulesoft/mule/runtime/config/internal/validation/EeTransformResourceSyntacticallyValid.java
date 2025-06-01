/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.mule.runtime.api.component.ComponentIdentifier
 *  org.mule.runtime.api.el.ExpressionLanguage
 *  org.mule.runtime.api.el.validation.Severity
 *  org.mule.runtime.ast.api.ComponentAst
 *  org.mule.runtime.ast.api.ComponentParameterAst
 *  org.mule.runtime.ast.api.util.ComponentAstPredicatesFactory
 *  org.mule.runtime.ast.api.validation.Validation
 *  org.mule.runtime.ast.api.validation.Validation$Level
 *  org.mule.runtime.ast.api.validation.ValidationResultItem
 *  org.mule.runtime.config.internal.dsl.utils.DslConstants
 *  org.mule.runtime.config.internal.validation.AbstractExpressionSyntacticallyValid
 */
package com.mulesoft.mule.runtime.config.internal.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.mule.runtime.api.component.ComponentIdentifier;
import org.mule.runtime.api.el.ExpressionLanguage;
import org.mule.runtime.api.el.validation.Severity;
import org.mule.runtime.ast.api.ComponentAst;
import org.mule.runtime.ast.api.ComponentParameterAst;
import org.mule.runtime.ast.api.util.ComponentAstPredicatesFactory;
import org.mule.runtime.ast.api.validation.Validation;
import org.mule.runtime.ast.api.validation.ValidationResultItem;
import org.mule.runtime.config.internal.dsl.utils.DslConstants;
import org.mule.runtime.config.internal.validation.AbstractExpressionSyntacticallyValid;

public abstract class EeTransformResourceSyntacticallyValid
extends AbstractExpressionSyntacticallyValid {
    private static final ComponentIdentifier EE_TRANSFORM_IDENTIFIER = ComponentIdentifier.builder().namespace("ee").namespaceUri(DslConstants.EE_NAMESPACE).name("transform").build();
    private final ClassLoader artifactRegionClassLoader;

    public EeTransformResourceSyntacticallyValid(ClassLoader artifactRegionClassLoader, ExpressionLanguage expressionLanguage, Supplier<Validation.Level> level, Severity severity) {
        super(expressionLanguage, level, severity);
        this.artifactRegionClassLoader = artifactRegionClassLoader;
    }

    public final ClassLoader getArtifactRegionClassLoader() {
        return this.artifactRegionClassLoader;
    }

    public final String getName() {
        return "Expression in DataWeave script files are syntactically valid";
    }

    public final String getDescription() {
        return "Expression in DataWeave script files are syntactically valid";
    }

    public final Predicate<List<ComponentAst>> applicable() {
        return ComponentAstPredicatesFactory.currentElemement(comp -> comp.getIdentifier().equals(EE_TRANSFORM_IDENTIFIER) && this.applicableInner((ComponentAst)comp));
    }

    protected abstract boolean applicableInner(ComponentAst var1);

    protected final List<ValidationResultItem> validateResourceExpressionValid(ComponentAst component, ComponentParameterAst resourceParam, String resource) {
        try {
            InputStream resourceAsStream = this.getArtifactRegionClassLoader().getResourceAsStream(resource);
            if (resourceAsStream != null) {
                return this.validateExpression(component, resourceParam, IOUtils.toString((InputStream)resourceAsStream, (Charset)StandardCharsets.UTF_8)).collect(Collectors.toList());
            }
            FileInputStream fileInputStream = new FileInputStream(new File(resource));
            return this.validateExpression(component, resourceParam, IOUtils.toString((InputStream)fileInputStream, (Charset)StandardCharsets.UTF_8)).collect(Collectors.toList());
        }
        catch (IOException e) {
            return Collections.singletonList(ValidationResultItem.create((ComponentAst)component, (ComponentParameterAst)resourceParam, (Validation)this, (String)("Error loading referenced file for parameter 'resource': " + resource + ": " + e.getClass().getName() + ": " + e.getMessage())));
        }
    }
}
