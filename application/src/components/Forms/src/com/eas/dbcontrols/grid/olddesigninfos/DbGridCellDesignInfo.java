/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.olddesigninfos;

import com.eas.client.model.ModelElementRef;
import com.eas.controls.DesignInfo;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsUtils;
import com.eas.store.ClassedSerial;
import com.eas.store.PropertiesSimpleFactory;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class DbGridCellDesignInfo extends DesignInfo implements PropertiesSimpleFactory {

    public static final String CELLCONTROLINFO = "cellControlInfo";
    public static final String CELLVALUEFIELD = "cellValueField";
    public static final String COLUMNSKEYFIELD = "columnsKeyField";
    public static final String ROWSKEYFIELD = "rowsKeyField";
    protected ModelElementRef rowsKeyField = new ModelElementRef();
    protected ModelElementRef columnsKeyField = new ModelElementRef();
    protected ModelElementRef cellValueField = new ModelElementRef();
    protected DbControlDesignInfo cellControlInfo = null;

    @Override
    public boolean isEqual(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DbGridCellDesignInfo other = (DbGridCellDesignInfo) obj;
        if (this.rowsKeyField != other.rowsKeyField && (this.rowsKeyField == null || !this.rowsKeyField.equals(other.rowsKeyField))) {
            return false;
        }
        if (this.columnsKeyField != other.columnsKeyField && (this.columnsKeyField == null || !this.columnsKeyField.equals(other.columnsKeyField))) {
            return false;
        }
        if (this.cellValueField != other.cellValueField && (this.cellValueField == null || !this.cellValueField.equals(other.cellValueField))) {
            return false;
        }
        if (this.cellControlInfo != other.cellControlInfo && (this.cellControlInfo == null || !this.cellControlInfo.isEqual(other.cellControlInfo))) {
            return false;
        }
        return true;
    }

    protected void assign(DbGridCellDesignInfo aInfo) {
        if (aInfo != null) {
            setRowsKeyField(aInfo.getRowsKeyField() != null ? aInfo.getRowsKeyField().copy() : null);
            setColumnsKeyField(aInfo.getColumnsKeyField() != null ? aInfo.getColumnsKeyField().copy() : null);
            setCellValueField(aInfo.getCellValueField() != null ? aInfo.getCellValueField().copy() : null);
            setCellControlInfo(aInfo.getCellControlInfo() != null ? (DbControlDesignInfo) aInfo.getCellControlInfo().copy() : null);
        }
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource != null && aSource instanceof DbGridCellDesignInfo) {
            assign((DbGridCellDesignInfo) aSource);
        }
    }

    @Serial
    public ModelElementRef getRowsKeyField() {
        return rowsKeyField;
    }

    @Serial
    public void setRowsKeyField(ModelElementRef aValue) {
        ModelElementRef old = rowsKeyField;
        rowsKeyField = aValue;
        firePropertyChange(ROWSKEYFIELD, old, aValue);
    }

    @Serial
    public ModelElementRef getColumnsKeyField() {
        return columnsKeyField;
    }

    @Serial
    public void setColumnsKeyField(ModelElementRef aValue) {
        ModelElementRef old = columnsKeyField;
        columnsKeyField = aValue;
        firePropertyChange(COLUMNSKEYFIELD, old, aValue);
    }

    @Serial
    public ModelElementRef getCellValueField() {
        return cellValueField;
    }

    @Serial
    public void setCellValueField(ModelElementRef aValue) {
        ModelElementRef old = cellValueField;
        cellValueField = aValue;
        firePropertyChange(CELLVALUEFIELD, old, aValue);
    }

    @ClassedSerial(propertyClassHint = "classHint")
    public DbControlDesignInfo getCellControlInfo() {
        return cellControlInfo;
    }

    @ClassedSerial(propertyClassHint = "classHint")
    public void setCellControlInfo(DbControlDesignInfo aValue) {
        DbControlDesignInfo old = cellControlInfo;
        cellControlInfo = aValue;
        firePropertyChange(CELLCONTROLINFO, old, aValue);
    }

    public Object createPropertyObjectInstance(String aSimpleClassName) {
        return DbControlsUtils.createDesignInfoBySimpleClassName(aSimpleClassName);
    }
}
