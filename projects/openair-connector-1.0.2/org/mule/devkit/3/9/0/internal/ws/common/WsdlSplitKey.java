/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package org.mule.devkit.3.9.0.internal.ws.common;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.mule.devkit.3.9.0.api.metadata.exception.InvalidKeyException;
import org.mule.devkit.3.9.0.internal.ws.common.WsdlAdapter;

public class WsdlSplitKey {
    private static final String ID = "id";
    private static final String OPERATION = "operation";
    private final String id;
    private final String operation;

    public WsdlSplitKey(String type, WsdlAdapter wsdlAdapter) throws Exception {
        if (StringUtils.isBlank((String)type)) {
            throw new InvalidKeyException("Key is empty");
        }
        if (wsdlAdapter.singleServiceDefinitionId().isPresent()) {
            this.id = (String)wsdlAdapter.singleServiceDefinitionId().get();
            this.operation = type;
        } else {
            Map<String, String> idOp = this.splitIdOperationWithSeparator(type, wsdlAdapter.wsdlSeparator());
            this.id = idOp.get(ID);
            this.operation = idOp.get(OPERATION);
        }
    }

    public String id() {
        return this.id;
    }

    public String operation() {
        return this.operation;
    }

    private Map<String, String> splitIdOperationWithSeparator(String type, String separator) {
        if (StringUtils.isBlank((String)separator)) {
            throw new InvalidKeyException("Separator is empty");
        }
        if (!StringUtils.contains((String)type, (String)separator)) {
            throw new InvalidKeyException(String.format("Key %s does not contains the expected service-operation separator %s", type, separator));
        }
        if (StringUtils.countMatches((String)type, (String)separator) != 1) {
            throw new InvalidKeyException(String.format("Key %s contains too many service-operation separators %s, only one keySeparator occurrence is expected.", type, separator));
        }
        String escapedSeparator = Pattern.quote(separator);
        String[] splitKey = type.split(escapedSeparator);
        if (splitKey.length != 2) {
            throw new InvalidKeyException(String.format("Key %s is not valid, two non-empty parts must be separated with %s", type, separator));
        }
        if (StringUtils.isBlank((String)splitKey[0]) || StringUtils.isBlank((String)splitKey[1])) {
            throw new InvalidKeyException(String.format("Key %s is not valid, two non-empty parts must be present", type));
        }
        HashMap<String, String> result = new HashMap<String, String>();
        result.put(ID, splitKey[0]);
        result.put(OPERATION, splitKey[1]);
        return result;
    }
}
