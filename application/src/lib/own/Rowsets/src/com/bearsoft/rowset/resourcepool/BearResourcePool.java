/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.resourcepool;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mg
 * @param <T>
 */
public abstract class BearResourcePool<T> implements ResourcePool<T> {

    //public static final int WAIT_TIMEOUT = 15;
    public static final int DEFAULT_MAXIMUM_SIZE = 5;
    protected int maximumSize = DEFAULT_MAXIMUM_SIZE;
    //protected int resourceTimeout = WAIT_TIMEOUT;
    protected int size;
    protected final Set<T> resources = new HashSet<>();
    private final Object waitPoint = new Object();

    public BearResourcePool(int aMaximumSize) {
        super();
        maximumSize = aMaximumSize;
    }

    /*
     public BearResourcePool(int aMaximumSize, int aResourceTimeout) {
     super();
     maximumSize = aMaximumSize;
     resourceTimeout = aResourceTimeout;
     }
     */
    @Override
    public T achieveResource() throws Exception {
        T res = tryAchieveResource();
        if (res == null) {// May become wrong during further execution in parallel threads...
            synchronized (waitPoint) {
                // res == null - is very old information, so let's begin another solid logic block... 
                res = tryAchieveResource();
                while (res == null && Thread.currentThread().isAlive()) {
                    waitPoint.wait(1000l);// Unlimited waiting is dangerous and limit is nevermind here.
                    res = tryAchieveResource();
                }
            }
        }
        return res;
    }

    @Override
    public void returnResource(T aResource) {
        accept(aResource);
        synchronized (waitPoint) {
            waitPoint.notifyAll();
        }
    }

    private synchronized T tryAchieveResource() throws Exception {
        if (resources.isEmpty() && size < maximumSize) {
            resources.add(createResource());
            size++;
        }
        if (!resources.isEmpty()) {
            T achievedResource = resources.iterator().next();
            resources.remove(achievedResource);
            return achievedResource;
        } else {
            return null;
        }
    }

    protected synchronized void accept(T aResource) {
        resources.add(aResource);
    }

    protected abstract T createResource() throws Exception;
}
