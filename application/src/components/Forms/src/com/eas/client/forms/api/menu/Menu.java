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

    public Menu() {
        super();
        setDelegate(new JMenu());
    }
        
    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* An implementation of a menu -- a popup window containing MenuItems"
            + " that is displayed when the user selects an item on the MenuBar."
            + " In addition to <code>MenuItems</code>, a <code>Menu</code> can also contain <code>MenuSeparators</code>.\n"
            + "* @param text the text for the menu label (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text"})
    public Menu(String aText) {
        super();
        setDelegate(new JMenu(aText));
    }

    protected Menu(JMenu aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    @Override
    @ScriptFunction(jsDoc="Gets the parent container.")
    public Container<?> getParent() {
        Container<?> parent = super.getParent();
        if (parent == null && delegate.getParent() instanceof JPopupMenu && ((JPopupMenu) delegate.getParent()).getInvoker() instanceof JMenu) {
            parent = getContainerWrapper(((JPopupMenu) delegate.getParent()).getInvoker());
        }
        return parent;
    }

    @Override
    @ScriptFunction(jsDoc="Gets the child item whith specified index.")
    public Component<?> child(int aIndex) {
        return getComponentWrapper(delegate.getMenuComponent(aIndex));
    }

    @ScriptFunction(jsDoc="The text of the menu.")
    public String getText() {
        return delegate.getText();
    }
    
    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    @ScriptFunction(jsDoc="Adds the item to the menu.")
    public void add(Component<?> aComp) {
        delegate.add(unwrap(aComp));
    }

    @Override
    @ScriptFunction(jsDoc="Gets the count of the menu items.")
    public int getCount() {
        return delegate.getMenuComponentCount();
    }
    
    
}
