/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

/**
 *
 * @author mg
 */
public class ActualCacheEntry<EV> {

    protected EV value;
    protected long txtContentSize;
    protected long txtContentCrc32;

    public ActualCacheEntry(EV aValue, long aTxtContentSize, long aTxtContentCrc32) {
        value = aValue;
        txtContentSize = aTxtContentSize;
        txtContentCrc32 = aTxtContentCrc32;
    }

    public EV getValue() {
        return value;
    }

    public long getTxtContentSize() {
        return txtContentSize;
    }

    public long getTxtContentCrc32() {
        return txtContentCrc32;
    }
}
