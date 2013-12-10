/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionToken;
import com.eas.designer.application.module.completion.CompletionPoint.CompletionTokenType;
import com.eas.designer.application.module.parser.AstUtlities;
import com.eas.script.JsParser;
import com.eas.script.ScriptFunction;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;
import org.netbeans.modules.editor.NbEditorDocument;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.ErrorManager;

/**
 *
 * @author mg
 */
public abstract class JsCompletionProvider implements CompletionProvider {

    private static final String BEANY_PREFIX_GET = "get";
    private static final String BEANY_PREFIX_SET = "set";
    private static final String BEANY_PREFIX_IS = "is";
    private static final String PARAMETER_TEMPLATE = "p%s";// NOI18N
    private static final String JSDOC_SIMPLE_TEMPLATE = ""
            + "/**\n"
            + "* %s\n"
            + "*/";

    @Override
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return CompletionProvider.COMPLETION_QUERY_TYPE;
    }

    @Override
    public CompletionTask createTask(int queryType, JTextComponent component) {
        return createCompletionTask(component);
    }

    public CompletionTask createCompletionTask(JTextComponent component) {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            @Override
            protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
                try {
                    PlatypusModuleDataObject dataObject = (PlatypusModuleDataObject) doc.getProperty(PlatypusModuleDataObject.DATAOBJECT_DOC_PROPERTY);
                    if (doc instanceof NbEditorDocument) {
                        CompletionPoint completionPoint = CompletionPoint.createInstance((NbEditorDocument)doc, caretOffset);
                        fillCompletionPoint(dataObject, completionPoint, resultSet, doc, caretOffset);
                    }
                    resultSet.finish();
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }, component);
    }

    protected static int getStartWordOffset(NbEditorDocument aDoc, int caretOffset) throws Exception {
        while (caretOffset > 0 && aDoc.getLength() > 0
                && (Character.isJavaIdentifierPart(aDoc.getText(caretOffset - 1, 1).toCharArray()[0]))) {
            caretOffset--;
        }
        return caretOffset;
    }

    public static int getEndWordOffset(NbEditorDocument aDoc, int caretOffset) throws BadLocationException {
        while (caretOffset < aDoc.getLength() && aDoc.getLength() > 0
                && Character.isJavaIdentifierPart(aDoc.getText(caretOffset, 1).toCharArray()[0])) {
            caretOffset++;
        }
        return caretOffset;
    }

    protected void addItem(CompletionResultSet resultSet, String aFilter, JsCompletionItem aCompletionItem) {
        if (aFilter == null || aFilter.isEmpty() || aCompletionItem.getText().toLowerCase().startsWith(aFilter.toLowerCase())) {
            resultSet.addItem(aCompletionItem);
        }
    }

    protected abstract void fillCompletionPoint(PlatypusModuleDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet, Document doc, int caretOffset) throws Exception;

    protected void fillJavaEntities(Class<?> aImplClass, CompletionPoint point, CompletionResultSet resultSet) {
        Map<String, PropBox> props = new HashMap<>();
        List<Method> methods = new ArrayList<>();
        if (aImplClass != null) {
            for (Method method : aImplClass.getMethods()) {
                if (method.isAnnotationPresent(ScriptFunction.class)) {
                    if (isBeanPatternMethod(method)) {
                        String propName = getPropertyName(method.getName());
                        if (point.filter == null || point.filter.isEmpty() || propName.startsWith(point.filter)) {
                            PropBox pb = props.get(propName);
                            if (pb == null) {
                                pb = new PropBox();
                                pb.name = propName;
                                props.put(pb.name, pb);
                            }
                            setPropertyAccessStatus(pb, method.getName());
                            setPropertyReturnType(pb, method);
                            pb.jsDoc = method.getAnnotation(ScriptFunction.class).jsDoc();
                        }
                    } else if (point.filter == null || point.filter.isEmpty() || method.getName().startsWith(point.filter)) {
                        methods.add(method);
                    }
                }
            }
            for (PropBox pb : props.values()) {
                resultSet.addItem(new PropertyCompletionItem(pb, point.caretBeginWordOffset, point.caretEndWordOffset));
            }
            for (Method method : methods) {
                List<String> parameters = new ArrayList<>();
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    parameters.add(String.format(PARAMETER_TEMPLATE, i));
                }
                JsFunctionCompletionItem functionCompletionItem = new JsFunctionCompletionItem(
                        method.getName(),
                        getTypeName(method.getReturnType()),
                        parameters, method.getAnnotation(ScriptFunction.class).jsDoc(),
                        point.caretBeginWordOffset,
                        point.caretEndWordOffset,
                        true);
                resultSet.addItem(functionCompletionItem);
            }
        }
    }

    private static void setPropertyReturnType(PropBox pb, Method method) {
        String typeName = getTypeName(method.getReturnType());
        if (typeName != null) {
            pb.typeName = typeName;
        }
    }

//    private static void setJsDoc(PropBox pb, Method method) {
//        String jsDoc = method.getAnnotation(ScriptFunction.class).jsDoc();
//        String jsDocText = method.getAnnotation(ScriptFunction.class).jsDocText();
//        if (jsDoc != null && !jsDoc.isEmpty()) {
//            pb.jsDoc = jsDoc;
//        } else if (jsDocText != null && !jsDocText.isEmpty()) {
//            pb.jsDoc = jsDocText;
//        }
//    }
    private static String getTypeName(Class<?> type) {
        if (!type.equals(Void.TYPE)) {
            if (isNumberClass(type)) {
                return "Number"; //NOI18N
            } else if (Boolean.class.isAssignableFrom(type) || Boolean.TYPE.equals(type)) {
                return "Boolean"; //NOI18N
            } else if (type.isArray()) {
                Class<?> cl = type;
                int dimensions = 0;
                while (cl.isArray()) {
                    dimensions++;
                    cl = cl.getComponentType();
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < dimensions; i++) {
                    sb.append("[]"); //NOI18N
                }
                return sb.toString();
            } else {
                return type.getSimpleName();
            }
        }
        return null;
    }

    private static boolean isNumberClass(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz)
                || Byte.TYPE.equals(clazz)
                || Short.TYPE.equals(clazz)
                || Integer.TYPE.equals(clazz)
                || Long.TYPE.equals(clazz)
                || Float.TYPE.equals(clazz)
                || Double.TYPE.equals(clazz);
    }

    private static void setPropertyAccessStatus(PropBox pb, String methodName) {
        if (methodName.startsWith(BEANY_PREFIX_GET) || methodName.startsWith(BEANY_PREFIX_IS)) {
            pb.readable = true;
        } else if (methodName.startsWith(BEANY_PREFIX_SET)) {
            pb.writeable = true;
        }
    }

    private static String getPropertyName(String methodName) {
        String capitalizedPropName = null;
        if (methodName.startsWith(BEANY_PREFIX_GET) || methodName.startsWith(BEANY_PREFIX_SET)) {
            capitalizedPropName = methodName.substring(3);
            assert !capitalizedPropName.isEmpty();
        } else if (methodName.startsWith(BEANY_PREFIX_IS)) {
            capitalizedPropName = methodName.substring(2);
            assert !capitalizedPropName.isEmpty();
        }
        return capitalizedPropName.substring(0, 1).toLowerCase() + capitalizedPropName.substring(1);
    }

    private static boolean isBeanPatternMethod(Method method) {
        return ((method.getName().startsWith(BEANY_PREFIX_GET) || method.getName().startsWith(BEANY_PREFIX_IS)) && method.getParameterTypes().length == 0)
                || (method.getName().startsWith(BEANY_PREFIX_SET) && method.getParameterTypes().length == 1);
    }
}