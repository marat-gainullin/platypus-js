/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.metadata;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author mg
 */
public class DbTableIndexSpec {

    public static final String UNIQUE_PROPERTY = "unique"; //NOI18N
    public static final String CLUSTERED_PROPERTY = "clustered"; //NOI18N
    public static final String HASHED_PROPERTY = "hashed"; //NOI18N
    public static final String NAME_PROPERTY = "name"; //NOI18N
    public static final String COLUMNS_PROPERTY = "columns"; //NOI18N
    
    protected List<DbTableIndexColumnSpec> columns = new ArrayList<>();
    protected boolean clustered = false;
    protected boolean hashed = false;
    protected boolean unique = false;
    protected String name = null;
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public DbTableIndexSpec() {
        super();
    }

    public DbTableIndexSpec(DbTableIndexSpec aSource) {
        super();
        assert aSource != null;
        clustered = aSource.isClustered();
        hashed = aSource.isHashed();
        unique = aSource.isUnique();
        name = null;
        if (aSource.getName() != null) {
            name = new String(aSource.getName().toCharArray());
        }
        List<DbTableIndexColumnSpec> sourceColumns = aSource.getColumns();
        for (int i = 0; i < sourceColumns.size(); i++) {
            columns.add(sourceColumns.get(i).copy());
        }
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public DbTableIndexSpec copy() {
        return new DbTableIndexSpec(this);
    }

    public boolean isEqual(Object obj)
    {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DbTableIndexSpec other = (DbTableIndexSpec) obj;
        if (this.unique != other.unique) {
            return false;
        }
        if (this.clustered != other.clustered) {
            return false;
        }
        if (this.hashed != other.hashed) {
            return false;
        }
        if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
            return false;
        }
        if (this.columns != other.columns && (this.columns == null || !this.columns.equals(other.columns))) {
            return false;
        }
        return true;
}

    public List<DbTableIndexColumnSpec> getColumns() {
        return columns;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean aValue) {
        boolean oldValue = unique;
        unique = aValue;
        changeSupport.firePropertyChange(UNIQUE_PROPERTY, oldValue, aValue);
    }

    public boolean isClustered() {
        return clustered;
    }

    public void setClustered(boolean aValue) {
        boolean oldValue = clustered;
        clustered = aValue;
        changeSupport.firePropertyChange(CLUSTERED_PROPERTY, oldValue, aValue);
    }

    public boolean isHashed() {
        return hashed;
    }

    public void setHashed(boolean aValue) {
        boolean oldValue = hashed;
        hashed = aValue;
        changeSupport.firePropertyChange(HASHED_PROPERTY, oldValue, aValue);
    }

    public String getName() {
        return name;
    }

    public void setName(String aValue) {
        String oldValue = name;
        name = aValue;
        changeSupport.firePropertyChange(NAME_PROPERTY, oldValue, aValue);
    }

    public DbTableIndexColumnSpec getColumn(String aColumnName) {
        for (int i = 0; i < columns.size(); i++) {
            DbTableIndexColumnSpec column = columns.get(i);
            if (column.getColumnName().equalsIgnoreCase(aColumnName)) {
                return column;
            }
        }
        return null;
    }

    public void addColumn(DbTableIndexColumnSpec aColumn) {
        if (aColumn != null && getColumn(aColumn.getColumnName()) == null) {
            columns.add(aColumn);
        }
    }

    public boolean findColumnByName(String aColumnName)
    {
        return indexOfColumnByName(aColumnName) != -1;
    }

    public int indexOfColumnByName(String aColumnName)
    {
        if(aColumnName != null && !aColumnName.isEmpty())
        {
            for(int i=0;i<columns.size();i++)
            {
                DbTableIndexColumnSpec column = columns.get(i);
                if(aColumnName.equalsIgnoreCase(column.getColumnName()))
                    return i;
            }
        }
        return -1;
    }

    public void sortColumns() {
        Map<Integer, DbTableIndexColumnSpec> tm = new TreeMap<>();
        for (DbTableIndexColumnSpec column : columns) {
            tm.put(column.getOrdinalPosition(), column);
        }
        columns.clear();
        for (DbTableIndexColumnSpec column : tm.values()) {
            columns.add(column);
        }
    }
}
