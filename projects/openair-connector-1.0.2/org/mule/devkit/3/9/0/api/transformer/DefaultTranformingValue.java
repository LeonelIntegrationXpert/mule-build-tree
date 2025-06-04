/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.transformer.DataType
 */
package org.mule.devkit.3.9.0.api.transformer;

import org.mule.api.transformer.DataType;
import org.mule.devkit.3.9.0.api.transformer.TransformingValue;

public class DefaultTranformingValue<V, D>
implements TransformingValue<V, DataType<D>> {
    private V value;
    private DataType<D> dataType;

    public DefaultTranformingValue(V value, DataType<D> dataType) {
        this.value = value;
        this.dataType = dataType;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public DataType<D> getDataType() {
        return this.dataType;
    }
}
