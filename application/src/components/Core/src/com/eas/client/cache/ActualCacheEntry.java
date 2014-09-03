/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import java.util.Date;

/**
 *
 * @author mg
 * @param <E>
 */
public class ActualCacheEntry<E> {

    protected E value;
    protected Date timeStamp;

    public ActualCacheEntry(E aValue, Date aTimeStamp) {
        super();
        value = aValue;
        timeStamp = aTimeStamp;
    }

    public E getValue() {
        return value;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }
}
