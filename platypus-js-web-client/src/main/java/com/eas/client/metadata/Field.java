package com.eas.client.metadata;

import java.util.Date;

import com.eas.client.IdGenerator;
import com.eas.core.Utils;
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

	protected String name = "";
	protected String tableName;
	protected String description;// May be null
	protected String type;
	protected boolean readonly;
	protected boolean nullable = true;
	protected boolean pk;
	protected ForeignKeySpec fk;

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
	public Field(String aName, String aDescription, String aType) {
		this(aName, aDescription);
		type = aType;
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
            Field other = (Field) obj;
            String rfDescription = other.getDescription();
            String rfName = other.getName();
            String rfTableName = other.getTableName();
            String rfType = other.getType();
            return nullable == other.isNullable()
                    && pk == other.isPk()
                    && readonly == other.isReadonly()
                    && (fk == null ? other.getFk() == null : fk.equals(other.getFk()))
                    && (description == null ? rfDescription == null : description.equals(rfDescription))
                    && (name == null ? rfName == null : name.equals(rfName))
                    && (tableName == null ? rfTableName == null : tableName.equals(rfTableName))
                    && (type == null ? rfType == null : type.equals(rfType));
        }
        return false;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 47 * hash + (this.description != null ? this.description.hashCode() : 0);
		hash = 47 * hash + (this.type != null ? this.type.hashCode() : 0);
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
	public String getType() {
		return type;
	}

	/**
	 * Sets the field's type description
	 * 
	 * @param typeInfo
	 *            The filed's type description
	 * @see DataTypeInfo
	 */
	public void setType(String aValue) {
		type = aValue;
	}

    public Object generateValue() {
        Object value;
        if (type != null) {
            switch (type) {
                case "Number":
                    value = IdGenerator.genId();
                    break;
                case "String":
                    value = String.valueOf(IdGenerator.genId());
                    break;
                case "Date":
                    value = new Date((long)IdGenerator.genId());
                    break;
                case "Boolean":
                    value = false;
                    break;
                default:
                    value = null;
                    break;
            }
        } else {
            value = null;
        }
        return Utils.toJs(value);
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
			setType(aSourceField.getType());
			setNullable(aSourceField.isNullable());
			setReadonly(aSourceField.isReadonly());
			setPk(aSourceField.isPk());
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
		sb.append(", ").append(type);
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
			Object.defineProperty(published, "pk", {
				get : function() {
					return aField.@com.eas.client.metadata.Field::isPk()();
				},
				set : function(aValue) {
					aField.@com.eas.client.metadata.Field::setPk(Z)(!!aValue);
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
			Object.defineProperty(published, "type", {
				get : function() {
					return aField.@com.eas.client.metadata.Field::getType()();
				}
			});
			aField.@com.eas.client.metadata.Field::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		}
		return published;
	}-*/;
}
