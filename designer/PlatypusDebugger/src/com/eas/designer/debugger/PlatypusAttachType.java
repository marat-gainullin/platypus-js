/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.debugger;

import com.eas.designer.debugger.ui.AttachToProcessCustomizer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.api.project.Project;
import org.netbeans.spi.debugger.ui.AttachType;
import org.netbeans.spi.debugger.ui.Controller;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.util.Utilities;

/**
 *
 * @author mg
 */
@AttachType.Registration(displayName = "#attachType")
public class PlatypusAttachType extends AttachType {

    protected class AttachTypeController implements Controller, PropertyChangeListener {

        protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

        @Override
        public boolean ok() {
            if (isValid()) {
                try {
                    DebuggerEnvironment env = new DebuggerEnvironment(Utilities.actionsGlobalContext().lookup(Project.class));
                    env.host = settings.getHost();
                    env.port = settings.getPort();
                    DebuggerUtils.attachDebugger(env);
                    return true;
                } catch (Exception ex) {
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage()));
                    ErrorManager.getDefault().notify(ex);
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        public boolean cancel() {
            return true;
        }

        @Override
        public boolean isValid() {
            return settings.getHost() != null && !settings.getHost().isEmpty() && settings.getPort() > 0;
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
        public void propertyChange(PropertyChangeEvent evt) {
            changeSupport.firePropertyChange(Controller.PROP_VALID, !isValid(), isValid());
        }
    }
    protected AttachSettings settings = new AttachSettings();
    protected AttachTypeController controller = new AttachTypeController();
    protected AttachToProcessCustomizer customizer = new AttachToProcessCustomizer(settings, controller);

    public PlatypusAttachType() {
        super();
        settings.getChangeSupport().addPropertyChangeListener(controller);
    }

    @Override
    public JComponent getCustomizer() {
        return customizer;
    }

    @Override
    public Controller getController() {
        return controller;
    }
}
