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

    protected ToolBar(JToolBar aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ToolBar(boolean floatable) {
        super();
        setDelegate(new JToolBar());
        delegate.setFloatable(floatable);
    }

    public ToolBar() {
        this(false);
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
