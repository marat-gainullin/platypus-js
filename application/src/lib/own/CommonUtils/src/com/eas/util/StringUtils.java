/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author pk
 */
public class StringUtils {

    private static final String SUBSTITUTE_CHAR = "_";
    private static final String FILENAME_INVALID_CHARACTER_PATTERN = "[\\/:*?\"<>|]";
    private static final String END_WHITESPACE = "\\s(?=\\s*$)";

    public static String join(String separator, String... parts) {
        String delimiter = "";
        StringBuilder bldr = new StringBuilder();
        for (String part : parts) {
            bldr.append(delimiter).append(part);
            delimiter = separator;
        }
        return bldr.toString();
    }

    public static String[] toStringArray(Object... args) {
        if (args != null && args.length > 0) {
            String[] res = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    res[i] = args[i].toString();
                } else {
                    res[i] = "";
                }
            }
            return res;
        } else {
            return new String[0];
        }
    }

    /**
     * Splits a string with a delimiter. Performs all needed checks. Empty
     * strings are cutted out.
     *
     * @param aSource
     * @param aDelimiter
     * @return
     */
    public static Set<String> split(String aSource, String aDelimiter) {
        Set<String> res = new HashSet();
        if (aSource != null) {
            String[] splitted = aSource.split(aDelimiter);
            if (splitted != null) {
                for (String element : splitted) {
                    element = element.trim();
                    if (!element.isEmpty()) {
                        res.add(element);
                    }
                }
            }
        }
        return res;
    }

    public static String replaceUnsupportedSymbols(String str) {
        StringBuilder sb = new StringBuilder();
        int len = str.length();
        char[] tmp = new char[len];
        str.getChars(0, len, tmp, 0);
        char c = tmp[0];
        if (Character.isUnicodeIdentifierPart(c) && !Character.isDigit(c)) {
            sb.append(c);
        } else {
            sb.append(SUBSTITUTE_CHAR);
        } //NOI18N
        for (int i = 1; i < len; i++) {
            char ch = tmp[i];
            if (Character.isUnicodeIdentifierPart(ch)) {
                sb.append(ch);
            } else {
                sb.append(SUBSTITUTE_CHAR); //NOI18N
            }
        }
        return sb.toString();
    }

    public static String replaceFileNamesInvalidCharacters(String str) {
        if (str != null) {
            String endWhiteSpase = str.replaceAll(END_WHITESPACE, SUBSTITUTE_CHAR);
            String trimStr = endWhiteSpase.trim();
            return trimStr.replaceAll(FILENAME_INVALID_CHARACTER_PATTERN, SUBSTITUTE_CHAR);
        } else {
            return null;
        }
    }

    public static Integer parseInt(String str, Integer defaultValue) {
        Integer val = defaultValue;
        try {
            val = Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            //no-op
        }
        return val;
    }

    public static String formatDouble(double aValue) {
        DecimalFormat formatter = new DecimalFormat();
        formatter.setGroupingUsed(false);
        formatter.setMinimumIntegerDigits(1);
        formatter.setMaximumFractionDigits(100);
        formatter.setRoundingMode(RoundingMode.DOWN);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(aValue);
    }

    public static String capitalize(String aValue) {
        return aValue.length() > 0 ? aValue.substring(0, 1).toUpperCase() + aValue.substring(1) : aValue;
    }
    
    /**
     * Reads string data from an abstract reader up to the length or up to the end of the reader.
     * @param aReader Reader to read from.
     * @param length Length of segment to be read. It length == -1, than reading is performed until the end of Reader.
     * @return String, containing data read from Reader.
     * @throws IOException
     */
    public static String readReader(Reader aReader, int length) throws IOException {
        char[] buffer = new char[32];
        StringWriter res = new StringWriter();
        int read = 0;
        int written = 0;
        while ((read = aReader.read(buffer)) != -1) {
            if (length < 0 || written + read <= length) {
                res.write(buffer, 0, read);
                written += read;
            } else {
                res.write(buffer, 0, read - (written + read - length));
                written += length - (written + read);
                break;
            }
        }
        res.flush();
        String str = res.toString();
        assert length < 0 || str.length() == length;
        return str;
    }

}
