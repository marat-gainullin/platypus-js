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
 */
public abstract class BearResourcePool<T> implements ResourcePool<T> {

    public static final int WAIT_TIMEOUT = 15;
    public static final int DEFAULT_MAXIMUM_SIZE = 5;
    protected int maximumSize = DEFAULT_MAXIMUM_SIZE;
    protected int resourceTimeout = WAIT_TIMEOUT;
    protected int size;
    protected final Set<T> resources = new HashSet<>();

    public BearResourcePool(int aMaximumSize) {
        super();
        maximumSize = aMaximumSize;
    }

    public BearResourcePool(int aMaximumSize, int aResourceTimeout) {
        super();
        maximumSize = aMaximumSize;
        resourceTimeout = aResourceTimeout;
    }

    protected abstract T createResource() throws Exception;

    @Override
    public T achieveResource() throws Exception {
        T res = tryAchieveResource();
        while (res == null && Thread.currentThread().isAlive()) {
            Thread.sleep(resourceTimeout);
            res = tryAchieveResource();
        }
        return res;
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

    @Override
    public synchronized void returnResource(T aResource) {
        resources.add(aResource);
    }
}
