/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author vv
 */
public class JsDoc {

    private String body;
    private String[] lines;
    private Tag tag;
    private List<Tag> tags = new ArrayList<>();

    public JsDoc(String aBody) {
        if (aBody == null) {
            throw new IllegalArgumentException("JsDoc body is null.");
        }
        body = aBody;
        String[] rawLines = body.split("\n"); //NOI18N
        lines = new String[rawLines.length];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = trimLeading(rawLines[i]);
        }
    }

    public String getBody() {
        return body;
    }

    public List<Tag> getAnnotations() {
        if (tags == null) {
            return null;
        }
        return Collections.unmodifiableList(tags);
    }

    public boolean isModuleLevel() {
        for (String line : lines) {
            if (line.startsWith(Tag.NAME_TAG)) {
                return true;
            }
        }
        return false;
    }

    public void parseAnnotations() {
        for (String line : lines) {
            if (line.startsWith("@")) { //NOI18N
                // Split whith all whitespace characters
                String[] annotationTokens = line.split("[\\s,]+"); //NOI18N
                String annotationName = annotationTokens[0];
                String annotationText = line.substring(annotationName.length()).trim();
                tag = new Tag(annotationName, annotationText);
                tags.add(tag);
            }
        }
    }

    private static String trimLeading(String source) {
        return trimLeading(source.trim(), '*').trim();
    }

    private static String trimLeading(String source, char symbol) {
        for (int i = 0; i < source.length(); ++i) {
            char c = source.charAt(i);
            if (c != symbol) {
                return source.substring(i);
            }
        }
        return source;
    }

    /**
     * An JsDoc tag, can be used as annotation
     */
    public static class Tag {

        /**
         * Annotation for defining application element's name.
         */
        public static final String NAME_TAG = "@name";
        /**
         * Annotation marks a module or top level function access restrictions for concrete roles
         * Example: @rolesAllowed admin, manager
         */
        public static final String ROLES_ALLOWED_TAG = "@rolesAllowed";
        public static final String ROLES_ALLOWED_READ_TAG = "@rolesAllowedRead";
        public static final String ROLES_ALLOWED_WRITE_TAG = "@rolesAllowedWrite";
        /**
         * Annotation marks query entity undelying table readonly for changes made on entity.
         */
        public static final String READONLY_TAG = "@readonly";
        /**
         * Annotation marks query entity undelying tables writable for changes made on entity.
         */
        public static final String WRITABLE_TAG = "@writable";
        public static final String MANUAL_TAG = "@manual";
        public static final String PROCEDURE_TAG = "@procedure";
        /**
         * Annotation marks module accessible for external execution (for example using HTTP).
         */
        public static final String PUBLIC_TAG = "@public";
        /**
         * Annotation marks module as stateless.
         */
        public static final String STATELESS_TAG = "@stateless";
        
        private String name;
        private String text;
        private List<String> params;

        public Tag(String aName, String aText) {
            name = aName;
            text = aText;
        }

        public String getName() {
            return name;
        }

        public String getText() {
            return text;
        }

        public List<String> getParams() {
            if (isParametrized()) {
                if (params == null) {
                    params = parseParams();
                }
                return Collections.unmodifiableList(params);
            } else {
                return null;
            }
        }
        
        /**
         * Utility method for checking if a tag with some name is in the list.
         * @param aTags Tags list
         * @param aTagName Tag's name
         * @return True if there is a tag with provided name
         */
        public static boolean containsTagWithName(List<Tag> aTags, String aTagName) {
            if (aTags == null) {
                throw new NullPointerException("Tag list is null.");
            }
            for (Tag tag : aTags) {
                if (tag.getName().equals(aTagName)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return name + (text != null ? " " + text : "");
        }

        private boolean isParametrized() {
            return ROLES_ALLOWED_TAG.equals(name) || ROLES_ALLOWED_READ_TAG.equals(name) || ROLES_ALLOWED_WRITE_TAG.equals(name) || READONLY_TAG.equals(name) || WRITABLE_TAG.equals(name);
        }

        private List<String> parseParams() {
            StringTokenizer st = new StringTokenizer(text, " \t\n\r\f,"); //NOI18N
            List<String> items = new ArrayList<>();
            while (st.hasMoreTokens()) {
                items.add(st.nextToken());
            }
            return items;
        }
    }
}
