/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.script.EventMethod;
import com.eas.util.PropertiesUtils;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.text.Position;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

/**
 * The code generator to produce JS code for the form source.
 *
 * @author vv
 */
public class JsCodeGenerator {
    
    public static final String FORM_OBJECT_NAME = "form";//NOI18N
    public static final String THIS_OBJECT_NAME = "this";//NOI18N
    private static final JsCodeGenerator INSTANCE = new JsCodeGenerator();
    private final Map<Class<?>, String> eventHandlersNames = new HashMap<>();
    
    public static final JsCodeGenerator getInstance() {
        return INSTANCE;
    }
    
    public void generateEventHandler(String componentName, Class<?> scriptClass, PlatypusFormDataObject dataObject) {
        if (isFormObjectExistsInJs(dataObject.getAst())) {
            String defaultHandlerName = getDefaultEventHandler(scriptClass);
            int handlerPosition;
            if (defaultHandlerName != null) {
                handlerPosition = findHandlerPosition(componentName, defaultHandlerName, dataObject);
                if (handlerPosition != -1) {
                    handlerPosition = insertEventHandler(defaultHandlerName, dataObject);
                }
                goToEventHandler(handlerPosition);
            }
        }
    }
    
    public boolean hasDefaultEventHandler(Class<?> scriptClass) {
        return getDefaultEventHandler(scriptClass) != null;
    }
    
    private String getDefaultEventHandler(Class<?> scriptClass) {
        
//        String handlerName = eventHandlersNames.get(scriptClass);
//        if (handlerName == null) {
//            handlerName = getDefaultEventHandlerImpl(scriptClass);
//            if (handlerName != null) {
//                eventHandlersNames.put(scriptClass, handlerName);
//            }
//        }
        return "onActionPerformed";
    }
    
    private boolean isFormObjectExistsInJs(AstRoot astRoot) {
        FunctionNode constructor = PlatypusFilesSupport.extractModuleConstructor(astRoot);
        if (constructor != null && constructor.getBody() != null) {
            Iterator<Node> constructorTopLevelNodes = PlatypusFilesSupport.extractModuleConstructor(astRoot).getBody().iterator();
            while (constructorTopLevelNodes.hasNext()) {
                Node n = constructorTopLevelNodes.next();
                if (n instanceof VariableDeclaration) {
                    VariableDeclaration vd = (VariableDeclaration) n;
                    for (VariableInitializer vi : vd.getVariables()) {
                        if (getFormObjectName().equals(vi.getTarget().toSource()) && THIS_OBJECT_NAME.equals(vi.getInitializer().toSource())) {
                            return true;
                        }
                    }
                    
                }
            }
        }
        return false;
    }
    
    private String getFormObjectName() {
        return FORM_OBJECT_NAME;
    }
    
    private String getDefaultEventHandlerImpl(Class<?> scriptClass) {
        for (Method method : scriptClass.getMethods()) {
            if (method.isAnnotationPresent(EventMethod.class)) {
                EventMethod em = method.getAnnotation(EventMethod.class);
                return PropertiesUtils.getPropertyName(method.getName());
            }
        }
        return null;
    }
    
    private int findHandlerPosition(String componentName, String handlerName, PlatypusFormDataObject dataObject) {
        AstRoot astRoot = dataObject.getAst();
        FunctionNode constructor = PlatypusFilesSupport.extractModuleConstructor(astRoot);
        if (constructor != null && constructor.getBody() != null) {
            Iterator<Node> constructorTopLevelNodes = PlatypusFilesSupport.extractModuleConstructor(astRoot).getBody().iterator();
            while (constructorTopLevelNodes.hasNext()) {
                Node n = constructorTopLevelNodes.next();
                if (n instanceof ExpressionStatement) {
                    ExpressionStatement es = (ExpressionStatement) n;
                    if (es.getExpression() instanceof Assignment) {
                        Assignment a = (Assignment) es.getExpression();
                        if (a.getLeft() instanceof PropertyGet) {
                            PropertyGet pg = (PropertyGet) a.getLeft();
                            if (handlerName.equals(pg.getProperty().toSource())) {
                                if (pg.getTarget() instanceof PropertyGet) {
                                    PropertyGet componentPg = (PropertyGet) pg.getTarget();
                                    if (componentName.equals(componentPg.getProperty().toSource())
                                            && getFormObjectName().equals(componentPg.getTarget().toSource())) {
                                        if (a.getRight() instanceof FunctionNode) {
                                            return a.getRight().getAbsolutePosition();
                                        }
                                    }
                                }
                            }
                        }
                        System.out.println(n);
                    }
                    
                }
                
            }
        }
        return -1;
    }
    
    private void goToEventHandler(int handlerPosition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private int insertEventHandler(String defaultHandlerName, PlatypusFormDataObject dataObject) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
