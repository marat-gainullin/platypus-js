/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.label;

import com.eas.controls.ControlsUtils;
import com.eas.controls.DesignInfo;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class DbLabelDesignInfo extends DbControlDesignInfo {

    public static final String FORMAT = "format";
    public static final String VALUETYPE = "valueType";
    protected String format;
    protected int valueType = ControlsUtils.MASK;

    public DbLabelDesignInfo() {
        super();
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final DbLabelDesignInfo other = (DbLabelDesignInfo) obj;
        if ((this.format == null) ? (other.format != null) : !this.format.equals(other.format)) {
            return false;
        }
        if (this.valueType != other.valueType) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        super.assign(aSource);
        if (aSource instanceof DbLabelDesignInfo) {
            DbLabelDesignInfo aInfo = (DbLabelDesignInfo) aSource;
            setFormat(aInfo.getFormat() != null ? new String(aInfo.getFormat().toCharArray()) : null);
            setValueType(aInfo.getValueType());
        }
    }

    public Class<?> getControlClass() {
        return DbLabel.class;
    }

    @Serial
    public String getFormat() {
        return format;
    }

    @Serial
    public void setFormat(String aValue) {
        String old = format;
        format = aValue;
        firePropertyChange(FORMAT, old, aValue);
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
    protected void accept(DbControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
