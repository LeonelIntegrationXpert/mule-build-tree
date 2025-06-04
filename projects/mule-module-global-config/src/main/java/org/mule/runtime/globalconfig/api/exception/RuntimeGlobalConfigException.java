package org.mule.runtime.globalconfig.api.exception;

import java.io.IOException;

import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.api.i18n.I18nMessage;

public final class RuntimeGlobalConfigException
        extends MuleRuntimeException {
    public RuntimeGlobalConfigException(I18nMessage message) {
        super(message);
    }

    public RuntimeGlobalConfigException(Exception e) {
        super(e);
    }

    public RuntimeGlobalConfigException(I18nMessage message, IOException e) {
        super(message, e);
    }
}
