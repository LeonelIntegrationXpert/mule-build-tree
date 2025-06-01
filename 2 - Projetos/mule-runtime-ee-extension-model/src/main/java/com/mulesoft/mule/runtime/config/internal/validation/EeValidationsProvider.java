package com.mulesoft.mule.runtime.config.internal.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

import org.mule.runtime.api.config.FeatureFlaggingService;
import org.mule.runtime.api.el.ExpressionLanguage;
import org.mule.runtime.api.el.validation.Severity;
import org.mule.runtime.ast.api.validation.Validation;
import org.mule.runtime.ast.api.validation.ValidationsProvider;
import org.mule.runtime.config.internal.validation.CoreValidationsProvider;

public class EeValidationsProvider implements ValidationsProvider {

    private ClassLoader artifactRegionClassLoader;

    @Inject
    private final Optional<FeatureFlaggingService> featureFlaggingService = Optional.empty();

    @Inject
    private ExpressionLanguage expressionLanguage;

    @Override
    public List<Validation> get() {
        List<Validation> validations = new ArrayList<>();

        validations.add(new EeTransformSetPayloadResourceExist(this.artifactRegionClassLoader));
        validations.add(new EeTransformSetAttributesResourceExist(this.artifactRegionClassLoader));
        validations.add(new EeTransformSetVariablesResourceExist(this.artifactRegionClassLoader));

        if (this.expressionLanguage != null) {
            validations.add(new EeTransformSetPayloadResourceSyntacticallyValidErrors(
                    this.artifactRegionClassLoader,
                    this.expressionLanguage,
                    () -> CoreValidationsProvider.getExpressionSyntacticValidationErrorLevel(this.featureFlaggingService),
                    Severity.ERROR
            ));
            validations.add(new EeTransformSetPayloadResourceSyntacticallyValidErrors(
                    this.artifactRegionClassLoader,
                    this.expressionLanguage,
                    () -> Validation.Level.WARN,
                    Severity.WARNING
            ));
            validations.add(new EeTransformSetAttributesResourceSyntacticallyValidErrors(
                    this.artifactRegionClassLoader,
                    this.expressionLanguage,
                    () -> CoreValidationsProvider.getExpressionSyntacticValidationErrorLevel(this.featureFlaggingService),
                    Severity.ERROR
            ));
            validations.add(new EeTransformSetAttributesResourceSyntacticallyValidErrors(
                    this.artifactRegionClassLoader,
                    this.expressionLanguage,
                    () -> Validation.Level.WARN,
                    Severity.WARNING
            ));
            validations.add(new EeTransformSetVariablesResourceSyntacticallyValidErrors(
                    this.artifactRegionClassLoader,
                    this.expressionLanguage,
                    () -> CoreValidationsProvider.getExpressionSyntacticValidationErrorLevel(this.featureFlaggingService),
                    Severity.ERROR
            ));
            validations.add(new EeTransformSetVariablesResourceSyntacticallyValidErrors(
                    this.artifactRegionClassLoader,
                    this.expressionLanguage,
                    () -> Validation.Level.WARN,
                    Severity.WARNING
            ));
        }

        return validations;
    }

    public void setArtifactRegionClassLoader(ClassLoader artifactRegionClassLoader) {
        this.artifactRegionClassLoader = artifactRegionClassLoader;
    }
}
