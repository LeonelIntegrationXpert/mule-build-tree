/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.transformer.DataType
 */
package org.mule.devkit.3.9.0.api.transformer;

import org.mule.api.transformer.DataType;

public interface TransformingValue<V, D extends DataType> {
    public V getValue();

    public D getDataType();
}
