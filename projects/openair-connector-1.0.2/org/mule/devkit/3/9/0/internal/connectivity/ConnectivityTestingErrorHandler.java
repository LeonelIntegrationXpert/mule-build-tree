/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.ConnectionException
 *  org.mule.api.ConnectionExceptionCode
 *  org.mule.common.DefaultResult
 *  org.mule.common.DefaultTestResult
 *  org.mule.common.FailureType
 *  org.mule.common.Result$Status
 *  org.mule.devkit.3.9.0.internal.connectivity.ConnectivityTestingErrorHandler$1
 */
package org.mule.devkit.3.9.0.internal.connectivity;

import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.common.DefaultResult;
import org.mule.common.DefaultTestResult;
import org.mule.common.FailureType;
import org.mule.common.Result;
import org.mule.devkit.3.9.0.internal.connectivity.ConnectivityTestingErrorHandler;

public class ConnectivityTestingErrorHandler {
    public static DefaultResult buildFailureTestResult(Exception exception) {
        ConnectionExceptionCode code;
        FailureType failureType = FailureType.UNSPECIFIED;
        if (exception instanceof ConnectionException && (code = ((ConnectionException)exception).getCode()) != null) {
            switch (1.$SwitchMap$org$mule$api$ConnectionExceptionCode[code.ordinal()]) {
                case 1: {
                    failureType = FailureType.UNKNOWN_HOST;
                    break;
                }
                case 2: {
                    failureType = FailureType.RESOURCE_UNAVAILABLE;
                    break;
                }
                case 3: {
                    failureType = FailureType.INVALID_CREDENTIALS;
                    break;
                }
                case 4: {
                    failureType = FailureType.INVALID_CREDENTIALS;
                    break;
                }
                default: {
                    failureType = FailureType.UNSPECIFIED;
                }
            }
        }
        return new DefaultTestResult(Result.Status.FAILURE, exception.getMessage(), failureType, (Throwable)exception);
    }

    public static DefaultResult buildWarningTestResult(Exception exception) {
        return new DefaultTestResult(Result.Status.SUCCESS, exception.getMessage(), FailureType.INVALID_CONFIGURATION, (Throwable)exception);
    }
}
