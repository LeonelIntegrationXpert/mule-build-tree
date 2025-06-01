package com.mulesoft.extension.policies.attributes;

import com.mulesoft.extension.policies.attributes.HttpPolicyTransformErrorTypes;
import com.mulesoft.extension.policies.attributes.HttpTransformOperations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.sdk.api.annotation.JavaVersionSupport;
import org.mule.sdk.api.meta.JavaVersion;

@Extension(name = "http-policy-transform")
@Operations(value = { HttpTransformOperations.class })
@ErrorTypes(value = HttpPolicyTransformErrorTypes.class)
@JavaVersionSupport(value = { JavaVersion.JAVA_8, JavaVersion.JAVA_11, JavaVersion.JAVA_17 })
public class HttpPolicyTransformExtension {
}