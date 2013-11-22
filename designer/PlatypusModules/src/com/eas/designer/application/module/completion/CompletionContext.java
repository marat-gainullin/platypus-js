/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.Entity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.script.ScriptFunction;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class CompletionContext {

    protected static final String MODEL_SCRIPT_NAME = "model";// NOI18N
    public static final String PARAMS_SCRIPT_NAME = "params";// NOI18N
    protected static final String METADATA_SCRIPT_NAME = ApplicationDbModel.DATASOURCE_METADATA_SCRIPT_NAME;
    protected static final String MODULE_NAME = "Module";// NOI18N
    protected static final String SERVER_MODULE_NAME = "ServerModule";// NOI18N
    protected static final String FORM_MODULE_NAME = "Form";// NOI18N
    protected static final String REPORT_MODULE_NAME = "Report";// NOI18N
    protected static final String MODULES_OBJECT_NAME = "Modules";// NOI18N
    protected static final String GET_METHOD_NAME = "get";// NOI18N
    protected static final String BEANY_PREFIX_GET = "get";// NOI18N
    protected static final String BEANY_PREFIX_SET = "set";// NOI18N
    protected static final String BEANY_PREFIX_IS = "is";// NOI18N
    protected Class<?> scriptClass;

    public CompletionContext(Class<?> aScriptClass) {
        scriptClass = aScriptClass;
    }

    public Class<?> getScriptClass() {
        return scriptClass;
    }

    public void applyCompletionItems(JsCompletionProvider.CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        fillJavaCompletionItems(point, resultSet);
    }

    public CompletionContext getChildContext(String str, int offset) throws Exception {
        return null;
    }

    protected void fillFields(Fields aFields, JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) {
        for (Field field : aFields.toCollection()) {
            addItem(resultSet, point.filter, new BeanCompletionItem(field.getClass(), field.getName(), field.getDescription(), point.caretBeginWordOffset, point.caretEndWordOffset));
        }
    }

    protected void fillFieldsValues(Fields aFields, JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) {
        for (Field field : aFields.toCollection()) {
            addItem(resultSet, point.filter, new FieldCompletionItem(field, point.caretBeginWordOffset, point.caretEndWordOffset));
        }
    }

    protected void fillEntities(Collection<? extends Entity> entities, CompletionResultSet resultSet, JsCompletionProvider.CompletionPoint point) throws Exception {
        for (Entity appEntity : entities) {
            if (appEntity.getName() != null && !appEntity.getName().isEmpty()) {
                addItem(resultSet, point.filter, new BeanCompletionItem(ScriptableRowset.class, appEntity.getName(), null, point.caretBeginWordOffset, point.caretEndWordOffset));
            }
        }
    }

    protected static void addItem(CompletionResultSet resultSet, String aFilter, JsCompletionItem aCompletionItem) {
        if (aFilter == null || aFilter.isEmpty() || aCompletionItem.getText().toLowerCase().startsWith(aFilter.toLowerCase())) {
            resultSet.addItem(aCompletionItem);
        }
    }

    protected void fillJavaCompletionItems(JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) {
        Map<String, PropBox> props = new HashMap<>();
        List<Method> methods = new ArrayList<>();
        if (scriptClass != null) {
            for (Method method : scriptClass.getMethods()) {
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
                            if (pb.jsDoc == null || pb.jsDoc.isEmpty()) {
                                pb.jsDoc = method.getAnnotation(ScriptFunction.class).jsDoc();
                            }
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
                String[] params = method.getAnnotation(ScriptFunction.class).params();
                if (params != null) {
                    for (String param : params) {
                        parameters.add(param);
                    }
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

    private static boolean isNumberClass(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz)
                || Byte.TYPE.equals(clazz)
                || Short.TYPE.equals(clazz)
                || Integer.TYPE.equals(clazz)
                || Long.TYPE.equals(clazz)
                || Float.TYPE.equals(clazz)
                || Double.TYPE.equals(clazz);
    }

    private static void setPropertyReturnType(PropBox pb, Method method) {
        String typeName = getTypeName(method.getReturnType());
        if (typeName != null) {
            pb.typeName = typeName;
        }
    }

    private static void setPropertyAccessStatus(PropBox pb, String methodName) {
        if (methodName.startsWith(BEANY_PREFIX_GET) || methodName.startsWith(BEANY_PREFIX_IS)) {
            pb.readable = true;
        } else if (methodName.startsWith(BEANY_PREFIX_SET)) {
            pb.writeable = true;
        }
    }

    private static String getTypeName(Class<?> aType) {
        if (!aType.equals(Void.TYPE)) {
            if (isNumberClass(aType)) {
                return "Number"; //NOI18N
            } else if (Boolean.class.isAssignableFrom(aType) || Boolean.TYPE.equals(aType)) {
                return "Boolean"; //NOI18N
            } else if (aType.isArray()) {
                Class<?> cl = aType;
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
                return aType.getSimpleName();
            }
        }
        return null;
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
