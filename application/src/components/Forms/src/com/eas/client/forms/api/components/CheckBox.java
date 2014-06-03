/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components;

import com.eas.client.forms.api.Component;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import javax.swing.JCheckBox;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class CheckBox extends Component<JCheckBox> {

    protected CheckBox(JCheckBox aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public CheckBox(String aText, boolean aSelected) {
        this(aText, aSelected, null);
    }

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* Check box component. \n"
            + "* @param text the text of the check box (optional).\n"
            + "* @param selected <code>true</code> if selected (optional).\n"
            + "* @param actionPerformed the function for the action performed (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text", "selected", "actionPerformed"})
    public CheckBox(String aText, boolean aSelected, JSObject aActionPerformedHandler) {
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

    private static final String TEXT_JSDOC = ""
            + "/**\n"
            + "* Text of the check box."
            + "*/";

    @ScriptFunction(jsDoc = TEXT_JSDOC)
    public String getText() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    private static final String SELECTED_JSDOC = ""
            + "/**\n"
            + "* Determines whether this component is selected."
            + "*/";

    @ScriptFunction(jsDoc = SELECTED_JSDOC)
    public boolean getSelected() {
        return delegate.isSelected();
    }

    @ScriptFunction
    public void setSelected(boolean aValue) {
        delegate.setSelected(aValue);
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
