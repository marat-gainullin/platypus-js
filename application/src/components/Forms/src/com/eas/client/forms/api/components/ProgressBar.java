/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.script.ScriptFunction;
import javax.swing.JProgressBar;

/**
 *
 * @author mg
 */
public class ProgressBar extends Component<JProgressBar> {

    protected ProgressBar(JProgressBar aDelegate)
    {
        super();
        setDelegate(aDelegate);
    }
    
    public ProgressBar(int min, int max) {
        super();
        setDelegate(new JProgressBar(JProgressBar.HORIZONTAL, min, max));
        delegate.setStringPainted(true);
    }

    public ProgressBar() {
        this(0, 0);
    }
    
    @ScriptFunction(jsDocText="The progress bar's minimum value.")
    public int getMinimum()
    {
        return delegate.getMinimum();
    }
    
    @ScriptFunction
    public void setMinimum(int aValue)
    {
        delegate.setMinimum(aValue);
    }
    
    @ScriptFunction(jsDocText="The progress bar's maximum value.")
    public int getMaximum()
    {
        return delegate.getMaximum();
    }
    
    @ScriptFunction
    public void setMaximum(int aValue)
    {
        delegate.setMaximum(aValue);
    }
    
    @ScriptFunction(jsDocText="The current value of the progress bar.")
    public int getValue()
    {
        return delegate.getValue();
    }
    
    @ScriptFunction
    public void setValue(int aValue)
    {
        delegate.setValue(aValue);
    }
    
    @ScriptFunction(jsDocText="String representation of the current progress.")
    public String getText()
    {
        return delegate.getString();
    }
    
    @ScriptFunction
    public void setText(String aValue)
    {
        delegate.setString(aValue);
    }
}
