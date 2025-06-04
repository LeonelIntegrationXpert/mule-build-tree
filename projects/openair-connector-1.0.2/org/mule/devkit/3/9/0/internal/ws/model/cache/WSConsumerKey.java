/*
 * Decompiled with CFR 0.152.
 */
package org.mule.devkit.3.9.0.internal.ws.model.cache;

import org.mule.devkit.3.9.0.internal.ws.model.cache.WSConsumerConfigKey;

public class WSConsumerKey {
    private WSConsumerConfigKey wsConsumerConfigKey;
    private String operation;

    public WSConsumerKey(WSConsumerConfigKey wsConsumerConfigKey, String operation) {
        this.wsConsumerConfigKey = wsConsumerConfigKey;
        this.operation = operation;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WSConsumerKey)) {
            return false;
        }
        WSConsumerKey that = (WSConsumerKey)o;
        if (this.operation != null ? !this.operation.equals(that.operation) : that.operation != null) {
            return false;
        }
        return !(this.wsConsumerConfigKey != null ? !this.wsConsumerConfigKey.equals(that.wsConsumerConfigKey) : that.wsConsumerConfigKey != null);
    }

    public int hashCode() {
        int result = this.wsConsumerConfigKey != null ? this.wsConsumerConfigKey.hashCode() : 0;
        result = 31 * result + (this.operation != null ? this.operation.hashCode() : 0);
        return result;
    }

    public WSConsumerConfigKey getWsConsumerConfigKey() {
        return this.wsConsumerConfigKey;
    }

    public String getOperation() {
        return this.operation;
    }
}
