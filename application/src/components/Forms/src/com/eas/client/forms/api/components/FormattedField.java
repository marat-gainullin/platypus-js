/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.controls.ControlsUtils;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
import java.text.ParseException;
import javax.swing.JFormattedTextField;

/**
 *
 * @author mg
 */
public class FormattedField extends Component<JFormattedTextField> {
    
    protected FormattedField(JFormattedTextField aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    public FormattedField(Object aValue) {
        super();
        setDelegate(new JFormattedTextField());
        setValue(aValue);
    }
    
    public FormattedField() {
        this((Object) null);
    }
    
    @ScriptFunction(jsDocText = "Gets text of this component.")
    public String getText() {
        return delegate.getText();
    }
    
    @ScriptFunction(jsDocText = "The value of this component.")
    public Object getValue() {
        return ScriptUtils.javaToJS(delegate.getValue(), jsWrapper);
    }
    
    @ScriptFunction
    public void setValue(Object aValue) {
        delegate.setValue(ScriptUtils.js2Java(aValue));
    }
    
    public String getFormat() {
        if (delegate.getFormatter() != null) {
            return ControlsUtils.formatByFormatter(delegate.getFormatter());
        } else {
            if (delegate.getFormatterFactory() != null) {
                return ControlsUtils.formatByFormatter(delegate.getFormatterFactory().getFormatter(delegate));
            } else {
                return null;
            }
        }
    }
    
    public void setFormat(String aValue) throws ParseException {
        if (getFormat() == null ? aValue != null : !getFormat().equals(aValue)) {
            if (delegate.getFormatter() != null) {
                ControlsUtils.applyFormat(delegate.getFormatter(), aValue);
                delegate.setText(delegate.getFormatter().valueToString(delegate.getValue()));
            }
        }
    }
}
