/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.menu;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class Menu extends Container<JMenu> {

    public Menu() {
        super();
        setDelegate(new JMenu());
    }

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* An implementation of a menu -- a popup window containing MenuItems\n"
            + "* that is displayed when the user selects an item on the MenuBar.\n"
            + "* In addition to <code>MenuItems</code>, a <code>Menu</code> can also contain <code>MenuSeparators</code>.\n"
            + "* @param text the text for the menu label (optional).\n"
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
    protected void setDelegate(JMenu aDelegate) {
        if(delegate != null && delegate.getPopupMenu() != null){
            delegate.getPopupMenu().removeContainerListener(invalidatorListener);
        }
        super.setDelegate(aDelegate);
        if(delegate != null && delegate.getPopupMenu() != null){
            delegate.getPopupMenu().addContainerListener(invalidatorListener);
        }
    }

    private static final String PARENT_JSDOC = ""
            + "/**\n"
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

    @ScriptFunction(jsDoc = CHILD_JSDOC, params = {"index"})
    @Override
    public Component<?> child(int aIndex) {
        return getComponentWrapper(delegate.getMenuComponent(aIndex));
    }

    private static final String TEXT_JSDOC = ""
            + "/**\n"
            + "* The text of the menu.\n"
            + "*/";

    @ScriptFunction(jsDoc = TEXT_JSDOC)
    public String getText() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    private static final String ADD_JSDOC = ""
            + "/**\n"
            + "* Adds an item to the menu.\n"
            + "* @param component the component to add\n"
            + "*/";

    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"component"})
    public void add(Component<?> aComp) {
        delegate.add(unwrap(aComp));
    }

    private static final String COUNT_JSDOC = ""
            + "/**\n"
            + "* The count of the menu items.\n"
            + "*/";

    @Override
    @ScriptFunction(jsDoc = COUNT_JSDOC)
    public int getCount() {
        return delegate.getMenuComponentCount();
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
