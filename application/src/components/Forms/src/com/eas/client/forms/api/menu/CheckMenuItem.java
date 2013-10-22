/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.menu;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.script.ScriptFunction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public class CheckMenuItem extends Component<JCheckBoxMenuItem> {

    public CheckMenuItem(String aText, boolean aSelected) {
        this(aText, aSelected, null);
    }
    
    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* A menu item that can be selected or deselected.\n"
            + "* @param text the text of the component (optional)\n"
            + "* @param selected true if selected (optional)\n"
            + "* @param actionPerformed On action performed function (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text", "selected", "actionPerformed"})
    public CheckMenuItem(String aText, boolean aSelected, Function aActionPerformedHandler) {
        super();
        setDelegate(new JCheckBoxMenuItem(aText, aSelected));
        setOnActionPerformed(aActionPerformedHandler);
    }

    public CheckMenuItem(String aText) {
        this(aText, false);
    }

    public CheckMenuItem() {
        this(null, false);
    }

    protected CheckMenuItem(JCheckBoxMenuItem aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    @Override
    public Container<?> getParent() {
        Container<?> parent = super.getParent();
        if (parent == null && delegate.getParent() instanceof JPopupMenu && ((JPopupMenu) delegate.getParent()).getInvoker() instanceof JMenu) {
            parent = getContainerWrapper(((JPopupMenu) delegate.getParent()).getInvoker());
        }
        return parent;
    }

    public String getText() {
        return delegate.getText();
    }

    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    public boolean isSelected() {
        return delegate.isSelected();
    }

    public void setSelected(boolean aValue) {
        delegate.setSelected(aValue);
    }
}
