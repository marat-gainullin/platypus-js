/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.forms.api.FormEventsIProxy;
import com.eas.client.model.application.ApplicationModel;
import com.eas.controls.FormEventsExecutor;
import com.eas.controls.events.ControlEventsIProxy;
import com.eas.dbcontrols.visitors.DbSwingFactory;

/**
 *
 * @author mg
 */
public class FormFactory extends DbSwingFactory {

    public FormFactory(FormEventsExecutor aEventsExecutor, ApplicationModel<?, ?, ?, ?> aModel) {
        super(aEventsExecutor, aModel);
    }

    @Override
    protected ControlEventsIProxy createEventsProxy() {
        return new FormEventsIProxy(eventsExecutor);
    }
}
