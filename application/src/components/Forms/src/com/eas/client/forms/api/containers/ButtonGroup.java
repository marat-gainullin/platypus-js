/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.controls.wrappers.ButtonGroupWrapper;
import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public class ButtonGroup extends Container<ButtonGroupWrapper> {

    protected ButtonGroup(ButtonGroupWrapper aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ButtonGroup() {
        super();
        setDelegate(new ButtonGroupWrapper());
    }

    @ScriptFunction(jsDocText = "Appends the specified component to the end of this container.")
    public void add(Component<?> aComp) {
        if (aComp != null) {
            delegate.add(unwrap(aComp));
        }
    }
}
