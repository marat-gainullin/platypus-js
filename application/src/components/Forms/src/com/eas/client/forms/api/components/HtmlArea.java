/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import javax.swing.JEditorPane;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class HtmlArea extends Component<JEditorPane> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
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
    private static final String TEXT_JSDOC = ""
            + "/**\n"
            + "* Text of the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = TEXT_JSDOC)
    public String getText() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    private static final String VALUE_JSDOC = ""
            + "/**\n"
            + "* Value of the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = VALUE_JSDOC)
    public String getValue() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setValue(String aValue) {
        delegate.setText(aValue);
    }
    
    private static final String EMPTY_TEXT_JSDOC = ""
            + "/**\n"
            + "* The text to be shown when component's value is absent.\n"
            + "*/";

    @ScriptFunction(jsDoc = EMPTY_TEXT_JSDOC)
    public String getEmptyText() {
        return (String) delegate.getClientProperty(Component.EMPTY_TEXT_PROP_NAME);
    }

    @ScriptFunction
    public void setEmptyText(String aValue) {
        delegate.putClientProperty(Component.EMPTY_TEXT_PROP_NAME, aValue);
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
