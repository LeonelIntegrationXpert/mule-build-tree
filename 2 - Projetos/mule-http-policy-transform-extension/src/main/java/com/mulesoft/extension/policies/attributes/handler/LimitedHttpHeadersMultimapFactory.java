/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.api.util.MultiMap
 *  org.mule.runtime.http.api.domain.CaseInsensitiveMultiMap
 */
package com.mulesoft.extension.policies.attributes.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.http.api.domain.CaseInsensitiveMultiMap;

public class LimitedHttpHeadersMultimapFactory {
    private static final Set<String> LIMITED_HEADERS = new HashSet<String>(Arrays.asList("transfer-encoding", "content-length", "content-type", "access-control-allow-origin"));

    public MultiMap<String, String> merge(MultiMap<String, String> originalHeaders, MultiMap<String, String> newHeaders) {
        CaseInsensitiveMultiMap mergedMap = new CaseInsensitiveMultiMap(originalHeaders, false);
        newHeaders.keySet().forEach(arg_0 -> LimitedHttpHeadersMultimapFactory.lambda$merge$0((MultiMap)mergedMap, newHeaders, arg_0));
        return mergedMap;
    }

    public MultiMap<String, String> create(MultiMap<String, String> headers) {
        CaseInsensitiveMultiMap cleanMap = new CaseInsensitiveMultiMap(false);
        headers.keySet().forEach(arg_0 -> LimitedHttpHeadersMultimapFactory.lambda$create$1((MultiMap)cleanMap, headers, arg_0));
        return cleanMap;
    }

    private static /* synthetic */ void lambda$create$1(MultiMap cleanMap, MultiMap headers, String key) {
        if (LIMITED_HEADERS.contains(key)) {
            cleanMap.put((Object)key, headers.get((Object)key));
        } else {
            cleanMap.put((Object)key, (Collection)headers.getAll((Object)key));
        }
    }

    private static /* synthetic */ void lambda$merge$0(MultiMap mergedMap, MultiMap newHeaders, String key) {
        if (LIMITED_HEADERS.contains(key)) {
            mergedMap.remove((Object)key);
            mergedMap.put((Object)key, newHeaders.get((Object)key));
        } else {
            mergedMap.put((Object)key, (Collection)newHeaders.getAll((Object)key));
        }
    }
}
