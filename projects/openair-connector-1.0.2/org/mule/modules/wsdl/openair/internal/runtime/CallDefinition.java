/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.mule.modules.wsdl.openair.internal.runtime;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mule.modules.wsdl.openair.internal.runtime.SoapCallException;
import org.mule.modules.wsdl.openair.internal.runtime.SoapClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallDefinition {
    private static final Logger logger = LoggerFactory.getLogger(SoapClient.class);
    public static final String SEPARATOR = "||";
    @NotNull
    private final String endpointPath;
    @NotNull
    private final String operationName;

    public CallDefinition(@NotNull String endpointPath, @NotNull String operationName) {
        this.endpointPath = endpointPath;
        this.operationName = operationName;
    }

    @NotNull
    public static CallDefinition parseDatasenseKey(@NotNull String datasenseKey) throws SoapCallException {
        logger.debug("Parsing datasense key:" + datasenseKey);
        if (StringUtils.isEmpty((String)datasenseKey) || !datasenseKey.contains(SEPARATOR)) {
            throw SoapCallException.createMetadataInvocationException(datasenseKey);
        }
        String[] split = StringUtils.split((String)datasenseKey, (String)SEPARATOR);
        String endpointPath = split[0];
        String operationName = split[1];
        if (StringUtils.isEmpty((String)endpointPath)) {
            throw SoapCallException.createMetadataInvocationException(datasenseKey);
        }
        if (StringUtils.isEmpty((String)operationName)) {
            throw SoapCallException.createMetadataInvocationException(datasenseKey);
        }
        return new CallDefinition(endpointPath, operationName);
    }

    @NotNull
    public String asString() {
        return StringUtils.join((Object[])new Object[]{this.endpointPath, this.operationName}, (String)SEPARATOR);
    }

    @NotNull
    public String getEndpointPath() {
        return this.endpointPath;
    }

    @NotNull
    public String getOperationName() {
        return this.operationName;
    }

    public String toString() {
        return "CallDefinition{endpointPath='" + this.endpointPath + '\'' + ", operationName='" + this.operationName + '\'' + '}';
    }
}
