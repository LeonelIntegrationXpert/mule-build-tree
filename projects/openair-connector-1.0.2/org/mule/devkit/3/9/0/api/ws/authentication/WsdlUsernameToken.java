/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.module.ws.security.PasswordType
 */
package org.mule.devkit.3.9.0.api.ws.authentication;

import org.mule.devkit.3.9.0.api.ws.authentication.WsdlSecurityStrategy;
import org.mule.module.ws.security.PasswordType;

public class WsdlUsernameToken
implements WsdlSecurityStrategy {
    private String username;
    private String password;
    private PasswordType passwordType;
    private boolean addNonce;
    private boolean addCreated;

    public WsdlUsernameToken(String username, String password, PasswordType passwordType, boolean addNonce, boolean addCreated) {
        this.username = username;
        this.password = password;
        this.passwordType = passwordType;
        this.addNonce = addNonce;
        this.addCreated = addCreated;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public PasswordType getPasswordType() {
        return this.passwordType;
    }

    public void setPasswordType(PasswordType passwordType) {
        this.passwordType = passwordType;
    }

    public boolean isAddNonce() {
        return this.addNonce;
    }

    public void setAddNonce(boolean addNonce) {
        this.addNonce = addNonce;
    }

    public boolean isAddCreated() {
        return this.addCreated;
    }

    public void setAddCreated(boolean addCreated) {
        this.addCreated = addCreated;
    }
}
