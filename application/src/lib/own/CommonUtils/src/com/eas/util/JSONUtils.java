/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

/**
 *
 * @author AB
 */
public class JSONUtils {
    public static String s(String aPropValue) {
        StringBuilder sb = new StringBuilder();
        aPropValue = aPropValue == null ? "null" : aPropValue.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t").replace("\r", "").replace("\n", "\\n");
        sb.append("\"").append(aPropValue).append("\"");
        return sb.toString();
    }

    public static StringBuilder p(StringBuilder sb, String aPropName, String aPropValue) {
        return sb.append("\"").append(aPropName).append("\"").append(":").append(aPropValue);
    }

    public static StringBuilder o(StringBuilder sb, String... aValues) {
        assert aValues != null && aValues.length % 2 == 0;
        sb.append("{");
        for (int i = 0; i < aValues.length; i += 2) {
            if (i > 0) {
                sb.append(",");
            }
            p(sb, aValues[i], aValues[i + 1]);
        }
        sb.append("}");
        return sb;
    }

    public static String a(StringBuilder sb, String... aValues) {
        assert aValues != null;
        sb.append("[");
        for (int i = 0; i < aValues.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(aValues[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
