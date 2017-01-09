/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author mg
 */
public class PlatypusThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean defaultDeamon;

    public PlatypusThreadFactory() {
        this("", true);
    }
    
    public PlatypusThreadFactory(String aNamePrefix, boolean aDefaultDeamon) {
        super();
        defaultDeamon = aDefaultDeamon;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup()
                : Thread.currentThread().getThreadGroup();
        namePrefix = aNamePrefix + "pool-"
                + poolNumber.getAndIncrement()
                + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        t.setDaemon(defaultDeamon);
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }
}
