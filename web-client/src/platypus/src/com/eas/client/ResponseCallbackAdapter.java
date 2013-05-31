/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 *
 * @author mg
 */
public abstract class ResponseCallbackAdapter implements Callback<XMLHttpRequest> {

    protected boolean cancelled;

    @Override
    public void run(XMLHttpRequest aResponse) throws Exception {
        if (!cancelled) {
            doWork(aResponse);
        }
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    protected abstract void doWork(XMLHttpRequest aResponse) throws Exception;
}
