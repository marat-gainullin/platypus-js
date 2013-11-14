/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.menu;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.script.ScriptFunction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 *
 * @author mg
 */
public class MenuSeparator extends Component<JSeparator> {

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* MenuSeparator provides a general purpose component for\n"
            + "* implementing divider lines - most commonly used as a divider\n"
            + "* between menu items that breaks them up into logical groupings.\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {})
    public MenuSeparator() {
        super();
    }

    protected MenuSeparator(JSeparator aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    
    private static final String PARENT_JSDOC = "/**\n"
            + "* The parent container.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = PARENT_JSDOC)
    @Override
    public Container<?> getParent() {
        Container<?> parent = super.getParent();
        if (parent == null && delegate.getParent() instanceof JPopupMenu && ((JPopupMenu) delegate.getParent()).getInvoker() instanceof JMenu) {
            parent = getContainerWrapper(((JPopupMenu) delegate.getParent()).getInvoker());
        }
        return parent;
    }
}
