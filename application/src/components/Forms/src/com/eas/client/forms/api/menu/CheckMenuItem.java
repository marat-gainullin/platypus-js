/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.menu;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public class CheckMenuItem extends Component<JCheckBoxMenuItem> {

    protected CheckMenuItem(JCheckBoxMenuItem aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public CheckMenuItem(String aText, boolean aSelected) {
        this(aText, aSelected, null);
    }
    
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
