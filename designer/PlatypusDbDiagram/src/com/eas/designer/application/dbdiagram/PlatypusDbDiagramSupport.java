/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram;

import com.eas.client.model.gui.edits.NotSavable;
import com.eas.designer.datamodel.ModelUndoProvider;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.SwingUtilities;
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
import org.openide.cookies.ViewCookie;
import org.openide.loaders.OpenSupport;
import org.openide.util.NbBundle;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author mg
 */
public class PlatypusDbDiagramSupport extends OpenSupport implements OpenCookie, ViewCookie, EditCookie, CloseCookie, SaveCookie, ModelUndoProvider {

    /*
    protected static class Env implements CloneableOpenSupport.Env {

        static final long serialVersionUID = 8453753008783256201L;
        protected PlatypusDbDiagramDataObject dataObject;
        protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
        protected VetoableChangeSupport vetoSupport = new VetoableChangeSupport(this);

        public Env(PlatypusDbDiagramDataObject aDataObject) {
            super();
            dataObject = aDataObject;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener l) {
            changeSupport.addPropertyChangeListener(l);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener l) {
            changeSupport.removePropertyChangeListener(l);
        }

        @Override
        public void addVetoableChangeListener(VetoableChangeListener l) {
            vetoSupport.addVetoableChangeListener(l);
        }

        @Override
        public void removeVetoableChangeListener(VetoableChangeListener l) {
            vetoSupport.removeVetoableChangeListener(l);
        }

        @Override
        public boolean isValid() {
            return dataObject.isValid();
        }

        @Override
        public boolean isModified() {
            return dataObject.isModified();
        }

        @Override
        public void markModified() throws IOException {
            boolean oldValue = isModified();
            dataObject.setModified(true);
            changeSupport.firePropertyChange(PROP_MODIFIED, oldValue, true);
        }

        @Override
        public void unmarkModified() {
            boolean oldValue = isModified();
            dataObject.setModified(false);
            changeSupport.firePropertyChange(PROP_MODIFIED, oldValue, false);
        }

        @Override
        public CloneableOpenSupport findCloneableOpenSupport() {
            return dataObject.getLookup().lookup(PlatypusDbDiagramSupport.class);
        }
    }
    */
    protected UndoRedo.Manager modelUndo;
    protected PlatypusDbDiagramDataObject dataObject;

    public PlatypusDbDiagramSupport(PlatypusDbDiagramDataObject aDataObject) {
        super(aDataObject.getPrimaryEntry());
        dataObject = aDataObject;
        modelUndo = new UndoRedo.Manager() {
            @Override
            public synchronized boolean addEdit(UndoableEdit anEdit) {
                try {
                    if (anEdit.isSignificant() && !(anEdit instanceof NotSavable)) {
                        env.markModified();
                    }
                    return super.addEdit(anEdit);
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                    return false;
                }
            }

            @Override
            public synchronized void undo() throws CannotUndoException {
                try {
                    if (!(editToBeUndone() instanceof NotSavable)) {
                        env.markModified();
                    }
                    super.undo();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }

            @Override
            public synchronized void redo() throws CannotRedoException {
                try {
                    if (!(editToBeRedone() instanceof NotSavable)) {
                        env.markModified();
                    }
                    super.redo();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        };
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        try {
            PlatypusDbDiagramView view = new PlatypusDbDiagramView();
            view.setDataObject(dataObject);
            return view;
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }

    @Override
    protected String messageOpening() {
        return MessageFormat.format(NbBundle.getMessage(PlatypusDbDiagramSupport.class, "PlatypusDbDiagramOpening"), dataObject.getPrimaryFile().getName());
    }

    @Override
    protected String messageOpened() {
        return MessageFormat.format(NbBundle.getMessage(PlatypusDbDiagramSupport.class, "PlatypusDbDiagramOpened"), dataObject.getPrimaryFile().getName());
    }

    @Override
    public void save() throws IOException {
        try {
            if (dataObject.isModified()) {
                dataObject.saveModel();
                env.unmarkModified();
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    protected boolean canClose() {
        if (dataObject.isModified()) {
            String confirmationString = NbBundle.getMessage(PlatypusDbDiagramSupport.class, "saveConfirmation", dataObject.getPrimaryFile().getName());
            NotifyDescriptor.Confirmation confirm = new NotifyDescriptor.Confirmation(confirmationString, NotifyDescriptor.Confirmation.YES_NO_CANCEL_OPTION, NotifyDescriptor.Confirmation.QUESTION_MESSAGE);
            Object res = DialogDisplayer.getDefault().notify(confirm);
            if (NotifyDescriptor.YES_OPTION.equals(res)) {
                try {
                    save();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
                return true;
            } else if (NotifyDescriptor.NO_OPTION.equals(res)) {
                env.unmarkModified();
                return true;
            } else {
                return false;
            }
        } else {
            return super.canClose();
        }
    }

    @Override
    public UndoRedo.Manager getModelUndo() {
        return modelUndo;
    }

    public List<CloneableTopComponent> getAllViews() {
        List<CloneableTopComponent> comps = new ArrayList<>();
        Enumeration en = allEditors.getComponents();
        while (en.hasMoreElements()) {
            comps.add((CloneableTopComponent) en.nextElement());
        }
        return comps;
    }

    public void shrink() {
        List<CloneableTopComponent> views = getAllViews();
        if (views == null || views.isEmpty()) {
            // Take care of memory consumption.
            dataObject.shrink();
            // Shrink has removed all parsed data from the data object.
            modelUndo.discardAllEdits();
        }
    }

    public void closeAllViews() {
        SwingUtilities.invokeLater(() -> {
            modelUndo.discardAllEdits();
            List<CloneableTopComponent> views = getAllViews();
            views.stream().forEach((view) -> {
                view.close();
            });
            // Take care of memory consumption.
            dataObject.shrink();
        });
    }
}
