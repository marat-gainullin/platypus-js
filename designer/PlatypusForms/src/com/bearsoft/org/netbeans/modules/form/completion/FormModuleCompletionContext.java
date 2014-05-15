/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.PersistenceException;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormDataObject;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormSupport;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.CompletionContext;
import com.eas.designer.application.module.completion.CompletionPoint;
import com.eas.designer.application.module.completion.ModuleCompletionContext;
import jdk.nashorn.internal.ir.VarNode;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.ErrorManager;

/**
 *
 * @author vv
 */
public class FormModuleCompletionContext extends ModuleCompletionContext {

    public static final String LOAD_FORM_METHOD_NAME = "loadForm";//NOI18N
    
    public FormModuleCompletionContext(PlatypusModuleDataObject dataObject, Class<? extends Object> aClass) {
        super(dataObject);
    }

    @Override
    public CompletionContext getVarContext(VarNode varNode) {
        CompletionContext cc = super.getVarContext(varNode);
        if (cc != null) {
            return cc;
        }
        if (isSystemObjectMethod(varNode.getAssignmentSource(), LOAD_FORM_METHOD_NAME)) {
            cc = new FormCompletionContext(this);
        } 
        return cc;
    }
    
    @Override
    public void applyCompletionItems(CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        super.applyCompletionItems(point, offset, resultSet);

    }

    protected FormModel getFormModel() {
        PlatypusFormDataObject formDataObject = (PlatypusFormDataObject) getDataObject();
        PlatypusFormSupport support = formDataObject.getLookup().lookup(PlatypusFormSupport.class);
        try {
            support.loadForm();
        } catch (PersistenceException ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return support.getFormModel();

    }
}
