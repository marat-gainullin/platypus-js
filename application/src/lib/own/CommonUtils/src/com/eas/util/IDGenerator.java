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

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author mg
 */
public class IDGenerator {

    private static final int RND_DIGITS = 1000;
    private static final long MILLIS_BIAS = 1000000000000L;
    private static final AtomicLong LAST_MILLIS = new AtomicLong();
    private static final Random RND = new Random();

    public static long genID() {
        long prevTime;
        long newTime;
        do {
            prevTime = LAST_MILLIS.get();
            newTime = System.currentTimeMillis() - MILLIS_BIAS;
        } while (prevTime == newTime || !LAST_MILLIS.compareAndSet(prevTime, newTime));
        return newTime * RND_DIGITS + RND.nextInt(RND_DIGITS);
    }
}
