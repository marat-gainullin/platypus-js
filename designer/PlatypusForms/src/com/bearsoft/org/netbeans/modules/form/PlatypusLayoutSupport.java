/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form;

import com.eas.designer.explorer.DataObjectProvider;
import java.io.IOException;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.OpenSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author mg
 */
public class PlatypusLayoutSupport extends OpenSupport implements
        OpenCookie, EditCookie, SaveCookie, CloseCookie, DataObjectProvider, LayoutFileProvider, ModifiedProvider {

    protected PlatypusLayoutDataObject dataObject;
    protected FormEditor formEditor;

    public PlatypusLayoutSupport(PlatypusLayoutDataObject aDataObject) {
        super(aDataObject.getPrimaryEntry());
        dataObject = aDataObject;
    }

    @Override
    public DataObject getDataObject() {
        return dataObject;
    }

    @Override
    public FileObject getLayoutFile() {
        return dataObject.getPrimaryFile();
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        if (formEditor == null) {
            formEditor = new FormEditor(dataObject);
        }
        return new PlatypusFormLayoutView(formEditor);
    }

    @Override
    protected boolean canClose() {
        if (dataObject.isModified()) {
            String confirmationString = NbBundle.getMessage(PlatypusLayoutSupport.class, "MSG_SaveConfirmation", dataObject.getPrimaryFile().getName());
            NotifyDescriptor.Confirmation confirm = new NotifyDescriptor.Confirmation(confirmationString, NotifyDescriptor.Confirmation.YES_NO_CANCEL_OPTION, NotifyDescriptor.Confirmation.QUESTION_MESSAGE);
            Object res = DialogDisplayer.getDefault().notify(confirm);
            if (NotifyDescriptor.YES_OPTION.equals(res)) {
                try {
                    save();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
                formEditor = null;
                return true;
            } else if (NotifyDescriptor.NO_OPTION.equals(res)) {
                env.unmarkModified();
                formEditor = null;
                return true;
            } else {
                return false;
            }
        } else {
            formEditor = null;
            return true;
        }
    }

    @Override
    public void save() throws IOException {
        try {
            formEditor.saveFormData();
            notifyUnmodified();
        } catch (PersistenceException ex) {
            formEditor.reportErrors(FormEditor.FormOperation.SAVING);
            throw new IOException(ex);
        }
    }

    @Override
    public boolean notifyModified() {
        try {
            env.markModified();
            return true;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    @Override
    public void notifyUnmodified() {
        env.unmarkModified();
    }
}
