/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.dbcontrols.check.DbCheck;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ModelCheckBox extends ModelComponentDecorator<DbCheck> {

    public ModelCheckBox() throws Exception {
        super();
        setDelegate(new DbCheck());
    }

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * An implementation of a model check box -- an item that can be selected or deselected, and which displays its state to the user.\n"
            + " * @param text the text of the component (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text"})
    public ModelCheckBox(String aText) throws Exception {
        this();
        delegate.setText(aText);
    }

    protected ModelCheckBox(DbCheck aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    private static final String TEXT_JSDOC = ""
            + "/**\n"
            + "* Text on the check box."
            + "*/";

    @ScriptFunction(jsDoc = TEXT_JSDOC)
    public String getText() {
        return delegate.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        delegate.setText(aValue);
    }

    private static final String EDITABLE_JSDOC = ""
            + "/**\n"
            + "* Determines if component is editable."
            + "*/";

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    public boolean getEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
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

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
