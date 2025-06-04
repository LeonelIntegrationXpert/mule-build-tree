/*
 * Decompiled with CFR 0.152.
 */
package org.mule.modules.openair.utils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.mule.modules.openair.utils.MethodAccessibilityException;

public class ReflectionCloseableWrapper<T>
implements Closeable {
    private final T closeable;
    private final String closeMethodName;

    public ReflectionCloseableWrapper(T closeable) {
        this("close", closeable);
    }

    public ReflectionCloseableWrapper(String closeMethodName, T closeable) {
        this.closeable = closeable;
        this.closeMethodName = closeMethodName;
    }

    @Override
    public void close() throws IOException {
        try {
            this.closeable.getClass().getMethod(this.closeMethodName, new Class[0]).invoke(this.closeable, new Object[0]);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new MethodAccessibilityException(this.closeMethodName, e);
        }
    }

    public T getCloseable() {
        return this.closeable;
    }
}
