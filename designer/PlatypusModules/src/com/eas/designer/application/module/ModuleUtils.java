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
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class ModuleUtils {

    private static final int DEFAULT_NUMBER_OF_SPACES_PER_INDENT = 4;
    private static final String FUNCTION_HEADER = " = function(event) {\n";//NOI18N
    private static final String FUNCTION_FOOTER = "};\n";//NOI18N
    private static final Map<String, Class<?>> scriptNames2PlatypusApiClasses = new HashMap<>();
    private static final Class[] apiClasses = {
        com.eas.client.scripts.PlatypusScriptedResource.class
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
    
    
    public static String getEventHandler(String handlerName, String tabs) {
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

    public static int getEventTemplateCaretPosition(int startOffset, String name, String tabs) {
        return startOffset + name.length() + FUNCTION_HEADER.length() + tabs.length() + getNumberOfSpacesPerIndent() + getHandlerBody().length();
    }
    
    private static String getHandlerBody() {
        return NbBundle.getMessage(ModuleUtils.class, "MSG_EventHandlerBody");//NOI18N
    }
   
    private static String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getNumberOfSpacesPerIndent();i++) {
            sb.append(" ");//NOI18N
        }
        return sb.toString();
    }
    
    
    private static int getNumberOfSpacesPerIndent() {
       return DEFAULT_NUMBER_OF_SPACES_PER_INDENT; //TODO read the NB editor's formating Number of Spaces per Indent value.
    }
}
