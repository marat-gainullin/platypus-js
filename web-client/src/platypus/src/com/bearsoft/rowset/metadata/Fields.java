/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.bearsoft.rowset.metadata;

import com.bearsoft.rowset.utils.CollectionEditingSupport;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.JavaScriptObject;

import java.util.*;

/**
 * This class is intended to hold rowset's fields information. Supports clone,
 * copy operations. Supports factory method for new fields creation. Supports
 * field search, operations with primary and foreign keys.
 */
public class Fields {

	private static final String DEFAULT_PARAM_NAME_PREFIX = "Field";
	protected String tableDescription = null;
	protected List<Field> fields = new ArrayList();
	protected Entity owner;
	// Map of field name to it's index (0-based)
	protected Map<String, Integer> fieldsHash;
	protected CollectionEditingSupport<Fields, Field> collectionSupport = new CollectionEditingSupport(this);

	/**
	 * The default constructor.
	 */
	public Fields() {
		super();
	}

	/**
	 * The copy constructor.
	 * 
	 * @param aSource
	 *            Instance of <code>Fields</code> class to be used as the
	 *            information source.
	 */
	public Fields(Fields aSource) {
		this();
		if (aSource != null) {
			for (int i = 0; i < aSource.getFieldsCount(); i++) {
				if (aSource.get(i + 1) != null) {
					Field copied = aSource.get(i + 1).copy();
					fields.add(i, copied);
					copied.setOwner(this);
				} else {
					fields.add(i, null);
				}
			}
			assert fields.size() == aSource.getFieldsCount();
			String sDesc = aSource.getTableDescription();
			if (sDesc != null) {
				setTableDescription(new String(sDesc.toCharArray()));
			}
		}
	}

	public Entity getOwner() {
		return owner;
	}

	public void setOwner(Entity aOwner) {
		owner = aOwner;
	}

	public CollectionEditingSupport<Fields, Field> getCollectionSupport() {
		return collectionSupport;
	}

	/**
	 * Rebuild hashmap with fields by name mapping.
	 */
	protected void validateFieldsHash() {
		if (fieldsHash == null) {
			fieldsHash = new HashMap();
			for (int i = 0; i < fields.size(); i++) {
				String fieldName = fields.get(i).getName();
				if (fieldName != null) {
					fieldsHash.put(fieldName.toLowerCase(), i);
				}
			}
		}
	}

	/**
	 * Invlidates inner fields hash. Fields are hashed by field name. It's not
	 * recomended to call it directly. It's intended for internal use.
	 */
	public void invalidateFieldsHash() {
		fieldsHash = null;
	}

	/**
	 * Returns if the <code>Fields</code> contains no fields.
	 * 
	 * @return <code>True</code> if this instance contains no fields.
	 */
	public boolean isEmpty() {
		return fields.isEmpty();
	}

	/**
	 * Returns default name prefix for generating new field names.
	 * 
	 * @return New (unique) name for this instance of <code>Fields</code>.
	 * @see #generateNewName()
	 * @see #generateNewName(String)
	 */
	public String getDefaultNamePrefix() {
		return DEFAULT_PARAM_NAME_PREFIX;
	}

	/**
	 * Returns table description if this <code>Fields</code> instance represents
	 * database table fields set.
	 * 
	 * @return Table description.
	 */
	public String getTableDescription() {
		return tableDescription;
	}

	/**
	 * Sets the table description if this <code>Fields</code> instance
	 * represents database table fields set.
	 * 
	 * @param aValue
	 *            Table description value.
	 */
	public void setTableDescription(String aValue) {
		tableDescription = aValue;
	}

	/**
	 * Generates and returns new(unique) field name for this fields set.
	 * 
	 * @return New(unique) field name for this fields set.
	 * @see #getDefaultNamePrefix()
	 * @see #generateNewName(String)
	 * @see #isNameAlreadyPresent(String aName, Field aField2Skip)
	 */
	public String generateNewName() {
		return generateNewName(null);
	}

	/**
	 * Generates and returns new(unique) field name for this fields set. New
	 * name will start with <code>aPrefix</code> value.
	 * 
	 * @param aPrefix
	 *            A string value new name will start with.
	 * @return New(unique) field name for this fields set.
	 * @see #getDefaultNamePrefix()
	 * @see #generateNewName()
	 * @see #isNameAlreadyPresent(String aName, Field aField2Skip)
	 */
	public String generateNewName(String aPrefix) {
		if (aPrefix == null || aPrefix.isEmpty()) {
			aPrefix = getDefaultNamePrefix();
		}
		int paramNumber = 0;
		for (int i = 0; i < getFieldsCount(); i++) {
			Field param = fields.get(i);
			String paramName = param.getName();
			if (paramName != null && paramName.toLowerCase().startsWith(aPrefix.toLowerCase())) {
				int existingNumber = 1;
				String maybeNumber = paramName.substring(aPrefix.length());
				if (maybeNumber != null && !maybeNumber.isEmpty()) {
					try {
						existingNumber = Integer.valueOf(maybeNumber);
						if (existingNumber > paramNumber) {
							paramNumber = existingNumber;
						}
					} catch (NumberFormatException fe) {
						// no op
					}
				}
			}
		}
		return aPrefix + String.valueOf(paramNumber + 1);
	}

	/**
	 * Tests whether the name already present in this fields set skipping
	 * particular field instance.
	 * 
	 * @param aName
	 *            Name to test.
	 * @param aField2Skip
	 *            <code>Field</code> to skip.
	 * @return <code>True</code> if the aspecified name already present.
	 * @see #getDefaultNamePrefix()
	 * @see #generateNewName()
	 * @see #generateNewName(String)
	 */
	public boolean isNameAlreadyPresent(String aName, Field aField2Skip) {
		if (aName == null) {
			aName = "";
		}
		for (int i = 0; i < getFieldsCount(); i++) {
			Field field = fields.get(i);
			if (field == aField2Skip) {
				continue;
			}
			String paramName = field.getName();
			if (paramName == null) {
				paramName = "";
			}
			if (aName.toLowerCase().equals(paramName.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates new field
	 * 
	 * @return New <code>Field</code> instance.
	 */
	public Field createNewField() {
		return createNewField(null);
	}

	/**
	 * Creates new field with the specified name.
	 * 
	 * @return New <code>Field</code> instance.
	 * @see #createNewField()
	 * @see #getDefaultNamePrefix()
	 * @see #generateNewName()
	 * @see #generateNewName(String)
	 * @see #isNameAlreadyPresent(String aName, Field aField2Skip)
	 */
	public Field createNewField(String aName) {
		if (aName == null || aName.isEmpty()) {
			aName = generateNewName();
		}
		Field lfield = new Field(aName, null);
		lfield.setTypeInfo(DataTypeInfo.VARCHAR);
		return lfield;
	}

	/**
	 * Returns fields count, contained in this fields set.
	 * 
	 * @return Fields count, contained in this fields set.
	 */
	public int getFieldsCount() {
		if (fields != null) {
			return fields.size();
		}
		return 0;
	}

	/**
	 * Clears all fields from this set.
	 */
	public void clear() {
		if (fields != null) {
			Collection<Field> oldCollection = fields.subList(0, fields.size());
			fields.clear();
			invalidateFieldsHash();
			collectionSupport.fireElementsRemoved(oldCollection);
			collectionSupport.fireCleared();
		}
	}

	/**
	 * Returns fields, representing primary keys.
	 * 
	 * @return A vector comprised of <code>Field</code> instances.
	 */
	public List<Field> getPrimaryKeys() {
		List<Field> pks = new ArrayList();
		for (int i = 0; i < getFieldsCount(); i++) {
			Field lfield = fields.get(i);
			if (lfield != null && lfield.isPk()) {
				pks.add(lfield);
			}
		}
		return pks;
	}

	/**
	 * Returns fields, representing foreign keys.
	 * 
	 * @return A vector comprised of <code>Field</code> instances.
	 */
	public List<Field> getForeinKeys() {
		List<Field> fks = new ArrayList();
		for (int i = 0; i < getFieldsCount(); i++) {
			Field lfield = fields.get(i);
			if (lfield != null && lfield.isFk()) {
				fks.add(lfield);
			}
		}
		return fks;
	}

	/**
	 * Returns indicies of primary-key fields. Values of indicies are 1-based;
	 * 
	 * @return A vector comprised of <code>Field</code> indicies.
	 */
	public List<Integer> getPrimaryKeysIndicies() {
		List<Integer> pksIndicies = new ArrayList();
		for (int i = 0; i < getFieldsCount(); i++) {
			Field lfield = fields.get(i);
			if (lfield != null && lfield.isPk()) {
				pksIndicies.add(i + 1);
			}
		}
		return pksIndicies;
	}

	/**
	 * Adds particular <code>Field</code> instance to this fields set.
	 * 
	 * @param aField
	 *            A <code>Field</code> to add.
	 * @return <code>True</code> if adding have succeded.
	 */
	public boolean add(Field aField) {
		if (aField != null && fields != null) {
			boolean res = fields.add(aField);
			aField.setOwner(this);
			invalidateFieldsHash();
			collectionSupport.fireElementAdded(aField);
			return res;
		}
		return false;
	}

	/**
	 * Adds particular <code>Field</code> instance to this fields set at the
	 * specified position.
	 * 
	 * @param index
	 *            An index at which aField is to be added.
	 * @param aField
	 *            A <code>Field</code> to add.
	 */
	public void add(int index, Field aField) {
		if (aField != null && fields != null) {
			fields.add(index - 1, aField);
			aField.setOwner(this);
			invalidateFieldsHash();
			collectionSupport.fireElementAdded(aField);
		}
	}

	/**
	 * Removes a particular <code>Field</code> instance from this fields set.
	 * 
	 * @param aField
	 *            <code>Field</code> instance to remove.
	 * @return <code>True</code> if removing succeded.
	 */
	public boolean remove(Field aField) {
		aField.setOwner(null);
		int oldSize = fields.size();
		fields.remove(aField);
		invalidateFieldsHash();
		collectionSupport.fireElementRemoved(aField);
		return oldSize > fields.size();
	}

	/**
	 * Returns <code>Field</code> instance at the specified index. Index is 1
	 * based.
	 * 
	 * @param index
	 *            Index of <code>Field</code> instance you are interested in.
	 * @return <code>Field</code> instance at the specified index. If wrong
	 *         index specified, than null is returned.
	 */
	public Field get(int index) {
		if (fields != null && index > 0 && index <= fields.size()) {
			return fields.get(index - 1);
		}
		return null;
	}

	/**
	 * Returns <code>Field</code> instance with the specified name.
	 * 
	 * @param aFieldName
	 *            Field name of <code>Field</code> instance you are interested
	 *            in.
	 * @return <code>Field</code> instance with the specified name. If absent
	 *         name is specified, than null is returned.
	 */
	public Field get(String aFieldName) {
		if (fields != null && aFieldName != null) {
			return get(find(aFieldName));
		}
		return null;
	}

	/**
	 * Returns ordinal position of <code>Field</code> instance with the
	 * specified name.
	 * 
	 * @param aFieldName
	 *            Field name of <code>Field</code> instance you are interested
	 *            in.
	 * @return 1 based ordinal position of <code>Field</code> instance with the
	 *         specified name. If absent name is specified, than 0 is returned.
	 */
	public int find(String aFieldName) {
		if (fields != null && aFieldName != null) {
			validateFieldsHash();
			assert fieldsHash != null;
			Integer fIndex = fieldsHash.get(aFieldName.toLowerCase());
			if (fIndex != null && fIndex >= 0 && fIndex < fields.size()) {
				return fIndex + 1;
			}
		}
		return 0;
	}

	/**
	 * Test if this <code>Fields</code> object contains a field with the
	 * <code>aFieldName</code> name.
	 * 
	 * @param aFieldName
	 *            Name to test.
	 * @return True if fields contains a field with such name.
	 */
	public boolean contains(String aFieldName) {
		return find(aFieldName) > 0;
	}

	/**
	 * Copies this <code>Fields</code> instance, creating a new one.
	 * 
	 * @return New instance of <code>Fields</code>.
	 * @see #clone()
	 */
	public Fields copy() {
		return new Fields(this);
	}

	/**
	 * Returns the fields vector as an abstract collection.
	 * 
	 * @return Fields vector as an abstract collection.
	 */
	public Collection<Field> toCollection() {
		return fields;
	}

	protected JavaScriptObject jsPublished;

	public void setPublished(JavaScriptObject aPublished) {
		jsPublished = aPublished;
	}

	public JavaScriptObject getPublished() {
		return jsPublished;
	}
}
