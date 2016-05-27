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

	private static final long COUNTER_DIGITS = 1000;
	private static long ID = System.currentTimeMillis() * COUNTER_DIGITS;

	public static double genId() {
		// Note! Truncation of fractional part is here.
		long idMillis = ID / COUNTER_DIGITS;
		String sm = "" + idMillis;
		String si = "" + ID;
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
				si = "" + ID;
			}
		} else {
			ID = System.currentTimeMillis() * COUNTER_DIGITS;
		}
		return ID;
	}
}
