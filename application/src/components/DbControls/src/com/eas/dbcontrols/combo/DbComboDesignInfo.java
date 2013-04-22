/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.combo;

import com.eas.client.model.ModelElementRef;
import com.eas.controls.DesignInfo;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class DbComboDesignInfo extends DbControlDesignInfo {

    public static final String DISPLAYFIELD = "displayField";
    public static final String LIST = "list";
    public static final String VALUEFIELD = "valueField";
    protected boolean list = false;
    protected ModelElementRef valueField = null;
    protected ModelElementRef displayField = null;

    public DbComboDesignInfo() {
        super();
    }

    protected DbComboDesignInfo(DbComboDesignInfo aInfo) {
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final DbComboDesignInfo other = (DbComboDesignInfo) obj;
        if (this.displayField != other.displayField && (this.displayField == null || !this.displayField.equals(other.displayField))) {
            return false;
        }
        if (this.valueField != other.valueField && (this.valueField == null || !this.valueField.equals(other.valueField))) {
            return false;
        }
        if (this.list != other.list) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        super.assign(aSource);
        if (aSource != null && aSource instanceof DbComboDesignInfo) {
            DbComboDesignInfo aInfo = (DbComboDesignInfo) aSource;
            setDisplayField(aInfo.getDisplayField() != null ? aInfo.getDisplayField().copy() : null);
            setValueField(aInfo.getValueField() != null ? aInfo.getValueField().copy() : null);
            setList(aInfo.isList());
        }
    }

    @Serial
    public ModelElementRef getDisplayField() {
        return displayField;
    }

    @Serial
    public void setDisplayField(ModelElementRef aDisplayField) {
        ModelElementRef old = displayField;
        displayField = aDisplayField;
        firePropertyChange(DISPLAYFIELD, old, displayField);
    }

    @Serial
    public boolean isList() {
        return list;
    }

    @Serial
    public void setList(boolean aValue) {
        boolean old = list;
        list = aValue;
        firePropertyChange(LIST, old, list);
    }

    @Serial
    public ModelElementRef getValueField() {
        return valueField;
    }

    @Serial
    public void setValueField(ModelElementRef aValueField) {
        ModelElementRef old = valueField;
        valueField = aValueField;
        firePropertyChange(VALUEFIELD, old, valueField);
    }

    @Override
    protected void accept(DbControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
