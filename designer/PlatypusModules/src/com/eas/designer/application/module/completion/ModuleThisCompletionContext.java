/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.completion;

import com.eas.client.model.application.ApplicationDbEntity;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class ModuleThisCompletionContext extends CompletionContext {
    private ModuleCompletionContext parentContext;

    public ModuleThisCompletionContext(ModuleCompletionContext aParentContext) {
        super(aParentContext.getScriptClass());
        parentContext = aParentContext;
    }

    public ModuleCompletionContext getParentContext() {
        return parentContext;
    }

    @Override
    public CompletionContext getChildContext(String fieldName, int offset) throws Exception {
        switch (fieldName) {
            case MODEL_SCRIPT_NAME:
                {
                    return new ModelCompletionContext(parentContext.getDataObject());
                }
            case PARAMS_SCRIPT_NAME:
                {
                    return new EntityCompletionContext(parentContext.getDataObject().getModel().getParametersEntity());
                }
        }
        ApplicationDbEntity entity = parentContext.getDataObject().getModel().getEntityByName(fieldName);
        if (entity != null) {
            return new EntityCompletionContext(entity);
        }
        return null;
    }

    @Override
    public void applyCompletionItems(JsCompletionProvider.CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        super.applyCompletionItems(point, offset, resultSet);
        ModuleCompletionContext.JsCodeCompletionScopeInfo completionScopeInfo = ModuleCompletionContext.getCompletionScopeInfo(parentContext.getDataObject(), offset, point.filter);
        if (completionScopeInfo.mode == ModuleCompletionContext.CompletionMode.VARIABLES_AND_FUNCTIONS) {
            fillVariablesAndFunctions(point, resultSet);
        }
    }

    protected void fillVariablesAndFunctions(JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) throws Exception {
        fillFieldsValues(parentContext.getDataObject().getModel().getParametersEntity().getFields(), point, resultSet);
        fillEntities(parentContext.getDataObject().getModel().getEntities().values(), resultSet, point);
        addItem(resultSet, point.filter, new BeanCompletionItem(parentContext.getDataObject().getModel().getClass(), MODEL_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset));
        addItem(resultSet, point.filter, new BeanCompletionItem(parentContext.getDataObject().getModel().getParametersEntity().getRowset().getClass(), PARAMS_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset));
        fillJavaCompletionItems(point, resultSet);
    }
    
}
