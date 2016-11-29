/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author ab, mg
 */
public class JsonUtils {

    public static StringBuilder s(String aValue) {
        StringBuilder sb = new StringBuilder();
        String sValue = aValue == null ? null : aValue.replace("\\", "\\\\").replace("\"", "\\\"").replace("\t", "\\t").replace("\r", "").replace("\n", "\\n");
        if (sValue == null) {
            sb.append("null");
        } else {
            sb.append("\"").append(sValue).append("\"");
        }
        return sb;
    }

    public static StringBuilder p(String aPropName, String aPropValue) {
        StringBuilder sb = new StringBuilder();
        return sb.append("\"").append(aPropName).append("\"").append(":").append(aPropValue);
    }

    public static StringBuilder p(StringBuilder aPropName, StringBuilder aPropValue) {
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

    public static StringBuilder o(StringBuilder... aValues) {
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

    public static String v(Object aValue) throws Exception {
        if (aValue != null) {
            if (aValue instanceof Boolean) {
                return aValue.toString();
            } else if (aValue instanceof Number) {
                return StringUtils.formatDouble(((Number) aValue).doubleValue());
            } else if (aValue instanceof String) {
                return JsonUtils.s((String) aValue).toString();
            } else if (aValue instanceof Date) {
                SimpleDateFormat sdf = new SimpleDateFormat(RowsetJsonConstants.DATE_FORMAT);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                return JsonUtils.s(sdf.format((Date) aValue)).toString();
            } else {
                throw new Exception("Value of unknown or unsupported type found! It's class is: " + aValue.getClass().getName());
            }
        } else {
            return "null";
        }
    }

    public static Object toValue(String aJson) throws Exception {
        if (aJson != null && !aJson.isEmpty()) {
            if ("true".equals(aJson)) {
                return true;
            } else if ("false".equals(aJson)) {
                return false;
            } else if ("null".equals(aJson)) {
                return null;
            } else if (aJson.matches("\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z\"")) {// date
                SimpleDateFormat sdf = new SimpleDateFormat(RowsetJsonConstants.DATE_FORMAT);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                return sdf.parse(aJson.substring(1, aJson.length() - 1));
            } else if (aJson.matches("\".*\"")) {
                return jsonUnescape(aJson);
            } else {
                return Double.valueOf(aJson);
            }
        } else {
            return null;
        }
    }

    public static String jsonUnescape(String aJson) {
        // string
        String unescapesQuotes = aJson.substring(1, aJson.length() - 1).replace("\\\"", "\"");
        String unescapesSlashes = unescapesQuotes.replace("\\\\", "\\");
        return unescapesSlashes;
    }

}
