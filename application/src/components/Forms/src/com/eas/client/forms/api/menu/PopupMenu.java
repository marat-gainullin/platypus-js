/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.menu;

import com.eas.client.forms.api.Container;
import com.eas.script.ScriptFunction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**
 *
 * @author mg
 */
public class PopupMenu extends Container<JPopupMenu> {

    protected PopupMenu(JPopupMenu aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public PopupMenu() {
        super();
        setDelegate(new JPopupMenu());
    }

    @ScriptFunction(jsDoc="Adds the item to the menu.")
    public void add(Menu aMenu) {
        delegate.add((JMenu) unwrap(aMenu));
    }
}
