/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.forms.api.FormEventsIProxy;
import com.eas.client.model.application.ApplicationModel;
import com.eas.controls.events.ControlEventsIProxy;
import com.eas.dbcontrols.visitors.DbSwingFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;

/**
 *
 * @author mg
 */
public class FormFactory extends DbSwingFactory {

    public FormFactory(ApplicationModel<?, ?, ?, ?> aModel) {
        super(aModel);
    }

    @Override
    protected ControlEventsIProxy createEventsProxy() {
        return new FormEventsIProxy();
    }

    @Override
    public Icon resolveIcon(String aIconName) {
        if (aIconName != null) {
            try {
                Icon res = IconResources.load(aIconName);
                return res != null ? res : super.resolveIcon(aIconName);
            } catch (Exception ex) {
                Logger.getLogger(FormFactory.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            return null;
        }
    }
}
