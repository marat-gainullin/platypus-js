/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.eas.designer.application.module.ModuleUtils;
import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.openide.cookies.EditorCookie;

/**
 * The code generator to produce JS code for the form source.
 *
 * @author vv
 */
public class JsCodeGenerator {

    public static final String FORM_OBJECT_NAME = "form";//NOI18N
    //public static final String THIS_OBJECT_NAME = "this";//NOI18N
    private static final int NOT_FOUND = -1;
    private static final JsCodeGenerator INSTANCE = new JsCodeGenerator();

    public static final JsCodeGenerator getInstance() {
        return INSTANCE;
    }

    public void generateEventHandler(String componentName, Class<?> scriptClass, PlatypusFormDataObject aDataObject) {
        /*
        if (isFormObjectExistsInJs(aDataObject.getConstructor())) {
            String handlerName = getDefaultEventPropertyName(scriptClass);
            int handlerPosition;
            if (handlerName != null) {
                handlerPosition = findHandlerPosition(componentName, handlerName, aDataObject);
                try {
                    if (handlerPosition == NOT_FOUND) {
                        FunctionNode ctor = aDataObject.getConstructor();
                        int insertPosition = ctor.getFinish() - 1;
                        String objAndProp = getObjectAndProp(FORM_OBJECT_NAME, componentName);
                        insertEventHandler(objAndProp, handlerName, insertPosition, aDataObject);
                        goToEventHandler(insertPosition
                                + ModuleUtils.getNumberOfSpacesPerIndent()
                                + objAndProp.length()
                                + handlerName.length()
                                + ModuleUtils.FUNCTION_HEADER.length()
                                + ModuleUtils.getNumberOfSpacesPerIndent() * 2, aDataObject);
                    } else {
                        goToEventHandler(handlerPosition
                                + ModuleUtils.FUNCTION_HEADER.length()
                                + ModuleUtils.getNumberOfSpacesPerIndent() * 2, aDataObject);
                    }
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }
*/
    }

    public boolean hasDefaultEventHandler(Class<?> scriptClass) {
        return getDefaultEventPropertyName(scriptClass) != null;
    }

    private String getDefaultEventPropertyName(Class<?> scriptClass) {
        return FormUtils.getDefaultEventPropertyName(scriptClass);
    }
/*
    private boolean isFormObjectExistsInJs(FunctionNode constructor) {
        if (constructor != null && constructor.getBody() != null) {
            for (Statement st : constructor.getBody().getStatements()) {
                if (st instanceof VarNode) {
                    VarNode vn = (VarNode) st;
                    if (FORM_OBJECT_NAME.equals(vn.getName().getName())) {
                        if (vn.getInit() instanceof CallNode) {
                            CallNode cn = (CallNode) vn.getInit();
                            return FormModuleCompletionContext.isSystemObjectMethod(cn, FormModuleCompletionContext.LOAD_FORM_METHOD_NAME);
                        }
                    }
                }
            }
        }
        return false;
    }

    private int findHandlerPosition(String componentName, String handlerName, PlatypusFormDataObject dataObject) {
        FunctionNode constructor = dataObject.getConstructor();
        if (constructor != null && constructor.getBody() != null) {
            for (Statement st : constructor.getBody().getStatements()) {
                if (st instanceof ExpressionStatement && ((ExpressionStatement) st).getExpression() instanceof BinaryNode) {
                    BinaryNode a = (BinaryNode) ((ExpressionStatement) st).getExpression();
                    if (a.isAssignment() && a.getAssignmentDest() instanceof AccessNode) {
                        AccessNode pg = (AccessNode) a.getAssignmentDest();
                        if (handlerName.equals(pg.getProperty())) {
                            if (pg.getBase() instanceof AccessNode) {
                                AccessNode componentPg = (AccessNode) pg.getBase();
                                if (componentName.equals(componentPg.getProperty())
                                        && componentPg.getBase() instanceof IdentNode
                                        && FORM_OBJECT_NAME.equals(((IdentNode) componentPg.getBase()).getName())) {
                                    if (a.getAssignmentSource() instanceof FunctionNode) {
                                        FunctionNode handlerFn = (FunctionNode) a.getAssignmentSource();
                                        return handlerFn.getStart();
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
*/
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
