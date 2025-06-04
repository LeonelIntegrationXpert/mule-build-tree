/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.ConnectionException
 */
package org.mule.devkit.3.9.0.internal.connection.management;

import org.mule.api.ConnectionException;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionKey;

public interface TestableConnection<K extends ConnectionManagementConnectionKey> {
    public void test(K var1) throws ConnectionException;
}
