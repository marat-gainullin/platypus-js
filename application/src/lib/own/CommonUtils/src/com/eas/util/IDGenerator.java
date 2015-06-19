/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author mg
 */
public class IDGenerator {

    private static final Long rndIDPart = 100L;
    private static final AtomicLong lastValue = new AtomicLong();

    public static long genID() {
        long newValue;
        long last;
        do {
            last = lastValue.get();
            newValue = System.currentTimeMillis() * rndIDPart + Math.round(Math.random() * rndIDPart);
        } while (last == newValue || !lastValue.compareAndSet(last, newValue));
        return newValue;
    }
}
