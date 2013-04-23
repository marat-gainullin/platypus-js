/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.google.gwt.xml.client.Document;

/**
 *
 * @author mg
 */
public abstract class DocumentCallbackAdapter implements Callback<Document> {

    protected boolean cancelled;

    @Override
    public void run(Document aDoc) throws Exception {
        if (!cancelled) {
            doWork(aDoc);
        }
    }

    protected abstract void doWork(Document aDoc) throws Exception;

    @Override
    public void cancel() {
        cancelled = true;
    }
}
