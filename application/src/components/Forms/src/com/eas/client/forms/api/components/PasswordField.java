/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.script.ScriptFunction;
import javax.swing.JPasswordField;

/**
 *
 * @author mg
 */
public class PasswordField extends Component<JPasswordField>{
    
    protected PasswordField(JPasswordField aDelegate)
    {
        super();
        setDelegate(aDelegate);
    }
    
    public PasswordField(String aText)
    {
        super();
        setDelegate(new JPasswordField(aText));
    }
    
    public PasswordField()
    {
        this((String)null);
    }
    
    @ScriptFunction(jsDoc="The text contained in this component.")
    public String getText()
    {
        return new String(delegate.getPassword());
    }
    
    @ScriptFunction
    public void setText(String aValue)
    {
        delegate.setText(aValue);
    }
}
