package com.eas.samples;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
public class PlatypusSamplesWizardPanel implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel {

    private WizardDescriptor wizardDescriptor;
    private PlatypusSamplesPanelVisual component;

    /**
     * Creates a new instance of templateWizardPanel
     */
    public PlatypusSamplesWizardPanel() {
        super();
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new PlatypusSamplesPanelVisual(this);
            component.setName(NbBundle.getMessage(PlatypusSamplesWizardPanel.class, "LBL_CreateProjectStep"));
            component.putClientProperty("NewProjectWizard_Title", NbBundle.getMessage(PlatypusSamplesWizardPanel.class, "LBL_TXT_NewPlatypusSample"));
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        getComponent();
        return component.valid(wizardDescriptor);
    }

    private final Set<ChangeListener> listeners = new HashSet(1);

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
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }

    @Override
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        component.read(wizardDescriptor);

        // XXX hack, TemplateWizard in final setTemplateImpl() forces new wizard's
        // title  this name is used in NewProjectWizard to modify the title
        Object substitute = ((JComponent) component).getClientProperty("NewProjectWizard_Title"); // NOI18N
        if (substitute != null) {
            wizardDescriptor.putProperty("NewProjectWizard_Title", substitute); // NOI18N
        }
    }

    @Override
    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        component.store(d);
        d.putProperty("NewProjectWizard_Title", null); // NOI18N
    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }

    @Override
    public void validate() throws WizardValidationException {
        getComponent();
        component.validate(wizardDescriptor);
    }
}
