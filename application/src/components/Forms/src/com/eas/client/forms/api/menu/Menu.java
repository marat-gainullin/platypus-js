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

/**
 *
 * @author mg
 */
public class Menu extends Container<JMenu> {

    protected Menu(JMenu aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public Menu() {
        super();
        setDelegate(new JMenu());
    }

    public Menu(String aText) {
        super();
        setDelegate(new JMenu(aText));
    }

    @Override
    @ScriptFunction(jsDocText="Gets the parent container.")
    public Container<?> getParent() {
        Container<?> parent = super.getParent();
        if (parent == null && delegate.getParent() instanceof JPopupMenu && ((JPopupMenu) delegate.getParent()).getInvoker() instanceof JMenu) {
            parent = getContainerWrapper(((JPopupMenu) delegate.getParent()).getInvoker());
        }
        return parent;
    }

    @Override
    @ScriptFunction(jsDocText="Gets the child item whith specified index.")
    public Component<?> child(int aIndex) {
        return getComponentWrapper(delegate.getMenuComponent(aIndex));
    }

    @ScriptFunction(jsDocText="The text of the menu.")
    public String getText() {
        return delegate.getText();
    }
    
    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    @ScriptFunction(jsDocText="Adds the item to the menu.")
    public void add(Component<?> aComp) {
        delegate.add(unwrap(aComp));
    }

    @Override
    @ScriptFunction(jsDocText="Gets the count of the menu items.")
    public int getCount() {
        return delegate.getMenuComponentCount();
    }
    
    
}
