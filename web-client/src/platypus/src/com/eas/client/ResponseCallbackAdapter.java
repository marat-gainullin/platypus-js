/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.google.gwt.http.client.Response;

/**
 *
 * @author mg
 */
public abstract class ResponseCallbackAdapter implements Callback<Response> {

    protected boolean cancelled;

    @Override
    public void run(Response aResponse) throws Exception {
        if (!cancelled) {
            doWork(aResponse);
        }
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    protected abstract void doWork(Response aResponse) throws Exception;
}
