/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.concurrent.DeamonThreadFactory;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author mg
 */
public class ScriptTimerTask {

    private static final ScheduledThreadPoolExecutor e = new ScheduledThreadPoolExecutor(10, new DeamonThreadFactory());

    static {
        e.setKeepAliveTime(100, TimeUnit.MILLISECONDS);
        e.allowCoreThreadTimeOut(true);
    }

    public static ScheduledFuture<?> schedule(final Runnable aDelegate, long aDelay) {
        return e.schedule(aDelegate, aDelay, TimeUnit.MILLISECONDS);
    }
}
