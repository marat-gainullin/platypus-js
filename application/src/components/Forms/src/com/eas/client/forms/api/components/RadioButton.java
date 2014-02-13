/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.client.forms.api.containers.ButtonGroup;
import com.eas.script.ScriptFunction;
import javax.swing.Icon;
import javax.swing.JRadioButton;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public class RadioButton extends Component<JRadioButton> {

    protected ButtonGroup group;
    
    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* Radio button component.\n"
            + "* @param text Component's text (optional)\n"
            + "* @param selected <code>true</code> if component is selected (optional)\n"
            + "* @param actionPerformed On action performed function (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text", "selected", "actionPerformed"})
    public RadioButton(String aText, boolean aSelected, Function aActionPerformedHandler) {
        super();
        setDelegate(new JRadioButton(aText, aSelected));
        setOnActionPerformed(aActionPerformedHandler);
    }

    public RadioButton(String aText, boolean aSelected) {
        this(aText, aSelected, null);
    }
    
    public RadioButton(String aText) {
        this(aText, false);
    }

    public RadioButton() {
        this(null, false);
    }
    
    protected RadioButton(JRadioButton aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    @ScriptFunction(jsDoc=""
            + "/**\n"
            + " * The button's text.\n"
            + " */")
    public String getText() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    @ScriptFunction(jsDoc=""
            + "/**\n"
            + " * The default icon.\n"
            + " */")
    public Icon getIcon() {
        return delegate.getIcon();
    }

    @ScriptFunction
    public void setIcon(Icon aValue) {
        delegate.setIcon(aValue);
    }

    @ScriptFunction(jsDoc=""
            + "/**\n"
            + " * The state of the button.\n"
            + " */")
    public boolean getSelected() {
        return delegate.isSelected();
    }

    @ScriptFunction
    public void setSelected(boolean aValue) {
        delegate.setSelected(aValue);
    }
    
    @ScriptFunction(jsDoc=""
            + "/**\n"
            + " * The ButtonGroup this component belongs to.\n"
            + " */")
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
