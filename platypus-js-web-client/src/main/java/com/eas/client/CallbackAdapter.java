package com.eas.client;

import com.google.gwt.core.client.Callback;
import com.eas.core.Logger;

/**
 *
 * @author mg
 */
public abstract class CallbackAdapter<T, F> implements Callback<T, F> {

    protected abstract void doWork(T aResult) throws Exception;

    @Override
    public void onSuccess(T aResult) {
        try {
            doWork(aResult);
        } catch (Exception ex) {
            Logger.severe(ex);
        }
    }
}
