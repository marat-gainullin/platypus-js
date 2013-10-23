/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.script.ScriptFunction;
import javax.swing.JEditorPane;

/**
 *
 * @author mg
 */
public class HtmlArea extends Component<JEditorPane>{
    
    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* HTML area component. \n"
            + "* @param text the initial text for the HTML area (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text"})
    public HtmlArea(String aText) {
        super();
        JEditorPane pane = new JEditorPane();
        pane.setContentType("text/html");
        pane.setText(aText);
        setDelegate(pane);
    }

    public HtmlArea() {
        this((String) null);
    }

    protected HtmlArea(JEditorPane aDelegate) {
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
