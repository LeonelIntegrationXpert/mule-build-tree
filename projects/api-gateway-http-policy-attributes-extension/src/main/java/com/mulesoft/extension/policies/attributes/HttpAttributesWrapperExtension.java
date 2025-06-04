package com.mulesoft.extension.policies.attributes;

import com.mulesoft.extension.policies.attributes.api.HttpRequestAttributesWrapper;
import org.mule.runtime.extension.api.annotation.Export;
import org.mule.runtime.extension.api.annotation.Extension;

@Extension(name="http-attributes-wrapper-extension")
@Export(classes={HttpRequestAttributesWrapper.class})
public class HttpAttributesWrapperExtension {
}