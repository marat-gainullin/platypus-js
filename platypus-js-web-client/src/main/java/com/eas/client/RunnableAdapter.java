package com.eas.client;

import com.eas.core.Logger;

public abstract class RunnableAdapter implements Runnable {

    protected abstract void doWork() throws Exception;

    @Override
    public void run() {
        try {
            doWork();
        } catch (Exception ex) {
            Logger.severe(ex);
        }
    }

}
