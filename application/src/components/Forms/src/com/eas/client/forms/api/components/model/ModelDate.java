/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.dbcontrols.date.DbDate;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ModelDate extends ScalarModelComponent<DbDate> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model component that shows a date. \n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelDate() {
        super();
        setDelegate(new DbDate());
    }

    protected ModelDate(DbDate aDelegate) {
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
    private static final String EXPANDED_JSDOC = ""
            + "/**\n"
            + "* Sets up the control appearance. If true, than calndar panel is displayed, otherwise date/time combo is displayed.\n"
            + "*/";

    @ScriptFunction(jsDoc = EXPANDED_JSDOC)
    public boolean getExpanded() {
        return delegate.isExpanded();
    }

    @ScriptFunction
    public void setExpanded(boolean aValue) throws Exception {
        delegate.setExpanded(aValue);
        delegate.revalidate();
        delegate.repaint();
    }

    @ScriptFunction
    public String getDateFormat() {
        return delegate.getDateFormat();
    }

    @ScriptFunction
    public void setDateFormat(String aValue) throws Exception {
        delegate.setDateFormat(aValue);
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
    public String getText() throws Exception {
        if (delegate.getValue() == null) {
            return null;
        } else {
            SimpleDateFormat format = new SimpleDateFormat(delegate.getDateFormat());
            return format.format(delegate.getValue());
        }
    }

    @ScriptFunction
    public void setText(String aValue) throws Exception {
        try {
            SimpleDateFormat format = new SimpleDateFormat(delegate.getDateFormat());
            delegate.setValue(format.parse(aValue));
        } catch (ParseException ex) {
            // no op
        }
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
