/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

/**
 *
 * @author mg
 */
public abstract class CumulativeCallbackAdapter extends CancellableCallbackAdapter {

    protected int exepectedExecutesCount;
    protected int executed;

    public CumulativeCallbackAdapter(int aExecutesCount) {
        exepectedExecutesCount = aExecutesCount;
    }

    @Override
    public void run() throws Exception {
        if (!cancelled && ++executed == exepectedExecutesCount) {
            doWork();
        }
    }
}
