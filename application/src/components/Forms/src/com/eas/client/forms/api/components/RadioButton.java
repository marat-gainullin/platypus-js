/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.script.ScriptFunction;
import javax.swing.Icon;
import javax.swing.JRadioButton;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public class RadioButton extends Component<JRadioButton> {

    protected RadioButton(JRadioButton aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public RadioButton(String aText, boolean aSelected) {
        this(aText, aSelected, null);
    }

    public RadioButton(String aText, boolean aSelected, Function aActionPerformedHandler) {
        super();
        setDelegate(new JRadioButton(aText, aSelected));
        setOnActionPerformed(aActionPerformedHandler);
    }

    public RadioButton(String aText) {
        this(aText, false);
    }

    public RadioButton() {
        this(null, false);
    }
    
    @ScriptFunction(jsDocText="The button's text.")
    public String getText() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    @ScriptFunction(jsDocText="The default icon.")
    public Icon getIcon() {
        return delegate.getIcon();
    }

    @ScriptFunction
    public void setIcon(Icon aValue) {
        delegate.setIcon(aValue);
    }

    @ScriptFunction(jsDocText="The state of the button.")
    public boolean isSelected() {
        return delegate.isSelected();
    }

    @ScriptFunction
    public void setSelected(boolean aValue) {
        delegate.setSelected(aValue);
    }
}
