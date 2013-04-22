/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.containers;

import com.eas.controls.ContainerDesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;
import com.eas.store.Serial;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author mg
 */
public class ScrollDesignInfo extends ContainerDesignInfo {

    protected int horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
    protected int verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
    protected boolean wheelScrollingEnabled;
    protected String columnHeader;
    protected String rowHeader;

    public ScrollDesignInfo() {
        super();
    }

    @Serial
    public int getHorizontalScrollBarPolicy() {
        return horizontalScrollBarPolicy;
    }

    @Serial
    public void setHorizontalScrollBarPolicy(int aValue) {
        int oldValue = horizontalScrollBarPolicy;
        horizontalScrollBarPolicy = aValue;
        firePropertyChange("horizontalScrollBarPolicy", oldValue, horizontalScrollBarPolicy);
    }

    @Serial
    public int getVerticalScrollBarPolicy() {
        return verticalScrollBarPolicy;
    }

    @Serial
    public void setVerticalScrollBarPolicy(int aValue) {
        int oldValue = verticalScrollBarPolicy;
        verticalScrollBarPolicy = aValue;
        firePropertyChange("verticalScrollBarPolicy", oldValue, verticalScrollBarPolicy);
    }

    @Serial
    public boolean isWheelScrollingEnabled() {
        return wheelScrollingEnabled;
    }

    @Serial
    public void setWheelScrollingEnabled(boolean aValue) {
        boolean oldValue = wheelScrollingEnabled;
        wheelScrollingEnabled = aValue;
        firePropertyChange("wheelScrollingEnabled", oldValue, wheelScrollingEnabled);
    }

    @Serial
    public String getColumnHeader() {
        return columnHeader;
    }

    @Serial
    public void setColumnHeader(String aValue) {
        String oldValue = columnHeader;
        columnHeader = aValue;
        firePropertyChange("columnHeader", oldValue, columnHeader);
    }

    @Serial
    public String getRowHeader() {
        return rowHeader;
    }

    @Serial
    public void setRowHeader(String aValue) {
        String oldValue = rowHeader;
        rowHeader = aValue;
        firePropertyChange("rowHeader", oldValue, rowHeader);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final ScrollDesignInfo other = (ScrollDesignInfo) obj;

        if (this.horizontalScrollBarPolicy != other.horizontalScrollBarPolicy) {
            return false;
        }
        if (this.verticalScrollBarPolicy != other.verticalScrollBarPolicy) {
            return false;
        }
        if ((this.columnHeader == null) ? (other.columnHeader != null) : !this.columnHeader.equals(other.columnHeader)) {
            return false;
        }
        if ((this.rowHeader == null) ? (other.rowHeader != null) : !this.rowHeader.equals(other.rowHeader)) {
            return false;
        }
        if (this.wheelScrollingEnabled != other.wheelScrollingEnabled) {
            return false;
        }
        return true;
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public void assign(DesignInfo aSource) {
        super.assign(aSource);
        if (aSource instanceof ScrollDesignInfo) {
            ScrollDesignInfo sSource = (ScrollDesignInfo) aSource;

            horizontalScrollBarPolicy = sSource.horizontalScrollBarPolicy;
            verticalScrollBarPolicy = sSource.verticalScrollBarPolicy;
            columnHeader = sSource.columnHeader != null ? new String(sSource.columnHeader.toCharArray()) : null;
            rowHeader = sSource.rowHeader != null ? new String(sSource.rowHeader.toCharArray()) : null;
            wheelScrollingEnabled = sSource.wheelScrollingEnabled;
        }
    }
}
