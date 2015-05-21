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

/**
 *
 * @author mg
 */
public class IDGenerator {

    public final static Long rndIDPart = 100L;
    private static double lastValue = 0;

    public static double genId() {
        double newValue = genIDImpl();
        while (lastValue >= newValue) {
            newValue = lastValue + 1;
        }
        lastValue = newValue;
        return lastValue;
    }

    private static double genIDImpl() {
        return System.currentTimeMillis() * rndIDPart + Math.round(Math.random() * rndIDPart);
    }
}
