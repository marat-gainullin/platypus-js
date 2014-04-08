/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionTokenType;
import com.eas.script.ScriptFunction;
import com.eas.util.PropertiesUtils;
import com.eas.util.PropertiesUtils.PropBox;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class CompletionContext {

    private static final int QUOTED_STRING_MIN_LENGTH = 2;
    private final Class<?> scriptClass;

    public CompletionContext(Class<?> aScriptClass) {
        scriptClass = aScriptClass;
    }

    public Class<?> getScriptClass() {
        return scriptClass;
    }

    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        fillJavaCompletionItems(point, resultSet);
    }

    public CompletionContext getChildContext(CompletionToken token, int offset) throws Exception {
        return null;
    }

    protected static void addItem(CompletionResultSet resultSet, String aFilter, JsCompletionItem aCompletionItem) {
        if (aFilter == null || aFilter.isEmpty() || (aCompletionItem.getText().toLowerCase().startsWith(aFilter.toLowerCase()) && !aCompletionItem.getText().equals(aFilter))) {
            if (aFilter == null || !(aFilter.endsWith(")") || aFilter.endsWith("}"))) {//NOI18N
                resultSet.addItem(aCompletionItem);
            }
        }
    }

    protected void fillJavaCompletionItems(CompletionPoint point, CompletionResultSet resultSet) {
        Map<String, PropBox> props = new HashMap<>();
        List<Method> methods = new ArrayList<>();
        if (scriptClass != null) {
            for (Method method : scriptClass.getMethods()) {
                if (method.isAnnotationPresent(ScriptFunction.class)) {
                    if (PropertiesUtils.isBeanPatternMethod(method)) {
                        String propName = PropertiesUtils.getPropertyName(method.getName());
                        if (point.getFilter() == null || point.getFilter().isEmpty() || propName.startsWith(point.getFilter())) {
                            PropBox pb = props.get(propName);
                            if (pb == null) {
                                pb = new PropBox();
                                pb.name = propName;
                                props.put(pb.name, pb);
                            }
                            PropertiesUtils.setPropertyAccessStatus(pb, method.getName());
                            PropertiesUtils.setPropertyReturnType(pb, method);
                            PropertiesUtils.setPropertyEventClass(pb, method);
                            if (pb.jsDoc == null || pb.jsDoc.isEmpty()) {
                                pb.jsDoc = method.getAnnotation(ScriptFunction.class).jsDoc();
                            }
                        }
                    } else if (point.getFilter() == null || point.getFilter().isEmpty() || method.getName().startsWith(point.getFilter())) {
                        methods.add(method);
                    }
                }
            }
            for (PropBox pb : props.values()) {
                resultSet.addItem(new PropertyCompletionItem(pb, point.getCaretBeginWordOffset(), point.getCaretEndWordOffset()));
            }
            for (Method method : methods) {
                List<String> parameters = new ArrayList<>();
                String[] params = method.getAnnotation(ScriptFunction.class).params();
                if (params != null) {
                    parameters.addAll(Arrays.asList(params));
                }
                JsFunctionCompletionItem functionCompletionItem = new JsFunctionCompletionItem(
                        method.getName(),
                        PropertiesUtils.getTypeName(method.getReturnType()),
                        parameters, method.getAnnotation(ScriptFunction.class).jsDoc(),
                        point.getCaretBeginWordOffset(),
                        point.getCaretEndWordOffset(),
                        true);
                resultSet.addItem(functionCompletionItem);
            }
        }
    }

    protected static boolean isPropertyGet(CompletionToken token, String propertyName) {
        return (CompletionTokenType.PROPERTY_GET == token.type && propertyName.equals(token.name))
                || (CompletionTokenType.ELEMENT_GET == token.type && isQuotedString(token.name) && propertyName.equals(removeQuotes(token.name)));
    }

    protected static boolean isQuotedString(String str) {
        return str != null & str.length() > QUOTED_STRING_MIN_LENGTH
                && ((str.startsWith("\"") && str.endsWith("\"")) || (str.startsWith("'") && str.endsWith("'")));//NOI18N
    }

    private static String removeQuotes(String str) {
        return str.substring(1, str.length() - 1);
    }
}
