package com.eas.client.metadata;

import java.beans.PropertyChangeSupport;

/**
 * A class intended to hold information about primary key constraint in database.
 */
public class PrimaryKeySpec {

    protected String schema = "";
    protected String table = "";
    protected String field = "";
    protected String cName = "";

    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    
    /**
     * The default constructor.
     */
    public PrimaryKeySpec() {
        super();
    }

    /**
     * Constructor with all information specified as the parameters.
     * @param aSchema Database schema. Null means application schema in application database.
     * @param aTable Table name. Null and empty string are not allowed.
     * @param aField Field name. Null and empty string are not allowed.
     * @param aCName Constraint name. Null and empty string are not allowed.
     */
    public PrimaryKeySpec(String aSchema, String aTable, String aField, String aCName) {
        this();
        copyPkFromValues(aSchema, aTable, aField, aCName);
    }

    /**
     * Copy contructor.
     * @param aSource <code>PrimaryKeySpec</code> instance to be used as information source.
     */
    protected PrimaryKeySpec(PrimaryKeySpec aSource) {
        super();
        if (aSource != null) {
            copyPkFromValues(aSource.getSchema(), aSource.getTable(), aSource.getField(), aSource.getCName());
        }
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + (this.schema != null ? this.schema.hashCode() : 0);
        hash = 11 * hash + (this.table != null ? this.table.hashCode() : 0);
        hash = 11 * hash + (this.field != null ? this.field.hashCode() : 0);
        hash = 11 * hash + (this.cName != null ? this.cName.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PrimaryKeySpec other = (PrimaryKeySpec) obj;
        if ((this.schema == null) ? (other.schema != null) : !this.schema.equals(other.schema)) {
            return false;
        }
        if ((this.table == null) ? (other.table != null) : !this.table.equals(other.table)) {
            return false;
        }
        if ((this.field == null) ? (other.field != null) : !this.field.equals(other.field)) {
            return false;
        }
        if ((this.cName == null) ? (other.cName != null) : !this.cName.equals(other.cName)) {
            return false;
        }
        return true;
    }

    /**
     * Copies this instance to a new instance using copy constructor.
     * @return Copied <code>PrimaryKeySpec</code> instance.
     * @see #PrimaryKeySpec(PrimaryKeySpec aSource)
     */
    public PrimaryKeySpec copy() {
        return new PrimaryKeySpec(this);
    }

    protected void copyPkFromValues(String aSchema, String aTable, String aField, String aCName) {
        if (aSchema != null) {
            setSchema(aSchema);
        }
        if (aTable != null) {
            setTable(aTable);
        }
        if (aField != null) {
            setField(aField);
        }
        if (aCName != null) {
            setCName(aCName);
        }
    }

    /**
     * Returns schema name of this constraint.
     * @return Schema name of this constraint.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Sets schema name of this constraint.
     * @param aValue Name of the schema.
     */
    public void setSchema(String aValue) {
        String oldValue = schema;
        schema = aValue;
        changeSupport.firePropertyChange("schema", oldValue, aValue);
    }

    /**
     * Returns table name of this constraint.
     * @return Table name of this constraint.
     */
    public String getTable() {
        return table;
    }

    /**
     * Sets table name for this constraint.
     * @param aValue Table name for this constraint.
     */
    public void setTable(String aValue) {
        String oldValue = table;
        table = aValue;
        changeSupport.firePropertyChange("table", oldValue, aValue);
    }

    /**
     * Returns field name of this constraint.
     * @return Field name of this constraint.
     */
    public String getField() {
        return field;
    }

    /**
     * Sets field name of this constraint.
     * @param aValue Field name of this constraint.
     */
    public void setField(String aValue) {
        String oldValue = field;
        field = aValue;
        changeSupport.firePropertyChange("field", oldValue, aValue);
    }

    /**
     * Returns constraint name.
     * @return Constraint name.
     */
    public String getCName() {
        return cName;
    }

    /**
     * Sets the constraint name.
     * @param aValue] Constraint name.
     */
    public void setCName(String aValue) {
        String oldValue = cName;
        cName = aValue;
        changeSupport.firePropertyChange("cName", oldValue, aValue);
    }
}
