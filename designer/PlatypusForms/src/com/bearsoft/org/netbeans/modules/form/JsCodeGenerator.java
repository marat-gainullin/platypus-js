/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.eas.client.cache.PlatypusFilesSupport;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.util.Exceptions;

/**
 * The code generator to produce JS code for the form source.
 *
 * @author vv
 */
public class JsCodeGenerator {
    
    public static final String FORM_OBJECT_NAME = "form";//NOI18N
    public static final String THIS_OBJECT_NAME = "this";//NOI18N
    private static final JsCodeGenerator INSTANCE = new JsCodeGenerator();
    
    public static final JsCodeGenerator getInstance() {
        return INSTANCE;
    }
    
    public void generateEventHandler(String componentName, Class<?> scriptClass, PlatypusFormDataObject dataObject) {
        if (isFormObjectExistsInJs(dataObject.getAst())) {
            String defaultHandlerName = getDefaultEventPropertyName(scriptClass);
            int handlerPosition;
            if (defaultHandlerName != null) {
                handlerPosition = findHandlerPosition(componentName, defaultHandlerName, dataObject);
                if (handlerPosition == -1) {
                    handlerPosition = insertEventHandler(defaultHandlerName, dataObject);
                }
                goToEventHandler(handlerPosition, dataObject);
            }
        }
    }
    
    public boolean hasDefaultEventHandler(Class<?> scriptClass) {
        return getDefaultEventPropertyName(scriptClass) != null;
    }
    
    private String getDefaultEventPropertyName(Class<?> scriptClass) {
        return FormUtils.getDefaultEventPropertyName(scriptClass);
    }
    
    private boolean isFormObjectExistsInJs(AstRoot astRoot) {
        FunctionNode constructor = PlatypusFilesSupport.extractModuleConstructor(astRoot);
        if (constructor != null && constructor.getBody() != null) {
            Iterator<Node> constructorTopLevelNodes = constructor.getBody().iterator();
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
                    }
                }
            }
        }
        return -1;
    }
    
    private void goToEventHandler(int handlerPosition, PlatypusFormDataObject dataObject) {
        try {
            getFormSupport(dataObject).openAt(getDocument(dataObject).createPosition(handlerPosition));
        } catch (BadLocationException | IOException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    private Document getDocument(PlatypusFormDataObject dataObject) throws IOException {
        EditorCookie ec = dataObject.getLookup().lookup(EditorCookie.class);
        if (ec == null) {
            return null;
        }
        Document doc = ec.getDocument();
        if (doc == null) {
            doc = ec.openDocument();
        }
        return doc;
    }
    
    private int insertEventHandler(String defaultHandlerName, PlatypusFormDataObject dataObject) {
        return 10;
    }
    
    private PlatypusFormSupport getFormSupport(PlatypusFormDataObject dataObject) {
        return dataObject.getLookup().lookup(PlatypusFormSupport.class);
    }
}
