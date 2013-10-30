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
      
    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* Password field component.\n"
            + "* @param text the text for the component (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text"})
    public PasswordField(String aText)
    {
        super();
        setDelegate(new JPasswordField(aText));
    }
    
    public PasswordField()
    {
        this((String)null);
    }
    
    protected PasswordField(JPasswordField aDelegate)
    {
        super();
        setDelegate(aDelegate);
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
