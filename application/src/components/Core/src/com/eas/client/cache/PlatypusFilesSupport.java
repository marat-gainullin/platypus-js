/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.settings.EasSettings;
import com.eas.client.settings.XmlDom2ConnectionSettings;
import com.eas.script.JsParser;
import com.eas.util.FileUtils;
import com.eas.util.StringUtils;
import com.eas.xml.dom.Source2XmlDom;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;

/**
 *
 * @author vv
 */
public class PlatypusFilesSupport {

    public static final String APP_ELEMENT_NAME = "app-element-name"; //NOI18N
    public static final String APP_ELEMENT_NAME_ANNOTATION = "@name"; //NOI18N
    public static final String PUBLIC_ANNOTATION = "@public"; //NOI18N
    // jsDoc or sqlDoc containing element name annotation regex parts 1 and 2
    private static final String NAMED_ANNOTATION_PATTERN_1 = "^\\s*/\\*\\*(?=(?:(?!\\*/)[\\s\\S])*?"; //NOI18N
    private static final String NAMED_ANNOTATION_PATTERN_2 = ")(?:(?!\\*/)[\\s\\S])*\\*/"; //NOI18N

    public static String extractFirstFunctionName(String aJsContent) {
        try {
            AstRoot parseResult = JsParser.parse(aJsContent);
            return extractFirstFunctionName(parseResult);
        } catch (EvaluatorException ex) {
            return null;
        }
    }

    public static String extractFirstFunctionName(AstRoot jsRoot) {
        FunctionNode func = extractFirstFunction(jsRoot);
        return func != null ? func.getFunctionName().getIdentifier() : null;
    }

    public static FunctionNode extractFirstFunction(AstRoot jsRoot) {
        if (jsRoot != null) {
            Iterator<Node> nodes = jsRoot.iterator();
            while (nodes.hasNext()) {
                Node node = nodes.next();
                if (node instanceof FunctionNode && ((FunctionNode) node).getFunctionName() != null
                        && ((FunctionNode) node).getFunctionName().getIdentifier() != null
                        && !((FunctionNode) node).getFunctionName().getIdentifier().isEmpty()) {
                    return ((FunctionNode) node);
                }
            }
            return null;
        } else {
            return null;
        }
    }

    public static String getAppElementIdByAnnotation(File aFile) {
        try {
            String fileContent = FileUtils.readString(aFile, PlatypusFiles.DEFAULT_ENCODING);
            if (aFile.getPath().endsWith("." + PlatypusFiles.JAVASCRIPT_EXTENSION)) {
                return extractFirstFunctionName(fileContent);
            } else {
                return getAnnotationValue(fileContent, APP_ELEMENT_NAME_ANNOTATION);
            }
        } catch (IOException ex) {
            Logger.getLogger(PlatypusFiles.class.getName()).log(Level.INFO, null, ex);
        }
        return null;
    }

    public static String getAppElementIdForConnectionAppElement(File file) {
        try {
            EasSettings connectionSettings = XmlDom2ConnectionSettings.document2Settings(Source2XmlDom.transform(FileUtils.readString(file, PlatypusFiles.DEFAULT_ENCODING)));
            if (connectionSettings != null) {
                return connectionSettings.getName();
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusFilesSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

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
            String[] lines = docComment.split(System.getProperty("line.separator")); //NOI18N
            for (String line : lines) {
                if (line.contains(aAnnotationName)) {
                    String[] tokens = line.split("\\s");  //NOI18N
                    for (int i = 0; i < tokens.length; i++) {
                        if (tokens[i].endsWith(aAnnotationName)) {
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
                docComment = StringUtils.join(System.getProperty("line.separator"), list.toArray(new String[]{})); //NOI18N
                return matcher.replaceFirst(docComment);
            }
        }
        list.add("/**"); //NOI18N
        list.add(" * " + aAnnotationName + " " + aValue); //NOI18N
        list.add(" */" + System.getProperty("line.separator")); //NOI18N
        String docComment = StringUtils.join(System.getProperty("line.separator"), list.toArray(new String[]{}));
        return docComment + aContent;
    }

    public static String getMainJsDocBody(String aContent) {
        Pattern pattern = Pattern.compile(getAnnotatedDocRegexStr(APP_ELEMENT_NAME_ANNOTATION));
        Matcher matcher = pattern.matcher(aContent);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    private static String getAnnotatedDocRegexStr(String anAnnotation) {
        return NAMED_ANNOTATION_PATTERN_1 + anAnnotation + NAMED_ANNOTATION_PATTERN_2;
    }
}
