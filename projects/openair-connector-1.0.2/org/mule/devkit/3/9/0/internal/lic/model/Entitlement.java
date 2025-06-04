/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package org.mule.devkit.3.9.0.internal.lic.model;

import org.apache.commons.lang.StringUtils;

public class Entitlement {
    public static final String DEFAULT_PROVIDER = "MuleSoft";
    private final String id;
    private final String description;
    private String provider = "MuleSoft";
    private String licName = "";
    private String version = "";

    public Entitlement(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public Entitlement(String id, String description, String provider, String licName, String version) {
        this.id = id;
        this.description = description;
        this.provider = provider;
        this.licName = licName;
        this.version = version;
    }

    public boolean isThirdParty() {
        return !StringUtils.equals((String)this.provider, (String)DEFAULT_PROVIDER);
    }

    public String id() {
        return this.id;
    }

    public String description() {
        return this.description;
    }

    public String provider() {
        return this.provider;
    }

    public String licenseName() {
        return this.licName;
    }

    public String version() {
        return this.version;
    }
}
