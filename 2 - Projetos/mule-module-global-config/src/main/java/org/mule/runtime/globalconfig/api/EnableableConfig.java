package org.mule.runtime.globalconfig.api;

import org.mule.api.annotation.NoImplement;

@NoImplement
public interface EnableableConfig {
    boolean isEnabled();
}
