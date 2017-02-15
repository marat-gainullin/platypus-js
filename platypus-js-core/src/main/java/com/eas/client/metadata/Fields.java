package com.eas.client.metadata;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.Scripts;
import com.eas.util.CollectionEditingSupport;
import java.beans.PropertyChangeSupport;
import java.util.*;
import jdk.nashorn.api.scripting.JSObject;

/**
 * This class is intended to hold fields information. Supports clone, copy
 * operations. Supports factory method for new fields creation. Supports field
 * search, operations with primary and foreign keys.
 */
public class Fields implements HasPublished {

    public static class OrmDef {

        private final String baseName;// if not null, -> property is a scalar orm expanding
        private final String name;
        private final String oppositeName;
        private final JSObject jsDef;

        public OrmDef(String aName, String aOppositeName, JSObject aJsDef) {
            this(null, aName, aOppositeName, aJsDef);
        }

        public OrmDef(String aBaseName, String aName, String aOppositeName, JSObject aDef) {
            baseName = aBaseName;
            name = aName;
            oppositeName = aOppositeName;
            jsDef = aDef;
        }

        public String getBaseName() {
            return baseName;
        }

        public String getOppositeName() {
            return oppositeName;
        }

        public String getName() {
            return name;
        }

        public JSObject getJsDef() {
            return jsDef;
        }

    }

    private static final String DEFAULT_PARAM_NAME_PREFIX = "Field";
    protected String tableDescription;
    protected List<Field> fields = new ArrayList<>();
    // Map of field name to it's index (0-based)
    protected Map<String, Integer> fieldsHash;
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    protected CollectionEditingSupport<Fields, Field> collectionSupport = new CollectionEditingSupport<>(this);
    protected JSObject instanceConstructor;
    protected Map<String, OrmDef> ormScalarDefinitions = new HashMap<>();
    protected Map<String, OrmDef> ormCollectionsDefinitions = new HashMap<>();
    protected Map<String, Set<OrmDef>> ormScalarExpandings = new HashMap<>();
    protected JSObject published;

    /**
     * The default constructor.
     */
    public Fields() {
        super();
    }

    /**
     * The copy constructor.
     *
     * @param aSource Instance of <code>Fields</code> class to be used as the
     * information source.
     */
    public Fields(Fields aSource) {
        this();
        if (aSource != null) {
            for (int i = 0; i < aSource.getFieldsCount(); i++) {
                if (aSource.get(i + 1) != null) {
                    fields.add(i, aSource.get(i + 1).copy());
                } else {
                    fields.add(i, null);
                }
            }
            assert fields.size() == aSource.getFieldsCount();
            setTableDescription(aSource.getTableDescription());
        }
    }

    public JSObject getInstanceConstructor() {
        return instanceConstructor;
    }

    public void setInstanceConstructor(JSObject aValue) {
        instanceConstructor = aValue;
    }

    public void putOrmScalarDefinition(String aName, OrmDef aDefinition) {
        if (aName != null && !aName.isEmpty() && aDefinition != null) {
            if (!ormScalarDefinitions.containsKey(aName)) {
                ormScalarDefinitions.put(aName, aDefinition);
                Set<OrmDef> expandings = ormScalarExpandings.get(aDefinition.getBaseName());
                if (expandings == null) {
                    expandings = new HashSet<>();
                    ormScalarExpandings.put(aDefinition.getBaseName(), expandings);
                }
                expandings.add(aDefinition);
            }
        }
    }

    public Map<String, OrmDef> getOrmScalarDefinitions() {
        return Collections.unmodifiableMap(ormScalarDefinitions);
    }

    public Map<String, Set<OrmDef>> getOrmScalarExpandings() {
        return Collections.unmodifiableMap(ormScalarExpandings);
    }

    public void putOrmCollectionDefinition(String aName, OrmDef aDefinition) {
        if (aName != null && !aName.isEmpty() && aDefinition != null) {
            if (!ormCollectionsDefinitions.containsKey(aName)) {
                ormCollectionsDefinitions.put(aName, aDefinition);
            }
        }
    }

    public Map<String, OrmDef> getOrmCollectionsDefinitions() {
        return Collections.unmodifiableMap(ormCollectionsDefinitions);
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public CollectionEditingSupport<Fields, Field> getCollectionSupport() {
        return collectionSupport;
    }

    /**
     * Rebuild hashmap with fields by name mapping.
     */
    protected void validateFieldsHash() {
        if (fieldsHash == null) {
            fieldsHash = new HashMap<>();
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

    public boolean isEqual(Fields other) {
        if (other == null) {
            return false;
        }
        if (tableDescription == null ? other.getTableDescription() != null : !tableDescription.equals(other.getTableDescription())) {
            return false;
        }
        if (fields.size() != other.getFieldsCount()) {
            return false;
        }
        for (int i = 0; i < fields.size(); i++) {
            if (!fields.get(i).isEqual(other.get(i + 1))) {
                return false;
            }
        }
        return true;
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
     * @param aValue Table description value.
     */
    public void setTableDescription(String aValue) {
        String oldValue = tableDescription;
        tableDescription = aValue;
        changeSupport.firePropertyChange("tableDescription", oldValue, aValue);
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
     * @param aPrefix A string value new name will start with.
     * @return New(unique) field name for this fields set.
     * @see #getDefaultNamePrefix()
     * @see #generateNewName()
     * @see #isNameAlreadyPresent(String aName, Field aField2Skip)
     */
    public String generateNewName(String aPrefix) {
        if (aPrefix == null || aPrefix.isEmpty()) {
            aPrefix = getDefaultNamePrefix();
        }
        int counter = 0;
        String currentName = aPrefix;
        while (contains(currentName)) {
            currentName += ++counter;
        }
        return currentName;
    }

    /**
     * Tests whether the name already present in this fields set skipping
     * particular field instance.
     *
     * @param aName Name to test.
     * @param aField2Skip <code>Field</code> to skip.
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
     * Yet another method to retrive fields count. Intended for collections like
     * syntax, in script for example.
     *
     * @return Fields count.
     * @see #getFieldsCount()
     */
    public int getLength() {
        return getFieldsCount();
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
        List<Field> pks = new ArrayList<>();
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
        List<Field> fks = new ArrayList<>();
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
        List<Integer> pksIndicies = new ArrayList<>();
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
     * @param aField A <code>Field</code> to add.
     * @return <code>True</code> if adding have succeded.
     */
    public boolean add(Field aField) {
        if (aField != null && fields != null) {
            boolean res = fields.add(aField);
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
     * @param index An index at which aField is to be added, starting on 1.
     * @param aField A <code>Field</code> to add.
     */
    public void add(int index, Field aField) {
        if (aField != null && fields != null) {
            fields.add(index - 1, aField);
            invalidateFieldsHash();
            collectionSupport.fireElementAdded(aField);
        }
    }

    /**
     * Removes a particular <code>Field</code> instance from this fields set.
     *
     * @param aField <code>Field</code> instance to remove.
     * @return <code>True</code> if removing succeded.
     */
    public boolean remove(Field aField) {
        int oldSize = fields.size();
        fields.remove(aField);
        invalidateFieldsHash();
        collectionSupport.fireElementRemoved(aField);
        return oldSize > fields.size();
    }

    /**
     * Changes the order of fields.
     *
     * @param newOrder New order for the fields, from 1 to fields count.
     */
    public void reorder(int[] newOrder) {
        if (newOrder.length != fields.size()) {
            throw new IllegalArgumentException("Order argument size must be equal to fields size."); //NOI18N
        }
        //Check argument's values correctness
        Set<Integer> orderNumbers = new HashSet<>();
        for (int i = 0; i < newOrder.length; i++) {
            orderNumbers.add(newOrder[i]);
        }
        for (int i = 1; i <= newOrder.length; i++) {
            if (!orderNumbers.remove(i)) {
                throw new IllegalArgumentException("Order argument is not correct - some values are missing."); //NOI18N
            }
        }
        if (!orderNumbers.isEmpty()) {
            throw new IllegalArgumentException("Order argument is not correct - some values are out of range."); //NOI18N
        }

        List<Field> oldFields = new ArrayList<>();
        oldFields.addAll(fields);
        Field[] farr = new Field[newOrder.length];
        for (int i = 0; i < oldFields.size(); i++) {
            farr[newOrder[i] - 1] = oldFields.get(i);
        }
        fields = new ArrayList<>();
        fields.addAll(Arrays.asList(farr));
        invalidateFieldsHash();
        collectionSupport.fireReodered();

    }

    /**
     * Returns <code>Field</code> instance at the specified index. Index is 1
     * based.
     *
     * @param index Index of <code>Field</code> instance you are interested in.
     * @return <code>Field</code> instance at the specified index. If wrong
     * index specified, than null is returned.
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
     * @param aFieldName Field name of <code>Field</code> instance you are
     * interested in.
     * @return <code>Field</code> instance with the specified name. If absent
     * name is specified, than null is returned.
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
     * @param aFieldName Field name of <code>Field</code> instance you are
     * interested in.
     * @return 1 based ordinal position of <code>Field</code> instance with the
     * specified name. If absent name is specified, than 0 is returned.
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
     * @param aFieldName Name to test.
     * @return True if fields contains a field with such name.
     */
    public boolean contains(String aFieldName) {
        return find(aFieldName) > 0;
    }

    /**
     * Copies this <code>Fields</code> instance, creating a new one.
     *
     * @return New instance of <code>Fields</code>.
     * @see #copy()
     */
    @Override
    public Fields clone() {
        return copy();
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
