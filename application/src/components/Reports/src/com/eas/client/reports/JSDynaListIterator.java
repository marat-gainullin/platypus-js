/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import java.util.Iterator;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author Andrew
 */
public class JSDynaListIterator implements Iterator {

    private final JSObject delegate;
    private final int timezoneOffset;
    private final int length;
    private int position = -1;

    public JSDynaListIterator(JSObject aDelegate, int aTimezoneOffset, int aLength) {
        super();
        if (aDelegate != null) {
            delegate = aDelegate;
            length = aLength;
            timezoneOffset = aTimezoneOffset;
        } else {
            throw new IllegalArgumentException("A Delegate could not be null.");
        }
    }

    @Override
    public boolean hasNext() {
        return position < (length - 1);
    }

    @Override
    public Object next() {
        return JSDynaBean.wrap(delegate.getSlot(++position), timezoneOffset);
    }

}
