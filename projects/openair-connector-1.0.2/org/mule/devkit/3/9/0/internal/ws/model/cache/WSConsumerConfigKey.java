/*
 * Decompiled with CFR 0.152.
 */
package org.mule.devkit.3.9.0.internal.ws.model.cache;

public class WSConsumerConfigKey {
    private String wsdlId;
    private String serviceAddress;
    private String service;
    private String port;

    public WSConsumerConfigKey(String wsdlId, String serviceAddress, String service, String port) {
        this.wsdlId = wsdlId;
        this.serviceAddress = serviceAddress;
        this.service = service;
        this.port = port;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WSConsumerConfigKey)) {
            return false;
        }
        WSConsumerConfigKey that = (WSConsumerConfigKey)o;
        if (this.port != null ? !this.port.equals(that.port) : that.port != null) {
            return false;
        }
        if (this.service != null ? !this.service.equals(that.service) : that.service != null) {
            return false;
        }
        if (this.serviceAddress != null ? !this.serviceAddress.equals(that.serviceAddress) : that.serviceAddress != null) {
            return false;
        }
        return !(this.wsdlId != null ? !this.wsdlId.equals(that.wsdlId) : that.wsdlId != null);
    }

    public int hashCode() {
        int result = this.wsdlId != null ? this.wsdlId.hashCode() : 0;
        result = 31 * result + (this.serviceAddress != null ? this.serviceAddress.hashCode() : 0);
        result = 31 * result + (this.service != null ? this.service.hashCode() : 0);
        result = 31 * result + (this.port != null ? this.port.hashCode() : 0);
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder().append("WSDL id: ").append(this.wsdlId).append(", Service Address: ").append(this.serviceAddress).append(", Service: ").append(this.service).append(", Port: ").append(this.port);
        return sb.toString();
    }

    public String getWsdlId() {
        return this.wsdlId;
    }

    public String getServiceAddress() {
        return this.serviceAddress;
    }

    public String getService() {
        return this.service;
    }

    public String getPort() {
        return this.port;
    }
}
