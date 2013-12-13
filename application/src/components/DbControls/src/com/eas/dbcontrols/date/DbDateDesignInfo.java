/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.date;

import com.eas.controls.DesignInfo;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class DbDateDesignInfo extends DbControlDesignInfo {

    public static final String DATEFORMAT = "dateFormat";
    public static final String EXPANDED = "expanded";
    protected String dateFormat = "DD_MM_YYYY";
    protected boolean expanded = false;
    protected String emptyText;

    public DbDateDesignInfo() {
        super();
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final DbDateDesignInfo other = (DbDateDesignInfo) obj;
        if ((this.dateFormat == null) ? (other.dateFormat != null) : !this.dateFormat.equals(other.dateFormat)) {
            return false;
        }
        if (this.expanded != other.expanded) {
            return false;
        }
        if ((this.emptyText == null) ? (other.emptyText != null) : !this.emptyText.equals(other.emptyText)) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        super.assign(aSource);
        if (aSource instanceof DbDateDesignInfo) {
            DbDateDesignInfo aInfo = (DbDateDesignInfo) aSource;
            setDateFormat(aInfo.getDateFormat() != null ? new String(aInfo.getDateFormat().toCharArray()) : null);
            setExpanded(aInfo.isExpanded());
            setEmptyText(aInfo.getEmptyText());
        }
    }

    @Serial
    public String getDateFormat() {
        return dateFormat;
    }

    @Serial
    public void setDateFormat(String aValue) {
        String old = dateFormat;
        dateFormat = aValue;
        firePropertyChange(DATEFORMAT, old, aValue);
    }

    @Serial
    public boolean isExpanded() {
        return expanded;
    }

    @Serial
    public void setExpanded(boolean aValue) {
        boolean old = expanded;
        expanded = aValue;
        firePropertyChange(EXPANDED, old, aValue);
    }

    @Serial
    public String getEmptyText() {
        return emptyText;
    }

    @Serial
    public void setEmptyText(String aValue) {
        String oldValue = emptyText;
        emptyText = aValue;
        firePropertyChange("emptyText", oldValue, emptyText);
    }

    @Override
    protected void accept(DbControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
