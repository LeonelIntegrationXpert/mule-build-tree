package com.mulesoft.extension.policies.attributes;

import com.mulesoft.extension.policies.attributes.HttpPolicyTransformErrorTypeProvider;
import com.mulesoft.extension.policies.attributes.HttpPolicyTransformMetadataResolver;
import com.mulesoft.extension.policies.attributes.api.config.Header;
import com.mulesoft.extension.policies.attributes.handler.HttpAttributesHandlerSelector;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.mule.extension.http.api.HttpResponseAttributes;
import org.mule.extension.http.api.policy.HttpPolicyRequestAttributes;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.http.api.domain.CaseInsensitiveMultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpTransformOperations {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTransformOperations.class);
    private HttpAttributesHandlerSelector attributesHandlerFactory = new HttpAttributesHandlerSelector();

    @OutputResolver(output=HttpPolicyTransformMetadataResolver.class)
    @Throws(value={HttpPolicyTransformErrorTypeProvider.class})
    public Result<Object, Object> addResponseHeaders(@Content(primary=true) TypedValue<Object> payload, @Optional(defaultValue="#[attributes]") TypedValue<Object> attributes, @Content MultiMap<String, String> headers) {
        return this.addResponseHeadersFromMultimap(payload, attributes, headers);
    }

    @OutputResolver(output=HttpPolicyTransformMetadataResolver.class)
    @Throws(value={HttpPolicyTransformErrorTypeProvider.class})
    public Result<Object, Object> addResponseHeadersList(@Content(primary=true) TypedValue<Object> payload, @Optional(defaultValue="#[attributes]") TypedValue<Object> attributes, List<Header> newHeaders) {
        return this.addResponseHeadersFromMultimap(payload, attributes, this.headerListToMap(newHeaders));
    }

    @OutputResolver(output=HttpPolicyTransformMetadataResolver.class)
    @Throws(value={HttpPolicyTransformErrorTypeProvider.class})
    public Result<Object, Object> addRequestHeaders(@Content(primary=true) TypedValue<Object> payload, @Optional(defaultValue="#[attributes]") TypedValue<Object> attributes, @Content MultiMap<String, String> headers) {
        return this.addRequestHeadersFromMultimap(payload, attributes, headers);
    }

    @OutputResolver(output=HttpPolicyTransformMetadataResolver.class)
    @Throws(value={HttpPolicyTransformErrorTypeProvider.class})
    public Result<Object, Object> addRequestHeadersList(@Content(primary=true) TypedValue<Object> payload, @Optional(defaultValue="#[attributes]") TypedValue<Object> attributes, List<Header> newHeaders) {
        return this.addRequestHeadersFromMultimap(payload, attributes, this.headerListToMap(newHeaders));
    }

    @OutputResolver(output=HttpPolicyTransformMetadataResolver.class)
    @Throws(value={HttpPolicyTransformErrorTypeProvider.class})
    public Result<Object, Object> removeHeaders(@Content(primary=true) TypedValue<Object> payload, @Optional(defaultValue="#[attributes]") TypedValue<Object> attributes, @Content List<String> headerNames) {
        List lowerCaseHeaderNames = headerNames.stream().map(String::toLowerCase).collect(Collectors.toList());
        LOGGER.debug("Removing headers {} from attributes of type {}", headerNames);
        Object updatedAttributes = this.attributesHandlerFactory.get(attributes.getValue()).removeHeaders(attributes.getValue(), lowerCaseHeaderNames);
        return this.buildResult(payload, updatedAttributes);
    }

    @OutputResolver(output=HttpPolicyTransformMetadataResolver.class)
    public Result<Object, Object> setResponse(@Content(primary=true) TypedValue<Object> body, @Optional(defaultValue="200") int statusCode, @Optional String reasonPhrase, @Optional @Content MultiMap<String, String> headers) {
        LOGGER.debug("Setting response with status code {} and headers {}", (Object)statusCode, headers);
        HttpResponseAttributes newHttpAttributes = new HttpResponseAttributes(statusCode, reasonPhrase, (MultiMap)(headers == null ? MultiMap.emptyMultiMap() : new CaseInsensitiveMultiMap(headers, false)));
        return this.buildResult(body, newHttpAttributes);
    }

    @OutputResolver(output=HttpPolicyTransformMetadataResolver.class)
    public Result<Object, Object> setRequesterRequest(@Content(primary=true) TypedValue<Object> body, @Optional String requestPath, @Optional @Content MultiMap<String, String> headers, @Optional @Content Map<String, String> uriParams, @Optional @Content MultiMap<String, String> queryParams) {
        LOGGER.debug("Setting requester request with request path {}, headers {}, uri params {} and query params {}", new Object[]{requestPath, headers, uriParams, queryParams});
        HttpPolicyRequestAttributes newHttpAttributes = new HttpPolicyRequestAttributes((MultiMap)(headers == null ? MultiMap.emptyMultiMap() : new CaseInsensitiveMultiMap(headers, false)), queryParams == null ? MultiMap.emptyMultiMap() : queryParams, uriParams == null ? Collections.emptyMap() : uriParams, requestPath);
        return this.buildResult(body, newHttpAttributes);
    }

    private Result<Object, Object> addResponseHeadersFromMultimap(TypedValue<Object> payload, TypedValue<Object> attributes, MultiMap<String, String> headers) {
        if (headers == null) {
            LOGGER.debug("Headers parameter is null, no headers will be added.");
            headers = MultiMap.emptyMultiMap();
        }
        LOGGER.debug("Adding response headers {}, to attributes of type {}", (Object)headers, attributes.getValue());
        Object updatedAttributes = this.attributesHandlerFactory.get(attributes.getValue()).addResponseHeaders(attributes.getValue(), headers);
        return this.buildResult(payload, updatedAttributes);
    }

    private Result<Object, Object> addRequestHeadersFromMultimap(TypedValue<Object> payload, TypedValue<Object> attributes, MultiMap<String, String> headers) {
        if (headers == null) {
            LOGGER.debug("Headers parameter is null, no headers will be added.");
            headers = MultiMap.emptyMultiMap();
        }
        LOGGER.debug("Adding request headers {}, to attributes of type {}", (Object)headers, attributes.getValue());
        Object updatedAttributes = this.attributesHandlerFactory.get(attributes.getValue()).addRequestHeaders(attributes.getValue(), headers);
        return this.buildResult(payload, updatedAttributes);
    }

    private Result<Object, Object> buildResult(TypedValue<Object> payload, Object attributes) {
        Result.Builder builder = Result.builder().output(payload.getValue()).mediaType(payload.getDataType().getMediaType()).attributes(attributes);
        payload.getByteLength().ifPresent(arg_0 -> ((Result.Builder)builder).length(arg_0));
        return builder.build();
    }

    private MultiMap<String, String> headerListToMap(List<Header> newHeaders) {
        if (newHeaders == null || newHeaders.size() == 0) {
            LOGGER.debug("Headers parameter is null or empty, no headers will be added.");
            return MultiMap.emptyMultiMap();
        }
        CaseInsensitiveMultiMap map = new CaseInsensitiveMultiMap(false);
        for (Header header : newHeaders) {
            if (header.getHeaderName() != null) {
            	map.put(String.valueOf(header.getHeaderName()), String.valueOf(header.getHeaderValue()));
                continue;
            }
            LOGGER.warn("Received a null Header, it will be ignored.");
        }
        return map;
    }
}