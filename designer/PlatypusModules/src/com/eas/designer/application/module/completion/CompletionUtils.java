/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Completion support utility class.
 *
 * @author vv
 */
public class CompletionUtils {

    private static final String JSDOC_PREFIX = "/**"; //NOI18N

    /**
     * Splits a JsDoc comment to a bunch of strings and removes the artifacts
     *
     * @param jsDoc a jsDoc comment text
     * @return a list of the converted strings
     */
    public static List<String> getComments(String jsDoc) {
        if (jsDoc == null || jsDoc.isEmpty()) {
            return new ArrayList<>();
        } else {
            List<String> comments = new ArrayList<>();
            for (String line : Arrays.asList(jsDoc.split("\n"))) { //NOI18N
                String trimmedLine = line.trim();
                // ignore end of block comment: "*/"
                if (!trimmedLine.equals(JSDOC_PREFIX)) {
                    if (trimmedLine.startsWith(JSDOC_PREFIX)) {
                        comments.add(trimmedLine.substring(JSDOC_PREFIX.length()).trim());
                    }else{
                        comments.add(trimmedLine);
                    }
                }
            }
            return comments;
        }
    }

}
