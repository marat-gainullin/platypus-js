/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.eas.script.EventMethod;
import com.eas.util.PropertiesUtils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.Position;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

/**
 * The code generator to produce JS code for the form source.
 *
 * @author vv
 */
public class JsCodeGenerator {

    private static final JsCodeGenerator INSTANCE = new JsCodeGenerator();
    private final Map<Class<?>, String> eventHandlersNames = new HashMap<>();
    
    public static final JsCodeGenerator getInstance() {
        return INSTANCE;
    }

    public void generateEventHandler(String componentName, Class<?> scriptClass, PlatypusFormDataObject dataObject) {
        String defaultHandlerName = getDefaultEventHandler(scriptClass);
        Position handlerPosition;
        if (defaultHandlerName != null) {
            handlerPosition = findHandler(defaultHandlerName, dataObject);
            if (handlerPosition == null) {
                handlerPosition = insertEventHandler(defaultHandlerName, dataObject);
            }
            goToEventHandler(handlerPosition);
        }
    }

    public boolean hasDefaultEventHandler(Class<?> scriptClass) {
        return getDefaultEventHandler(scriptClass) != null;
    }

    private String getDefaultEventHandler(Class<?> scriptClass) {
        String handlerName = eventHandlersNames.get(scriptClass);
        if (handlerName == null) {
            handlerName = getDefaultEventHandlerImpl(scriptClass);
            if (handlerName != null) {
                eventHandlersNames.put(scriptClass, handlerName);
            }
        }
        return handlerName;
    }

    private String getDefaultEventHandlerImpl(Class<?> scriptClass) {
        for (Method method : scriptClass.getMethods()) {
            if (method.isAnnotationPresent(EventMethod.class)) {
                EventMethod em = method.getAnnotation(EventMethod.class);
                if (em.isDefaultEvent()) {
                    return PropertiesUtils.getPropertyName(method.getName());
                }
            }
        }
        return null;
    }
    
    private Position findHandler(final String defaultHandlerName, PlatypusFormDataObject dataObject) {
        Position pos = null;
        dataObject.getAst().visit(new NodeVisitor() {

            @Override
            public boolean visit(AstNode an) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        return pos;
    }

    private void goToEventHandler(Position handlerPosition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Position insertEventHandler(String defaultHandlerName, PlatypusFormDataObject dataObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
