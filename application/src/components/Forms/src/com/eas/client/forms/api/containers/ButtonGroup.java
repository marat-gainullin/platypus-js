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

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * Creates a multiple-exclusion scope for a set of buttons.\n"
            +"  * Creating a set of buttons with the same <code>ButtonGroup</code> object means that turning \"on\" one of those buttons turns off all other buttons in the group.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {})
    public ButtonGroup() {
        super();
        setDelegate(new ButtonGroupWrapper());
    }

    protected ButtonGroup(ButtonGroupWrapper aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Appends the specified component to the end of this container.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ADD_JSDOC)
    public void add(Component<?> aComp) {
        if (aComp != null) {
            delegate.add(unwrap(aComp));
        }
    }
}
