/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package org.mule.devkit.3.9.0.internal.lic.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class Version {
    private static final Pattern versionPattern = Pattern.compile("^([0-9])\\.([0-9]|x)\\.([0-9]|x)\\-?(.*SNAPSHOT)?$");
    private Integer major = -1;
    private Integer minor = -1;
    private Integer bug = -1;
    private boolean isSnapshot = false;

    public Version(String version) {
        if (StringUtils.isBlank((String)version)) {
            return;
        }
        Matcher matcher = versionPattern.matcher(version);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(version + " is not a valid version format");
        }
        this.isSnapshot = matcher.groupCount() == 4;
        this.major = Integer.valueOf(matcher.group(1));
        if (!StringUtils.equalsIgnoreCase((String)matcher.group(2), (String)"x")) {
            this.minor = Integer.valueOf(matcher.group(2));
        }
        if (!StringUtils.equalsIgnoreCase((String)matcher.group(3), (String)"x")) {
            this.bug = Integer.valueOf(matcher.group(3));
        }
    }

    public Integer major() {
        return this.major;
    }

    public Integer minor() {
        return this.minor;
    }

    public Integer bug() {
        return this.bug;
    }

    public boolean snapshot() {
        return this.isSnapshot;
    }

    public boolean isLowerThan(Version version, boolean includeLimit) {
        return includeLimit ? this.isLowerOrEqual(version) : this.isLower(version);
    }

    private boolean isLower(Version version) {
        return this.major == -1 || version.major() == -1 || this.major < version.major() && (this.minor == -1 || version.minor() == -1 || this.minor < version.minor()) && (this.bug == -1 || version.bug() == -1 || this.bug < version.bug());
    }

    private boolean isLowerOrEqual(Version version) {
        return this.major == -1 || version.major() == -1 || this.major <= version.major() && (this.minor == -1 || version.minor() == -1 || this.minor <= version.minor()) && (this.bug == -1 || version.bug() == -1 || this.bug <= version.bug());
    }

    public boolean isGraterThan(Version version, boolean includeLimit) {
        return includeLimit ? this.isGraterOrEqual(version) : this.isGrater(version);
    }

    private boolean isGrater(Version version) {
        return this.major == -1 || version.major() == -1 || this.major > version.major() && (this.minor == -1 || version.minor() == -1 || this.minor > version.minor()) && (this.bug == -1 || version.bug() == -1 || this.bug > version.bug());
    }

    private boolean isGraterOrEqual(Version version) {
        return this.major == -1 || version.major() == -1 || this.major >= version.major() && (this.minor == -1 || version.minor() == -1 || this.minor >= version.minor()) && (this.bug == -1 || version.bug() == -1 || this.bug >= version.bug());
    }
}
