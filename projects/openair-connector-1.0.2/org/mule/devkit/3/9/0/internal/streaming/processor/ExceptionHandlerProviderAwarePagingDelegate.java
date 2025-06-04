/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleException
 *  org.mule.streaming.ProviderAwarePagingDelegate
 */
package org.mule.devkit.3.9.0.internal.streaming.processor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.mule.api.MuleException;
import org.mule.streaming.ProviderAwarePagingDelegate;

public class ExceptionHandlerProviderAwarePagingDelegate
extends ProviderAwarePagingDelegate {
    private ProviderAwarePagingDelegate innerPagination;
    private Class<?> handlerClass;
    private String handlerMethodName;

    public ExceptionHandlerProviderAwarePagingDelegate(ProviderAwarePagingDelegate innerPagination, Class<?> handlerClass, String handlerMethodName) {
        this.innerPagination = innerPagination;
        this.handlerClass = handlerClass;
        this.handlerMethodName = handlerMethodName;
    }

    public List getPage(Object provider) throws Exception {
        try {
            return this.innerPagination.getPage(provider);
        }
        catch (Exception e) {
            this.handleException(e);
            throw e;
        }
    }

    public int getTotalResults(Object provider) throws Exception {
        return this.innerPagination.getTotalResults(provider);
    }

    public void close() throws MuleException {
        this.innerPagination.close();
    }

    private void handleException(Exception e) throws Exception {
        try {
            Object handler = this.handlerClass.newInstance();
            Method handlerMethod = this.handlerClass.getMethod(this.handlerMethodName, Exception.class);
            handlerMethod.invoke(handler, e);
        }
        catch (InstantiationException ie) {
            throw e;
        }
        catch (IllegalAccessException iae) {
            throw e;
        }
        catch (NoSuchMethodException nsme) {
            throw e;
        }
        catch (InvocationTargetException ite) {
            if (ite.getCause() != null && ite.getCause() instanceof Exception) {
                throw (Exception)ite.getCause();
            }
            throw e;
        }
    }
}
