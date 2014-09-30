/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.proto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;

/**
 *
 * @author pk
 */
public class ProtoUtil {

    public static final String CHARSET_4_STRING_SERIALIZATION_NAME = "UTF-16LE";
    public static final Charset charset4StringSerialization = Charset.forName(CHARSET_4_STRING_SERIALIZATION_NAME);

    public static BigDecimal number2BigDecimal(Number aNumber) {
        if (aNumber instanceof Float || aNumber instanceof Double) {
            return new BigDecimal(aNumber.doubleValue());
        } else if (aNumber instanceof BigInteger) {
            return new BigDecimal((BigInteger) aNumber);
        } else if (aNumber instanceof BigDecimal) {
            return (BigDecimal) aNumber;
        } else {
            return new BigDecimal(aNumber.longValue());
        }
    }

}
