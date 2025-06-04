/*
 * Decompiled with CFR 0.152.
 */
package org.mule.devkit.3.9.0.api.ws.transport;

import org.mule.devkit.3.9.0.api.ws.transport.WsdlTransport;

public class HttpBasicWsdlTransport
implements WsdlTransport {
    private String user;
    private String pass;
    private boolean preemptive;

    public HttpBasicWsdlTransport(String user, String pass) {
        this(user, pass, false);
    }

    public HttpBasicWsdlTransport(String user, String pass, boolean preemptive) {
        this.user = user;
        this.pass = pass;
        this.preemptive = preemptive;
    }

    public String getUser() {
        return this.user;
    }

    public String getPass() {
        return this.pass;
    }

    public boolean isPreemptive() {
        return this.preemptive;
    }
}
