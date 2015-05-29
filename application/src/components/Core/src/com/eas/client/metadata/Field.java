/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.metadata;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import java.beans.PropertyChangeSupport;
import jdk.nashorn.api.scripting.JSObject;

/**
 * This class is table field representation. It holds information about field
 * name, description, typeInfo, size and information about primary and foreign
 * keys. If <code>isPk()</code> returns true, than this field is the primary key
 * in corresponding table. If <code>getFk()</code> returns reference to a
 * <code>PrimaryKeySpec</code>, than it is a foreign key in corresponding table,
 * and it references to returning <code>PrimaryKeySpec</code>.
 *
 * @author mg
 */
public class Field implements HasPublished {

    public static int UNDEFINED_FILED_INDEX = -1;
    // Our user-supplied information
    protected String name = "";
    // In queries, such as select t1.f1 as f11, t2.f1 as f21 to preserve output fields' names unique,
    // but be able to generate right update sql clauses for multiple tables.
    protected String originalName = "";
    protected String description;// Such data is read from db, and so may be null
    protected DataTypeInfo typeInfo = DataTypeInfo.JAVA_OBJECT.copy();
    protected int size;
    protected int scale;
    protected int precision;
    protected boolean signed = true;
    protected boolean nullable = true;
    protected boolean readonly;
    protected boolean pk;
    protected ForeignKeySpec fk;
    protected String tableName;
    protected String schemaName;
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    protected JSObject published;

    public static final String PK_PROPERTY = "pk";
    public static final String STRONG4INSERT_PROPERTY = "strong4Insert";
    public static final String FK_PROPERTY = "fk";
    public static final String READONLY_PROPERTY = "readonly";
    public static final String NAME_PROPERTY = "name";
    public static final String ORIGINAL_NAME_PROPERTY = "originalName";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String TYPE_INFO_PROPERTY = "typeInfo";
    public static final String SCHEMA_NAME_PROPERTY = "schemaName";
    public static final String TABLE_NAME_PROPERTY = "tableName";
    public static final String SIZE_PROPERTY = "size";
    public static final String SCALE_PROPERTY = "scale";
    public static final String PRECISION_PROPERTY = "precision";
    public static final String SIGNED_PROPERTY = "signed";
    public static final String NULLABLE_PROPERTY = "nullable";

    /**
     * The default constructor.
     */
    public Field() {
        super();
    }

    /**
     * Constructor with name.
     *
     * @param aName Name of the created field.
     */
    public Field(String aName) {
        this();
        name = aName;
    }

    /**
     * Constructor with name and description.
     *
     * @param aName Name of the created field.
     * @param aDescription Description of the created field.
     */
    public Field(String aName, String aDescription) {
        this(aName);
        description = aDescription;
    }

    /**
     * Constructor with name, description and typeInfo.
     *
     * @param aName Name of the created field.
     * @param aDescription Description of the created field.
     * @param aTypeInfo Type info of the created field.
     * @see DataTypeInfo
     */
    public Field(String aName, String aDescription, DataTypeInfo aTypeInfo) {
        this(aName, aDescription);
        typeInfo = aTypeInfo;
    }

    /**
     * Copy constructor of <code>Field</code> class.
     *
     * @param aSourceField Source of created field.
     */
    public Field(Field aSourceField) {
        super();
        assignFrom(aSourceField);
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    private static final String FK_JS_DOC = "/**\n"
            + "* Indicates that this field is a foreign key to another table or it is a self-reference key.\n"
            + "*/";

    /**
     * Returns if this field is foreign key to another table or it is a
     * self-reference key.
     *
     * @return If this field is foreign key to another table or it is
     * self-reference key.
     */
    @ScriptFunction(jsDoc = FK_JS_DOC)
    public boolean isFk() {
        return fk != null;
    }

    private static final String PK_JS_DOC = "/**\n"
            + "* Determines that this field is a primary key.\n"
            + "*/";

    /**
     * Returns if this field is primary key.
     *
     * @return If this field is primary key.
     */
    @ScriptFunction(jsDoc = PK_JS_DOC)
    public boolean isPk() {
        return pk;
    }

    /**
     * Sets indicating primary key state of this field.
     *
     * @param aValue Flag, indicating primary key state of this field.
     */
    @ScriptFunction
    public void setPk(boolean aValue) {
        boolean oldValue = pk;
        pk = aValue;
        changeSupport.firePropertyChange(PK_PROPERTY, oldValue, aValue);
    }

    /**
     * Returns foreign key specification of this field if it references to some
     * table.
     *
     * @return Foreign key specification of this field if it references to some
     * table.
     */
    public ForeignKeySpec getFk() {
        return fk;
    }

    /**
     * Sets foreign key specification to this field, making it the reference to
     * some table.
     *
     * @param aValue Foreign key specification to be set to this field.
     */
    public void setFk(ForeignKeySpec aValue) {
        ForeignKeySpec oldValue = fk;
        fk = aValue;
        changeSupport.firePropertyChange(FK_PROPERTY, oldValue, aValue);
    }

    private static final String READOLNY_JS_DOC = "/**\n"
            + "* Determines if this field is readonly.\n"
            + "*/";

    /**
     * Returns if this field is readonly.
     *
     * @return If true this field is readonly.
     */
    @ScriptFunction(jsDoc = READOLNY_JS_DOC)
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * Sets readonly flag to this field.
     *
     * @param aValue Flag to be set to this field.
     */
    @ScriptFunction
    public void setReadonly(boolean aValue) {
        boolean oldValue = readonly;
        readonly = aValue;
        changeSupport.firePropertyChange(READONLY_PROPERTY, oldValue, aValue);
    }

    /**
     * Tests the equality of this field to another object.
     *
     * @param obj Object to be tested as equal or n ot equal.
     * @return The equality of this field to another object.
     */
    public boolean isEqual(Object obj) {
        if (obj != null && obj instanceof Field) {
            Field rf = (Field) obj;
            String rfDescription = rf.getDescription();
            String rfName = rf.getName();
            String rfOriginalName = rf.getOriginalName();
            String rfTableName = rf.getTableName();
            String rfSchemaName = rf.getSchemaName();
            PrimaryKeySpec lfk = rf.getFk();
            return nullable == rf.isNullable()
                    && signed == rf.isSigned()
                    && pk == rf.isPk()
                    && readonly == rf.isReadonly()
                    && precision == rf.getPrecision()
                    && scale == rf.getScale()
                    && size == rf.getSize()
                    && typeInfo.equals(rf.getTypeInfo())
                    && ((fk == null && lfk == null) || (fk != null && fk.equals(lfk)))
                    && ((description == null && rfDescription == null) || (description != null && description.equals(rfDescription)))
                    && ((name == null && rfName == null) || (name != null && name.equals(rfName)))
                    && ((originalName == null && rfOriginalName == null) || (originalName != null && originalName.equals(rfOriginalName)))
                    && ((tableName == null && rfTableName == null) || (tableName != null && tableName.equals(rfTableName)))
                    && ((schemaName == null && rfSchemaName == null) || (schemaName != null && schemaName.equals(rfSchemaName)));
        }
        return false;
    }
    /*
     @Override
     public int hashCode() {
     int hash = 3;
     hash = 97 * hash + Objects.hashCode(this.name);
     hash = 97 * hash + Objects.hashCode(this.originalName);
     hash = 97 * hash + Objects.hashCode(this.description);
     hash = 97 * hash + Objects.hashCode(this.typeInfo);
     hash = 97 * hash + this.size;
     hash = 97 * hash + this.scale;
     hash = 97 * hash + this.precision;
     hash = 97 * hash + (this.signed ? 1 : 0);
     hash = 97 * hash + (this.nullable ? 1 : 0);
     hash = 97 * hash + (this.readonly ? 1 : 0);
     hash = 97 * hash + (this.pk ? 1 : 0);
     hash = 97 * hash + (this.strong4Insert ? 1 : 0);
     hash = 97 * hash + Objects.hashCode(this.fk);
     hash = 97 * hash + Objects.hashCode(this.tableName);
     hash = 97 * hash + Objects.hashCode(this.schemaName);
     return hash;
     }
     */

    private static final String NAME_JS_DOC = "/**\n"
            + "* The name of the field.\n"
            + "*/";

    /**
     * Returns the name of the field.
     *
     * @return The name of the field.
     */
    @ScriptFunction(jsDoc = NAME_JS_DOC)
    public String getName() {
        return name;
    }

    /**
     * Set the name to this field.
     *
     * @param aValue A name to be set.
     */
    @ScriptFunction
    public void setName(String aValue) {
        String oldValue = name;
        name = aValue;
        changeSupport.firePropertyChange(NAME_PROPERTY, oldValue, aValue);
    }

    private static final String ORIGINAL_NAME_JS_DOC = "/**\n"
            + "* The original name of the field.\n"
            + "* In queries, such as select t1.f1 as f11, t2.f1 as f21 to preserve output fields' names unique,\n"
            + "* but be able to generate right update sql clauses for multiple tables.\n"
            + "*/";

    @ScriptFunction(jsDoc = ORIGINAL_NAME_JS_DOC)
    public String getOriginalName() {
        return originalName;
    }

    @ScriptFunction
    public void setOriginalName(String aValue) {
        String oldValue = originalName;
        originalName = aValue;
        changeSupport.firePropertyChange(ORIGINAL_NAME_PROPERTY, oldValue, originalName);
    }

    private static final String DESCRIPTION_JS_DOC = "/**\n"
            + "* The description of the field.\n"
            + "*/";

    /**
     * Returns description of the field.
     *
     * @return Description of the field.
     */
    @ScriptFunction(jsDoc = DESCRIPTION_JS_DOC)
    public String getDescription() {
        return description;
    }

    /**
     * Set the description to this field.
     *
     * @param aValue A description to be set.
     */
    @ScriptFunction
    public void setDescription(String aValue) {
        String oldValue = description;
        description = aValue;
        changeSupport.firePropertyChange(DESCRIPTION_PROPERTY, oldValue, aValue);
    }

    private static final String TYPE_INFO_JS_DOC = "/**\n"
            + "* The field's type information.\n"
            + "*/";

    /**
     * Returns the field's type information
     *
     * @return The field's type information
     */
    @ScriptFunction(jsDoc = TYPE_INFO_JS_DOC)
    public DataTypeInfo getTypeInfo() {
        return typeInfo;
    }

    /**
     * Sets the field's type description
     *
     * @param aValue The filed's type description
     * @see DataTypeInfo
     */
    @ScriptFunction
    public void setTypeInfo(DataTypeInfo aValue) {
        DataTypeInfo oldValue = typeInfo;
        typeInfo = aValue != null ? aValue.copy() : null;
        changeSupport.firePropertyChange(TYPE_INFO_PROPERTY, oldValue, typeInfo);
    }

    private static final String SCHEMA_NAME_JS_DOC = "/**\n"
            + "* This field schema name.\n"
            + "*/";

    /**
     * Returns the field's schema name.
     *
     * @return The field's schema name.
     */
    @ScriptFunction(jsDoc = SCHEMA_NAME_JS_DOC)
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Sets this field schema name.
     *
     * @param aValue This field schema name.
     */
    @ScriptFunction
    public void setSchemaName(String aValue) {
        String oldValue = schemaName;
        schemaName = aValue;
        changeSupport.firePropertyChange(SCHEMA_NAME_PROPERTY, oldValue, aValue);
    }

    private static final String TABLE_NAME_JS_DOC = "/**\n"
            + "* This field table's name.\n"
            + "*/";

    /**
     * Returns the field's table name.
     *
     * @return The field's table name.
     */
    @ScriptFunction(jsDoc = TABLE_NAME_JS_DOC)
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets table name.
     *
     * @param aValue The table name to be set.
     */
    @ScriptFunction
    public void setTableName(String aValue) {
        String oldValue = tableName;
        tableName = aValue;
        changeSupport.firePropertyChange(TABLE_NAME_PROPERTY, oldValue, aValue);
    }

    private static final String SIZE_JS_DOC = "/**\n"
            + "* The size of the field.\n"
            + "*/";

    /**
     * Returns the field size.
     *
     * @return The field size.
     */
    @ScriptFunction(jsDoc = SIZE_JS_DOC)
    public int getSize() {
        return size;
    }

    /**
     * Sets the field size.
     *
     * @param aValue The field size to be set.
     */
    @ScriptFunction
    public void setSize(int aValue) {
        int oldValue = size;
        size = aValue;
        changeSupport.firePropertyChange(SIZE_PROPERTY, oldValue, aValue);
    }

    private static final String SCALE_JS_DOC = "/**\n"
            + "* The scale of the field.\n"
            + "*/";

    /**
     * Returns the field's scale.
     *
     * @return The field's scale.
     */
    @ScriptFunction(jsDoc = SCALE_JS_DOC)
    public int getScale() {
        return scale;
    }

    /**
     * Sets the field's scale.
     *
     * @param aValue The field's scale to be set.
     */
    @ScriptFunction
    public void setScale(int aValue) {
        int oldValue = scale;
        scale = aValue;
        changeSupport.firePropertyChange(SCALE_PROPERTY, oldValue, aValue);
    }

    private static final String PRECISION_JS_DOC = "/**\n"
            + "* The precision of the field.\n"
            + "*/";

    /**
     * Returns the field's precision.
     *
     * @return The field's precision.
     */
    @ScriptFunction(jsDoc = PRECISION_JS_DOC)
    public int getPrecision() {
        return precision;
    }

    /**
     * Sets the field's precision.
     *
     * @param aValue The field's precision.
     */
    @ScriptFunction
    public void setPrecision(int aValue) {
        int oldValue = precision;
        precision = aValue;
        changeSupport.firePropertyChange(PRECISION_PROPERTY, oldValue, aValue);
    }

    private static final String SIGNED_JS_DOC = "/**\n"
            + "* Determines if the field is signed.\n"
            + "*/";

    /**
     * Returns whether this field is signed.
     *
     * @return Whether this field is signed.
     */
    @ScriptFunction(jsDoc = SIGNED_JS_DOC)
    public boolean isSigned() {
        return signed;
    }

    /**
     * Sets the field's signed state.
     *
     * @param aValue Field's signed flag.
     */
    @ScriptFunction
    public void setSigned(boolean aValue) {
        boolean oldValue = signed;
        signed = aValue;
        changeSupport.firePropertyChange(SIGNED_PROPERTY, oldValue, aValue);
    }

    private static final String NULLABLE_JS_DOC = "/**\n"
            + "* Determines if field is nullable.\n"
            + "*/";

    /**
     * Returns whether this field is nullable.
     *
     * @return Whether this field is nullable.
     */
    @ScriptFunction(jsDoc = NULLABLE_JS_DOC)
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Sets the field's nullable state.
     *
     * @param aValue Field's nullable flag.
     */
    @ScriptFunction
    public void setNullable(boolean aValue) {
        boolean oldValue = nullable;
        nullable = aValue;
        changeSupport.firePropertyChange(NULLABLE_PROPERTY, oldValue, aValue);
    }

    /**
     * Copies this feld's information to another instance.
     *
     * @return Another instance of <code>Field</code> class, initialized with
     * this field information.
     */
    public Field copy() {
        return new Field(this);
    }

    /**
     * Assignes <code>aSourceField</code> information to this <code>Field</code>
     * instance.
     *
     * @param aSourceField <code>Field</code> instance used as a source for
     * assigning.
     */
    public void assignFrom(Field aSourceField) {
        if (aSourceField != null) {
            if (!equalsOrNulls(getName(), aSourceField.getName())) {
                setName(aSourceField.getName());
            }
            if (!equalsOrNulls(getOriginalName(), aSourceField.getOriginalName())) {
                setOriginalName(aSourceField.getOriginalName());
            }
            if (!equalsOrNulls(getDescription(), aSourceField.getDescription())) {
                setDescription(aSourceField.getDescription());
            }
            if (!equalsOrNulls(getTableName(), aSourceField.getTableName())) {
                setTableName(aSourceField.getTableName());
            }
            if (!equalsOrNulls(getSchemaName(), aSourceField.getSchemaName())) {
                setSchemaName(aSourceField.getSchemaName());
            }
            setTypeInfo(aSourceField.getTypeInfo().copy());
            if (getSize() != aSourceField.getSize()) {
                setSize(aSourceField.getSize());
            }
            if (getScale() != aSourceField.getScale()) {
                setScale(aSourceField.getScale());
            }
            if (getPrecision() != aSourceField.getPrecision()) {
                setPrecision(aSourceField.getPrecision());
            }
            if (isSigned() != aSourceField.isSigned()) {
                setSigned(aSourceField.isSigned());
            }
            if (isNullable() != aSourceField.isNullable()) {
                setNullable(aSourceField.isNullable());
            }
            if (isReadonly() != aSourceField.isReadonly()) {
                setReadonly(aSourceField.isReadonly());
            }
            if (isPk() != aSourceField.isPk()) {
                setPk(aSourceField.isPk());
            }
            if (!equalsOrNulls(getFk(), aSourceField.getFk())) {
                setFk((ForeignKeySpec) aSourceField.getFk().copy());
            }
        }
    }

    protected static boolean equalsOrNulls(Object o1, Object o2) {
        return (o1 == null && o2 == null) || (o1 != null && o1.equals(o2)) || (o2 != null && o2.equals(o1));
    }

    /**
     * @inheritDoc
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
        sb.append(", ").append(typeInfo.toString());
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

    @Override
    public JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }
}
