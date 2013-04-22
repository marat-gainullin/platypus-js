/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.script.ScriptFunction;
import javax.swing.JTextField;

/**
 *
 * @author mg
 */
public class TextField extends Component<JTextField>{
    
    protected TextField(JTextField aDelegate)
    {
        super();
        setDelegate(aDelegate);
    }
    
    public TextField(String aText)
    {
        super();
        setDelegate(new JTextField(aText));
    }
    
    public TextField()
    {
        this((String)null);
    }
    
    @ScriptFunction(jsDocText = "The text contained in this component.")
    public String getText()
    {
        return delegate.getText();
    }
    
    @ScriptFunction
    public void setText(String aValue)
    {
        delegate.setText(aValue);
    }
}
