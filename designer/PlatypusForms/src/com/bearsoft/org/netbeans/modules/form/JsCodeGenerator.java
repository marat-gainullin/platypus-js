/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.designer.application.module.ModuleUtils;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;

/**
 * The code generator to produce JS code for the form source.
 *
 * @author vv
 */
public class JsCodeGenerator {

    public static final String FORM_OBJECT_NAME = "form";//NOI18N
    public static final String THIS_OBJECT_NAME = "this";//NOI18N
    private static final int NOT_FOUND = -1;
    private static final JsCodeGenerator INSTANCE = new JsCodeGenerator();

    public static final JsCodeGenerator getInstance() {
        return INSTANCE;
    }

    public void generateEventHandler(String componentName, Class<?> scriptClass, PlatypusFormDataObject dataObject) {
        if (isFormObjectExistsInJs(dataObject.getAst())) {
            String handlerName = getDefaultEventPropertyName(scriptClass);
            int handlerPosition;
            if (handlerName != null) {
                handlerPosition = findHandlerPosition(componentName, handlerName, dataObject);
                try {
                    if (handlerPosition == NOT_FOUND) {
                        AstNode ctor = PlatypusFilesSupport.extractModuleConstructor(dataObject.getAst());
                        int insertPosition = ctor.getAbsolutePosition() + ctor.getLength() - 1;
                        String objAndProp = getObjectAndProp(FORM_OBJECT_NAME, componentName);
                        insertEventHandler(objAndProp, handlerName, insertPosition, dataObject);
                        goToEventHandler(insertPosition
                                + ModuleUtils.getNumberOfSpacesPerIndent()
                                + objAndProp.length()
                                + handlerName.length()
                                + ModuleUtils.FUNCTION_HEADER.length()
                                + ModuleUtils.getNumberOfSpacesPerIndent()*2
                                , dataObject);
                    } else {
                        goToEventHandler(handlerPosition
                                + ModuleUtils.FUNCTION_HEADER.length()
                                + ModuleUtils.getNumberOfSpacesPerIndent()*2
                                , dataObject);
                    }  
                } catch (IOException | BadLocationException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
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
                                            return a.getLeft().getAbsolutePosition() + a.getLeft().getLength();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return NOT_FOUND;
    }

    private void goToEventHandler(int handlerPosition, PlatypusFormDataObject dataObject) throws IOException, BadLocationException {
        getFormSupport(dataObject).openAt(getDocument(dataObject).createPosition(handlerPosition));
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

    private void insertEventHandler(String objectName, String defaultHandlerName, int insertPos, PlatypusFormDataObject dataObject) throws IOException, BadLocationException {
        String eventHandlerJS = ModuleUtils.getEventHandlerObjectJs(objectName, defaultHandlerName);
        getDocument(dataObject).insertString(insertPos, eventHandlerJS, null);
    }

    private PlatypusFormSupport getFormSupport(PlatypusFormDataObject dataObject) {
        return dataObject.getLookup().lookup(PlatypusFormSupport.class);
    }

    private String getObjectAndProp(String formObj, String componentName) {
        return String.format("%s.%s.", formObj, componentName);//NOI18N
    }
}
