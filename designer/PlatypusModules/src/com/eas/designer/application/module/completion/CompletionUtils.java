/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author vv
 */
public class CompletionUtils {

    private static final String JSDOC_PREFIX = "/**"; //NOI18N
            
    public static List<String> getComments(String jsDoc) {
        if (jsDoc == null || jsDoc.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> comments = new ArrayList<>();
        for (String line : Arrays.asList(jsDoc.split("\n"))) {
            String trimmedLine = line.trim();
            // ignore end of block comment: "*/"
            if (!trimmedLine.equals(JSDOC_PREFIX)) {
                if (trimmedLine.startsWith(JSDOC_PREFIX)) {
                    comments.add(trimmedLine.substring(JSDOC_PREFIX.length()).trim());
                } else if (trimmedLine.length() == 1 || (trimmedLine.length() > 1 && trimmedLine.charAt(1) != '/')) { //NOI18N
                    comments.add(trimmedLine.substring(1));
                }
            }
        }
        return comments;
    }
    
    
}
