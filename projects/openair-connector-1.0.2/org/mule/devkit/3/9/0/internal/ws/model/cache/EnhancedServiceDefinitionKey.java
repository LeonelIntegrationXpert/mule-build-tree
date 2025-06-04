/*
 * Decompiled with CFR 0.152.
 */
package org.mule.devkit.3.9.0.internal.ws.model.cache;

public class EnhancedServiceDefinitionKey {
    private String wsdlId;
    private String operation;

    public EnhancedServiceDefinitionKey(String wsdlId, String operation) {
        this.wsdlId = wsdlId;
        this.operation = operation;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnhancedServiceDefinitionKey)) {
            return false;
        }
        EnhancedServiceDefinitionKey that = (EnhancedServiceDefinitionKey)o;
        if (this.operation != null ? !this.operation.equals(that.operation) : that.operation != null) {
            return false;
        }
        return !(this.wsdlId != null ? !this.wsdlId.equals(that.wsdlId) : that.wsdlId != null);
    }

    public int hashCode() {
        int result = this.wsdlId != null ? this.wsdlId.hashCode() : 0;
        result = 31 * result + (this.operation != null ? this.operation.hashCode() : 0);
        return result;
    }
}
