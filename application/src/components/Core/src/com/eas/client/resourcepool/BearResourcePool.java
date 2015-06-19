/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.resourcepool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author mg
 * @param <T>
 */
public abstract class BearResourcePool<T> implements ResourcePool<T> {

    public static final int DEFAULT_MAXIMUM_SIZE = 5;
    protected final LinkedBlockingQueue<T> resources;
    protected int maximumSize;
    protected volatile int currentSize;// may be mush greater than maximumSize

    public BearResourcePool(int aMaximumSize) {
        super();
        maximumSize = Math.max(1, aMaximumSize);
        resources = new LinkedBlockingQueue<>(maximumSize);
    }

    @Override
    public T achieveResource() throws Exception {
        T resource = resources.poll();
        if (resource == null) {
            if (currentSize < maximumSize) {// zombie condition
                try {
                    T created = createResource();
                    currentSize++;
                    return created;
                } catch (Exception ex) {
                    if (currentSize > 0) {// ever-increasing counter saves this logic
                        return resources.poll(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                    } else {
                        throw ex;
                    }
                }
            } else {
                return resources.poll(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            }
        } else {
            return resource;
        }
    }

    @Override
    public void returnResource(T aResource) {
        if (!resources.offer(aResource)) {
            resourceOverflow(aResource);
        }
    }

    protected abstract T createResource() throws Exception;

    protected abstract void resourceOverflow(T aResource);
}
