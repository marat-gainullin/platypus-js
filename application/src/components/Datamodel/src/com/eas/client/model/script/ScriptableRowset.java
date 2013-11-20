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
            if (eRowset != null && eRowset.size() > 0 && (!eRowset.isBeforeFirst() && !eRowset.isAfterLast()) || eRowset.isInserting()) {
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
    @ScriptFunction(jsDoc = "Finds rows using field - field value pairs.")
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

    @ScriptFunction(jsDoc = "Finds row by its key. Key must a single property.")
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

    @ScriptFunction(jsDoc = "Sets rowset cursor to specified row.")
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

    // Rowset scroll interface
    @ScriptFunction(jsDoc = "Moves cursor to the position before the first row.")
    public void beforeFirst() throws Exception {
        getRowset().beforeFirst();
    }

    @ScriptFunction(jsDoc = "Moves cursor to the position after the last row.")
    public void afterLast() throws Exception {
        getRowset().afterLast();
    }

    @ScriptFunction(jsDoc = "Returns true if cursor in the position before the first row.")
    public boolean bof() throws Exception {
        return getRowset().isBeforeFirst();
    }

    @ScriptFunction(jsDoc = "Returns true if cursor in the position before the first row.")
    public boolean eof() throws Exception {
        return getRowset().isAfterLast();
    }

    @ScriptFunction(jsDoc = "Moves cursor to the first row.")
    public boolean first() throws Exception {
        return getRowset().first();
    }

    @ScriptFunction(jsDoc = "Moves cursor to the next row.")
    public boolean next() throws Exception {
        return getRowset().next();
    }

    @ScriptFunction(jsDoc = "Moves cursor to the previous row.")
    public boolean prev() throws Exception {
        return getRowset().previous();
    }

    @ScriptFunction(jsDoc = "Moves cursor to the last row.")
    public boolean last() throws Exception {
        return getRowset().last();
    }

    @ScriptFunction(jsDoc = "Returns true if cursor is on the specified index.")
    public boolean pos(int recordIndex) throws Exception {
        return getRowset().absolute(recordIndex);
    }

    @ScriptFunction(jsDoc = "Gets the row at specified index.")
    public RowHostObject getRow(int aIndex) throws Exception {
        return RowHostObject.publishRow(entity.getModel().getScriptThis(), getRowset().getRow(aIndex), entity);
    }

    @ScriptFunction(jsDoc = "The current cursor position.")
    public int getRowIndex() throws Exception {
        Rowset rowset = getRowset();
        return rowset.getCursorPos();
    }

    @ScriptFunction
    public void setRowIndex(int aRowIndex) throws Exception {
        pos(aRowIndex);
    }

    @ScriptFunction
    public void setSubstitute(ScriptableRowset<E> aSRowset) {
        if (entity != null) {
            entity.setSubstitute(aSRowset != null ? aSRowset.getEntity() : null);
        }
    }

    @ScriptFunction
    public ScriptableRowset<E> getSubstitute() {
        return entity != null && entity.getSubstitute() != null ? entity.getSubstitute().getRowsetWrap().rowset : null;
    }

    @ScriptFunction(jsDoc = "Rowset's size.")
    public int getLength() throws Exception {
        return getSize();
    }

    @ScriptFunction(jsDoc = "Rowset's size.")
    public int getSize() throws Exception {
        Rowset rowset = getRowset();
        return rowset.size();
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
    @ScriptFunction(jsDoc = "Removes the last element from an array and returns that element.")
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

    @ScriptFunction(jsDoc = "Removes the first element from an array and returns that element.")
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

    @ScriptFunction(jsDoc = "Adds one or more elements to the end of an array and returns the new length of the array.")
    public void push(Object... aArguments) throws Exception {
        Rowset rowset = getRowset();
        for (int i = 0; i < aArguments.length; i++) {
            if (aArguments[i] instanceof ScriptableObject) {
                rowset.insertAt(rowset.size() + 1, propsToArray((ScriptableObject) aArguments[i]));
            } else {
                throw new IllegalArgumentException(String.format("Expected Object at index %d as pushed element.", i));
            }
        }
    }

    @ScriptFunction(jsDoc = "Adds one or more elements to the front of an array and returns the new length of the array.")
    public void unshift(Object... aArguments) throws Exception {
        Rowset rowset = getRowset();
        for (int i = aArguments.length - 1; i >= 0; i--) {
            if (aArguments[i] instanceof ScriptableObject) {
                rowset.insertAt(1, propsToArray((ScriptableObject) aArguments[i]));
            } else {
                throw new IllegalArgumentException(String.format("Expected Object at index %d as unshifted element.", i));
            }
        }
    }

    @ScriptFunction(jsDoc = "Reverses the order of the elements of an array - the first becomes the last, and the last becomes the first.")
    public void reverse() throws Exception {
        Rowset rowset = getRowset();
        rowset.reverse();
    }

    @ScriptFunction(jsDoc = "Adds and/or removes elements from an array.")
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

    @ScriptFunction(jsDoc = "Sorts the elements of an array.")
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
    @ScriptFunction(jsDoc = "Returns a new array comprised of this array joined with other array(s) and/or value(s).")
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

    @ScriptFunction(jsDoc = "Joins all elements of an array into a string.")
    public String join(String aSeparator) throws Exception {
        return join(aSeparator, Integer.MAX_VALUE);
    }

    protected String join(String aSeparator, int aMaxSize) throws Exception {
        StringBuilder sb = new StringBuilder();
        Rowset rowset = getRowset();
        int size = rowset.size();
        sb.append("[");
        for (int i = 1; i <= Math.min(aMaxSize, size); i++) {
            if (i > 1) {
                sb.append(", ");
            }
            Row row = rowset.getRow(i);
            RowHostObject rowFacade = RowHostObject.publishRow(entity.getModel().getScriptThis(), row, entity);
            sb.append(rowFacade.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    @ScriptFunction(jsDoc = "Extracts a section of an array and returns a new array.")
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

    @Override
    @ScriptFunction(jsDoc = "Returns a string representing the array and its elements.")
    public String toString() {
        try {
            if (entity != null) {
                if (entity.getRowset() != null) {
                    String res = join(",\n", 100);
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

    @ScriptFunction(jsDoc = "Returns the first (least) index of an element within the array equal to the specified value, or -1 if none is found.")
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

    @ScriptFunction(jsDoc = "Returns the last (greatest) index of an element within the array equal to the specified value, or -1 if none is found.")
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
    @ScriptFunction(jsDoc = "Creates a new array with all of the elements of this array for which the provided filtering function returns true.")
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

    @ScriptFunction(jsDoc = "Calls a function for each element in the array.")
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

    @ScriptFunction(jsDoc = "Returns true if every element in this array satisfies the provided testing function.")
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

    @ScriptFunction(jsDoc = "Creates a new array with the results of calling a provided function on every element in this array.")
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

    @ScriptFunction(jsDoc = "Returns true if at least one element in this array satisfies the provided testing function.")
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

    @ScriptFunction(jsDoc = "Apply a function simultaneously against two values of the array (from left-to-right) as to reduce it to a single value.")
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

    @ScriptFunction(jsDoc = "Apply a function simultaneously against two values of the array (from right-to-left) as to reduce it to a single value.")
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
    @ScriptFunction(jsDoc = "Creates an instace of Locator object using specified constraints objects.")
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

    @ScriptFunction(jsDoc = "Creates an instace of Filter object using specified constraints objects.")
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

    @ScriptFunction(jsDoc = "Creates an instace of comparator object using specified constraints objects.")
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

    @ScriptFunction(jsDoc = "Gets active Filter object.")
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

    @ScriptFunction(jsDoc = "Disables automatic model update on parameters change.")
    public void beginUpdate() {
        if (entity != null) {
            entity.beginUpdate();
        }
    }

    @ScriptFunction(jsDoc = "Enables automatic model update on parameters change.")
    public void endUpdate() throws Exception {
        if (entity != null) {
            entity.endUpdate();
        }
    }

    @ScriptFunction(jsDoc = "Refreshes rowset only if any of its parameters has changed.")
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

    @ScriptFunction(jsDoc = "Refreshes rowset only if any of its parameters has changed with callback.")
    public void execute(Function aOnSuccess) throws Exception {
        execute(aOnSuccess, null);
    }

    @ScriptFunction(jsDoc = "Refreshes rowset only if any of its parameters has changed with callback.")
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

    @ScriptFunction(jsDoc = "Enqueues rowset's changes.")
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

    @ScriptFunction(jsDoc = "Applies a sql clause into the database.")
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

    /*
     @ScriptFunction(jsDoc = "Refreshes children entities.")
     public void executeChildrenOnly() throws Exception {
     if (entity != null) {
     checkModelExecuted();
     Set<Relation<E>> outRels = entity.getOutRelations();
     if (outRels != null) {
     for (Relation<E> rel : outRels) {
     if (rel != null && rel.getRightEntity() != null) {
     E rEnt = rel.getRightEntity();
     rEnt.execute();
     }
     }
     }
     }

     }
     */
    // Requery interface
    @ScriptFunction(jsDoc = "Requeries rowset's data.")
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

    @ScriptFunction(jsDoc = "Requeries rowset's data with a callbacks.")
    public void requery(Function aOnSuccess) throws Exception {
        requery(aOnSuccess, null);
    }

    @ScriptFunction(jsDoc = "Requeries rowset's data with a callbacks.")
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

    /*
     @ScriptFunction(jsDoc = "Requeries children entities.")
     public void requeryChildrenOnly() throws Exception {
     if (entity != null) {
     checkModelExecuted();
     Set<Relation<E>> outRels = entity.getOutRelations();
     if (outRels != null) {
     for (Relation<E> rel : outRels) {
     if (rel != null && rel.getRightEntity() != null) {
     E rEnt = rel.getRightEntity();
     rEnt.refresh();
     }
     }
     }
     }
     }
     */
    // modify interface
    @ScriptFunction(jsDoc = "Inserts new row in the rowset and sets cursor on this row.")
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

    @ScriptFunction(jsDoc = "Deletes all rows in rowset.")
    public boolean deleteAll() throws Exception {
        Rowset rowset = getRowset();
        rowset.deleteAll();
        return rowset.isEmpty();
    }

    /*
     public void save() throws Exception {
     //Rowset rowset = getRowset();
     if (entity.getModel().isCommitable()) {
     entity.getModel().commit();
     }
     }
     */
    public Rowset unwrap() throws Exception {
        return getRowset();
    }

    @ScriptFunction(jsDoc = "Deletes row on cursor position.")
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

    @ScriptFunction(jsDoc = "After object data change event.")
    public Function getOnChanged() {
        return entity.getOnAfterChange();
    }

    @ScriptFunction
    public void setOnChanged(Function aValue) {
        entity.setOnAfterChange(aValue);
    }

    @ScriptFunction(jsDoc = "After delete object event.")
    public Function getOnDeleted() {
        return entity.getOnAfterDelete();
    }

    @ScriptFunction
    public void setOnDeleted(Function aValue) {
        entity.setOnAfterDelete(aValue);
    }

    @ScriptFunction(jsDoc = "After insert object event.")
    public Function getOnInserted() {
        return entity.getOnAfterInsert();
    }

    @ScriptFunction
    public void setOnInserted(Function aValue) {
        entity.setOnAfterInsert(aValue);
    }

    @ScriptFunction(jsDoc = "After cursor position change event.")
    public Function getOnScrolled() {
        return entity.getOnAfterScroll();
    }

    @ScriptFunction
    public void setOnScrolled(Function aValue) {
        entity.setOnAfterScroll(aValue);
    }

    @ScriptFunction(jsDoc = "Before object data change event.")
    public Function getWillChange() {
        return entity.getOnBeforeChange();
    }

    @ScriptFunction
    public void setWillChange(Function aValue) {
        entity.setOnBeforeChange(aValue);
    }

    @ScriptFunction(jsDoc = "Before delete object event.")
    public Function getWillDelete() {
        return entity.getOnBeforeDelete();
    }

    @ScriptFunction
    public void setWillDelete(Function aValue) {
        entity.setOnBeforeDelete(aValue);
    }

    @ScriptFunction(jsDoc = "Before insert object event.")
    public Function getWillInsert() {
        return entity.getOnBeforeInsert();
    }

    @ScriptFunction
    public void setWillInsert(Function aValue) {
        entity.setOnBeforeInsert(aValue);
    }

    @ScriptFunction(jsDoc = "Before cursor position change event.")
    public Function getWillScroll() {
        return entity.getOnBeforeScroll();
    }

    @ScriptFunction
    public void setWillScroll(Function aValue) {
        entity.setOnBeforeScroll(aValue);
    }

    @ScriptFunction(jsDoc = "After filter event.")
    public Function getOnFiltered() {
        return entity.getOnFiltered();
    }

    @ScriptFunction
    public void setOnFiltered(Function aValue) {
        entity.setOnFiltered(aValue);
    }

    @ScriptFunction(jsDoc = "After requery event.")
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
