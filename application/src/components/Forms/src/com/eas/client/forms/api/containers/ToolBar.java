/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.containers;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.script.ScriptFunction;
import javax.swing.JToolBar;

/**
 *
 * @author mg
 */
public class ToolBar extends Container<JToolBar> {

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* <code>ToolBar</code> provides a component that is useful for displaying commonly used actions or controls.\n"
            + "* @param floatable if <code>true</code>, the tool bar can be moved; <code>false</code> otherwise (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {})
    public ToolBar(boolean floatable) {
        super();
        setDelegate(new JToolBar());
        delegate.setFloatable(floatable);
    }

    public ToolBar() {
        this(false);
    }

    protected ToolBar(JToolBar aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    @ScriptFunction(jsDoc = "Appends the specified component to the end of this container.")
    public void add(Component<?> aComp) {
        if (aComp != null) {
            delegate.add(unwrap(aComp));
            delegate.revalidate();
            delegate.repaint();
        }
    }
}
