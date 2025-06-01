/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.api.util.MultiMap
 *  org.mule.runtime.http.api.domain.CaseInsensitiveMultiMap
 */
package com.mulesoft.extension.policies.attributes.handler;

import com.mulesoft.extension.policies.attributes.handler.LimitedHttpHeadersMultimapFactory;
import java.util.Collection;
import java.util.List;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.http.api.domain.CaseInsensitiveMultiMap;

public abstract class HttpAttributesHandler<T> {
    private final LimitedHttpHeadersMultimapFactory limitedHeaderFactory = new LimitedHttpHeadersMultimapFactory();

    public abstract Object addResponseHeaders(T var1, MultiMap<String, String> var2);

    public abstract Object addRequestHeaders(T var1, MultiMap<String, String> var2);

    public abstract Object removeHeaders(T var1, List<String> var2);

    protected MultiMap<String, String> mergeHeaders(MultiMap<String, String> originalHeaders, MultiMap<String, String> headersToAdd) {
        return this.limitedHeaderFactory.merge(originalHeaders, headersToAdd);
    }

    protected MultiMap<String, String> enforceHeaders(MultiMap<String, String> headers) {
        return this.limitedHeaderFactory.create(headers);
    }

    protected MultiMap<String, String> removeHeaders(MultiMap<String, String> headers, List<String> headersToRemove) {
        CaseInsensitiveMultiMap filteredHeaders = new CaseInsensitiveMultiMap(false);
        headers.keySet().stream().filter(headerName -> {
            if (headersToRemove.contains(headerName.toLowerCase())) return false;
            if (!headersToRemove.stream().noneMatch(headerName::matches)) return false;
            return true;
        }).forEach(arg_0 -> HttpAttributesHandler.lambda$removeHeaders$1((MultiMap)filteredHeaders, headers, arg_0));
        return filteredHeaders;
    }

    private static /* synthetic */ void lambda$removeHeaders$1(MultiMap filteredHeaders, MultiMap headers, String headerName) {
        filteredHeaders.put((Object)headerName, (Collection)headers.getAll((Object)headerName));
    }
}
