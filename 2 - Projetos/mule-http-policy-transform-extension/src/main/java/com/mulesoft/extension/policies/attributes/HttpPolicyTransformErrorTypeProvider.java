package com.mulesoft.extension.policies.attributes;

import com.mulesoft.extension.policies.attributes.HttpPolicyTransformErrorTypes;
import java.util.HashSet;
import java.util.Set;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

public class HttpPolicyTransformErrorTypeProvider implements ErrorTypeProvider {
	public Set<ErrorTypeDefinition> getErrorTypes() {
		HashSet<ErrorTypeDefinition> errorTypes = new HashSet<ErrorTypeDefinition>();
		errorTypes.add((ErrorTypeDefinition) HttpPolicyTransformErrorTypes.INVALID_INPUT);
		return errorTypes;
	}
}