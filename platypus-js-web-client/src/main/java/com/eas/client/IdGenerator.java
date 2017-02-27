package com.eas.client;

/**
 * 
 * @author mg
 */
public class IdGenerator {

	private static final long COUNTER_DIGITS = 100;
	private static long ID = System.currentTimeMillis() * COUNTER_DIGITS;

	private static final long LONG_COUNTER_DIGITS = 1000000;
	private static long LONG_ID = System.currentTimeMillis() * LONG_COUNTER_DIGITS;
	
	public static double genId() {
		// Note! Truncation of fractional part is here.
		long idMillis = ID / COUNTER_DIGITS;
		if (idMillis == System.currentTimeMillis()) {
			long oldCounter = ID - idMillis * COUNTER_DIGITS;
			long newCounter = oldCounter + 1;
			if (newCounter == COUNTER_DIGITS) {
				// Spin with maximum duration of one millisecond ...
				long newMillis;
				do {
					newMillis = System.currentTimeMillis();
				} while (newMillis == idMillis);
				ID = newMillis * COUNTER_DIGITS;
			} else {
				ID = idMillis * COUNTER_DIGITS + newCounter;
			}
		} else {
			ID = System.currentTimeMillis() * COUNTER_DIGITS;
		}
		return ID;
	}
	
	public static String genLongId() {
		// Note! Truncation of fractional part is here.
		long idMillis = LONG_ID / LONG_COUNTER_DIGITS;
		if (idMillis == System.currentTimeMillis()) {
			long oldCounter = LONG_ID - idMillis * LONG_COUNTER_DIGITS;
			long newCounter = oldCounter + 1;
			if (newCounter == LONG_COUNTER_DIGITS) {
				// Spin with maximum duration of one millisecond ...
				long newMillis;
				do {
					newMillis = System.currentTimeMillis();
				} while (newMillis == idMillis);
				LONG_ID = newMillis * LONG_COUNTER_DIGITS;
			} else {
				LONG_ID = idMillis * LONG_COUNTER_DIGITS + newCounter;
			}
		} else {
			LONG_ID = System.currentTimeMillis() * LONG_COUNTER_DIGITS;
		}
		return String.valueOf(LONG_ID);
	}
}
