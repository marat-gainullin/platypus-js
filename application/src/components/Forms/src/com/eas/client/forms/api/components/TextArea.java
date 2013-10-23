/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.script.ScriptFunction;
import javax.swing.JTextPane;

/**
 *
 * @author mg
 */
public class TextArea extends Component<JTextPane> {

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* Text area component. \n"
            + "* @param text the text for the component (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text"})
    public TextArea(String aText) {
        super();
        JTextPane pane = new JTextPane();
        pane.setText(aText);
        setDelegate(pane);
    }

    public TextArea() {
        this((String) null);
    }

    protected TextArea(JTextPane aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    @ScriptFunction(jsDoc = "The text contained in this component.")
    public String getText() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }
}
