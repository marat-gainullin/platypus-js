/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.controls.ControlsUtils;
import com.eas.dbcontrols.label.DbLabel;
import com.eas.script.ScriptFunction;
import java.text.ParseException;
import javax.swing.JFormattedTextField;

/**
 *
 * @author mg
 */
public class ModelFormattedField extends ScalarModelComponent<DbLabel> {

    public ModelFormattedField(DbLabel aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ModelFormattedField() {
        super();
        setDelegate(new DbLabel());
    }

    @ScriptFunction(jsDoc = "Determines if component is editable.")
    public boolean isEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }

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
}
