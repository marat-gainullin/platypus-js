/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.model.ModelElementRef;
import com.eas.dbcontrols.combo.DbCombo;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ModelCombo extends ModelComponentDecorator<DbCombo> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model component that combines a button or editable field and a drop-down list.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelCombo() {
        super();
        setDelegate(new DbCombo());
    }

    protected ModelCombo(DbCombo aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    private static final String VALUE_FIELD_JSDOC = ""
            + "/**\n"
            + "* Value field of the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = VALUE_FIELD_JSDOC)
    public Field getValueField() {
        return delegate.getValueField() != null ? delegate.getValueField().getField() : null;
    }

    @ScriptFunction
    public void setValueField(Field aField) throws Exception {
        if (getValueField() != aField) {
            ModelElementRef modelRef = fieldToModelRef(aField);
            delegate.setValueField(modelRef);
            delegate.configure();
            delegate.setEditingValue(delegate.getValueFromRowset());
            delegate.revalidate();
            delegate.repaint();
        }
    }

    private static final String DISPLAY_FIELD_JSDOC = ""
            + "/**\n"
            + "* Display field of the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = DISPLAY_FIELD_JSDOC)
    public Field getDisplayField() {
        return delegate.getDisplayField() != null ? delegate.getDisplayField().getField() : null;
    }

    @ScriptFunction
    public void setDisplayField(Field aField) throws Exception {
        if (getDisplayField() != aField) {
            ModelElementRef modelRef = fieldToModelRef(aField);
            delegate.setDisplayField(modelRef);
            delegate.configure();
            delegate.setEditingValue(delegate.getValueFromRowset());
            delegate.revalidate();
            delegate.repaint();
        }
    }

    private static final String LIST_JSDOC = ""
            + "/**\n"
            + "* Determines if component shown as list.\n"
            + "*/";

    @ScriptFunction(jsDoc = LIST_JSDOC)
    public boolean getList() throws Exception {
        return delegate.isList();
    }

    @ScriptFunction
    public void setList(boolean aValue) throws Exception {
        delegate.setList(aValue);
        delegate.configure();
        delegate.setEditingValue(delegate.getValueFromRowset());
        delegate.revalidate();
        delegate.repaint();
    }

    private static final String EDITABLE_JSDOC = ""
            + "/**\n"
            + "* Determines if component is editable.\n"
            + "*/";

    @ScriptFunction(jsDoc = EDITABLE_JSDOC)
    public boolean getEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }

    @ScriptFunction
    public String getText() throws Exception {
        return delegate.getText();
    }

    @ScriptFunction
    public String getEmptyText() {
        return delegate.getEmptyText();
    }

    @ScriptFunction
    public void setEmptyText(String aValue) {
        delegate.setEmptyText(aValue);
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
