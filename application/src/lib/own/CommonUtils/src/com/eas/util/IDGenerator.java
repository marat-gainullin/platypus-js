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

    private static final int COUNTER_DIGITS = 100;
    private static final AtomicLong ID = new AtomicLong(System.currentTimeMillis() * COUNTER_DIGITS);

    public static long genID() {
        long newId;
        long id;
        do {
            id = ID.get();
            long idMillis = id / COUNTER_DIGITS;// Note! Truncation of fractional part is here.
            if (idMillis == System.currentTimeMillis()) {
                long oldCounter = id - idMillis * COUNTER_DIGITS;
                long newCounter = oldCounter + 1;
                if (newCounter == COUNTER_DIGITS) {
                    // Spin lock with maximum duration of one millisecond ...
                    long newMillis;
                    do {
                        newMillis = System.currentTimeMillis();
                    } while (newMillis == idMillis);
                    newId = newMillis * COUNTER_DIGITS;
                } else {
                    newId = idMillis * COUNTER_DIGITS + newCounter;
                }
            } else {
                newId = System.currentTimeMillis() * COUNTER_DIGITS;
            }
        } while (!ID.compareAndSet(id, newId));
        return newId;
    }
}
