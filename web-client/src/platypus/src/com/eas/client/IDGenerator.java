/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client;

import com.google.gwt.user.client.Random;

/**
 *
 * @author mg
 */
public class IDGenerator {

    private static final int RND_DIGITS = 1000;
    private static final double MILLIS_BIAS = 1000000000000d;
    private static double LAST_MILLIS;

    public static double genId() {
        double newValue;
        double prevTime;
        double newTime;
        do {
            prevTime = LAST_MILLIS;
            newTime = System.currentTimeMillis() - MILLIS_BIAS;
        } while (prevTime == newTime);
        newValue = newTime * RND_DIGITS + Random.nextInt(RND_DIGITS);
        LAST_MILLIS = newTime;
        return newValue;
    }
}
