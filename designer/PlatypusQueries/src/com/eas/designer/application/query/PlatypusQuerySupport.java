/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query;

import com.eas.designer.application.query.editing.QueryDocumentEditsComplementor;
import com.eas.designer.application.query.editing.SqlTextEditsComplementor;
import com.eas.designer.datamodel.ModelUndoProvider;
import com.eas.designer.explorer.DataObjectProvider;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.UndoRedo;
import org.openide.cookies.*;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.CloneableOpenSupport;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author mg
 */
public class PlatypusQuerySupport extends CloneableOpenSupport implements OpenCookie,
        ViewCookie,
        EditCookie,
        OpenedPaneEditorCookie,
        CloseCookie,
        SaveCookie,
        DataObjectProvider,
        ModelUndoProvider {

    public Rectangle findPlaceForEntityAdd(int aInitialX, int aInitialY) {
        if (!allEditors.isEmpty()) {
            CloneableTopComponent arbitraryEditor = allEditors.getArbitraryComponent();
            return ((PlatypusQueryView) arbitraryEditor).getModelView().findPlaceForEntityAdd(aInitialX, aInitialY);
        } else {
            return null;
        }
    }

    protected static class Env implements CloneableOpenSupport.Env {

        static final long serialVersionUID = 2453753006663253208L;
        protected PlatypusQueryDataObject dataObject;
        protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
        protected VetoableChangeSupport vetoSupport = new VetoableChangeSupport(this);

        public Env(PlatypusQueryDataObject aDataObject) {
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
            return dataObject.getLookup().lookup(PlatypusQuerySupport.class);
        }
    }
    protected UndoRedo.Manager undo;
    protected PlatypusQueryDataObject dataObject;
    protected QueryDocumentEditsComplementor queryDocumentEditsComplementor;
    protected SqlTextEditsComplementor sqlTextEditsComplementor;
    private JEditorPane openedEditorPane;

    public PlatypusQuerySupport(PlatypusQueryDataObject aDataObject) {
        super(new Env(aDataObject));
        dataObject = aDataObject;
        queryDocumentEditsComplementor = new QueryDocumentEditsComplementor(dataObject);
        sqlTextEditsComplementor = new SqlTextEditsComplementor(dataObject);
        undo = new UndoRedo.Manager() {
            @Override
            public void undoableEditHappened(UndoableEditEvent ue) {
                try {
                    if (!queryDocumentEditsComplementor.isComplementing()) {
                        if (ue.getSource() != dataObject && dataObject.getBasesProxy() != null
                                && ue.getEdit() != CloneableEditorSupport.BEGIN_COMMIT_GROUP
                                && ue.getEdit() != CloneableEditorSupport.END_COMMIT_GROUP) {
                            UndoableEdit anEdit;
                            if (ue.getSource() == dataObject.getSqlTextDocument()) {
                                anEdit = complementSqlTextEdit(ue.getEdit());
                            } else {
                                anEdit = complementQueryDocumentEdit(ue.getEdit());
                            }
                            if (anEdit != null) {
                                ue = new UndoableEditEvent(ue.getSource(), anEdit);
                                super.undoableEditHappened(ue);
                            }
                        } else {
                            super.undoableEditHappened(ue);
                        }
                    }
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }

            @Override
            public synchronized boolean addEdit(UndoableEdit anEdit) {
                try {
                    if (anEdit != null) {
                        if (anEdit.isSignificant()) {
                            env.markModified();
                        }
                        return super.addEdit(anEdit);
                    } else {
                        return false;
                    }
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                    return false;
                }
            }

            @Override
            public synchronized void undo() throws CannotUndoException {
                try {
                    super.undo();
                    env.markModified();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }

            @Override
            public synchronized void redo() throws CannotRedoException {
                try {
                    super.redo();
                    env.markModified();
                } catch (IOException ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        };
    }

    @Override
    public PlatypusQueryDataObject getDataObject() {
        return dataObject;
    }

    public UndoRedo.Manager getUndo() {
        return undo;
    }

    @Override
    public UndoRedo.Manager getModelUndo() {
        return undo;
    }

    protected UndoableEdit complementSqlTextEdit(UndoableEdit anEdit) throws Exception {
        CompoundEdit edit = new CompoundEdit();
        sqlTextEditsComplementor.complementStatement(edit);
        if (anEdit != null) {
            edit.addEdit(anEdit);
        }
        edit.end();
        return edit;
    }

    protected UndoableEdit complementQueryDocumentEdit(UndoableEdit anEdit) throws Exception {
        return queryDocumentEditsComplementor.complementEdit(anEdit);
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        try {
            PlatypusQueryView view = new PlatypusQueryView();
            view.setDataObject(dataObject);
            return view;
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }

    @Override
    protected String messageOpening() {
        return MessageFormat.format(NbBundle.getMessage(PlatypusQuerySupport.class, "PlatypusQueryOpening"), dataObject.getPrimaryFile().getName());
    }

    @Override
    protected String messageOpened() {
        return MessageFormat.format(NbBundle.getMessage(PlatypusQuerySupport.class, "PlatypusQueryOpened"), dataObject.getPrimaryFile().getName());
    }

    @Override
    public void save() throws IOException {
        try {
            if (dataObject.isModified()) {
                dataObject.saveQuery();
                env.unmarkModified();
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    protected boolean canClose() {
        try {
            if (dataObject.isModified()) {
                String confirmationString = MessageFormat.format(NbBundle.getMessage(PlatypusQuerySupport.class, "saveConfirmation"), dataObject.getPrimaryFile().getName());
                NotifyDescriptor.Confirmation confirm = new NotifyDescriptor.Confirmation(confirmationString, NotifyDescriptor.Confirmation.YES_NO_CANCEL_OPTION, NotifyDescriptor.Confirmation.QUESTION_MESSAGE);
                Object res = DialogDisplayer.getDefault().notify(confirm);
                if (NotifyDescriptor.YES_OPTION.equals(res)) {
                    try {
                        save();
                        return true;
                    } catch (IOException ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                } else if (NotifyDescriptor.NO_OPTION.equals(res)) {
                    env.unmarkModified();
                    return true;
                } else {
                    return false;
                }
            }
            return super.canClose();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return true;
        }
    }

    @Override
    public JEditorPane getOpenedPane() {
        return openedEditorPane;
    }

    public void setOpenedPane(JEditorPane aOpenedEditorPane) {
        openedEditorPane = aOpenedEditorPane;
    }

    public List<CloneableTopComponent> getAllViews() {
        List<CloneableTopComponent> comps = new ArrayList<>();
        Enumeration<CloneableTopComponent> en = allEditors.getComponents();
        while (en.hasMoreElements()) {
            comps.add(en.nextElement());
        }
        return comps;
    }

    public void shrink() throws IOException {
        List<CloneableTopComponent> views = getAllViews();
        if (views == null || views.isEmpty()) {
            // Take care of memory consumption.
            dataObject.shrink();
            // Shrink has removed all parsed data from the data object.
            undo.discardAllEdits();
        }
        openedEditorPane = null;
    }

    public void closeAllViews() {
        EventQueue.invokeLater(() -> {
            try {
                undo.discardAllEdits();
                List<CloneableTopComponent> views = getAllViews();
                for (CloneableTopComponent view : views) {
                    view.close();
                }
                // Take care of memory consumption.
                dataObject.shrink();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        });
    }
}
