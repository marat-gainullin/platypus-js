/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.store.ApplicationModel2XmlDom;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.module.nodes.ApplicationModelNodeChildren;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.datamodel.ModelUndoProvider;
import com.eas.designer.datamodel.nodes.ModelNode;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.UndoRedo;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.loaders.OpenSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author mg
 */
public class PlatypusModelSupport extends OpenSupport implements
        OpenCookie, EditCookie, SaveCookie, CloseCookie, ModelProvider, ModelUndoProvider, ModelModifiedProvider {

    protected PlatypusModelDataObject dataObject;
    protected ApplicationDbModel model;
    protected ModelNode<ApplicationDbEntity, ApplicationDbModel> modelNode;
    protected UndoRedo.Manager modelUndo;

    public PlatypusModelSupport(PlatypusModelDataObject aDataObject) {
        super(aDataObject.getPrimaryEntry());
        dataObject = aDataObject;
        modelUndo = new UndoRedo.Manager() {
            @Override
            public synchronized boolean addEdit(UndoableEdit anEdit) {
                try {
                    if (anEdit.isSignificant()) {
                        notifyModified();
                    }
                    return super.addEdit(anEdit);
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                    return false;
                }
            }

            @Override
            public synchronized void undo() throws CannotUndoException {
                super.undo();
                notifyModified();
            }

            @Override
            public synchronized void redo() throws CannotRedoException {
                super.redo();
                notifyModified();
            }
        };
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        try {
            return new PlatypusDatamodelView(dataObject);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    protected boolean canClose() {
        if (dataObject.isModified()) {
            String confirmationString = NbBundle.getMessage(PlatypusModelSupport.class, "MSG_SaveConfirmation", dataObject.getPrimaryFile().getName());
            NotifyDescriptor.Confirmation confirm = new NotifyDescriptor.Confirmation(confirmationString, NotifyDescriptor.Confirmation.YES_NO_CANCEL_OPTION, NotifyDescriptor.Confirmation.QUESTION_MESSAGE);
            Object res = DialogDisplayer.getDefault().notify(confirm);
            if (NotifyDescriptor.YES_OPTION.equals(res)) {
                try {
                    save();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
                model = null;
                modelNode = null;
                return true;
            } else if (NotifyDescriptor.NO_OPTION.equals(res)) {
                env.unmarkModified();
                model = null;
                modelNode = null;
                return true;
            } else {
                return false;
            }
        } else {
            model = null;
            modelNode = null;
            return true;
        }
    }

    @Override
    public void save() throws IOException {
        if (model != null) {
            org.w3c.dom.Document doc = ApplicationModel2XmlDom.transform(model);
            String modelContent = XmlDom2String.transform(doc);
            try (OutputStream out = dataObject.getPrimaryFile().getOutputStream()) {
                out.write(modelContent.getBytes(PlatypusUtils.COMMON_ENCODING_NAME));
                out.flush();
            }
        }
        notifyUnmodified();
    }

    @Override
    public ApplicationDbModel getModel() throws Exception {
        checkModelRead();
        return model;
    }

    @Override
    public UndoRedo.Manager getModelUndo() {
        return modelUndo;
    }

    private void checkModelRead() throws Exception {
        PlatypusProject project = dataObject.getProject();
        assert project != null : "Project is null.";
        if (model == null) {
            model = readModel();
            modelNode = createModelNode();
        }
    }

    protected ModelNode<ApplicationDbEntity, ApplicationDbModel> createModelNode() {
        return new ModelNode<>(new ApplicationModelNodeChildren(model,
                modelUndo,
                dataObject.getLookup()), dataObject);
    }

    @Override
    public ModelNode<ApplicationDbEntity, ApplicationDbModel> getModelNode() throws Exception {
        checkModelRead();
        return modelNode;
    }

    protected ApplicationDbModel readModel() throws Exception {
        String modelContent = dataObject.getPrimaryFile().asText(PlatypusUtils.COMMON_ENCODING_NAME);
        org.w3c.dom.Document doc = Source2XmlDom.transform(modelContent);
        ApplicationDbModel modelRead = new ApplicationDbModel(dataObject.getProject().getBasesProxy(), dataObject.getProject().getQueries());
        modelRead.accept(new XmlDom2ApplicationModel<>(doc));
        modelRead.getEntities().values().stream().forEach((ApplicationDbEntity aEntity) -> {
            aEntity.setPublished(new EntityJSObject(aEntity));
        });
        modelRead.setPublished(new ModelJSObject(modelRead));
        return modelRead;
    }

    public boolean notifyModified() {
        try {
            env.markModified();
            return true;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    public void notifyUnmodified() {
        env.unmarkModified();
    }

    @Override
    public boolean isModelModified() {
        return env.isModified();
    }

    @Override
    public void setModelModified(boolean aValue) {
        if (aValue) {
            notifyModified();
        } else {
            notifyUnmodified();
        }
    }
}
