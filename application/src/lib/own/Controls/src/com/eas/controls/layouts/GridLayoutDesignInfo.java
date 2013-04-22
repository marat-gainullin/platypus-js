/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts;

import com.eas.store.Serial;
import com.eas.controls.DesignInfo;

/**
 *
 * @author mg
 */
public class GridLayoutDesignInfo extends LayoutDesignInfo {

    protected int hgap;
    protected int vgap;
    protected int rows = 1;
    protected int columns;

    public GridLayoutDesignInfo() {
        super();
    }

    @Serial
    public int getHgap() {
        return hgap;
    }

    @Serial
    public void setHgap(int aValue) {
        int oldValue = hgap;
        hgap = aValue;
        firePropertyChange("hgap", oldValue, hgap);
    }

    @Serial
    public int getVgap() {
        return vgap;
    }

    @Serial
    public void setVgap(int aValue) {
        int oldValue = vgap;
        vgap = aValue;
        firePropertyChange("vgap", oldValue, vgap);
    }

    @Serial
    public int getRows() {
        return rows;
    }

    @Serial
    public void setRows(int aValue) {
        int oldValue = rows;
        rows = aValue;
        firePropertyChange("rows", oldValue, rows);
    }

    @Serial
    public int getColumns() {
        return columns;
    }

    @Serial
    public void setColumns(int aValue) {
        int oldValue = columns;
        columns = aValue;
        firePropertyChange("columns", oldValue, columns);
    }

    @Override
    public void accept(LayoutDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        GridLayoutDesignInfo other = (GridLayoutDesignInfo) obj;
        if (this.hgap != other.hgap) {
            return false;
        }
        if (this.vgap != other.vgap) {
            return false;
        }
        if (this.rows != other.rows) {
            return false;
        }
        if (this.columns != other.columns) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof GridLayoutDesignInfo) {
            GridLayoutDesignInfo source = (GridLayoutDesignInfo) aSource;
            hgap = source.hgap;
            vgap = source.vgap;
            rows = source.rows;
            columns = source.columns;
        }
    }
}
