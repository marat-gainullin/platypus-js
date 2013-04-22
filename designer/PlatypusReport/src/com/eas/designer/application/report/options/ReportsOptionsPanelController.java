/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report.options;

import com.eas.client.settings.PreferencesPanel;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author mg
 */
@OptionsPanelController.TopLevelRegistration(categoryName="#reports.setting.panel", iconBase="com/eas/designer/application/report/options/report-settings32.png")
public class ReportsOptionsPanelController extends OptionsPanelController {

    protected PreferencesPanel prefsPanel = new PreferencesPanel();

    @Override
    public void update() {
        prefsPanel.readSettings();
    }

    @Override
    public void applyChanges() {
        prefsPanel.save();
    }

    @Override
    public void cancel() {
        // no op here, because it is nothing to do, since no any state is maintained inside preferences panel.
    }

    @Override
    public boolean isValid() {
        return prefsPanel.isInputValid();
    }

    @Override
    public boolean isChanged() {
        return prefsPanel.isChanged();
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return prefsPanel;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        prefsPanel.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        prefsPanel.removePropertyChangeListener(l);
    }
}
