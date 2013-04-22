/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.connection;

import com.eas.designer.explorer.DataObjectProvider;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.cookies.ViewCookie;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;
import org.openide.windows.CloneableOpenSupport;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author mg
 */
public class PlatypusConnectionSupport extends CloneableOpenSupport implements OpenCookie, ViewCookie, EditCookie, CloseCookie, SaveCookie, DataObjectProvider {

    protected static class Env implements CloneableOpenSupport.Env {

        static final long serialVersionUID = 8453753008783256201L;
        protected PlatypusConnectionDataObject dataObject;
        protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
        protected VetoableChangeSupport vetoSupport = new VetoableChangeSupport(this);

        public Env(PlatypusConnectionDataObject aDataObject) {
            super();
            dataObject = aDataObject;
            dataObject.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    try {
                        if (PlatypusConnectionDataObject.PROP_URL.equals(evt.getPropertyName())
                                || PlatypusConnectionDataObject.PROP_SCHEMA.equals(evt.getPropertyName())
                                || PlatypusConnectionDataObject.PROP_USER.equals(evt.getPropertyName())
                                || PlatypusConnectionDataObject.PROP_PASSWORD.equals(evt.getPropertyName())) {
                            markModified();
                        }
                    } catch (IOException ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            });
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
            return dataObject.getLookup().lookup(PlatypusConnectionSupport.class);
        }
    }
    protected PlatypusConnectionDataObject dataObject;

    public PlatypusConnectionSupport(PlatypusConnectionDataObject aDataObject) {
        super(new Env(aDataObject));
        dataObject = aDataObject;
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        try {
            PlatypusConnectionView view = new PlatypusConnectionView();
            view.setDataObject(dataObject);
            return view;
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }

    @Override
    protected String messageOpening() {
        return MessageFormat.format(NbBundle.getMessage(PlatypusConnectionSupport.class, "PlatypusConnectionOpening"), dataObject.getPrimaryFile().getName());
    }

    @Override
    protected String messageOpened() {
        return MessageFormat.format(NbBundle.getMessage(PlatypusConnectionSupport.class, "PlatypusConnectionOpened"), dataObject.getPrimaryFile().getName());
    }

    @Override
    public void save() throws IOException {
        try {
            if (dataObject.isModified()) {
                dataObject.saveSettings();
                env.unmarkModified();
            }
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    protected boolean canClose() {
        if (dataObject.isModified()) {
            String confirmationString = MessageFormat.format(NbBundle.getMessage(PlatypusConnectionSupport.class, "saveConfirmation"), dataObject.getPrimaryFile().getName());
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
    }
    
    @Override
    public DataObject getDataObject() {
        return dataObject;
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
        }
    }
    
    public void closeAllViews() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //modelUndo.discardAllEdits();
                List<CloneableTopComponent> views = getAllViews();
                for (CloneableTopComponent view : views) {
                    view.close();
                }
                // Take care of memory consumption.
                dataObject.shrink();
            }
        });
    }
}
