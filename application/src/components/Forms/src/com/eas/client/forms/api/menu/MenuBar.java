/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.menu;

import com.eas.client.forms.api.Container;
import com.eas.script.ScriptFunction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 *
 * @author mg
 */
public class MenuBar extends Container<JMenuBar> {

    protected MenuBar(JMenuBar aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public MenuBar() {
        super();
        setDelegate(new JMenuBar());
    }

    @ScriptFunction(jsDoc="Adds the item to the menu.")
    public void add(Menu aMenu) {
        delegate.add((JMenu) unwrap(aMenu));
    }

}
