/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.controls.ControlsUtils;
import com.eas.dbcontrols.label.DbLabel;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ModelFormattedField extends ScalarModelComponent<DbLabel> {

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * A model component that shows a date.\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC)
    public ModelFormattedField() {
        super();
        setDelegate(new DbLabel());
    }

    protected ModelFormattedField(DbLabel aDelegate) {
        super();
        setDelegate(aDelegate);
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

    private static final String FORMAT_JSDOC = ""
            + "/**\n"
            + "* The format string of the component.\n"
            + "*/";

    @ScriptFunction(jsDoc = FORMAT_JSDOC)
    public String getFormat() {
        if (delegate.getFocusTargetComponent() instanceof JFormattedTextField) {
            JFormattedTextField delegateFf = (JFormattedTextField) delegate.getFocusTargetComponent();
            if (delegateFf.getFormatter() != null) {
                return ControlsUtils.formatByFormatter(delegateFf.getFormatter());
            } else {
                if (delegateFf.getFormatterFactory() != null) {
                    return ControlsUtils.formatByFormatter(delegateFf.getFormatterFactory().getFormatter(delegateFf));
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    @ScriptFunction
    public void setFormat(String aValue) throws ParseException {
        if (delegate.getFocusTargetComponent() instanceof JFormattedTextField) {
            JFormattedTextField delegateFf = (JFormattedTextField) delegate.getFocusTargetComponent();
            if (getFormat() == null ? aValue != null : !getFormat().equals(aValue)) {
                if (delegateFf.getFormatter() != null) {
                    ControlsUtils.applyFormat(delegateFf.getFormatter(), aValue);
                }
            }
        }
    }

    @ScriptFunction
    public String getText() throws Exception {
        Object value = getValue();
        if (delegate.getFocusTargetComponent() instanceof JFormattedTextField) {
            JFormattedTextField formatField = (JFormattedTextField) delegate.getFocusTargetComponent();
            return formatField.getText();
        } else {
            return value != null ? value instanceof String ? (String) value : value.toString() : null;
        }
    }

    @ScriptFunction
    public void setText(String aValue) throws Exception {
        if (delegate.getFocusTargetComponent() instanceof JFormattedTextField) {
            JFormattedTextField formatField = (JFormattedTextField) delegate.getFocusTargetComponent();
            formatField.setText(aValue);
            formatField.commitEdit();
            setValue(formatField.getValue());
        }
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
