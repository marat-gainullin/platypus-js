/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.ClientConstants;
import com.eas.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vv
 */
public class PlatypusFilesSupport {

    // jsDoc or sqlDoc containing element name annotation regex parts 1 and 2
    private static final String NAMED_ANNOTATION_PATTERN_FIRST_PART = "^\\s*/\\*\\*(?=(?:(?!\\*/)[\\s\\S])*?"; //NOI18N
    private static final String NAMED_ANNOTATION_PATTERN_SECOND_PART = ")(?:(?!\\*/)[\\s\\S])*\\*/"; //NOI18N

    /**
     * Extracts annotation value form given content. May return annotation
     * value, null or an empty string.
     *
     * @param aContent Content to be analyzed.
     * @param aAnnotationName Name of the extracted annotation.
     * @return Annotaion value if it exists, null otherwise and empty string if
     * annotation exists, but has no value.
     */
    public static String getAnnotationValue(String aContent, String aAnnotationName) {
        Pattern pattern = Pattern.compile(getAnnotatedDocRegexStr(aAnnotationName));
        Matcher matcher = pattern.matcher(aContent);
        if (matcher.find()) {
            String docComment = matcher.group();
            String[] lines = docComment.split(System.getProperty(ClientConstants.LINE_SEPARATOR_PROP_NAME)); //NOI18N
            for (String line : lines) {
                if (line.toLowerCase().contains(aAnnotationName.toLowerCase())) {
                    String[] tokens = line.split("\\s");  //NOI18N
                    for (int i = 0; i < tokens.length; i++) {
                        if (tokens[i].toLowerCase().endsWith(aAnnotationName.toLowerCase())) {
                            if (i < tokens.length - 1) {
                                return tokens[i + 1];
                            } else {
                                return ""; //NOI18N
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String replaceAnnotationValue(String aContent, String aAnnotationName, String aValue) {
        Pattern pattern = Pattern.compile(getAnnotatedDocRegexStr(aAnnotationName));
        Matcher matcher = pattern.matcher(aContent);
        boolean found = matcher.find();
        if (!found) {
            pattern = Pattern.compile(getAnnotatedDocRegexStr("(.*)")); //NOI18N
            matcher = pattern.matcher(aContent);
            found = matcher.find();
        }
        List<String> list = new ArrayList<>();
        if (found) {
            String docComment = matcher.group();
            String[] lines = docComment.split("\r\n"); //NOI18N
            if (lines.length == 1) {
                lines = docComment.split("\n"); //NOI18N
            }
            if (lines.length > 1) {
                boolean processed = false;
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i];
                    if (line.contains(aAnnotationName)) {
                        processed = true;
                        String[] tokens = line.split("\\s");  //NOI18N
                        String rightPadding = "";
                        for (int t = 0; t < tokens.length; t++) {
                            if (tokens[t].endsWith(aAnnotationName)) {
                                if (aValue != null) {
                                    if (t < tokens.length - 1) {
                                        tokens[t + 1] = aValue;
                                    } else {
                                        rightPadding = " " + aValue;
                                    }
                                } else {
                                    tokens[t] = "";
                                    if (t < tokens.length - 1) {
                                        tokens[t + 1] = "";
                                    }
                                }
                            }
                        }
                        String tokensStr = StringUtils.join(" ", tokens) + rightPadding;
                        if (!"*".equals(tokensStr.trim())) {
                            list.add(tokensStr);
                        }
                    } else {
                        list.add(line);
                    }
                }
                if (!processed && aValue != null) {
                    list.add(list.size() > 2 ? 2 : 1, " * " + aAnnotationName + " " + aValue); //NOI18N
                }
                docComment = StringUtils.join(System.getProperty(ClientConstants.LINE_SEPARATOR_PROP_NAME), list.toArray(new String[]{})); //NOI18N
                return matcher.replaceFirst(docComment);
            }
        }
        list.add("/**"); //NOI18N
        list.add(" * " + aAnnotationName + " " + aValue); //NOI18N
        list.add(" */" + System.getProperty(ClientConstants.LINE_SEPARATOR_PROP_NAME)); //NOI18N
        String docComment = StringUtils.join(System.getProperty(ClientConstants.LINE_SEPARATOR_PROP_NAME), list.toArray(new String[]{}));
        return docComment + aContent;
    }

    private static String getAnnotatedDocRegexStr(String anAnnotation) {
        return NAMED_ANNOTATION_PATTERN_FIRST_PART + anAnnotation + NAMED_ANNOTATION_PATTERN_SECOND_PART;
    }
}
