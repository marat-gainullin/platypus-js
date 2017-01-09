package com.eas.script;

/**
 *
 * @author vv
 */
public class FileNameSupport {

    /**
     * Generates a file name by adding a dash on an camel case letter.
     *
     * @param aCamelCaseName a string like 'ConstructorName'
     * @return a string like 'constructor-name'
     */
    public static String getFileName(String aCamelCaseName) {
        int lastMinusIdx = aCamelCaseName.lastIndexOf("-");
        if (lastMinusIdx != -1) {
            aCamelCaseName = aCamelCaseName.substring(0, lastMinusIdx);
        }
        if (aCamelCaseName.startsWith("platypus-js-")) {
            aCamelCaseName = aCamelCaseName.substring("platypus-js-".length());
        }
        char[] chars = aCamelCaseName.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < aCamelCaseName.length(); i++) {
            sb.append(Character.toLowerCase(chars[i]));
            if (i < aCamelCaseName.length() - 1 && Character.isLowerCase(chars[i]) && Character.isUpperCase(chars[i + 1])) {
                sb.append('-');
            }
        }
        return sb.toString();
    }

}
