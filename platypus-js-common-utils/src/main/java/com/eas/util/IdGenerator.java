package com.eas.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author mg
 */
public class IdGenerator {

    private static final int COUNTER_DIGITS = 100;
    private static final AtomicLong ID = new AtomicLong(System.currentTimeMillis() * COUNTER_DIGITS);
    private static final int LONG_COUNTER_DIGITS = 1000000;
    private static final AtomicLong LONG_ID = new AtomicLong(System.currentTimeMillis() * LONG_COUNTER_DIGITS);

    public static long genId() {
        return generate(ID, COUNTER_DIGITS);
    }

    public static long genLongId() {
        return generate(LONG_ID, LONG_COUNTER_DIGITS);
    }

    public static String genStringId() {
        return String.valueOf(genLongId());
    }

    private static long generate(AtomicLong aLastId, int aCounterDigits) {
        long newId;
        long id;
        do {
            id = aLastId.get();
            long idMillis = id / aCounterDigits;// Note! Truncation of fractional part is here.
            if (idMillis == System.currentTimeMillis()) {
                long oldCounter = id - idMillis * aCounterDigits;
                long newCounter = oldCounter + 1;
                if (newCounter == aCounterDigits) {
                    // Spin lock with maximum duration of one millisecond ...
                    long newMillis;
                    do {
                        newMillis = System.currentTimeMillis();
                    } while (newMillis == idMillis);
                    newId = newMillis * aCounterDigits;
                } else {
                    newId = idMillis * aCounterDigits + newCounter;
                }
            } else {
                newId = System.currentTimeMillis() * aCounterDigits;
            }
        } while (!aLastId.compareAndSet(id, newId));
        return newId;
    }
}
