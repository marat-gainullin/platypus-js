/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.text.DecimalFormat;
import javax.swing.JTextField;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class TextField extends Component<JTextField> {

    private static JSObject publisher;
    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* Text field component. \n"
            + "* @param text the initial text for the component (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text"})
    public TextField(String aText) {
        super();
        setDelegate(new JTextField(aText));
    }

    public TextField() {
        this((String) null);
    }

    protected TextField(JTextField aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The text contained in this component.\n"
            + " */")
    public String getText() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    @ScriptFunction
    public Object getValue(){
        return getText();
    }
    
    @ScriptFunction
    public void setValue(Object aValue){
        if(aValue instanceof Number){
            Number n = (Number)aValue;
            DecimalFormat df = new DecimalFormat();
            aValue = df.format(n.doubleValue());
        }
        setText(aValue != null ? aValue.toString() : null);
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
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject)publisher.call(null, new Object[]{this});
        }
        return published;
    }

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
