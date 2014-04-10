/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.bearsoft.org.netbeans.modules.form.Event;
import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.PersistenceException;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormDataObject;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormSupport;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.eas.client.scripts.ScriptRunner;
import com.eas.designer.application.module.PlatypusModuleDataObject;
import com.eas.designer.application.module.completion.CompletionPoint;
import com.eas.designer.application.module.completion.ModuleCompletionContext;
import com.eas.designer.application.module.completion.ModuleThisCompletionContext;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.ErrorManager;

/**
 *
 * @author vv
 */
public class FormCompletionContext extends ModuleCompletionContext {

    private static final Class EVENT_WRAPPER_CLASS = com.eas.client.forms.api.events.EventsWrapper.class;
    private static final String EVENTS_WRAPPER_METHOD_NAME = "wrap";//NOI18N

    public FormCompletionContext(PlatypusModuleDataObject dataObject, Class<? extends ScriptRunner> aClass) {
        super(dataObject, aClass);
    }

    @Override
    public ModuleThisCompletionContext createThisContext(boolean anEnableJsElementsCompletion) {
        return new FormThisCompletionContext(this, anEnableJsElementsCompletion);
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
