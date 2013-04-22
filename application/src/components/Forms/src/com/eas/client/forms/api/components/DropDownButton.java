/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.Container;
import com.eas.client.forms.api.HorizontalPosition;
import com.eas.client.forms.api.VerticalPosition;
import com.eas.client.forms.api.menu.PopupMenu;
import com.eas.gui.JDropDownButton;
import com.eas.script.ScriptFunction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public class DropDownButton extends Component<JDropDownButton> {

    protected DropDownButton(JDropDownButton aDelegate)
    {
        super();
        setDelegate(aDelegate);
    }
    
    public DropDownButton(String aText, Icon aIcon, int aIconTextGap) {
        this(aText, aIcon, aIconTextGap, null);
    }

    public DropDownButton(String aText, Icon aIcon) {
        this(aText, aIcon, 4);
    }

    public DropDownButton(String aText, Icon aIcon, int aIconTextGap, Function aActionPerformedHandler) {
        super();
        setDelegate(new JDropDownButton(aText, aIcon));
        delegate.setIconTextGap(aIconTextGap);
        setOnActionPerformed(aActionPerformedHandler);
    }

    public DropDownButton(String aText, Icon aIcon, Function aActionPerformedHandler) {
        this(aText, aIcon, 4, aActionPerformedHandler);
    }
    
    public DropDownButton(String aText) {
        this(aText, null, 4);
    }

    public DropDownButton() {
        this(null, null, 4);
    }
    
    @ScriptFunction(jsDocText="PopupMenu for the component.")
    public PopupMenu getDropDownMenu() {
        Container<?> cMenu = getContainerWrapper(delegate.getDropDownMenu());
        return cMenu instanceof PopupMenu ? (PopupMenu) cMenu : null;
    }
    
    @ScriptFunction
    public void setDropDownMenu(PopupMenu aMenu) {
        JComponent jPopupMenu = unwrap(aMenu);
        delegate.setDropDownMenu(jPopupMenu instanceof JPopupMenu ? (JPopupMenu) jPopupMenu : null);
    }

    @ScriptFunction(jsDocText="Text on the button.")
    public String getText() {
        return delegate.getText();
    }
    
    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    @ScriptFunction(jsDocText="Image picture for the button.")
    public Icon getIcon() {
        return delegate.getIcon();
    }

    @ScriptFunction
    public void setIcon(Icon aValue) {
        delegate.setIcon(aValue);
    }

    @ScriptFunction(jsDocText="The amount of space between the text and the icon displayed in this button.")
    public int getIconTextGap() {
        return delegate.getIconTextGap();
    }
    
    @ScriptFunction
    public void setIconTextGap(int aValue) {
        delegate.setIconTextGap(aValue);
    }

    @ScriptFunction(jsDocText="Horizontal position of the text relative to the icon.")
    public int getHorizontalTextPosition() {
        switch (delegate.getHorizontalTextPosition()) {
            case JLabel.LEFT:
                return HorizontalPosition.LEFT;
            case JLabel.CENTER:
                return HorizontalPosition.CENTER;
            case JLabel.RIGHT:
                return HorizontalPosition.RIGHT;
            default:
                return HorizontalPosition.LEFT;
        }
    }

    @ScriptFunction
    public void setHorizontalTextPosition(int aValue) {
        switch (aValue) {
            case HorizontalPosition.LEFT:
                delegate.setHorizontalTextPosition(JLabel.LEFT);
                break;
            case HorizontalPosition.CENTER:
                delegate.setHorizontalTextPosition(JLabel.CENTER);
                break;
            case HorizontalPosition.RIGHT:
                delegate.setHorizontalTextPosition(JLabel.RIGHT);
                break;
            default:
                delegate.setHorizontalTextPosition(JLabel.LEFT);
                break;
        }
    }
    
    @ScriptFunction(jsDocText="Vertical position of the text relative to the icon.")
    public int getVerticalTextPosition() {
        switch (delegate.getHorizontalTextPosition()) {
            case JLabel.TOP:
                return VerticalPosition.TOP;
            case JLabel.CENTER:
                return VerticalPosition.CENTER;
            case JLabel.BOTTOM:
                return VerticalPosition.BOTTOM;
            default:
                return VerticalPosition.CENTER;
        }
    }
    
    @ScriptFunction
    public void setVerticalTextPosition(int aValue) {
        switch (aValue) {
            case VerticalPosition.TOP:
                delegate.setVerticalTextPosition(JLabel.TOP);
                break;
            case VerticalPosition.CENTER:
                delegate.setVerticalTextPosition(JLabel.CENTER);
                break;
            case VerticalPosition.BOTTOM:
                delegate.setVerticalTextPosition(JLabel.BOTTOM);
                break;
            default:
                delegate.setVerticalTextPosition(JLabel.CENTER);
                break;
        }
    }
}
