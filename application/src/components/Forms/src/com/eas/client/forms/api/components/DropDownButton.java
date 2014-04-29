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
import jdk.nashorn.api.scripting.JSObject;

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
    
    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* Drop-down button component.\n"
            + "* @param text the text of the component (optional).\n"
            + "* @param icon the icon of the component (optional).\n"
            + "* @param iconTextGap the text gap (optional).\n"
            + "* @param actionPerformed the function for the action performed handler (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text", "icon", "iconTextGap", "actionPerformed"})
    public DropDownButton(String aText, Icon aIcon, int aIconTextGap, JSObject aActionPerformedHandler) {
        super();
        setDelegate(new JDropDownButton(aText, aIcon));
        delegate.setIconTextGap(aIconTextGap);
        setOnActionPerformed(aActionPerformedHandler);
    }

    public DropDownButton(String aText, Icon aIcon, JSObject aActionPerformedHandler) {
        this(aText, aIcon, 4, aActionPerformedHandler);
    }
    
    public DropDownButton(String aText) {
        this(aText, null, 4);
    }

    public DropDownButton() {
        this(null, null, 4);
    }
    
    private static final String DROP_DOWN_MENU_JSDOC = ""
            + "/**\n"
            + "* <code>PopupMenu</code> for the component.\n"
            + "*/";
    @ScriptFunction(jsDoc=DROP_DOWN_MENU_JSDOC)
    public PopupMenu getDropDownMenu() {
        Container<?> cMenu = getContainerWrapper(delegate.getDropDownMenu());
        return cMenu instanceof PopupMenu ? (PopupMenu) cMenu : null;
    }
    
    @ScriptFunction
    public void setDropDownMenu(PopupMenu aMenu) {
        JComponent jPopupMenu = unwrap(aMenu);
        delegate.setDropDownMenu(jPopupMenu instanceof JPopupMenu ? (JPopupMenu) jPopupMenu : null);
    }

    private static final String TEXT_JSDOC = ""
            + "/**\n"
            + "* Text on the button.\n"
            + "*/";
    @ScriptFunction(jsDoc=TEXT_JSDOC)
    public String getText() {
        return delegate.getText();
    }
    
    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    private static final String ICON_JSDOC = ""
            + "/**\n"
            + "* Image picture for the button.\n"
            + "*/";
    @ScriptFunction(jsDoc=ICON_JSDOC)
    public Icon getIcon() {
        return delegate.getIcon();
    }

    @ScriptFunction
    public void setIcon(Icon aValue) {
        delegate.setIcon(aValue);
    }

    private static final String ICON_TEXT_GAP_JSDOC = ""
            + "/**\n"
            + "* The amount of space between the text and the icon displayed in this button.\n"
            + "*/";
    @ScriptFunction(jsDoc=ICON_TEXT_GAP_JSDOC)
    public int getIconTextGap() {
        return delegate.getIconTextGap();
    }
    
    @ScriptFunction
    public void setIconTextGap(int aValue) {
        delegate.setIconTextGap(aValue);
    }

    private static final String HORIZONTAL_TEXT_POSITION_JSDOC = ""
            + "/**\n"
            + "* Horizontal position of the text relative to the icon.\n"
            + "*/";
    @ScriptFunction(jsDoc=HORIZONTAL_TEXT_POSITION_JSDOC)
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
    
    private static final String VERTICAL_TEXT_POSITION_JSDOC = ""
            + "/**\n"
            + "* Vertical position of the text relative to the icon.\n"
            + "*/";
    @ScriptFunction(jsDoc=VERTICAL_TEXT_POSITION_JSDOC)
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
