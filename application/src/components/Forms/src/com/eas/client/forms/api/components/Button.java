/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.HorizontalPosition;
import com.eas.client.forms.api.VerticalPosition;
import com.eas.script.ScriptFunction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public class Button extends Component<JButton> {

    protected Button(JButton aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public Button(String aText, Icon aIcon, int aIconTextGap) {
        this(aText, aIcon, aIconTextGap, null);
    }

    public Button(String aText, Icon aIcon) {
        this(aText, aIcon, 4);
    }
    
    private static final String BUTTON_CONSTRUCTOR_JSDOC = "/**\n"
            + "* Simple button.\n"
            + "* @param text Button's text\n"
            + "* @param icon Button's icon (optional)\n"
            + "* @param iconTextGap Text gap (optional)\n"
            + "* @param actionHandler On clicked actions function (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = BUTTON_CONSTRUCTOR_JSDOC, params = {"text", "icon", "iconTextGap", "actionHandler"})
    public Button(String aText, Icon aIcon, int aIconTextGap, Function aActionPerformedHandler) {
        super();
        setDelegate(new JButton(aText, aIcon));
        delegate.setIconTextGap(aIconTextGap);
        setOnActionPerformed(aActionPerformedHandler);
    }

    public Button(String aText, Icon aIcon, Function aActionPerformedHandler) {
        this(aText, aIcon, 4, aActionPerformedHandler);
    }

    public Button(String aText) {
        this(aText, null, 4);
    }

    public Button() {
        this(null, null, 4);
    }

    @ScriptFunction(jsDoc = "Text on the button.")
    public String getText() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    @ScriptFunction(jsDoc = "Image picture for the button.")
    public Icon getIcon() {
        return delegate.getIcon();
    }

    @ScriptFunction
    public void setIcon(Icon aValue) {
        delegate.setIcon(aValue);
    }

    @ScriptFunction(jsDoc = "The amount of space between the text and the icon displayed in this button.")
    public int getIconTextGap() {
        return delegate.getIconTextGap();
    }

    @ScriptFunction
    public void setIconTextGap(int aValue) {
        delegate.setIconTextGap(aValue);
    }

    @ScriptFunction(jsDoc = "Horizontal position of the text relative to the icon.")
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

    @ScriptFunction(jsDoc = "Vertical position of the text relative to the icon.")
    public int getVerticalTextPosition() {
        switch (delegate.getVerticalTextPosition()) {
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
