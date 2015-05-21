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

import com.google.gwt.core.client.JavaScriptObject;

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
public class Field {

	public static int UNDEFINED_FILED_INDEX = -1;
	// Our user-supplied information
	protected String name = "";
	protected String description;// Such data is read from db, and so may
										// be null
    protected DataTypeInfo typeInfo = DataTypeInfo.JAVA_OBJECT.copy();
	protected int size;
	protected int scale;
	protected int precision;
	protected boolean signed = true;
	protected boolean nullable = true;
	protected boolean readonly;
	protected boolean pk;
	// Data is always inserted
	protected boolean strong4Insert;
	protected ForeignKeySpec fk;
	protected String tableName;
	protected String schemaName;
	protected Fields owner;

	/**
	 * The default constructor.
	 */
	public Field() {
		super();
	}

	/**
	 * Constructor with name.
	 * 
	 * @param aName
	 *            Name of the created field.
	 */
	public Field(String aName) {
		this();
		name = aName;
	}

	/**
	 * Constructor with name and description.
	 * 
	 * @param aName
	 *            Name of the created field.
	 * @param aDescription
	 *            Description of the created field.
	 */
	public Field(String aName, String aDescription) {
		this(aName);
		description = aDescription;
	}

	/**
	 * Constructor with name, description and typeInfo.
	 * 
	 * @param aName
	 *            Name of the created field.
	 * @param aDescription
	 *            Description of the created field.
	 * @param aTypeInfo
	 *            Type info of the created field.
	 * @see DataTypeInfo
	 */
	public Field(String aName, String aDescription, DataTypeInfo aTypeInfo) {
		this(aName, aDescription);
		typeInfo = aTypeInfo;
	}

	public Field(String aName, String aDescription, int aSize, DataTypeInfo aTypeInfo) {
		this(aName, aDescription);
		typeInfo = aTypeInfo;
		size = aSize;
	}

	/**
	 * Copy constructor of <code>Field</code> class.
	 * 
	 * @param aSourceField
	 *            Source of created field.
	 */
	public Field(Field aSourceField) {
		super();
		assignFrom(aSourceField);
	}

	public Fields getOwner() {
		return owner;
	}

	public void setOwner(Fields aOwner) {
		owner = aOwner;
	}

	/**
	 * Returns if this field is foreign key to another table or it is
	 * self-reference key.
	 * 
	 * @return If this field is foreign key to another table or it is
	 *         self-reference key.
	 */
	public boolean isFk() {
		return fk != null;
	}

	/**
	 * Returns if this field is primary key.
	 * 
	 * @return If this field is primary key.
	 */
	public boolean isPk() {
		return pk;
	}

	/**
	 * Sets indicating primary key state of this field.
	 * 
	 * @param aValue
	 *            Flag, indicating primary key state of this field.
	 */
	public void setPk(boolean aValue) {
		pk = aValue;
	}

	/**
	 * Indicates that this field will be allways present in insert statements.
	 * 
	 * @return Whether this field is allways present in insert statements.
	 */
	public boolean isStrong4Insert() {
		return strong4Insert;
	}

	public void setStrong4Insert(boolean aValue) {
		strong4Insert = aValue;
	}

	/**
	 * Returns foreign key specification of this field if it references to some
	 * table.
	 * 
	 * @return Foreign key specification of this field if it references to some
	 *         table.
	 */
	public ForeignKeySpec getFk() {
		return fk;
	}

	/**
	 * Sets foreign key specification to this field, making it the reference to
	 * some table.
	 * 
	 * @param fk
	 *            Foreign key specification to be set to this field.
	 */
	public void setFk(ForeignKeySpec aValue) {
		fk = aValue;
	}

	/**
	 * Returns if this field is readonly.
	 * 
	 * @return If this field is readonly.
	 */
	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * Sets readonly flag to this field.
	 * 
	 * @param readonly
	 *            Flag to be set to this field.
	 */
	public void setReadonly(boolean aValue) {
		readonly = aValue;
	}

	/**
	 * Tests the equality of this field to another object.
	 * 
	 * @param obj
	 *            Object to be tested as equal or n ot equal.
	 * @return The equality of this field to another object.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Field) {
			Field rf = (Field) obj;
			String rfDescription = rf.getDescription();
			String rfName = rf.getName();
			String rfTableName = rf.getTableName();
			String rfSchemaName = rf.getSchemaName();
			PrimaryKeySpec lfk = rf.getFk();
			return nullable == rf.isNullable() && signed == rf.isSigned() && pk == rf.isPk() && readonly == rf.isReadonly() && strong4Insert == rf.isStrong4Insert() && precision == rf.getPrecision()
			        && scale == rf.getScale() && size == rf.getSize() && typeInfo.equals(rf.getTypeInfo()) && ((fk == null && lfk == null) || (fk != null && fk.equals(lfk)))
			        && ((description == null && rfDescription == null) || (description != null && description.equals(rfDescription)))
			        && ((name == null && rfName == null) || (name != null && name.equals(rfName)))
			        && ((tableName == null && rfTableName == null) || (tableName != null && tableName.equals(rfTableName)))
			        && ((schemaName == null && rfSchemaName == null) || (schemaName != null && schemaName.equals(rfSchemaName)));
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 47 * hash + (this.description != null ? this.description.hashCode() : 0);
		hash = 47 * hash + this.typeInfo.hashCode();
		hash = 47 * hash + this.size;
		hash = 47 * hash + this.scale;
		hash = 47 * hash + this.precision;
		hash = 47 * hash + (this.signed ? 1 : 0);
		hash = 47 * hash + (this.nullable ? 1 : 0);
		hash = 47 * hash + (this.readonly ? 1 : 0);
		hash = 47 * hash + (this.tableName != null ? this.tableName.hashCode() : 0);
		return hash;
	}

	/**
	 * Returns the name of the field.
	 * 
	 * @return The name of the field.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name to this field.
	 * 
	 * @param aValue
	 *            A name to be set.
	 */
	public void setName(String aValue) {
		name = aValue;
	}

	/**
	 * Returns description of the field.
	 * 
	 * @return Description of the field.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description to this field.
	 * 
	 * @param aValue
	 *            A description to be set.
	 */
	public void setDescription(String aValue) {
		description = aValue;
	}

	/**
	 * Returns the field's type description
	 * 
	 * @return The field's type description
	 */
	public DataTypeInfo getTypeInfo() {
		return typeInfo;
	}

	/**
	 * Sets the field's type description
	 * 
	 * @param typeInfo
	 *            The filed's type description
	 * @see DataTypeInfo
	 */
	public void setTypeInfo(DataTypeInfo aValue) {
		typeInfo = aValue != null ? aValue.copy() : null;
	}

	/**
	 * Sets this field schema name.
	 * 
	 * @param aValue
	 *            This field schema name.
	 */
	public void setSchemaName(String aValue) {
		schemaName = aValue;
	}

	/**
	 * Sets table name.
	 * 
	 * @param aValue
	 *            The table name to be set.
	 */
	public void setTableName(String aValue) {
		tableName = aValue;
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
	 * @param aValue
	 *            The field size to be set.
	 */
	public void setSize(int aValue) {
		size = aValue;
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
	 * @param aValue
	 *            The field's scale to be set.
	 */
	public void setScale(int aValue) {
		scale = aValue;
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
	 * @param aValue
	 *            The field's precision.
	 */
	public void setPrecision(int aValue) {
		precision = aValue;
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
	 * @param signed
	 *            Field's signed flag.
	 */
	public void setSigned(boolean aValue) {
		signed = aValue;
	}

	/**
	 * Returns whether this field is nullable.
	 * 
	 * @return Whether this field is nullable.
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * Sets the field's nullable state.
	 * 
	 * @param nullable
	 *            Field's nullable flag.
	 */
	public void setNullable(boolean aValue) {
		nullable = aValue;
	}

	/**
	 * Returns the field's table name.
	 * 
	 * @return The field's table name.
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Returns the field's schema name.
	 * 
	 * @return The field's schema name.
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * Copies this feld's information to another instance.
	 * 
	 * @return Another instance of <code>Field</code> class, initialized with
	 *         this field information.
	 */
	public Field copy() {
		return new Field(this);
	}

	/**
	 * Assignes <code>aSourceField</code> information to this <code>Field</code>
	 * instance.
	 * 
	 * @param aSourceField
	 *            <code>Field</code> instance used as a source for assigning.
	 */
	public void assignFrom(Field aSourceField) {
		if (aSourceField != null) {
			String lSourceString = aSourceField.getName();
			if (lSourceString != null) {
				setName(new String(lSourceString.toCharArray()));
			} else {
				setName(null);
			}
			lSourceString = aSourceField.getDescription();
			if (lSourceString != null) {
				setDescription(new String(lSourceString.toCharArray()));
			} else {
				setDescription(null);
			}
			lSourceString = aSourceField.getTableName();
			if (lSourceString != null) {
				setTableName(new String(lSourceString.toCharArray()));
			} else {
				setTableName(null);
			}
			lSourceString = aSourceField.getSchemaName();
			if (lSourceString != null) {
				setSchemaName(new String(lSourceString.toCharArray()));
			} else {
				setSchemaName(null);
			}
			setTypeInfo(aSourceField.getTypeInfo().copy());
			setSize(aSourceField.getSize());
			setScale(aSourceField.getScale());
			setPrecision(aSourceField.getPrecision());
			setSigned(aSourceField.isSigned());
			setNullable(aSourceField.isNullable());
			setReadonly(aSourceField.isReadonly());
			setPk(aSourceField.isPk());
			setStrong4Insert(aSourceField.isStrong4Insert());
			ForeignKeySpec lfk = aSourceField.getFk();
			if (lfk != null) {
				setFk((ForeignKeySpec) lfk.copy());
			} else {
				setFk(null);
			}
		}
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
		sb.append(name);
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
		if (strong4Insert) {
			sb.append(", strong4Insert");
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

	protected JavaScriptObject jsPublished;

	public void setPublished(JavaScriptObject aPublished) {
		jsPublished = aPublished;
	}

	public JavaScriptObject getPublished() {
		return jsPublished;
	}
	
	public static native JavaScriptObject publishFacade(Field aField) throws Exception/*-{
		var published = aField.@com.eas.client.metadata.Field::getPublished()();
		if (published == null) {
			published = {
				unwrap : function() {
					return aField;
				}
			};
			Object.defineProperty(published, "name", {
				get : function() {
					return aField.@com.eas.client.metadata.Field::getName()();
				}
			});
			Object.defineProperty(published, "description", {
				get : function() {
					return aField.@com.eas.client.metadata.Field::getDescription()();
				}
			});
			Object.defineProperty(published, "size", {
				get : function() {
					return aField.@com.eas.client.metadata.Field::getSize()();
				}
			});
			Object.defineProperty(published, "pk", {
				get : function() {
					return aField.@com.eas.client.metadata.Field::isPk()();
				},
				set : function(aValue) {
					aField.@com.eas.client.metadata.Field::setPk(Z)(!!aValue);
				}
			});
			Object.defineProperty(published, "strong4Insert", {
				get : function() {
					return aField.@com.eas.client.metadata.Field::isStrong4Insert()();
				},
				set : function(aValue) {
					aField.@com.eas.client.metadata.Field::setStrong4Insert(Z)(!!aValue);
				}
			});
			Object.defineProperty(published, "nullable", {
				get : function() {
					return aField.@com.eas.client.metadata.Field::isNullable()();
				}
			});
			Object.defineProperty(published, "readonly", {
				get : function() {
					return aField.@com.eas.client.metadata.Field::isReadonly()();
				}
			});
			aField.@com.eas.client.metadata.Field::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		}
		return published;
	}-*/;
}
