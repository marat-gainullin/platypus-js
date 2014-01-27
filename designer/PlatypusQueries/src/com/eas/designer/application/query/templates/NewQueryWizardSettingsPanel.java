/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.templates;

import com.eas.designer.explorer.project.PlatypusProjectImpl;
import com.eas.designer.application.utils.DatabaseConnections;
import java.awt.Component;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author mg
 */
public class NewQueryWizardSettingsPanel implements WizardDescriptor.Panel<WizardDescriptor>, WizardDescriptor.FinishablePanel<WizardDescriptor> {

    public static final String CONNECTION_PROP_NAME = "connectionId";
    protected PlatypusProjectImpl project;
    private WizardDescriptor wizardDescriptor;
    protected QuerySettingsVisualPanel component;

    public NewQueryWizardSettingsPanel(PlatypusProjectImpl aProject) {
        super();
        project = aProject;
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new QuerySettingsVisualPanel(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(WizardDescriptor settings) {
        try {
            wizardDescriptor = settings;
            component.read(wizardDescriptor);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        try {
            component.store(settings);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }

    @Override
    public boolean isValid() {
        try {
            getComponent();
            return component.valid(wizardDescriptor);
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return false;
        }
    }
    private final Set<ChangeListener> listeners = new HashSet<>(1); // or can use ChangeSupport in NB 6.0

    @Override
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    public final void fireChangeEvent() {
        Set<ChangeListener> ls;
        synchronized (listeners) {
            ls = new HashSet<>(listeners);
        }
        ChangeEvent ev = new ChangeEvent(this);
        for (ChangeListener l : ls) {
            l.stateChanged(ev);
        }
    }

    @Override
    public boolean isFinishPanel() {
        return false;
    }

    public boolean connectionExist(String aDatasourceName) throws Exception {
        DatabaseConnection conn = DatabaseConnections.lookup(aDatasourceName);        
        return conn != null;
    }

    public PlatypusProjectImpl getProject() {
        return project;
    }
}
