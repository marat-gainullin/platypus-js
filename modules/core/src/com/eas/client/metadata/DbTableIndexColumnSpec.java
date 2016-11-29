/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.metadata;

import java.beans.PropertyChangeSupport;

/**
 *
 * @author mg
 */
public class DbTableIndexColumnSpec {

    public static final String ASCENDING_PROPERTY = "ascending"; //NOI18N
    public static final String ORDINAL_POSITION_PROPERTY = "ordinalPosition"; //NOI18N
    protected String columnName;
    protected boolean ascending = true;
    protected int ordinalPosition = -1;
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public DbTableIndexColumnSpec(String aColumnName, boolean aAscending) {
        super();
        columnName = aColumnName;
        ascending = aAscending;
    }

    public DbTableIndexColumnSpec(DbTableIndexColumnSpec aSource) {
        columnName = new String(aSource.getColumnName().toCharArray());
        ascending = aSource.isAscending();
        ordinalPosition = aSource.getOrdinalPosition();
    }

    public DbTableIndexColumnSpec copy() {
        return new DbTableIndexColumnSpec(this);
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.columnName != null ? this.columnName.hashCode() : 0);
        hash = 59 * hash + (this.ascending ? 1 : 0);
        hash = 59 * hash + this.ordinalPosition;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DbTableIndexColumnSpec other = (DbTableIndexColumnSpec) obj;
        if ((this.columnName == null) ? (other.columnName != null) : !this.columnName.equals(other.columnName)) {
            return false;
        }
        if (this.ascending != other.ascending) {
            return false;
        }
        if (this.ordinalPosition != other.ordinalPosition) {
            return false;
        }
        return true;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setAscending(boolean anAscending) {
        boolean oldValue = ascending;
        ascending = anAscending;
        changeSupport.firePropertyChange(ASCENDING_PROPERTY, oldValue, ascending);
    }

    public void setOrdinalPosition(int anOrdinalPosition) {
        int oldValue = ordinalPosition;
        ordinalPosition = anOrdinalPosition;
        changeSupport.firePropertyChange(ORDINAL_POSITION_PROPERTY, oldValue, ordinalPosition);
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }
}
