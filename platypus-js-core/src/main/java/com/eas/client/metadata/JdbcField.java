/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.metadata;

/**
 *
 * @author mg
 */
public class JdbcField extends Field {

    public static final String SCHEMA_NAME_PROPERTY = "schemaName";
    public static final String SIZE_PROPERTY = "size";
    public static final String SCALE_PROPERTY = "scale";
    public static final String PRECISION_PROPERTY = "precision";
    public static final String SIGNED_PROPERTY = "signed";
    public static final String JDBC_TYPE_PROPERTY = "jdbcType";

    protected int size;
    protected int scale;
    protected int precision;
    protected boolean signed = true;
    protected String schemaName;
    protected int jdbcType;

    public JdbcField() {
        super();
    }
    
    public JdbcField(String aName) {
        super();
        name = aName;
    }
    
    public JdbcField(JdbcField aTemplate) {
        super(aTemplate);
    }

    private static final String SCHEMA_NAME_JS_DOC = "/**\n"
            + "* This field schema name.\n"
            + "*/";

    /**
     * Returns the field's schema name.
     *
     * @return The field's schema name.
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Sets this field schema name.
     *
     * @param aValue This field schema name.
     */
    public void setSchemaName(String aValue) {
        String oldValue = schemaName;
        schemaName = aValue;
        changeSupport.firePropertyChange(SCHEMA_NAME_PROPERTY, oldValue, aValue);
    }

    /**
     * Returns the field size.
     *
     * @return The field size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the field size.
     *
     * @param aValue The field size to be set.
     */
    public void setSize(int aValue) {
        int oldValue = size;
        size = aValue;
        changeSupport.firePropertyChange(SIZE_PROPERTY, oldValue, aValue);
    }

    /**
     * Returns the field's scale.
     *
     * @return The field's scale.
     */
    public int getScale() {
        return scale;
    }

    /**
     * Sets the field's scale.
     *
     * @param aValue The field's scale to be set.
     */
    public void setScale(int aValue) {
        int oldValue = scale;
        scale = aValue;
        changeSupport.firePropertyChange(SCALE_PROPERTY, oldValue, aValue);
    }

    /**
     * Returns the field's precision.
     *
     * @return The field's precision.
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Sets the field's precision.
     *
     * @param aValue The field's precision.
     */
    public void setPrecision(int aValue) {
        int oldValue = precision;
        precision = aValue;
        changeSupport.firePropertyChange(PRECISION_PROPERTY, oldValue, aValue);
    }

    /**
     * Returns whether this field is signed.
     *
     * @return Whether this field is signed.
     */
    public boolean isSigned() {
        return signed;
    }

    /**
     * Sets the field's signed state.
     *
     * @param aValue Field's signed flag.
     */
    public void setSigned(boolean aValue) {
        boolean oldValue = signed;
        signed = aValue;
        changeSupport.firePropertyChange(SIGNED_PROPERTY, oldValue, aValue);
    }

    /**
     * Returns the field size.
     *
     * @return The field size.
     */
    public int getJdbcType() {
        return jdbcType;
    }

    /**
     * Sets the field size.
     *
     * @param aValue The field size to be set.
     */
    public void setJdbcType(int aValue) {
        int oldValue = jdbcType;
        jdbcType = aValue;
        changeSupport.firePropertyChange(JDBC_TYPE_PROPERTY, oldValue, aValue);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (obj != null && obj instanceof JdbcField) {
            JdbcField other = (JdbcField) obj;
            if (super.isEqual(obj)) {
                String rfSchemaName = other.getSchemaName();
                return signed == other.isSigned()
                        && precision == other.getPrecision()
                        && scale == other.getScale()
                        && size == other.getSize()
                        && jdbcType == other.getJdbcType()
                        && (schemaName == null ? rfSchemaName == null : schemaName.equals(rfSchemaName));
            }
        }
        return false;
    }

    /**
     * Copies this feld's information to another instance.
     *
     * @return Another instance of <code>Field</code> class, initialized with
     * this field information.
     */
    @Override
    public JdbcField copy() {
        return new JdbcField(this);
    }

    /**
     * Assignes <code>aSourceField</code> information to this <code>Field</code>
     * instance.
     *
     * @param aSourceField <code>Field</code> instance used as a source for
     * assigning.
     */
    @Override
    public void assignFrom(Field aSourceField) {
        if (aSourceField != null) {
            super.assignFrom(aSourceField);
            if (aSourceField instanceof JdbcField) {
                JdbcField sourceField = (JdbcField) aSourceField;
                setSize(sourceField.getSize());
                setScale(sourceField.getScale());
                setPrecision(sourceField.getPrecision());
                setSigned(sourceField.isSigned());
                setJdbcType(sourceField.getJdbcType());
                setSchemaName(sourceField.getSchemaName());
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (schemaName != null && !schemaName.isEmpty()) {
            sb.append(schemaName).append(".");
        }
        if (tableName != null && !tableName.isEmpty()) {
            sb.append(tableName).append(".");
        }
        if (originalName != null && !originalName.isEmpty()) {
            sb.append(originalName);
        } else {
            sb.append(name);
        }
        if (description != null && !description.isEmpty()) {
            sb.append(" (").append(description).append(")");
        }
        if (pk) {
            sb.append(", primary key");
        }
        if (fk != null && fk.getReferee() != null) {
            PrimaryKeySpec rf = fk.getReferee();
            sb.append(", foreign key to ");
            if (rf.schema != null && !rf.schema.isEmpty()) {
                sb.append(rf.schema).append(".");
            }
            if (rf.table != null && !rf.table.isEmpty()) {
                sb.append(rf.table).append(".");
            }
            sb.append(rf.field);
        }
        sb.append(", ").append(type);
        sb.append(", size ").append(size).append(", precision ").append(precision).append(", scale ").append(scale);
        if (signed) {
            sb.append(", signed");
        }
        if (nullable) {
            sb.append(", nullable");
        }
        if (readonly) {
            sb.append(", readonly");
        }
        return sb.toString();
    }

}
