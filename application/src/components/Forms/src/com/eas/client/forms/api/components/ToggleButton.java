/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.HorizontalPosition;
import com.eas.client.forms.api.VerticalPosition;
import com.eas.client.forms.api.containers.ButtonGroup;
import com.eas.script.ScriptFunction;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public class ToggleButton extends Component<JToggleButton> {

    protected ButtonGroup group;

    public ToggleButton(String aText, Icon aIcon, boolean aSelected, int aIconTextGap) {
        this(aText, aIcon, aSelected, aIconTextGap, null);
    }

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* Toggle button component.\n"
            + "* @param text the text for the component (optional)\n"
            + "* @param icon the icon for the component (optional)\n"
            + "* @param iconTextGap the text gap (optional)\n"
            + "* @param actionPerformed the function for the action performed handler (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text", "icon", "iconTextGap", "actionPerformed"})
    public ToggleButton(String aText, Icon aIcon, boolean aSelected, int aIconTextGap, Function aActionPerformedHandler) {
        super();
        setDelegate(new JToggleButton(aText, aIcon, aSelected));
        delegate.setIconTextGap(aIconTextGap);
        setOnActionPerformed(aActionPerformedHandler);
    }

    public ToggleButton(String aText, Icon aIcon, boolean aSelected, Function aActionPerformedHandler) {
        this(aText, aIcon, aSelected, 4, aActionPerformedHandler);
    }

    public ToggleButton(String aText, Icon aIcon, boolean aSelected) {
        this(aText, aIcon, aSelected, 4);
    }

    public ToggleButton(String aText, Icon aIcon) {
        this(aText, aIcon, false, 4);
    }

    public ToggleButton(String aText) {
        this(aText, null, false, 4);
    }

    public ToggleButton() {
        this(null, null, false, 4);
    }

    protected ToggleButton(JToggleButton aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    @ScriptFunction(jsDoc="Text on the button.")
    public String getText() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    @ScriptFunction(jsDoc="Image picture for the button.")
    public Icon getIcon() {
        return delegate.getIcon();
    }

    @ScriptFunction
    public void setIcon(Icon aValue) {
        delegate.setIcon(aValue);
    }

    @ScriptFunction(jsDoc="The amount of space between the text and the icon displayed in this button.")
    public int getIconTextGap() {
        return delegate.getIconTextGap();
    }

    @ScriptFunction
    public void setIconTextGap(int aValue) {
        delegate.setIconTextGap(aValue);
    }

    @ScriptFunction(jsDoc="Horizontal position of the text relative to the icon.")
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
    
    @ScriptFunction(jsDoc="Vertical position of the text relative to the icon.")
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

    @ScriptFunction(jsDoc="The state of the button.")
    public boolean isSelected() {
        return delegate.isSelected();
    }

    @ScriptFunction
    public void setSelected(boolean aValue) {
        delegate.setSelected(aValue);
    }

    @ScriptFunction(jsDoc="The ButtonGroup this component belongs to.")
    public ButtonGroup getButtonGroup() {
        return group;
    }

    @ScriptFunction
    public void setButtonGroup(ButtonGroup aGroup) {
        if (group != aGroup) {
            if (group != null) {
                group.remove(this);
            }
            group = aGroup;
            if (group != null) {
                group.add(this);
            }
        }
    }
}
