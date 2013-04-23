/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

/**
 *
 * @author mg
 */
public abstract class CancellableCallbackAdapter implements CancellableCallback {

    protected boolean cancelled;

    @Override
    public void run() throws Exception {
        if (!cancelled) {
            doWork();
        }
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    protected abstract void doWork() throws Exception;
}
