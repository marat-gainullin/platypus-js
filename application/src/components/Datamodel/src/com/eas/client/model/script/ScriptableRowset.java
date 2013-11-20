/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.script;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.filters.Filter;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.locators.RowWrap;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.ordering.HashOrderer.TaggedList;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import com.eas.client.AppClient;
import com.eas.client.DbClient;
import com.eas.client.events.ScriptSourcedEvent;
import com.eas.client.model.RowsetMissingException;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.queries.SqlQuery;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.*;

/**
 *
 * @author mg
 */
public class ScriptableRowset<E extends ApplicationEntity<?, ?, E>> {

    public static final String BAD_FIELD_NAME_MSG = "Bad field name %s";
    public static final String BAD_FIND_AGRUMENTS_MSG = "Bad find agruments";
    public static final String BAD_FIND_ARGUMENT_MSG = "Argument at index %d must be a rowset's field.";
    public static final String BAD_PRIMARY_KEYS_MSG = "Bad primary keys detected. Required one and only one primary key field, but %d found.";
    public static final String CANT_CONVERT_TO_MSG = "Can't convert to %s, substituting with null.";
    protected E entity;
    public static Method getValueScriptableFieldMethod = null;
    public static Method setValueScriptableFieldMethod = null;
    protected Map<String, ScriptableRowset<E>.ScriptableField> scriptableFields = null;
    protected Map<List<Integer>, Locator> userLocators = new HashMap<>();
    protected Scriptable tag;

    static {
        checkScriptableFieldMethods();
    }

    protected static void checkScriptableFieldMethods() {
        if (getValueScriptableFieldMethod == null) {
            try {
                getValueScriptableFieldMethod = ScriptableRowset.ScriptableField.class.getMethod("getValue", new Class<?>[]{
                    Scriptable.class
                });
            } catch (NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(ScriptableRowset.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (setValueScriptableFieldMethod == null) {
            try {
                setValueScriptableFieldMethod = ScriptableRowset.ScriptableField.class.getMethod("setValue", new Class<?>[]{
                    Scriptable.class, Object.class
                });
            } catch (NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(ScriptableRowset.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Locator checkUserLocator(List<Integer> constraints, Rowset rs) throws IllegalStateException {
        Locator loc = userLocators.get(constraints);
        if (loc == null) {
            loc = rs.createLocator();
            loc.beginConstrainting();
            try {
                for (int colIdx : constraints) {
                    loc.addConstraint(colIdx);
                }
            } finally {
                loc.endConstrainting();
            }
            userLocators.put(constraints, loc);
        }
        return loc;
    }

    public class ScriptableFilter {

        protected Filter hf;
        protected boolean applied = false;

        ScriptableFilter(Filter aHf) {
            super();
            hf = aHf;
        }

        public void apply(Object... values) throws Exception {
            assert hf != null;
            checkRowset();
            entity.setUserFiltering(true);
            Fields fields = getRowset().getFields();
            Converter converter = getRowset().getConverter();
            Object[] converted;
            if (values instanceof Object[]) {
                converted = new Object[values.length];
                for (int i = 0; i < values.length; i++) {
                    converted[i] = ScriptUtils.js2Java(values[i]);
                    converted[i] = converter.convert2RowsetCompatible(converted[i], fields.get(hf.getFields().get(i)).getTypeInfo());
                }
            } else {
                converted = new Object[]{values};
                converted[0] = ScriptUtils.js2Java(converted[0]);
                converted[0] = converter.convert2RowsetCompatible(converted[0], fields.get(hf.getFields().get(0)).getTypeInfo());
            }
            hf.filterRowset(converted);
            applied = true;
        }

        public boolean isApplied() throws RowsetMissingException {
            return applied;
        }

        public void cancel() throws Exception {
            cancelFilter();
            applied = false;
        }
    }

    public class ScriptableLocator {

        protected Locator loc;

        ScriptableLocator(Locator aLocator) {
            super();
            loc = aLocator;
            assert loc != null;
        }

        public boolean first() throws RowsetException {
            return loc.first();
        }

        public boolean last() throws RowsetException {
            return loc.last();
        }

        public boolean next() throws RowsetException {
            return loc.next();
        }

        public boolean prev() throws RowsetException {
            return loc.previous();
        }

        public boolean isBeforeFirst() {
            return loc.isBeforeFirst();
        }

        public boolean isAfterLast() {
            return loc.isAfterLast();
        }

        public boolean find(Object... values) throws Exception {
            // Converting work will be performed in locator's find(Object...) method
            //Fields fields = getRowset().getFields();
            //Converter converter = getRowset().getConverter();
            Object[] converted;
            if (values instanceof Object[]) {
                converted = new Object[values.length];
                for (int i = 0; i < values.length; i++) {
                    converted[i] = ScriptUtils.js2Java(values[i]);
                    // Converting work will be performed in locator's find(Object...) method
                    //converted[i] = converter.convert2RowsetCompatible(converted[i], fields.get(loc.getFields().get(i)).getTypeInfo());
                }
            } else {
                converted = new Object[]{values};
                converted[0] = ScriptUtils.js2Java(converted[0]);
                // Converting work will be performed in locator's find(Object...) method
                //converted[0] = converter.convert2RowsetCompatible(converted[0], fields.get(loc.getFields().get(0)).getTypeInfo());
            }
            return loc.find(converted);
        }

        public RowHostObject getRow(int aIndex) throws Exception {
            return RowHostObject.publishRow(entity.getModel().getScriptThis(), loc.getRow(aIndex), entity);
        }

        public int getSize() {
            return loc.getSize();
        }

        public Locator unwrap() {
            return loc;
        }
    }

    public class ScriptableField {

        protected String fieldName = null;

        public ScriptableField(String aFieldName) {
            super();
            fieldName = aFieldName;
        }

        public E getEntity() {
            return entity;
        }

        public Object getValue(Scriptable aDelegate) throws Exception {
            Object lValue = null;
            Rowset eRowset = getRowset();
            if (eRowset != null && ((eRowset.size() > 0 && !eRowset.isBeforeFirst() && !eRowset.isAfterLast()) || eRowset.isInserting())) {
                lValue = eRowset.getObject(eRowset.getFields().find(fieldName));
            }
            if (lValue == null) {
                lValue = entity.getSubstituteRowsetObject(fieldName);
            }
            if (lValue instanceof CompactClob) {
                lValue = ((CompactClob) lValue).getData();
            }
            return ScriptUtils.javaToJS(lValue, aDelegate);
        }

        public void setValue(Scriptable aDelegate, Object aValue) throws Exception {
            Rowset rowset = getRowset();
            if (rowset != null) {
                if ((!rowset.isBeforeFirst() && !rowset.isAfterLast()) || rowset.isInserting()) {
                    aValue = ScriptUtils.js2Java(aValue);
                    rowset.updateObject(rowset.getFields().find(fieldName), aValue);
                }
            }
        }
    }

    public ScriptableRowset(E aEntity) {
        super();
        entity = aEntity;
    }

    /*
     protected void checkModelExecuted() throws Exception {
     if (entity != null) {
     if (!entity.getModel().isRuntime() && !(entity instanceof ApplicationParametersEntity)) {
     entity.getModel().setRuntime(true);
     }
     }
     }
     */
    protected void checkRowset() throws Exception {
        if (entity != null) {
            //checkModelExecuted();
            Rowset rs = entity.getRowset();
            if (rs == null) {
                throw new RowsetMissingException();
            }
        } else {
            throw new RowsetMissingException();
        }
    }

    protected Rowset getRowset() throws Exception {
        checkRowset();
        return entity.getRowset();
    }

    public void createScriptableFields() throws RowsetMissingException {
        Fields md = getFields();
        if (md != null && scriptableFields == null) {
            scriptableFields = new HashMap<>();
            for (int i = 1; i <= md.getFieldsCount(); i++) {
                Field field = md.get(i);
                String fName = field.getName();
                if (fName != null && !fName.isEmpty()) {
                    ScriptableRowset<E>.ScriptableField scriptableField = new ScriptableRowset<E>.ScriptableField(fName);
                    scriptableFields.put(fName, scriptableField);
                }
            }
        }
    }

    public Fields getFields() throws RowsetMissingException {
        if (entity != null) {
            return entity.getFields();
        }
        return null;
    }

    // Query metadata interface
    protected Parameters getParams() throws Exception {
        if (entity != null && entity.getQuery() != null) {
            return entity.getQuery().getParameters();
        }
        return null;
    }

    public String getDbId() throws Exception {
        return isTableQuery() ? entity.getTableDbId() : entity.getQuery().getDbId();
    }

    public String getQueryId() {
        if (entity != null) {
            return entity.getQueryId();
        }
        return null;
    }

    public String getTableName() {
        if (entity != null) {
            return entity.getTableName();
        }
        return null;
    }

    public String getTableSchemaName() {
        if (entity != null) {
            return entity.getTableSchemaName();
        }
        return null;
    }

    public String getFullTableName() {
        if (entity != null) {
            String lTn = entity.getTableName();
            String lSn = entity.getTableSchemaName();
            if (lSn != null && !lSn.isEmpty()) {
                lTn = lSn + "." + lTn;
            }
            return lTn;
        }
        return null;
    }

    /*
     public String getMainTable() throws Exception {
     if (entity != null && entity.getQuery() != null) {
     return entity.getQuery().getMainTable();
     }
     return null;
     }
     */
    public boolean isModified() throws Exception {
        if (entity != null && entity.getRowset() != null) {
            return entity.getRowset().isModified();
        }
        return false;
    }

    public boolean isTableQuery() {
        if (entity != null) {
            return entity.getQueryId() == null;
        }
        return false;
    }
    private static final String EMPTY_JSDOC = ""
            + "/**\n"
            + "* Checks if the rowset is empty.\n"
            + "* @return <code>true</code> if the rowset is empty and <code>false</code> otherwise\n"
            + "*/";

    @ScriptFunction(jsDoc = EMPTY_JSDOC)
    public boolean isEmpty() throws Exception {
        if (entity != null && getRowset() != null) {
            return getRowset().size() <= 0;
        }
        return true;
    }

    public boolean isInserting() throws Exception {
        if (entity != null && getRowset() != null) {
            return getRowset().isInserting();
        }
        return true;
    }
    // Find and positioning interface
    private static final String FIND_JSDOC = ""
            + "/**\n"
            + "* Finds rows using field -- field value pairs.\n"
            + "* @param pairs the search conditions pairs, if a form of key-values pairs, where the key is the property object (e.g. entity.md.propName) and the value for this property\n"
            + "* @return the rows object's array accordind to the search condition or empty array if nothing is found\n"
            + "*/";

    @ScriptFunction(jsDoc = FIND_JSDOC, params = {"pairs"})
    public Scriptable find(Object... values) throws Exception {
        Rowset rs = getRowset();
        if (rs != null && values != null && values.length > 0 && values.length % 2 == 0) {
            Fields fields = rs.getFields();
            Converter converter = rs.getConverter();
            List<Integer> constraints = new ArrayList<>();
            List<Object> keyValues = new ArrayList<>();
            for (int i = 0; i < values.length - 1; i += 2) {
                if (values[i] != null && values[i] instanceof Field) {
                    Field field = (Field) values[i];
                    int colIndex = fields.find(field.getName());
                    if (colIndex > 0) {
                        constraints.add(colIndex);
                    } else {
                        Logger.getLogger(ScriptableRowset.class.getName()).log(Level.SEVERE, String.format(BAD_FIELD_NAME_MSG, field.getName()));
                    }
                    Object keyValue = ScriptUtils.js2Java(values[i + 1]);
                    if (converter != null) {
                        try {
                            keyValue = converter.convert2RowsetCompatible(keyValue, field.getTypeInfo());
                        } catch (Exception ex) {
                            Logger.getLogger(ScriptableRowset.class.getName()).warning(String.format(CANT_CONVERT_TO_MSG, field.getTypeInfo().getSqlTypeName()));
                            keyValue = null;
                        }
                    }
                    keyValues.add(keyValue);
                } else {
                    Logger.getLogger(ScriptableRowset.class.getName()).log(Level.SEVERE, String.format(BAD_FIND_ARGUMENT_MSG, i));
                }
            }
            if (!constraints.isEmpty() && constraints.size() == keyValues.size()) {
                Locator loc = checkUserLocator(constraints, rs);
                if (loc.find(keyValues.toArray())) {
                    TaggedList<RowWrap> subSet = loc.getSubSet();
                    if (subSet.tag == null) {
                        List<RowHostObject> found = new ArrayList<>();
                        for (RowWrap rw : subSet) {
                            found.add(RowHostObject.publishRow(entity.getModel().getScriptThis(), rw.getRow(), entity));
                        }
                        subSet.tag = wrapArray(found.toArray());
                    }
                    assert subSet.tag instanceof Scriptable;
                    return (Scriptable) subSet.tag;
                }
            }
        } else {
            Logger.getLogger(ScriptableRowset.class.getName()).log(Level.SEVERE, BAD_FIND_AGRUMENTS_MSG);
        }
        return wrapArray(new Object[]{});
    }

    protected Scriptable wrapArray(Object[] elements) {
        return Context.getCurrentContext().newArray(entity.getModel().getScriptThis(), elements);
    }
    private static final String FIND_BY_ID_JSDOC = ""
            + "/**\n"
            + "* Finds row by its key. Key must a single property.\n"
            + "* @param key the unique identifier of the row\n"
            + "* @return a row object or <code>null</code> if nothing is found\n"
            + "*/";

    @ScriptFunction(jsDoc = FIND_BY_ID_JSDOC, params = {"key"})
    public RowHostObject findById(Object aValue) throws Exception {
        Rowset rs = getRowset();
        Fields fields = rs.getFields();
        List<Field> pks = fields.getPrimaryKeys();
        if (pks.size() == 1) {
            Scriptable found = find(pks.get(0), aValue);
            assert found instanceof NativeArray;
            long length = ((NativeArray) found).getLength();
            if (length == 1) {
                assert ((NativeArray) found).get(0) instanceof RowHostObject;
                return (RowHostObject) ((NativeArray) found).get(0);
            } else if (length > 1) {
                Logger.getLogger(ScriptableRowset.class.getName()).log(Level.SEVERE, String.format("More than one object found with such ids. Use find() to get correct results."));
            }//else nothing found (length == 0)
        } else {
            Logger.getLogger(ScriptableRowset.class.getName()).log(Level.SEVERE, String.format(BAD_PRIMARY_KEYS_MSG, pks.size()));
        }
        return null;
    }
    private static final String SCROLL_TO_JSDOC = ""
            + "/**\n"
            + "* Sets the rowset cursor to the specified row.\n"
            + "* @param row the row to position the entity cursor\n"
            + "* @return <code>true</code> if the rowset scrolled successfully and <code>false</code> otherwise\n"
            + "*/";

    @ScriptFunction(jsDoc = SCROLL_TO_JSDOC, params = {"row"})
    public boolean scrollTo(RowHostObject aRow) throws Exception {
        if (aRow != null) {
            Rowset rs = getRowset();
            List<Integer> constraints = aRow.unwrap().getFields().getPrimaryKeysIndicies();
            Locator loc = checkUserLocator(constraints, rs);
            List<Object> values = new ArrayList<>();
            for (Integer colIndx : constraints) {
                values.add(aRow.getColumnObject(colIndx));
            }
            return loc.find(values.toArray()) && loc.first();
        } else {
            return false;
        }
    }
    private static final String BEFORE_FIRST_JSDOC = ""
            + "/**\n"
            + "* Moves the rowset cursor to the position before the first row.\n"
            + "*/";

    // Rowset scroll interface
    @ScriptFunction(jsDoc = BEFORE_FIRST_JSDOC)
    public void beforeFirst() throws Exception {
        getRowset().beforeFirst();
    }
    private static final String AFTER_LAST_JSDOC = ""
            + "/**\n"
            + "* Moves the rowset cursor to the position after the last row.\n"
            + "*/";

    @ScriptFunction(jsDoc = AFTER_LAST_JSDOC)
    public void afterLast() throws Exception {
        getRowset().afterLast();
    }
    private static final String BOF_JSDOC = ""
            + "/**\n"
            + "* Checks if cursor in the position before the first row.\n"
            + "* @return <code>true</code> if cursor in the position before the first row and <code>false</code> otherwise\n"
            + "*/";

    @ScriptFunction(jsDoc = BOF_JSDOC)
    public boolean bof() throws Exception {
        return getRowset().isBeforeFirst();
    }
    private static final String EOF_JSDOC = ""
            + "/**\n"
            + "* Checks if cursor in the position before the first row.\n"
            + "* @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise\n"
            + "*/";

    @ScriptFunction(jsDoc = EOF_JSDOC)
    public boolean eof() throws Exception {
        return getRowset().isAfterLast();
    }
    private static final String FIRST_JSDOC = ""
            + "/**\n"
            + "* Moves the rowset cursor to the first row.\n"
            + "* @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise\n"
            + "*/";

    @ScriptFunction(jsDoc = FIRST_JSDOC)
    public boolean first() throws Exception {
        return getRowset().first();
    }
    private static final String NEXT_JSDOC = ""
            + "/**\n"
            + "* Moves the rowset cursor to the next row.\n"
            + "* @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise\n"
            + "*/";

    @ScriptFunction(jsDoc = NEXT_JSDOC)
    public boolean next() throws Exception {
        return getRowset().next();
    }
    private static final String PREV_JSDOC = ""
            + "/**\n"
            + "* Moves the rowset cursor to the privious row.\n"
            + "* @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise\n"
            + "*/";

    @ScriptFunction(jsDoc = PREV_JSDOC)
    public boolean prev() throws Exception {
        return getRowset().previous();
    }
    private static final String LAST_JSDOC = ""
            + "/**\n"
            + "* Moves the rowset cursor to the last row.\n"
            + "* @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise\n"
            + "*/";

    @ScriptFunction(jsDoc = LAST_JSDOC)
    public boolean last() throws Exception {
        return getRowset().last();
    }
    private static final String POS_JSDOC = ""
            + "/**\n"
            + "* Checks if the cursor is on the specified index.\n"
            + "* @param index the row index to check, starting form <code>1</code>.\n"
            + "* @return <code>true</code> if the cursor is on the row with specified index and <code>false</code> otherwise\n"
            + "*/";

    @ScriptFunction(jsDoc = POS_JSDOC, params = {"index"})
    public boolean pos(int recordIndex) throws Exception {
        return getRowset().absolute(recordIndex);
    }
    private static final String GET_ROW_JSDOC = ""
            + "/**\n"
            + "* Gets the row at specified index.\n"
            + "* @param index the row index, starting form <code>1</code>.\n"
            + "* @return the row object or <code>null</code> if no row object have found at the specified index\n"
            + "*/";

    @ScriptFunction(jsDoc = GET_ROW_JSDOC, params = {"index"})
    public RowHostObject getRow(int aIndex) throws Exception {
        return RowHostObject.publishRow(entity.getModel().getScriptThis(), getRowset().getRow(aIndex), entity);
    }
    private static final String ROW_INDEX_JSDOC = ""
            + "/**\n"
            + "* The current cursor position, starting form <code>1</code>.\n"
            + "*/";

    @ScriptFunction(jsDoc = ROW_INDEX_JSDOC)
    public int getRowIndex() throws Exception {
        Rowset rowset = getRowset();
        return rowset.getCursorPos();
    }

    @ScriptFunction
    public void setRowIndex(int aRowIndex) throws Exception {
        pos(aRowIndex);
    }
    private static final String LENGTH_JSDOC = ""
            + "/**\n"
            + "* The rowset array length.\n"
            + "*/";

    @ScriptFunction(jsDoc = LENGTH_JSDOC)
    public int getLength() throws Exception {
        return getSize();
    }
    private static final String SIZE_JSDOC = ""
            + "/**\n"
            + "* The rowset size.\n"
            + "*/";

    @ScriptFunction(jsDoc = SIZE_JSDOC)
    public int getSize() throws Exception {
        Rowset rowset = getRowset();
        return rowset.size();
    }

    public void setSubstitute(ScriptableRowset<E> aSRowset) {
        if (entity != null) {
            entity.setSubstitute(aSRowset != null ? aSRowset.getEntity() : null);
        }
    }

    public ScriptableRowset<E> getSubstitute() {
        return entity != null && entity.getSubstitute() != null ? entity.getSubstitute().getRowsetWrap().rowset : null;
    }

    public Object getFieldColIndex(Field aField) throws Exception {
        Rowset rowset = getRowset();
        return rowset.getFields().find(aField.getName());
    }

    public Object getValue(int recordIndex, Field aField) throws Exception {
        Rowset rowset = getRowset();
        int colIndex = rowset.getFields().find(aField.getName());
        return ScriptUtils.javaToJS(rowset.getCurrent().get(recordIndex - 1).getColumnObject(colIndex), entity.getModel().getScriptThis());
    }

    public Object getValue(int recordIndex, int aColIndex) throws Exception {
        Rowset rowset = getRowset();
        return ScriptUtils.javaToJS(rowset.getCurrent().get(recordIndex - 1).getColumnObject(aColIndex), entity.getModel().getScriptThis());
    }

    public ScriptableRowset<E>.ScriptableField getScriptableField(String aFieldName) {
        return scriptableFields.get(aFieldName);
    }
    // Array mutator methods
    private static final String POP_JSDOC = ""
            + "/**\n"
            + "* Removes the last element from an array and returns that element.\n"
            + "* @return the last object or <code>undefined</code> if the array is empty.\n"
            + "*/";

    @ScriptFunction(jsDoc = POP_JSDOC)
    public Object pop() throws Exception {
        Rowset rowset = getRowset();
        if (!rowset.isEmpty()) {
            Set<Row> toDel = new HashSet<>();
            Row r = rowset.getRow(rowset.size());
            toDel.add(r);
            rowset.delete(toDel);
            return RowHostObject.publishRow(entity.getModel().getScriptThis(), r, entity);
        } else {
            return Context.getUndefinedValue();
        }
    }
    private static final String SHIFT_JSDOC = ""
            + "/**\n"
            + "* Removes the first element from an array and returns that element.\n"
            + "* @return the first object or <code>undefined</code> if the array is empty.\n"
            + "*/";

    @ScriptFunction(jsDoc = SHIFT_JSDOC)
    public Object shift() throws Exception {
        Rowset rowset = getRowset();
        if (!rowset.isEmpty()) {
            Row r = rowset.getRow(1);
            Set<Row> toDel = new HashSet<>();
            toDel.add(r);
            rowset.delete(toDel);
            return RowHostObject.publishRow(entity.getModel().getScriptThis(), r, entity);
        } else {
            return Context.getUndefinedValue();
        }
    }

    protected Object[] propsToArray(ScriptableObject aRowObject) throws Exception {
        Rowset rowset = getRowset();
        List<Object> duplets = new ArrayList<>();
        for (Object jsId : aRowObject.getIds()) {
            if (jsId instanceof String) {
                int colIndex = rowset.getFields().find((String) jsId);
                if (colIndex > 0) {
                    duplets.add(colIndex);
                    duplets.add(ScriptUtils.js2Java(aRowObject.get(jsId)));
                }
            }
        }
        return duplets.toArray();
    }
    private static final String PUSH_JSDOC = ""
            + "/**\n"
            + "* Adds one or more elements to the end of an array and returns the new length of the array.\n"
            + "* @param objects the objects to push, e.g.: { propName1: propValue1, propName2: propValue2 } (optional)\n"
            + "*/";

    @ScriptFunction(jsDoc = PUSH_JSDOC, params = {"objects"})
    public int push(Object... aArguments) throws Exception {
        Rowset rowset = getRowset();
        for (int i = 0; i < aArguments.length; i++) {
            if (aArguments[i] instanceof ScriptableObject) {
                rowset.insertAt(rowset.size() + 1, propsToArray((ScriptableObject) aArguments[i]));
            } else {
                throw new IllegalArgumentException(String.format("Expected Object at index %d as pushed element.", i));
            }
        }
        return rowset.size();
    }
    private static final String UHSHIFT_JSDOC = ""
            + "/**\n"
            + "* Adds one or more elements to the front of an array and returns the new length of the array.\n"
            + "* @param elements the objects to add\n"
            + "*/";

    @ScriptFunction(jsDoc = UHSHIFT_JSDOC, params = {"elements"})
    public int unshift(Object... aArguments) throws Exception {
        Rowset rowset = getRowset();
        for (int i = aArguments.length - 1; i >= 0; i--) {
            if (aArguments[i] instanceof ScriptableObject) {
                rowset.insertAt(1, propsToArray((ScriptableObject) aArguments[i]));
            } else {
                throw new IllegalArgumentException(String.format("Expected Object at index %d as unshifted element.", i));
            }
        }
        return rowset.size();
    }
    private static final String REVERSE_JSDOC = ""
            + "/**\n"
            + "* Reverses an array in place. The first array element becomes the last and the last becomes the first.\n"
            + "*/";

    @ScriptFunction(jsDoc = REVERSE_JSDOC)
    public void reverse() throws Exception {
        Rowset rowset = getRowset();
        rowset.reverse();
    }
    private static final String SPLICE_JSDOC = ""
            + "/**\n"
            + "* Changes the content of an array, adding new elements while removing old elements.\n"
            + "* @param index index at which to start changing the array\n"
            + "* @param howMany an integer indicating the number of old array elements to remove\n"
            + "* @param elements the elements to add to the array. If you don't specify any elements, <code>splice</code> simply removes elements from the array.\n"
            + "* @return an array containing the removed elements\n"
            + "*/";

    @ScriptFunction(jsDoc = SPLICE_JSDOC, params = {"index", "howMany", "elements"})
    public Scriptable splice(Object... arguments) throws Exception {
        if (arguments.length > 0) {
            Rowset rowset = getRowset();
            int size = rowset.size();
            if (!(arguments[0] instanceof Number)) {
                throw new IllegalArgumentException("The first argument 'index' must be a number");
            }
            int startAt = ((Number) arguments[0]).intValue();
            if (startAt < 0) {
                startAt = size + startAt;
            }
            if (startAt < 0) {
                throw new IllegalArgumentException("Bad first argument 'index'. It should be less than or equal array's length by absolute value");
            }
            if (arguments.length > 1 && !(arguments[1] instanceof Number)) {
                throw new IllegalArgumentException("The second argument 'howMany' must be a number if exists at all.");
            }
            int howMany = arguments.length > 1 ? ((Number) arguments[1]).intValue() : size;
            if (howMany < 0) {
                throw new IllegalArgumentException("Bad second argument 'howMany'. It should greater or equal to zero");
            }
            List<RowHostObject> removed = new ArrayList<>();
            while (startAt < size && removed.size() < howMany) {
                Row deleted = rowset.getRow(startAt + 1);
                rowset.deleteAt(startAt + 1);
                RowHostObject deletedFacade = RowHostObject.publishRow(entity.getModel().getScriptThis(), deleted, entity);
                removed.add(deletedFacade);
                size = rowset.size();
            }
            for (int l = arguments.length - 1; l >= 2; l--) {
                Object[] propsAsArray = propsToArray((ScriptableObject) arguments[l]);
                rowset.insertAt(startAt + 1, propsAsArray);
            }
            return wrapArray(removed.toArray());
        } else {
            throw new IllegalArgumentException("Bad arguments. There are must at least one argument");
        }
    }
    private static final String SORT_JSDOC = ""
            + "/**\n"
            + "* Sorts the elements of an array.\n"
            + "*/";

    @ScriptFunction(jsDoc = SORT_JSDOC)
    public void sort(Object aComparator) throws Exception {
        if (aComparator != null) {
            if (aComparator instanceof RowsComparator) {
                RowsComparator criteria = (RowsComparator) aComparator;
                Rowset rowset = getRowset();
                rowset.sort(criteria);
            } else if (aComparator instanceof Function) {
                final Function cFunc = (Function) aComparator;
                Comparator<Row> criteria = new Comparator<Row>() {
                    @Override
                    public int compare(Row r1, Row r2) {
                        try {
                            Object compValue = ScriptUtils.js2Java(
                                    cFunc.call(Context.getCurrentContext(),
                                    cFunc.getParentScope(),
                                    entity.getModel().getScriptThis(),
                                    new Object[]{
                                RowHostObject.publishRow(cFunc.getParentScope(), r1, entity),
                                RowHostObject.publishRow(cFunc.getParentScope(), r2, entity)
                            }));
                            return compValue instanceof Number ? ((Number) compValue).intValue() : 0;
                        } catch (Exception ex) {
                            throw new IllegalStateException(ex);
                        }
                    }
                };
                Rowset rowset = getRowset();
                rowset.sort(criteria);
            }
        } else {
            throw new RowsetException("Cirteria argument is invalid.");
        }
    }
    // Array accessor methods
    private static final String CONCAT_JSDOC = ""
            + "/**\n"
            + "* Creates a new array comprised of this array joined with other array(s) and/or value(s).\n"
            + "* @param elements array(s) and/or value(s) to concatenate to the resulting array\n"
            + "* @return a new array comprised of this array joined with other value(s)\n"
            + "*/";

    @ScriptFunction(jsDoc = CONCAT_JSDOC, params = {"elements"})
    public Scriptable concat(Object... arguments) throws Exception {
        List<Object> concated = new ArrayList<>();
        Rowset rowset = getRowset();
        int size = rowset.size();
        for (int i = 0; i < size; i++) {
            Row row = rowset.getRow(i + 1);
            RowHostObject rowFacade = RowHostObject.publishRow(entity.getModel().getScriptThis(), row, entity);
            concated.add(rowFacade);
        }
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] instanceof NativeArray) {
                for (int l = 0; l < ((NativeArray) arguments[i]).getLength(); l++) {
                    concated.add(((NativeArray) arguments[i]).get(l));
                }
            } else {
                concated.add(arguments[i]);
            }
        }
        return wrapArray(concated.toArray());
    }
    private static final String JOIN_JSDOC = ""
            + "/**\n"
            + "* Joins all elements of an array into a string.\n"
            + "* @param separator a separator <code>String</code>, if omitted, the array elements are separated with a comma (optional)\n"
            + "* @return a <code>String</code> conversions of all array elements\n"
            + "*/";

    @ScriptFunction(jsDoc = JOIN_JSDOC, params = {"separator"})
    public String join(String aSeparator) throws Exception {
        String sep;
        if (aSeparator == null || Context.getUndefinedValue().equals(aSeparator)) {
            sep = ",";//NOI18N
        } else {
            sep = aSeparator;
        }
        return join("", "", sep, Integer.MAX_VALUE);//NOI18N
    }

    protected String join(String aPrefix, String aPostfix, String aSeparator, int aMaxSize) throws Exception {
        StringBuilder sb = new StringBuilder();
        Rowset rowset = getRowset();
        int size = rowset.size();
        sb.append(aPrefix);
        for (int i = 1; i <= Math.min(aMaxSize, size); i++) {
            if (i > 1) {
                sb.append(aSeparator);
            }
            Row row = rowset.getRow(i);
            RowHostObject rowFacade = RowHostObject.publishRow(entity.getModel().getScriptThis(), row, entity);
            sb.append(rowFacade.toString());
        }
        sb.append(aPostfix);
        return sb.toString();
    }

    private static final String SLICE_JSDOC = ""
            + "/**\n"
            + "* Creates a shallow copy of a portion of an array.\n"
            + "* @param begin zero-based index at which to begin extraction.\n"
            + "* @param end zero-based index at which to end extraction. slice extracts up to but not including end\n"
            + "* @return a new \"one level deep\" copy that contains copies of the elements sliced from the original array (optional)\n"
            + "*/";
    
    @ScriptFunction(jsDoc = SLICE_JSDOC, params = {"begin", "end"})
    public Scriptable slice(Integer... arguments) throws Exception {
        if (arguments != null && arguments.length <= 2) {
            Rowset rowset = getRowset();
            int size = rowset.size();
            int startAt = arguments[0];
            int endAt;
            if (arguments.length > 1) {
                endAt = arguments[1];
            } else {
                endAt = size - 1;
            }

            List<RowHostObject> sliced = new ArrayList<>();
            if (startAt < 0) {
                startAt = size + startAt;
            }
            if (startAt < 0) {
                throw new IllegalArgumentException("Bad first argument 'begin'. It should be less than or equal array's length by absolute value");
            }
            if (endAt < 0) {
                endAt = size + endAt;
            }
            if (endAt < 0) {
                throw new IllegalArgumentException("Bad second argument 'end'. It should be less than or equal array's length by absolute value");
            }

            for (int i = startAt; i <= endAt; i++) {
                Row row = rowset.getRow(i + 1);
                RowHostObject rowFacade = RowHostObject.publishRow(entity.getModel().getScriptThis(), row, entity);
                sliced.add(rowFacade);
            }
            return wrapArray(sliced.toArray());
        } else {
            throw new IllegalArgumentException("There are must be one or two arguments to slice function");
        }
    }

    private static final String STRING_JSDOC = ""
            + "/**\n"
            + "* Creates a <code>String</code> representation the rowset.\n"
            + "* @return a <code>String</code> representing the array and its elements\n"
            + "*/";
    
    @Override
    @ScriptFunction(jsDoc = STRING_JSDOC)
    public String toString() {
        try {
            if (entity != null) {
                if (entity.getRowset() != null) {
                    String res = join("[", "]", "\n", 100);
                    if (entity.getRowset().size() > 100) {
                        res += ",\n...";
                    }
                    return res;
                } else {
                    String res = entity.getName();
                    if (res == null || res.isEmpty()) {
                        res = entity.getTitle();
                        if (res == null || res.isEmpty()) {
                            return entity.toString();
                        }
                    } else {
                        res += "[" + entity.getTitle() + "]";
                    }
                    return res;
                }
            } else {
                return super.toString();
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static final String INDEX_OF_JSDOC = ""
            + "/**\n"
            + "* Gets the first index of an element within the array equal to the specified value, or -1 if none is found.\n"
            + "* @param searchElement an element to locate in the array\n"
            + "* @return the first index of the element in the array\n"
            + "*/";
    
    @ScriptFunction(jsDoc = INDEX_OF_JSDOC, params = {"searchElement"})
    public int indexOf(Object aObj) throws Exception {
        Rowset rowset = getRowset();
        int size = rowset.size();
        for (int i = 0; i < size; i++) {
            Row row = rowset.getRow(i + 1);
            RowHostObject rowFacade = RowHostObject.publishRow(entity.getModel().getScriptThis(), row, entity);
            if (rowFacade == aObj) {
                return i;
            }
        }
        return -1;
    }

    private static final String LAST_INDEX_OF_JSDOC = ""
            + "/**\n"
            + "* Gets the last index of an element within the array equal to the specified value, or -1 if none is found.\n"
            + "* @param searchElement an element to locate in the array\n"
            + "* @return the first index of the element in the array\n"
            + "*/";
    
    @ScriptFunction(jsDoc = LAST_INDEX_OF_JSDOC, params = {"searchElement"})
    public int lastIndexOf(Object aObj) throws Exception {
        Rowset rowset = getRowset();
        int size = rowset.size();
        for (int i = size - 1; i >= 0; i--) {
            Row row = rowset.getRow(i + 1);
            RowHostObject rowFacade = RowHostObject.publishRow(entity.getModel().getScriptThis(), row, entity);
            if (rowFacade == aObj) {
                return i;
            }
        }
        return -1;
    }

    // Array iteration methods
    
    private static final String FILTER_JSDOC = ""
            + "/**\n"
            + "* Creates a new array with all of the elements of this array for which the provided filtering function returns true.\n"
            + "* @param callback the function to test each element of the array\n"
            + "* @param thisObject <code>Object</code> to use as <code>this</code> when executing callback (optional)\n"
            + "* @return the new filtered array\n"
            + "*/";
    
    @ScriptFunction(jsDoc = FILTER_JSDOC, params = {"callback", "thisObject"})
    public Scriptable filter(Object... arguments) throws Exception {
        if (arguments != null && arguments.length <= 2) {
            if (arguments[0] instanceof Function) {
                Function callback = (Function) arguments[0];
                Scriptable thisObj = null;
                if (arguments.length > 1) {
                    if (arguments[1] instanceof Scriptable) {
                        thisObj = (Scriptable) arguments[1];
                    } else {
                        throw new IllegalArgumentException("Second parameter to filter function must be Object if exists at all.");
                    }
                }
                List<RowHostObject> filtered = new ArrayList<>();
                Rowset rowset = getRowset();
                assert tag instanceof RowsetHostObject;
                RowsetHostObject<E> rowsetFacade = (RowsetHostObject<E>) tag;
                int size = rowset.size();
                for (int i = 0; i < size; i++) {
                    Row row = rowset.getRow(i + 1);
                    RowHostObject rowFacade = RowHostObject.publishRow(entity.getModel().getScriptThis(), row, entity);
                    Object res = callback.call(Context.getCurrentContext(), entity.getModel().getScriptThis(), thisObj, new Object[]{rowFacade, i, rowsetFacade});
                    if (Boolean.TRUE.equals(res)) {
                        filtered.add(rowFacade);
                    }
                }
                return wrapArray(filtered.toArray());
            } else {
                throw new IllegalArgumentException("First parameters to filter function must be a callback function");
            }
        } else {
            throw new IllegalArgumentException("There are must be one or two arguments to filter function");
        }
    }

    private static final String FOR_EACH_JSDOC = ""
            + "/**\n"
            + "* Calls a function for each element in the array.\n"
            + "* @param callback the function to execute for each element\n"
            + "* @param thisObject <code>Object</code> to use as <code>this</code> when executing callback (optional)\n"
            + "*/";
    
    @ScriptFunction(jsDoc = FOR_EACH_JSDOC, params = {"callback", "thisObject"})
    public void forEach(Object... arguments) throws Exception {
        if (arguments != null && arguments.length <= 2) {
            if (arguments[0] instanceof Function) {
                Function callback = (Function) arguments[0];
                Scriptable thisObj = null;
                if (arguments.length > 1) {
                    if (arguments[1] instanceof Scriptable) {
                        thisObj = (Scriptable) arguments[1];
                    } else {
                        throw new IllegalArgumentException("Second parameter to forEach function must be Object if exists at all.");
                    }
                }
                Rowset rowset = getRowset();
                assert tag instanceof RowsetHostObject;
                RowsetHostObject<E> rowsetFacade = (RowsetHostObject<E>) tag;
                int size = rowset.size();
                for (int i = 0; i < size; i++) {
                    Row row = rowset.getRow(i + 1);
                    RowHostObject rowFacade = RowHostObject.publishRow(entity.getModel().getScriptThis(), row, entity);
                    callback.call(Context.getCurrentContext(), entity.getModel().getScriptThis(), thisObj, new Object[]{rowFacade, i, rowsetFacade});
                }
            } else {
                throw new IllegalArgumentException("First parameters to forEach function must be a callback function");
            }
        } else {
            throw new IllegalArgumentException("There are must be one or two arguments to forEach function");
        }
    }

    private static final String EVERY_JSDOC = ""
            + "/**\n"
            + "* Tests whether all elements in the array pass the test implemented by the provided function.\n"
            + "* @param callback the test function to execute for each element\n"
            + "* @param thisObject <code>Object</code> to use as <code>this</code> when executing callback (optional)\n"
            + "* @return <code>true</code> if <code>callback</code> returned a true value for all elements, will return <code>true</code> and will return <code>false</code> otherwise\n"
            + "*/";
    
    @ScriptFunction(jsDoc = EVERY_JSDOC, params = {"callback", "thisObject"})
    public boolean every(Object... arguments) throws Exception {
        if (arguments != null && arguments.length <= 2) {
            if (arguments[0] instanceof Function) {
                Function callback = (Function) arguments[0];
                Scriptable thisObj = null;
                if (arguments.length > 1) {
                    if (arguments[1] instanceof Scriptable) {
                        thisObj = (Scriptable) arguments[1];
                    } else {
                        throw new IllegalArgumentException("Second parameter to every function must be Object if exists at all.");
                    }
                }
                Rowset rowset = getRowset();
                assert tag instanceof RowsetHostObject;
                RowsetHostObject<E> rowsetFacade = (RowsetHostObject<E>) tag;
                int size = rowset.size();
                for (int i = 0; i < size; i++) {
                    Row row = rowset.getRow(i + 1);
                    RowHostObject rowFacade = RowHostObject.publishRow(entity.getModel().getScriptThis(), row, entity);
                    Object res = callback.call(Context.getCurrentContext(), entity.getModel().getScriptThis(), thisObj, new Object[]{rowFacade, i, rowsetFacade});
                    if (Boolean.FALSE.equals(res)) {
                        return false;
                    }
                }
                return true;
            } else {
                throw new IllegalArgumentException("First parameters to every function must be a callback function");
            }
        } else {
            throw new IllegalArgumentException("There are must be one or two arguments to every function");
        }
    }

    private static final String MAP_JSDOC = ""
            + "/**\n"
            + "* Creates a new array with the results of calling a provided function on every element in this array. <code>map</code> does not mutate the array on which it is called.\n"
            + "* @param callback the function that produces an element of the new Array from an element of the current one\n"
            + "* @param thisObject <code>Object</code> to use as <code>this</code> when executing callback (optional)\n"
            + "* @return a new array with the results of calling a provided function on every element\n"
            + "*/";
    
    @ScriptFunction(jsDoc = MAP_JSDOC, params = {"callback", "thisObject"})
    public Scriptable map(Object... arguments) throws Exception {
        if (arguments != null && arguments.length <= 2) {
            if (arguments[0] instanceof Function) {
                List<Object> mapped = new ArrayList<>();
                Function callback = (Function) arguments[0];
                Scriptable thisObj = null;
                if (arguments.length > 1) {
                    if (arguments[1] instanceof Scriptable) {
                        thisObj = (Scriptable) arguments[1];
                    } else {
                        throw new IllegalArgumentException("Second parameter to map function must be Object if exists at all.");
                    }
                }
                Rowset rowset = getRowset();
                assert tag instanceof RowsetHostObject;
                RowsetHostObject<E> rowsetFacade = (RowsetHostObject<E>) tag;
                int size = rowset.size();
                for (int i = 0; i < size; i++) {
                    Row row = rowset.getRow(i + 1);
                    RowHostObject rowFacade = RowHostObject.publishRow(entity.getModel().getScriptThis(), row, entity);
                    Object res = callback.call(Context.getCurrentContext(), entity.getModel().getScriptThis(), thisObj, new Object[]{rowFacade, i, rowsetFacade});
                    mapped.add(res);
                }
                return wrapArray(mapped.toArray());
            } else {
                throw new IllegalArgumentException("First parameters to map function must be a callback function");
            }
        } else {
            throw new IllegalArgumentException("There are must be one or two arguments to map function");
        }
    }

    private static final String SOME_JSDOC = ""
            + "/**\n"
            + "* Tests whether any of elements in the array pass the test implemented by the provided function.\n"
            + "* @param callback the test function to execute for each element\n"
            + "* @param thisObject <code>Object</code> to use as <code>this</code> when executing callback (optional)\n"
            + "* @return <code>true</code> if <code>callback</code> returned a true value for any element, will return <code>true</code> and will return <code>false</code> otherwise\n"
            + "*/";
    
    @ScriptFunction(jsDoc = SOME_JSDOC, params = {"callback", "thisObject"})
    public boolean some(Object... arguments) throws Exception {
        if (arguments != null && arguments.length <= 2) {
            if (arguments[0] instanceof Function) {
                Function callback = (Function) arguments[0];
                Scriptable thisObj = null;
                if (arguments.length > 1) {
                    if (arguments[1] instanceof Scriptable) {
                        thisObj = (Scriptable) arguments[1];
                    } else {
                        throw new IllegalArgumentException("Second parameter to some function must be Object if exists at all.");
                    }
                }
                Rowset rowset = getRowset();
                assert tag instanceof RowsetHostObject;
                RowsetHostObject<E> rowsetFacade = (RowsetHostObject<E>) tag;
                int size = rowset.size();
                for (int i = 0; i < size; i++) {
                    Row row = rowset.getRow(i + 1);
                    RowHostObject rowFacade = RowHostObject.publishRow(entity.getModel().getScriptThis(), row, entity);
                    Object res = callback.call(Context.getCurrentContext(), entity.getModel().getScriptThis(), thisObj, new Object[]{rowFacade, i, rowsetFacade});
                    if (Boolean.TRUE.equals(res)) {
                        return true;
                    }
                }
                return false;
            } else {
                throw new IllegalArgumentException("First parameters to some function must be a callback function");
            }
        } else {
            throw new IllegalArgumentException("There are must be one or two arguments to some function");
        }
    }

    private static final String REDUCE_JSDOC = ""
            + "/**\n"
            + "* Applies a function against an accumulator and each value of the array (from left-to-right) as to reduce it to a single value.\n"
            + "* @param callback the function to execute on each value in the array, taking four arguments:\n"
            + "*     @param previousValue the value previously returned in the last invocation of the <code>callback</code>, or <code>initialValue</code>, if supplied. (See below.)\n"
            + "*     @param currentValue the current element being processed in the array\n"
            + "*     @param index the index of the current element being processed in the array\n"
            + "*     @param array the array <code>reduce</code> was called upon\n"
            + "* @param initialValue the <code>Object</code> to use as the first argument to the first call of the callback (optional)\n"
            + "* @return the result value\n"
            + "*/";
    
    @ScriptFunction(jsDoc = REDUCE_JSDOC, params = {"callback", "initialValue"})
    public Object reduce(Object... arguments) throws Exception {
        if (arguments != null && arguments.length <= 2) {
            if (arguments[0] instanceof Function) {
                Function callback = (Function) arguments[0];
                Rowset rowset = getRowset();
                assert tag instanceof RowsetHostObject;
                RowsetHostObject<E> rowsetFacade = (RowsetHostObject<E>) tag;
                int size = rowset.size();
                int startAt;
                Object previousValue;
                if (arguments.length == 1) {
                    startAt = 1;
                    Row _row = rowset.getRow(1);
                    previousValue = RowHostObject.publishRow(entity.getModel().getScriptThis(), _row, entity);

                } else {
                    startAt = 0;
                    previousValue = arguments[1];
                }
                for (int i = startAt; i < size; i++) {
                    Row row1 = rowset.getRow(i + 1);
                    RowHostObject rowFacade1 = RowHostObject.publishRow(entity.getModel().getScriptThis(), row1, entity);
                    previousValue = callback.call(Context.getCurrentContext(), entity.getModel().getScriptThis(), null, new Object[]{previousValue, rowFacade1, i, rowsetFacade});
                }
                return previousValue;
            } else {
                throw new IllegalArgumentException("First argument to reduce function must be a function");
            }
        } else {
            throw new IllegalArgumentException("There are must be one or two arguments to reduce function");
        }
    }

    private static final String REDUCE_RIGHT_JSDOC = ""
            + "/**\n"
            + "* Applies a function against an accumulator and each value of the array (from right-to-left) as to reduce it to a single value.\n"
            + "* @param callback the function to execute on each value in the array, taking four arguments:\n"
            + "*     @param previousValue the value previously returned in the last invocation of the <code>callback</code>, or <code>initialValue</code>, if supplied. (See below.)\n"
            + "*     @param currentValue the current element being processed in the array\n"
            + "*     @param index the index of the current element being processed in the array\n"
            + "*     @param array the array <code>reduce</code> was called upon\n"
            + "* @param initialValue the <code>Object</code> to use as the first argument to the first call of the callback (optional)\n"
            + "* @return the result value\n"
            + "*/";
    
    @ScriptFunction(jsDoc = REDUCE_RIGHT_JSDOC, params = {"callback", "initialValue"})
    public Object reduceRight(Object... arguments) throws Exception {
        if (arguments != null && arguments.length <= 2) {
            if (arguments[0] instanceof Function) {
                Function callback = (Function) arguments[0];
                Rowset rowset = getRowset();
                assert tag instanceof RowsetHostObject;
                RowsetHostObject<E> rowsetFacade = (RowsetHostObject<E>) tag;
                int size = rowset.size();
                int startAt;
                Object previousValue;
                if (arguments.length == 1) {
                    startAt = size - 2;
                    Row _row = rowset.getRow(1);
                    previousValue = RowHostObject.publishRow(entity.getModel().getScriptThis(), _row, entity);

                } else {
                    startAt = size - 1;
                    previousValue = arguments[1];
                }
                for (int i = startAt; i >= 0; i--) {
                    Row row1 = rowset.getRow(i + 1);
                    RowHostObject rowFacade1 = RowHostObject.publishRow(entity.getModel().getScriptThis(), row1, entity);
                    previousValue = callback.call(Context.getCurrentContext(), entity.getModel().getScriptThis(), null, new Object[]{previousValue, rowFacade1, i, rowsetFacade});
                }
                return previousValue;
            } else {
                throw new IllegalArgumentException("First argument to reduceRight function must be a function");
            }
        } else {
            throw new IllegalArgumentException("There are must be one or two arguments to reduceRight function");
        }
    }

    // Table-pattern API
    public ScriptableLocator createLocator(Object... constraints) throws Exception {
        if (constraints != null && constraints.length > 0) {
            Rowset rowset = getRowset();
            Locator loc = rowset.createLocator();
            loc.beginConstrainting();
            try {
                for (int i = 0; i < constraints.length; i++) {
                    if (constraints[i] instanceof Double) {
                        Double d = (Double) constraints[i];
                        if (Math.abs(Math.round(d) - d) < 1e-10) {
                            Long lFieldIndex = Math.round(d);
                            if (Math.abs(lFieldIndex.intValue() - lFieldIndex) == 0) {
                                loc.addConstraint(lFieldIndex.intValue());
                            } else {
                                throw new RowsetException(String.valueOf(i + 1) + " fieldIndex is out of integer value range.");
                            }
                        } else {
                            throw new RowsetException(String.valueOf(i + 1) + " fieldIndex must be an integer value, but it is not.");
                        }
                    } else if (constraints[i] instanceof Field) {
                        Field field = (Field) constraints[i];
                        int colIndex = rowset.getFields().find(field.getName());
                        if (colIndex > 0) {
                            loc.addConstraint(colIndex);
                        } else {
                            throw new RowsetException(String.valueOf(i + 1) + String.format(" field name % not found.", field.getName()));
                        }
                    } else {
                        throw new RowsetException(String.valueOf(i + 1) + " field must be an integer col index or field instance.");
                    }
                }
            } finally {
                loc.endConstrainting();
            }
            if (!loc.isEmpty()) {
                return new ScriptableLocator(loc);
            } else {
                return null;
            }
        }
        return null;
    }

     private static final String CREATE_FILTER_JSDOC = ""
            + "/**\n"
            + "* Creates an instace of filter object to filter rowset data in-place using specified constraints objects.\n"
            + "* @param pairs the search conditions pairs, if a form of key-values pairs, where the key is the property object (e.g. entity.md.propName) and the value for this property\n"
            + "* @return a comparator object\n"
            + "*/";
    
    @ScriptFunction(jsDoc = CREATE_FILTER_JSDOC, params = {"pairs"})
    public ScriptableFilter createFilter(Object... constraints) throws Exception {
        if (constraints != null && constraints.length > 0) {
            Rowset rowset = getRowset();
            Filter hf = rowset.createFilter();
            hf.beginConstrainting();
            try {
                for (int i = 0; i < constraints.length; i++) {
                    if (constraints[i] instanceof Double) {
                        Double d = (Double) constraints[i];
                        if (Math.abs(Math.round(d) - d) < 1e-10) {
                            Long lFieldIndex = Math.round(d);
                            if (Math.abs(lFieldIndex.intValue() - lFieldIndex) == 0) {
                                hf.addConstraint(lFieldIndex.intValue());
                            } else {
                                throw new RowsetException(String.valueOf(i + 1) + " fieldIndex is out of integer value range.");
                            }
                        } else {
                            throw new RowsetException(String.valueOf(i + 1) + " fieldIndex must be an integer value, but it is not.");
                        }
                    } else if (constraints[i] instanceof Field) {
                        Field rsFmd = (Field) constraints[i];
                        int colIndex = rowset.getFields().find(rsFmd.getName());
                        if (colIndex > 0) {
                            hf.addConstraint(colIndex);
                        } else {
                            throw new RowsetException(String.valueOf(i + 1) + " field name not found.");
                        }
                    } else {
                        throw new RowsetException(String.valueOf(i + 1) + " field must be an integer col index or field metadata descriptor.");
                    }
                }
            } finally {
                hf.endConstrainting();
            }
            if (!hf.isEmpty()) {
                entity.setUserFiltering(true);
                if (rowset.getActiveFilter() != null) {
                    rowset.getActiveFilter().cancelFilter();
                }
                hf.build();
                cancelFilter();
                assert !entity.isUserFiltering();
                return new ScriptableFilter(hf);
            } else {
                return null;
            }
        }
        return null;
    }

    private static final String CREATE_SORTER_JSDOC = ""
            + "/**\n"
            + "* Creates an instance of comparator object using specified constraints objects.\n"
            + "* @param pairs the search conditions pairs, in a form of key-values pairs, where the key is the property object (e.g. entity.md.propName) and the value for this property\n"
            + "* @return a comparator object to be passed as a parameter to entity's <code>sort</code> method\n"
            + "*/";
    
    @ScriptFunction(jsDoc = CREATE_SORTER_JSDOC)
    public RowsComparator createSorting(Object... constraints) throws Exception {
        if (constraints != null && constraints.length > 0) {
            Rowset rowset = getRowset();
            if (constraints != null && constraints.length > 0) {
                List<SortingCriterion> criteria = new ArrayList<>();
                for (int i = 0; i < constraints.length; i += 2) {
                    int colIndex = 0;
                    if (constraints[i] instanceof Double) {
                        Double d = (Double) constraints[i];
                        if (Math.abs(Math.round(d) - d) < 1e-10) {
                            Long lFieldIndex = Math.round(d);
                            if (Math.abs(lFieldIndex.intValue() - lFieldIndex) == 0) {
                                colIndex = lFieldIndex.intValue();
                            } else {
                                throw new RowsetException(String.valueOf(i + 1) + " fieldIndex is out of integer value range.");
                            }
                        } else {
                            throw new RowsetException(String.valueOf(i + 1) + " fieldIndex must be an integer value, but it is not.");
                        }
                    } else if (constraints[i] instanceof Field) {
                        Field rsFmd = (Field) constraints[i];
                        colIndex = rowset.getFields().find(rsFmd.getName());
                        if (colIndex <= 0) {
                            throw new RowsetException(String.valueOf(i + 1) + " field name not found.");
                        }
                    } else {
                        throw new RowsetException(String.valueOf(i + 1) + " field must be an integer col index or field metadata descriptor.");
                    }
                    if (colIndex > 0) {
                        boolean ascending = true;
                        if (constraints.length > i + 1) {
                            if (constraints[i + 1] instanceof Boolean) {
                                ascending = (Boolean) constraints[i + 1];
                            } else {
                                throw new RowsetException(String.valueOf(i + 2) + " ascending/descending order argument is invalid. It must be a boolean value");
                            }
                        }
                        criteria.add(new SortingCriterion(colIndex, ascending));
                    } else {
                        throw new RowsetException(String.valueOf(i + 1) + " invalid arguments.");
                    }
                }
                return new RowsComparator(criteria);
            }
        }
        return null;
    }

    private static final String ACTIVE_FILTER_JSDOC = ""
            + "/**\n"
            + "* Entity's active <code>Filter</code> object.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ACTIVE_FILTER_JSDOC)
    public Filter getActiveFilter() throws Exception {
        Rowset rowset = getRowset();
        return rowset.getActiveFilter();
    }

    public E getEntity() {
        return entity;
    }

    protected void cancelFilter() throws Exception {
        Filter hf = getActiveFilter();
        if (hf != null) {
            hf.cancelFilter();
        }
        entity.setUserFiltering(false);
        entity.filterRowset();
    }

    private static final String BEGIN_UPDATE_JSDOC = ""
            + "/**\n"
            + "* Disables automatic model update on parameters change, @see endUpdate method.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = BEGIN_UPDATE_JSDOC)
    public void beginUpdate() {
        if (entity != null) {
            entity.beginUpdate();
        }
    }

    private static final String END_UPDATE_JSDOC = ""
            + "/**\n"
            + "* Enables automatic model update on parameters change, @see beginUpdate method.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = END_UPDATE_JSDOC)
    public void endUpdate() throws Exception {
        if (entity != null) {
            entity.endUpdate();
        }
    }

    public void execute() throws Exception {
        if (entity != null) {
            //checkModelExecuted();
            if (entity.getQuery().isManual()) {
                entity.getQuery().setManual(false);
                try {
                    entity.execute();
                } finally {
                    entity.getQuery().setManual(true);
                }
            } else {
                entity.execute();
            }
        }
    }

    
    public void execute(Function aOnSuccess) throws Exception {
        execute(aOnSuccess, null);
    }

    private static final String EXECUTE_JSDOC = ""
            + "/**\n"
            + "* Refreshes rowset, only if any of its parameters has changed.\n"
            + "* @param onSuccessCallback the handler function for refresh data on success event (optional)\n"
            + "* @param onFailureCallback the handler function for refresh data on failure event (optional)\n"
            + "*/";
    
    @ScriptFunction(jsDoc = EXECUTE_JSDOC, params = {"onSuccessCallback", "onFailureCallback"})
    public void execute(final Function aOnSuccess, final Function aOnFailure) throws Exception {
        if (entity != null) {
            assert tag instanceof RowsetHostObject;
            final RowsetHostObject<E> rowsetFacade = (RowsetHostObject<E>) tag;
            try {
                //checkModelExecuted();
                if (entity.getQuery().isManual()) {
                    entity.getQuery().setManual(false);
                    try {
                        entity.execute();
                    } finally {
                        entity.getQuery().setManual(true);
                    }
                } else {
                    entity.execute();
                }
                if (aOnSuccess != null) {
                    entity.executeScriptEvent(aOnSuccess, new ScriptSourcedEvent(rowsetFacade));
                }
            } catch (final Exception ex) {
                if (aOnFailure != null) {
                    ScriptUtils.inContext(new ScriptUtils.ScriptAction() {
                        @Override
                        public Object run(Context cx) throws Exception {
                            aOnFailure.call(cx, rowsetFacade, rowsetFacade, new Object[]{ex.getMessage()});
                            return null;
                        }
                    });
                } else {
                    throw ex;
                }
            }
        }
    }

    private static final String ENQEUE_UPDATE_JSDOC = ""
            + "/**\n"
            + "* Enqueues DML SQL clause (e.g. UPDATE, DELETE) provided in this entity query.\n"
            + "* Provide query parameters if required. To commit the transaction invoke @see executeUpdate.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ENQEUE_UPDATE_JSDOC)
    public int enqueueUpdate() throws Exception {
        if (entity != null) {
            if (entity.getModel().getClient() instanceof AppClient) {
                ((AppClient) entity.getModel().getClient()).enqueueUpdate(entity.getQueryId(), entity.getQuery().getParameters());
            } else {
                assert entity.getModel().getClient() instanceof DbClient;
                ((DbClient) entity.getModel().getClient()).enqueueUpdate(((SqlQuery) entity.getQuery()).compile());
            }
        }
        return 0;
    }

    private static final String EXECUTE_UPDATE_JSDOC = ""
            + "/**\n"
            + "* Applies the updates into the database and commits the transaction.\n"
            + "* To enqueue updates use @see enqueueUpdate method.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = EXECUTE_UPDATE_JSDOC)
    public int executeUpdate() throws Exception {
        if (entity != null) {
            if (entity.getModel().getClient() instanceof AppClient) {
                ((AppClient) entity.getModel().getClient()).enqueueUpdate(entity.getQueryId(), entity.getQuery().getParameters());
            } else {
                assert entity.getModel().getClient() instanceof DbClient;
                return ((DbClient) entity.getModel().getClient()).executeUpdate(((SqlQuery) entity.getQuery()).compile());
            }
        }
        return 0;
    }

    // Requery interface
    public void requery() throws Exception {
        if (entity != null) {
            //checkModelExecuted();
            if (entity.getQuery().isManual()) {
                entity.getQuery().setManual(false);
                try {
                    entity.refresh();
                } finally {
                    entity.getQuery().setManual(true);
                }
            } else {
                entity.refresh();
            }
        }
    }

    public void requery(Function aOnSuccess) throws Exception {
        requery(aOnSuccess, null);
    }

    private static final String REQUERY_JSDOC = ""
            + "/**\n"
            + "* Requeries the rowset's data. Forses the rowset to refresh its data, no matter if its parameters has changed or not.\n"
            + "* @param onSuccessCallback the handler function for refresh data on success event (optional)\n"
            + "* @param onFailureCallback the handler function for refresh data on failure event (optional)\n"
            + "*/";
    
    @ScriptFunction(jsDoc = REQUERY_JSDOC, params = {"onSuccessCallback", "onFailureCallback"})
    public void requery(final Function aOnSuccess, final Function aOnFailure) throws Exception {
        if (entity != null) {
            assert tag instanceof RowsetHostObject;
            final RowsetHostObject<E> rowsetFacade = (RowsetHostObject<E>) tag;
            try {
                //checkModelExecuted();
                if (entity.getQuery().isManual()) {
                    entity.getQuery().setManual(false);
                    try {
                        entity.refresh();
                    } finally {
                        entity.getQuery().setManual(true);
                    }
                } else {
                    entity.refresh();
                }
                if (aOnSuccess != null) {
                    entity.executeScriptEvent(aOnSuccess, new ScriptSourcedEvent(rowsetFacade));
                }
            } catch (final Exception ex) {
                if (aOnFailure != null) {
                    ScriptUtils.inContext(new ScriptUtils.ScriptAction() {
                        @Override
                        public Object run(Context cx) throws Exception {
                            aOnFailure.call(cx, rowsetFacade, rowsetFacade, new Object[]{ex.getMessage()});
                            return null;
                        }
                    });
                } else {
                    throw ex;
                }
            }
        }
    }

    // modify interface
    
     private static final String INSERT_JSDOC = ""
            + "/**\n"
            + "* Inserts new row in the rowset and sets cursor on this row. @see push\n"
            + "* @param pairs the fields value pairs, in a form of key-values pairs, where the key is the property object (e.g. entity.md.propName) and the value for this property (optional)\n"
            + "*/";
    
    @ScriptFunction(jsDoc = INSERT_JSDOC)
    public void insert(Object... requiedFields) throws Exception {
        Rowset rowset = getRowset();
        if (requiedFields != null) {
            for (int i = 0; i < requiedFields.length; i++) {
                requiedFields[i] = ScriptUtils.js2Java(requiedFields[i]);
            }
        }
        rowset.insert(requiedFields);
    }

    public boolean delete() throws Exception {
        Rowset rowset = getRowset();
        int oldCount = rowset.size();
        if (oldCount > 0) {
            rowset.delete();
        }
        return oldCount != rowset.size();
    }

    private static final String DELETE_ALL_JSDOC = ""
            + "/**\n"
            + "* Deletes all rows in the rowset.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = DELETE_ALL_JSDOC)
    public boolean deleteAll() throws Exception {
        Rowset rowset = getRowset();
        rowset.deleteAll();
        return rowset.isEmpty();
    }

    public Rowset unwrap() throws Exception {
        return getRowset();
    }

    private static final String DELETE_ROW_JSDOC = ""
            + "/**\n"
            + "* Deletes the row on the cursor position.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = DELETE_ROW_JSDOC)
    public boolean deleteRow() throws Exception {
        return delete();
    }

    public boolean deleteAllRows() throws Exception {
        Logger.getLogger(ScriptableRowset.class.getName()).warning("Call of a deprecated method 'deleteAllRows()', use 'deleteAll()' method instead");
        return deleteAll();
    }

    public void insertSingleRow(Object... requiedFields) throws Exception {
        Logger.getLogger(ScriptableRowset.class.getName()).warning("Call of a deprecated method 'insertSingleRow()', use 'insert()' method instead");
        insert(requiedFields);
    }

    public void insertRow(Object... requiedFields) throws Exception {
        Logger.getLogger(ScriptableRowset.class.getName()).warning("Call of a deprecated method 'insertRow()', use 'insert()' method instead");
        insert(requiedFields);
    }

    private static final String INSTANCE_CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* The constructor funciton for the entity's data array elements creation.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = INSTANCE_CONSTRUCTOR_JSDOC)
    public Function getElementConstructor() {
        return entity.getInstanceConstructor();
    }

    @ScriptFunction
    public void setElementConstructor(Function aValue) {
        entity.setInstanceConstructor(aValue);
    }

    private static final String ON_CHANGED_JSDOC = ""
            + "/**\n"
            + "* The handler funciton for the event occured after the entity data change.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ON_CHANGED_JSDOC)
    public Function getOnChanged() {
        return entity.getOnAfterChange();
    }

    @ScriptFunction
    public void setOnChanged(Function aValue) {
        entity.setOnAfterChange(aValue);
    }

    private static final String ON_DELETED_JSDOC = ""
            + "/**\n"
            + "* The handler funciton for the event occured after an entity row has been deleted.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ON_DELETED_JSDOC)
    public Function getOnDeleted() {
        return entity.getOnAfterDelete();
    }

    @ScriptFunction
    public void setOnDeleted(Function aValue) {
        entity.setOnAfterDelete(aValue);
    }

    private static final String ON_INSERTED_JSDOC = ""
            + "/**\n"
            + "* The handler funciton for the event occured after an entity row has been inserted.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ON_INSERTED_JSDOC)
    public Function getOnInserted() {
        return entity.getOnAfterInsert();
    }

    @ScriptFunction
    public void setOnInserted(Function aValue) {
        entity.setOnAfterInsert(aValue);
    }

    private static final String ON_SCROLLED_JSDOC = ""
            + "/**\n"
            + "* The handler funciton for the event occured after the cursor position changed.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ON_SCROLLED_JSDOC)
    public Function getOnScrolled() {
        return entity.getOnAfterScroll();
    }

    @ScriptFunction
    public void setOnScrolled(Function aValue) {
        entity.setOnAfterScroll(aValue);
    }

    private static final String WILL_CHANGE_JSDOC = ""
            + "/**\n"
            + "* The handler funciton for the event occured before the entity data change.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = WILL_CHANGE_JSDOC)
    public Function getWillChange() {
        return entity.getOnBeforeChange();
    }

    @ScriptFunction
    public void setWillChange(Function aValue) {
        entity.setOnBeforeChange(aValue);
    }

    private static final String WILL_DELETE_JSDOC = ""
            + "/**\n"
            + "* The handler funciton for the event occured before an entity row has been deleted.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = WILL_DELETE_JSDOC)
    public Function getWillDelete() {
        return entity.getOnBeforeDelete();
    }

    @ScriptFunction
    public void setWillDelete(Function aValue) {
        entity.setOnBeforeDelete(aValue);
    }

    private static final String WILL_INSERT_JSDOC = ""
            + "/**\n"
            + "* The handler funciton for the event occured before an entity row has been inserted.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = WILL_INSERT_JSDOC)
    public Function getWillInsert() {
        return entity.getOnBeforeInsert();
    }

    @ScriptFunction
    public void setWillInsert(Function aValue) {
        entity.setOnBeforeInsert(aValue);
    }

    private static final String WILL_SCROLL_JSDOC = ""
            + "/**\n"
            + "* The handler funciton for the event occured before the cursor position changed.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = WILL_SCROLL_JSDOC)
    public Function getWillScroll() {
        return entity.getOnBeforeScroll();
    }

    @ScriptFunction
    public void setWillScroll(Function aValue) {
        entity.setOnBeforeScroll(aValue);
    }

    private static final String ON_FILTERED_JSDOC = ""
            + "/**\n"
            + "* The handler funciton for the event occured after the filter have been applied to the entity.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ON_FILTERED_JSDOC)
    public Function getOnFiltered() {
        return entity.getOnFiltered();
    }

    @ScriptFunction
    public void setOnFiltered(Function aValue) {
        entity.setOnFiltered(aValue);
    }

    private static final String ON_REQUIRED_JSDOC = ""
            + "/**\n"
            + "* The handler funciton for the event occured after the entity data have been required.\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ON_REQUIRED_JSDOC)
    public Function getOnRequeried() {
        return entity.getOnRequeried();
    }

    @ScriptFunction
    public void setOnRequeried(Function aValue) {
        entity.setOnRequeried(aValue);
    }

    public Scriptable getTag() {
        return tag;
    }

    public void setTag(Scriptable aValue) {
        tag = aValue;
    }
}
