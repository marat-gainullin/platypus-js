/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author pk
 */
public class StringUtils {

    private static String SUBSTITUTE_CHAR = "_";
    private static String FILENAME_INVALID_CHARACTER_PATTERN = "[\\/:*?\"<>|]";
    private static String END_WHITE_SPASE = "\\s(?=\\s*$)";
    
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
    
    public static Set<String> split(String aRoles, String aDelimiter){
        Set<String> aResRoles= new HashSet();
        if (aRoles != null){
            String[] aRolesArray = aRoles.split(aDelimiter);
            for (String aRole : aRolesArray){
                if (!aRole.isEmpty()){
                    aResRoles.add(aRole);
                }
            }
        }
        return aResRoles;
    }  
    
    public static String replaceUnsupportedSymbols(String str) {
        StringBuilder sb = new StringBuilder();
        int len = str.length();
        char[] tmp = new char[len];
        str.getChars(0, len, tmp, 0);
        char c = tmp[0];
        if (Character.isUnicodeIdentifierPart(c) && !Character.isDigit(c)) {
            sb.append(c);
        } else {sb.append(SUBSTITUTE_CHAR);} //NOI18N
        for (int i = 1; i < len; i++) {
            char ch = tmp[i];
            if (Character.isUnicodeIdentifierPart(ch)){
                sb.append(ch);
            } else {
                sb.append(SUBSTITUTE_CHAR); //NOI18N
            }
        }
        return sb.toString();
    }
    
    public static String replaceUnsupportedSymbolsinFileNames(String str) {
        if (str != null) {
            String endWhiteSpase = str.replaceAll(END_WHITE_SPASE, SUBSTITUTE_CHAR);
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
}
