/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.dbcontrols.text.DbText;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.text.DecimalFormat;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ModelTextArea extends ScalarModelComponent<DbText> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model components for a text area.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelTextArea() {
        super();
        setDelegate(new DbText());
    }

    protected ModelTextArea(DbText aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    private static final String EDITABLE_JSDOC = ""
            + "/**\n"
            + " * Determines if component is editable.\n"
            + " */";

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    public boolean getEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }

    @ScriptFunction
    public String getEmptyText() {
        return delegate.getEmptyText();
    }

    @ScriptFunction
    public void setEmptyText(String aValue) {
        delegate.setEmptyText(aValue);
    }

    @ScriptFunction
    @Override
    public Object getValue() throws Exception {
        return super.getValue();
    }

    @ScriptFunction
    @Override
    public void setValue(Object aValue) throws Exception {
        if(aValue instanceof Number){
            Number n = (Number)aValue;
            DecimalFormat df = new DecimalFormat("#.#");
            aValue = df.format(n.doubleValue());
        }
        super.setValue(aValue);
    }

    @ScriptFunction
    public String getText() throws Exception {
        Object value = getValue();
        return value != null ? value instanceof String ? (String) value : value.toString() : null;
    }

    @ScriptFunction
    public void setText(String aValue) throws Exception {
        setValue(aValue);
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
