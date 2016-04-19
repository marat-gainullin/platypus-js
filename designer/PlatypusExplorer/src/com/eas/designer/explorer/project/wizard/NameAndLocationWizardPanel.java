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
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
public abstract class NameAndLocationWizardPanel implements WizardDescriptor.Panel<WizardDescriptor>,
        WizardDescriptor.ValidatingPanel<WizardDescriptor> {

    private WizardDescriptor wizardDescriptor;
    private NameAndLocationWizardPanelVisual component;

    public NameAndLocationWizardPanel() {
        super();
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            component = createComponent();
            component.setName(NbBundle.getMessage(NameAndLocationWizardPanel.class, "LBL_CreateProjectStep"));
        }
        return component;
    }

    protected abstract NameAndLocationWizardPanelVisual createComponent();

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx(NameAndLocationWizardPanel.class.getName());
    }

    @Override
    public boolean isValid() {
        getComponent();
        return component.valid(wizardDescriptor);
    }
    private final Set<ChangeListener> listeners = new HashSet<>(1);

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
        component.store(settings);
    }

    @Override
    public void validate() throws WizardValidationException {
        getComponent();
        component.validate(wizardDescriptor);
    }
}
