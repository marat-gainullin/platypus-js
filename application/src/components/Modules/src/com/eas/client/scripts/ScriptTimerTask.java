/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import java.util.TimerTask;

/**
 *
 * @author mg
 */
public class ScriptTimerTask extends TimerTask {

    protected Runnable delegate;

    public ScriptTimerTask(Runnable aDelegate) {
        super();
        delegate = aDelegate;
    }

    @Override
    public void run() {
        delegate.run();
    }
}
