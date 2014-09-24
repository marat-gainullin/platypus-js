/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class ModuleUtils {

    public static final int DEFAULT_NUMBER_OF_SPACES_PER_INDENT = 4;
    public static final String FUNCTION_HEADER = " = function(event) {\n";//NOI18N
    public static final String FUNCTION_FOOTER = "};\n";//NOI18N
    
    private static final Map<String, Class<?>> scriptNames2PlatypusApiClasses = new HashMap<>();
    private static final Class[] apiClasses = {
        com.eas.client.scripts.ScriptedResource.class
    };

    static {
        initScriptNames2PlatypusApiClasses();
    }
    
    public static Class[] getPlatypusApiClasses() {
        return apiClasses;
    }

    public static Class<?> getPlatypusApiClassByName(String name) {
        return scriptNames2PlatypusApiClasses.get(name);
    }
    
    private static void initScriptNames2PlatypusApiClasses() {
        for (Class<?> clazz : apiClasses) {
            scriptNames2PlatypusApiClasses.put(getScriptConstructorName(clazz), clazz);
        }
    }

    public static String getScriptConstructorName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ScriptObj.class)) {
            ScriptObj objectInfo = clazz.getAnnotation(ScriptObj.class);
            if (!objectInfo.name().isEmpty()) {
                return objectInfo.name();
            }
        }
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.isAnnotationPresent(ScriptFunction.class)) {
                ScriptFunction scriptInfo = constructor.getAnnotation(ScriptFunction.class);
                if (!scriptInfo.name().isEmpty()) {
                    return scriptInfo.name();
                }
            }
        }
        return clazz.getSimpleName();
    }
    
    public static String getEventHandlerObjectJs(String obj, String handlerName) {
        StringBuilder sb = new StringBuilder();
        sb.append(getIndent());
        sb.append(obj);
        sb.append(getEventHandlerJs(handlerName));
        return sb.toString();
    }
    
    public static String getEventHandlerJs(String handlerName) {
        return getEventHandlerJs(handlerName, getIndent());
    }
        
    public static String getEventHandlerJs(String handlerName, String tabs) {
        StringBuilder sb = new StringBuilder();
        sb.append(handlerName);
        sb.append(FUNCTION_HEADER);
        sb.append(tabs);
        sb.append(getIndent());
        sb.append(getHandlerBody());
        sb.append("\n");//NOI18N
        sb.append(tabs);
        sb.append(FUNCTION_FOOTER);
        return sb.toString();
    }
    
    public static String getHandlerBody() {
        return NbBundle.getMessage(ModuleUtils.class, "MSG_EventHandlerBody");//NOI18N
    }
   
    public static String getLineTabs(StyledDocument doc, int startOffset) {
        int i = startOffset;
        String s;
        try {
            do {

                s = doc.getText(i, 1);
                if ((i > 0 && !"\n".equals(s))) {//NOI18N
                    i--;
                } else {
                    break;
                }

            } while (true);
            StringBuilder tabs = new StringBuilder();
            i++;
            do {
                s = doc.getText(i, 1);
                if (" ".equals(s) || "\t".equals(s)) {//NOI18N
                    tabs.append(s);
                } else if (Character.isJavaIdentifierPart(s.charAt(0))) {
                    break;
                }
                i++;

            } while (i < doc.getLength());
            return tabs.toString();
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);//should never happen
        }
    }

    public static boolean isLineEndClear(StyledDocument doc, int pos) {
        String s;
        int i = pos;
        try {
            while (i < doc.getLength()) {
                s = doc.getText(i, 1);
                if ("\r".equals(s) || "\n".equals(s)) {//NOI18N
                    return true;
                } else if (Character.isJavaIdentifierPart(s.charAt(0))) {
                    return false;
                }
                i++;
            }
            return true;
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }
        
    public static String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getNumberOfSpacesPerIndent();i++) {
            sb.append(" ");//NOI18N
        }
        return sb.toString();
    }
    
    
    public static int getNumberOfSpacesPerIndent() {
       return DEFAULT_NUMBER_OF_SPACES_PER_INDENT; //TODO read the NB editor's formating Number of Spaces per Indent value.
    }
}
