/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.script;

/**
 *
 * @author vv
 */
public class FileNameSupport {
    
    /**
     * Generates a file name by adding a dash on an camel case letter.
     * @param str a string like 'ConstructorName' 
     * @return a string like 'constructor-name'
     */
    public static String getFileName(String str) {
        char[] chars = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            sb.append(Character.toLowerCase(chars[i]));
            if (i < str.length() - 1 && Character.isLowerCase(chars[i]) && Character.isUpperCase(chars[i + 1])) {
                sb.append('-');
            }
        }
        return sb.toString();
    }
    
}
