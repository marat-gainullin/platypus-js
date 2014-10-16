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
    
    public static StringBuilder s(String aValue) {
        StringBuilder sb = new StringBuilder();
        aValue = aValue == null ? "null" : aValue.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t").replace("\r", "").replace("\n", "\\n");
        sb.append("\"").append(aValue).append("\"");
        return sb;
    }

    public static StringBuilder p(String aPropName, String aPropValue) {
        StringBuilder sb = new StringBuilder();
        return sb.append("\"").append(aPropName).append("\"").append(":").append(aPropValue);
    }

    public static StringBuilder p(String aPropName, StringBuilder aPropValue) {
        StringBuilder sb = new StringBuilder();
        return sb.append("\"").append(aPropName).append("\"").append(":").append(aPropValue);
    }

    public static StringBuilder o(String... aValues) {
        StringBuilder sb = new StringBuilder();
        assert aValues != null && aValues.length % 2 == 0;
        sb.append("{");
        for (int i = 0; i < aValues.length; i += 2) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(p(aValues[i], aValues[i + 1]));
        }
        sb.append("}");
        return sb;
    }

    public static StringBuilder o(String[] aNames, StringBuilder[] aValues) {
        StringBuilder sb = new StringBuilder();
        assert aNames != null && aValues != null && aNames.length == aValues.length;
        sb.append("{");
        for (int i = 0; i < aValues.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(p(aNames[i], aValues[i]));
        }
        sb.append("}");
        return sb;
    }

    public static StringBuilder a(String... aValues) {
        assert aValues != null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < aValues.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(aValues[i]);
        }
        sb.append("]");
        return sb;
    }
    
    public static StringBuilder as(String... aValues) {
        assert aValues != null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < aValues.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(s(aValues[i]));
        }
        sb.append("]");
        return sb;
    }
    
    public static StringBuilder a(StringBuilder... aValues) {
        assert aValues != null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < aValues.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(aValues[i]);
        }
        sb.append("]");
        return sb;
    }
    
    public static StringBuilder as(StringBuilder... aValues) {
        assert aValues != null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < aValues.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(s(aValues[i].toString()));
        }
        sb.append("]");
        return sb;
    }
}
