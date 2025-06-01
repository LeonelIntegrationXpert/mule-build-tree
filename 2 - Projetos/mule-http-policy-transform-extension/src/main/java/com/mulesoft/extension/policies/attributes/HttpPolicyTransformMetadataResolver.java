package com.mulesoft.extension.policies.attributes;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.model.AnyType;
import org.mule.metadata.api.model.MetadataFormat;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.metadata.MetadataContext;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;

public class HttpPolicyTransformMetadataResolver implements OutputTypeResolver<Object> {
	private static final AnyType ANY_TYPE = BaseTypeBuilder.create((MetadataFormat) MetadataFormat.JAVA).anyType()
			.build();

	public String getCategoryName() {
		return "HttpPolicyTransform";
	}

	public MetadataType getOutputType(MetadataContext context, Object key) {
		return ANY_TYPE;
	}
}