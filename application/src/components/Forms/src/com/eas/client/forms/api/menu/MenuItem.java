/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.menu;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.script.ScriptFunction;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public class MenuItem extends Component<JMenuItem> {

    public MenuItem(String aText, Icon aIcon) {
        this(aText, aIcon, null);
    }

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* A menu item that can be selected or deselected.\n"
            + "* @param text the text of the component (optional)\n"
            + "* @param icon the icon of the component (optional)\n"
            + "* @param actionPerformed the function for the action performed handler (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text", "icon", "actionPerformed"})
    public MenuItem(String aText, Icon aIcon, Function aActionPerformedHandler) {
        super();
        setDelegate(new JMenuItem(aText, aIcon));
        setOnActionPerformed(aActionPerformedHandler);
    }

    public MenuItem(String aText) {
        this(aText, null);
    }

    public MenuItem() {
        this(null, null);
    }

    protected MenuItem(JMenuItem aDelegate) {
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

    private static final String TEXT_JSDOC = "/**\n"
            + "* The menu item text.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = TEXT_JSDOC)
    public String getText() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    private static final String ICON_JSDOC = "/**\n"
            + "* Image picture for the menu item.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ICON_JSDOC)
    public Icon getIcon() {
        return delegate.getIcon();
    }

    @ScriptFunction
    public void setIcon(Icon aValue) {
        delegate.setIcon(aValue);
    }
}
