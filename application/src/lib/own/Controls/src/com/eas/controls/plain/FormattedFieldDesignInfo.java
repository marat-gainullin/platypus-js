/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.plain;

import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.ControlsUtils;
import com.eas.controls.DesignInfo;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class FormattedFieldDesignInfo extends TextFieldDesignInfo {

    protected int focusLostBehavior;
    protected String format;
    protected int valueType = ControlsUtils.MASK;

    public FormattedFieldDesignInfo() {
        super();
    }

    @Serial
    public int getFocusLostBehavior() {
        return focusLostBehavior;
    }

    @Serial
    public void setFocusLostBehavior(int aValue) {
        int oldValue = focusLostBehavior;
        focusLostBehavior = aValue;
        firePropertyChange("focusLostBehavior", oldValue, focusLostBehavior);
    }

    @Serial
    public String getFormat() {
        return format;
    }

    @Serial
    public void setFormat(String aValue) {
        String oldValue = format;
        format = aValue;
        firePropertyChange("format", oldValue, format);
    }

    @Serial
    public int getValueType() {
        return valueType;
    }

    @Serial
    public void setValueType(int aValue) {
        int oldValue = valueType;
        valueType = aValue;
        firePropertyChange("valueType", oldValue, valueType);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        FormattedFieldDesignInfo other = (FormattedFieldDesignInfo) obj;
        if (this.focusLostBehavior != other.focusLostBehavior) {
            return false;
        }
        if ((this.format == null) ? (other.format != null) : !this.format.equals(other.format)) {
            return false;
        }
        if (this.valueType != other.valueType) {
            return false;
        }
        return true;
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public void assign(DesignInfo aValue) {
        super.assign(aValue);
        if (aValue instanceof FormattedFieldDesignInfo) {
            FormattedFieldDesignInfo source = (FormattedFieldDesignInfo) aValue;
            focusLostBehavior = source.focusLostBehavior;
            format = source.format != null ? new String(source.format.toCharArray()) : null;
            valueType = source.valueType;
        }
    }
}
