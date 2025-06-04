/*
 * Decompiled with CFR 0.152.
 */
package org.mule.devkit.3.9.0.api.ws.authentication;

import org.mule.devkit.3.9.0.api.ws.authentication.WsdlSecurityStrategy;

public class WsdlTimestamp
implements WsdlSecurityStrategy {
    private long expires;

    public WsdlTimestamp(long expires) {
        this.expires = expires;
    }

    public long getExpires() {
        return this.expires;
    }
}
