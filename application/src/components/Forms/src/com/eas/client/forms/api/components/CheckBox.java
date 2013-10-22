/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.script.ScriptFunction;
import javax.swing.JCheckBox;
import org.mozilla.javascript.Function;

/**
 *
 * @author mg
 */
public class CheckBox extends Component<JCheckBox> {
    
    protected CheckBox(JCheckBox aDelegate)
    {
        super();
        setDelegate(aDelegate);
    }
    
    public CheckBox(String aText, boolean aSelected) {
        this(aText, aSelected, null);
    }
    
    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* Check box component. \n"
            + "* @param text Check box text (optional)\n"
            + "* @param selected true if selected (optional)\n"
            + "* @param actionPerformed On action performed function (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text", "selected",  "actionPerformed"})
    public CheckBox(String aText, boolean aSelected, Function aActionPerformedHandler) {
        super();
        setDelegate(new JCheckBox(aText, aSelected));
        setOnActionPerformed(aActionPerformedHandler);
    }

    public CheckBox(String aText) {
        this(aText, false);
    }

    public CheckBox() {
        this(null, false);
    }
    
    @ScriptFunction(jsDoc="Text of the check box.")
    public String getText() {
        return delegate.getText();
    }
    
    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    /*
    public Icon getIcon() {
        return delegate.getIcon();
    }

    public void setIcon(Icon aValue) {
        delegate.setIcon(aValue);
    }
*/
    @ScriptFunction(jsDoc="Determines whether this component is selected.")
    public boolean isSelected() {
        return delegate.isSelected();
    }
    
    @ScriptFunction
    public void setSelected(boolean aValue) {
        delegate.setSelected(aValue);
    }
}
