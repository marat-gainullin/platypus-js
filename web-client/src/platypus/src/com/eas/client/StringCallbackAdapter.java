/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Callback;

/**
 *
 * @author mg
 */
public abstract class StringCallbackAdapter implements Callback<String>{

    protected boolean cancelled;

    @Override
    public void run(String aResult) throws Exception {
        if (!cancelled) {
            doWork(aResult);
        }
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    protected abstract void doWork(String aResult) throws Exception;
}
