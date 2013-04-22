/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project.wizard;

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
public class DatabaseSetupWizardPanel implements
        WizardDescriptor.ValidatingPanel<WizardDescriptor>, WizardDescriptor.FinishablePanel<WizardDescriptor> {

    public static final String DATABASE_SETUP_PAGE_NAME = "LBL_SetupProjectStep";
    private WizardDescriptor wizardDescriptor;
    private DatabaseSetupWizardPanelVisual component;

    public DatabaseSetupWizardPanel() {
        super();
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new DatabaseSetupWizardPanelVisual(this);
            component.setName(NbBundle.getMessage(NameAndLocationWizardPanel.class, DATABASE_SETUP_PAGE_NAME));
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        try {
            getComponent();
            return component.valid(wizardDescriptor);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
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

    protected final void fireChangeEvent() {
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
    public void readSettings(WizardDescriptor settings) {
        wizardDescriptor = settings;
        component.read(wizardDescriptor);
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        try {
            component.store(settings);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void validate() throws WizardValidationException {
    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }
}
