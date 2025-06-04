/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleException
 *  org.mule.api.lifecycle.Disposable
 *  org.mule.api.lifecycle.Initialisable
 *  org.mule.api.lifecycle.InitialisationException
 *  org.mule.api.lifecycle.Startable
 *  org.mule.api.lifecycle.Stoppable
 */
package org.mule.devkit.3.9.0.api.lifecycle;

import org.mule.api.MuleException;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;

public class LifeCycleManager {
    public static void initialise(Object o) throws InitialisationException {
        if (o instanceof Initialisable) {
            ((Initialisable)o).initialise();
        }
    }

    public static void start(Object o) throws MuleException {
        if (o instanceof Startable) {
            ((Startable)o).start();
        }
    }

    public static void stop(Object o) throws MuleException {
        if (o instanceof Stoppable) {
            ((Stoppable)o).stop();
        }
    }

    public static void dispose(Object o) {
        if (o instanceof Disposable) {
            ((Disposable)o).dispose();
        }
    }

    public static void executeInitialiseAndStart(Object o) throws MuleException {
        LifeCycleManager.initialise(o);
        LifeCycleManager.start(o);
    }

    public static void executeStopAndDispose(Object o) throws MuleException {
        LifeCycleManager.stop(o);
        LifeCycleManager.dispose(o);
    }
}
