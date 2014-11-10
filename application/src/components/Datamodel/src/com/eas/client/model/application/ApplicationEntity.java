/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.DelegatingFlowProvider;
import com.bearsoft.rowset.events.*;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.filters.Filter;
import com.bearsoft.rowset.locators.Locator;
import com.bearsoft.rowset.locators.RowWrap;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.ordering.HashOrderer.TaggedList;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import com.bearsoft.rowset.utils.KeySet;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.SQLUtils;
import com.eas.client.events.PublishedSourcedEvent;
import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import com.eas.client.queries.Query;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.EventMethod;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptUtils;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 * @param <M>
 * @param <Q>
 * @param <E>
 */
public abstract class ApplicationEntity<M extends ApplicationModel<E, Q>, Q extends Query, E extends ApplicationEntity<M, Q, E>> extends Entity<M, Q, E> implements HasPublished, RowsetListener {

    public static final String BAD_FIELD_NAME_MSG = "Bad field name %s";
    public static final String BAD_FIND_AGRUMENTS_MSG = "Bad find agruments";
    public static final String BAD_FIND_ARGUMENT_MSG = "Argument at index %d must be a rowset's field.";
    public static final String BAD_PRIMARY_KEYS_MSG = "Bad primary keys detected. Required one and only one primary key field, but %d found.";
    public static final String CANT_CONVERT_TO_MSG = "Can't convert to %s, substituting with null.";
    // for runtime
    protected JSObject onBeforeChange;
    protected JSObject onAfterChange;
    protected JSObject onBeforeScroll;
    protected JSObject onAfterScroll;
    protected JSObject onBeforeInsert;
    protected JSObject onAfterInsert;
    protected JSObject onBeforeDelete;
    protected JSObject onAfterDelete;
    protected JSObject onRequeried;
    protected JSObject onFiltered;
    //
    protected Object published;
    protected transient List<Integer> filterConstraints = new ArrayList<>();
    protected transient Rowset rowset;
    protected boolean valid;
    protected Future<Void> pending;
    protected transient Filter rowsetFilter;
    protected transient boolean userFiltering;
    protected Map<List<Integer>, Locator> userLocators = new HashMap<>();
    // to preserve relation order
    protected transient List<Relation<E>> rtInFilterRelations;
    protected transient int updatingCounter = 0;
    protected E substitute;

    public ApplicationEntity() {
        super();
    }

    public ApplicationEntity(M aModel) {
        super(aModel);
    }

    public ApplicationEntity(String aEntityId) {
        super(aEntityId);
    }

    public boolean isValid() {
        return valid;
    }

    public void invalidate() {
        valid = false;
    }

    protected class RowsetRefreshTask implements Future<Void> {

        protected boolean cancelled;
        protected Consumer<Exception> onCancel;

        public RowsetRefreshTask(Consumer<Exception> aOnCancel) {
            super();
            onCancel = aOnCancel;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            cancelled = true;
            valid = true;
            pending = null;
            Exception ex = new InterruptedException("Canceled");
            model.terminateProcess((E) ApplicationEntity.this, ex);
            if (onCancel != null) {
                onCancel.accept(ex);
            }
            return cancelled;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public Void get() throws InterruptedException, ExecutionException {
            return null;
        }

        @Override
        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }

    public boolean isPending() {
        return pending != null;
    }

    public void unpend() {
        if (pending != null) {
            pending.cancel(true);
            pending = null;
        }
    }

    protected void silentUnpend() {
        ApplicationModel.RequeryProcess<E, Q> p = model.process;
        model.process = null;
        try {
            unpend();
        } finally {
            model.process = p;
        }
    }

    private Locator checkUserLocator(List<Integer> constraints) throws IllegalStateException {
        Locator loc = userLocators.get(constraints);
        if (loc == null) {
            loc = rowset.createLocator();
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

    // Find and positioning interface
    private static final String FIND_JSDOC = ""
            + "/**\n"
            + "* Finds rows using field - value pairs.\n"
            + "* @param pairs the search conditions pairs, if a form of key-values pairs, where the key is the property object (e.g. entity.schema.propName or just a prop name in a string form) and the value for this property.\n"
            + "* @return the rows object's array accordind to the search condition or empty array if nothing is found.\n"
            + "*/";

    protected List<Row> emptyFoundResults = new TaggedList<>();

    @ScriptFunction(jsDoc = FIND_JSDOC, params = {"pairs"})
    public List<Row> find(Object... values) throws Exception {
        if (values != null) {
            values = ScriptUtils.jsObjectToCriteria(values);
            if (values.length > 0 && values.length % 2 == 0) {
                Fields fields = rowset.getFields();
                Converter converter = rowset.getConverter();
                List<Integer> constraints = new ArrayList<>();
                List<Object> keyValues = new ArrayList<>();
                for (int i = 0; i < values.length - 1; i += 2) {
                    if (values[i] != null && (values[i] instanceof Number || values[i] instanceof Field || values[i] instanceof String)) {
                        Field field = values[i] instanceof Number ? fields.get(((Number) values[i]).intValue()) : (values[i] instanceof Field ? (Field) values[i] : fields.get((String) values[i]));
                        int colIndex = fields.find(field.getName());
                        if (colIndex > 0) {
                            constraints.add(colIndex);
                        } else {
                            Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, String.format(BAD_FIELD_NAME_MSG, field.getName()));
                        }
                        Object keyValue = ScriptUtils.toJava(values[i + 1]);
                        if (converter != null) {
                            try {
                                keyValue = converter.convert2RowsetCompatible(keyValue, field.getTypeInfo());
                            } catch (Exception ex) {
                                Logger.getLogger(ApplicationEntity.class.getName()).warning(String.format(CANT_CONVERT_TO_MSG, field.getTypeInfo().getSqlTypeName()));
                                keyValue = null;
                            }
                        }
                        keyValues.add(keyValue);
                    } else {
                        Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, String.format(BAD_FIND_ARGUMENT_MSG, i));
                    }
                }
                if (!constraints.isEmpty() && constraints.size() == keyValues.size()) {
                    Locator loc = checkUserLocator(constraints);
                    if (loc.find(keyValues.toArray())) {
                        TaggedList<RowWrap> subSet = loc.getSubSet();
                        if (subSet.tag == null) {
                            List<Row> found = new TaggedList<>();
                            subSet.stream().forEach((rw) -> {
                                found.add(rw.getRow());
                            });
                            subSet.tag = found;
                        }
                        return (TaggedList<Row>) subSet.tag;
                    } else {
                        return emptyFoundResults;
                    }
                }
            } else {
                Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, BAD_FIND_AGRUMENTS_MSG);
            }
        } else {
            Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, BAD_FIND_AGRUMENTS_MSG);
        }
        return null;
    }

    private static final String FIND_BY_ID_JSDOC = ""
            + "/**\n"
            + "* Finds row by its key. Key must a single property.\n"
            + "* @param key the unique identifier of the row.\n"
            + "* @return a row object or <code>null</code> if nothing is found.\n"
            + "*/";

    @ScriptFunction(jsDoc = FIND_BY_ID_JSDOC, params = {"key"})
    public Row findById(Object aValue) throws Exception {
        Rowset rs = getRowset();
        Fields fields = rs.getFields();
        List<Field> pks = fields.getPrimaryKeys();
        if (pks.size() == 1) {
            List<Row> res = find(pks.get(0), aValue);
            if (res != null && !res.isEmpty()) {
                return res.get(0);
            } else {
                return null;
            }
        } else {
            Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, String.format(BAD_PRIMARY_KEYS_MSG, pks.size()));
        }
        return null;
    }
    private static final String SCROLL_TO_JSDOC = ""
            + "/**\n"
            + "* Sets the array cursor to the specified object.\n"
            + "* @param object the object to position the entity cursor on.\n"
            + "* @return <code>true</code> if the cursor changed successfully and <code>false</code> otherwise.\n"
            + "*/";

    @ScriptFunction(jsDoc = SCROLL_TO_JSDOC, params = {"row"})
    public boolean scrollTo(Row aObject) throws Exception {
        if (aObject != null) {
            List<Integer> constraints = aObject.getFields().getPrimaryKeysIndicies();
            Locator loc = checkUserLocator(constraints);
            List<Object> values = new ArrayList<>();
            for (Integer colIndx : constraints) {
                values.add(aObject.getColumnObject(colIndx));
            }
            return loc.find(values.toArray()) && loc.first();
        } else {
            return false;
        }
    }

    // Rowset scroll interface
    private static final String BEFORE_FIRST_JSDOC = ""
            + "/**\n"
            + "* Moves the rowset cursor to the position before the first row.\n"
            + "*/";

    /*
     @ScriptFunction(jsDoc = BEFORE_FIRST_JSDOC)
     public void beforeFirst() throws Exception {
     rowset.beforeFirst();
     }
     */
    private static final String AFTER_LAST_JSDOC = ""
            + "/**\n"
            + "* Moves the rowset cursor to the position after the last row.\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = AFTER_LAST_JSDOC)
     public void afterLast() throws Exception {
     rowset.afterLast();
     }
     */
    private static final String BOF_JSDOC = ""
            + "/**\n"
            + "* Checks if cursor in the position before the first row.\n"
            + "* @return <code>true</code> if cursor in the position before the first row and <code>false</code> otherwise.\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = BOF_JSDOC)
     public boolean bof() throws Exception {
     return rowset.isBeforeFirst();
     }
     */
    private static final String EOF_JSDOC = ""
            + "/**\n"
            + "* Checks if cursor in the position before the first row.\n"
            + "* @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise.\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = EOF_JSDOC)
     public boolean eof() throws Exception {
     return rowset.isAfterLast();
     }
     */
    private static final String FIRST_JSDOC = ""
            + "/**\n"
            + "* Moves the rowset cursor to the first row.\n"
            + "* @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise.\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = FIRST_JSDOC)
     public boolean first() throws Exception {
     return rowset.first();
     }
     */
    private static final String NEXT_JSDOC = ""
            + "/**\n"
            + "* Moves the rowset cursor to the next row.\n"
            + "* @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise.\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = NEXT_JSDOC)
     public boolean next() throws Exception {
     return rowset.next();
     }
     */
    private static final String PREV_JSDOC = ""
            + "/**\n"
            + "* Moves the rowset cursor to the privious row.\n"
            + "* @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise.\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = PREV_JSDOC)
     public boolean prev() throws Exception {
     return rowset.previous();
     }
     */
    private static final String LAST_JSDOC = ""
            + "/**\n"
            + "* Moves the rowset cursor to the last row.\n"
            + "* @return <code>true</code> if cursor moved successfully and <code>false</code> otherwise.\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = LAST_JSDOC)
     public boolean last() throws Exception {
     return rowset.last();
     }
     */
    private static final String POS_JSDOC = ""
            + "/**\n"
            + "* Positions the rowset cursor on the specified row number. Row number is 1-based.\n"
            + "* @param index the row index to check, starting form <code>1</code>.\n"
            + "* @return <code>true</code> if the cursor is on the row with specified index and <code>false</code> otherwise.\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = POS_JSDOC, params = {"index"})
     public boolean pos(int recordIndex) throws Exception {
     return rowset.absolute(recordIndex);
     }
     */
    private static final String GET_ROW_JSDOC = ""
            + "/**\n"
            + "* Gets the row at specified index.\n"
            + "* @param index the row index, starting form <code>1</code>.\n"
            + "* @return the row object or <code>null</code> if no row object have found at the specified index.\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = GET_ROW_JSDOC, params = {"index"})
     public Row getRow(int aIndex) throws Exception {
     return rowset.getRow(aIndex);
     }
     */

    private static final String CURSOR_JSDOC = ""
            + "/**\n"
            + "* Gets the row at cursor position.\n"
            + "* @return the row object or <code>null</code> if cursor is before first or after last position.\n"
            + "*/";

    @ScriptFunction(jsDoc = CURSOR_JSDOC)
    public Row getCursor() throws Exception {
        return rowset.getCurrentRow();
    }

    private static final String EMPTY_JSDOC = ""
            + "/**\n"
            + "* Checks if the rowset is empty.\n"
            + "* @return <code>true</code> if the rowset is empty and <code>false</code> otherwise.\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = EMPTY_JSDOC)
     public boolean isEmpty() throws Exception {
     return rowset.isEmpty();
     }
     */

    private static final String SIZE_JSDOC = ""
            + "/**\n"
            + "* The rowset size.\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = SIZE_JSDOC)
     public int getSize() throws Exception {
     return rowset.size();
     }
     */

    private static final String CURSOR_POS_JSDOC = ""
            + "/**\n"
            + "* Current position of cursor (1-based). There are two special values: 0 - before first; length + 1 - after last;\n"
            + "*/";

    @ScriptFunction(jsDoc = CURSOR_POS_JSDOC)
    public int getCursorPos() {
        return rowset.getCursorPos();
    }

    @ScriptFunction()
    public void setCursorPos(int aValue) throws InvalidCursorPositionException {
        rowset.absolute(aValue);
    }

    // Table-pattern API
    public Locator createLocator(Object... constraints) throws Exception {
        if (constraints != null && constraints.length > 0) {
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
                            throw new RowsetException(String.format(" field name %s not found.", field.getName()));
                        }
                    } else if (constraints[i] instanceof String) {
                        int colIndex = rowset.getFields().find((String) constraints[i]);
                        if (colIndex > 0) {
                            loc.addConstraint(colIndex);
                        } else {
                            throw new RowsetException(String.format(" field name %s not found.", (String) constraints[i]));
                        }
                    } else {
                        throw new RowsetException(String.valueOf(i + 1) + " field must be an integer col index or a field name or field instance.");
                    }
                }
            } finally {
                loc.endConstrainting();
            }
            if (!loc.isEmpty()) {
                return loc;
            } else {
                return null;
            }
        }
        return null;
    }
    private static final String CREATE_FILTER_JSDOC = ""
            + "/**\n"
            + "* Creates an instace of filter object to filter rowset data in-place using specified constraints objects.\n"
            + "* @param fields The filter conditions fields in following form: entity.schema.propName or just a propName in a string form.\n"
            + "* @return a comparator object.\n"
            + "*/";

    @ScriptFunction(jsDoc = CREATE_FILTER_JSDOC, params = {"fields"})
    public Filter createFilter(Object... constraints) throws Exception {
        if (constraints != null && constraints.length > 0) {
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
                        Field field = (Field) constraints[i];
                        int colIndex = rowset.getFields().find(field.getName());
                        if (colIndex > 0) {
                            hf.addConstraint(colIndex);
                        } else {
                            throw new RowsetException(field.getName() + " field name not found.");
                        }
                    } else if (constraints[i] instanceof String) {
                        int colIndex = rowset.getFields().find((String) constraints[i]);
                        if (colIndex > 0) {
                            hf.addConstraint(colIndex);
                        } else {
                            throw new RowsetException((String) constraints[i] + " field name not found.");
                        }
                    } else {
                        throw new RowsetException(String.valueOf(i + 1) + " field must be an integer col index or a field name or field metadata descriptor.");
                    }
                }
            } finally {
                hf.endConstrainting();
            }
            if (!hf.isEmpty()) {
                hf.build();
                return hf;
            } else {
                return null;
            }
        }
        return null;
    }
    private static final String CREATE_SORTER_JSDOC = ""
            + "/**\n"
            + "* Creates an instance of comparator object using specified constraints objects.\n"
            + "* @param pairs the sort criteria pairs, in a form of property object (e.g. entity.schema.propName or just a propName in a string form) and the order of sort (ascending - true; descending - false).\n"
            + "* @return a comparator object to be passed as a parameter to entity's <code>sort</code> method.\n"
            + "*/";

    @ScriptFunction(jsDoc = CREATE_SORTER_JSDOC, params = {"pairs"})
    public RowsComparator createSorting(Object... constraints) throws Exception {
        if (constraints != null && constraints.length > 0) {
            constraints = ScriptUtils.jsObjectToCriteria(constraints);
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
                    Field field = (Field) constraints[i];
                    colIndex = rowset.getFields().find(field.getName());
                    if (colIndex <= 0) {
                        throw new RowsetException(field.getName() + " field name not found.");
                    }
                } else if (constraints[i] instanceof String) {
                    colIndex = rowset.getFields().find((String) constraints[i]);
                    if (colIndex <= 0) {
                        throw new RowsetException((String) constraints[i] + " field name not found.");
                    }
                } else {
                    throw new RowsetException(String.valueOf(i + 1) + " field must be an integer col index or a field name or field metadata descriptor.");
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
        return null;
    }

    private static final String SORT_JSDOC = ""
            + "/**\n"
            + "* Sorts data according to comparator object returned by createSorting() or by comparator function.\n"
            + "* @param comparator A comparator function or object returned from createSorting() method.\n"
            + "*/";

    @ScriptFunction(jsDoc = SORT_JSDOC, params = {"comparator"})
    public void sort(RowsComparator aComparator) throws InvalidCursorPositionException {
        rowset.sort(aComparator);
    }

    private static final String ACTIVE_FILTER_JSDOC = ""
            + "/**\n"
            + "* Entity's active <code>Filter</code> object.\n"
            + "*/";

    @ScriptFunction(jsDoc = ACTIVE_FILTER_JSDOC)
    public Filter getActiveFilter() throws Exception {
        return rowset.getActiveFilter();
    }

    public void execute() throws Exception {
        execute(null, null);
    }

    public void execute(Consumer<Void> aOnSuccess) throws Exception {
        execute(aOnSuccess, null);
    }
    private static final String EXECUTE_JSDOC = ""
            + "/**\n"
            + "* Refreshes entity, only if any of its parameters has changed.\n"
            + "* @param onSuccess The handler function for refresh data on success event (optional).\n"
            + "* @param onFailure The handler function for refresh data on failure event (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = EXECUTE_JSDOC, params = {"onSuccess", "onFailure"})
    public void execute(final Consumer<Void> aOnSuccess, final Consumer<Exception> aOnFailure) throws Exception {
        internalExecute(aOnSuccess, aOnFailure);
    }

    // Requery interface
    public void requery() throws Exception {
        requery(null, null);
    }

    public void requery(JSObject aOnSuccess) throws Exception {
        requery(aOnSuccess, null);
    }
    private static final String REQUERY_JSDOC = ""
            + "/**\n"
            + "* Requeries the entity's data. Forses the entity to refresh its data, no matter if its parameters has changed or not.\n"
            + "* @param onSuccess The callback function for refresh data on success event (optional).\n"
            + "* @param onFailure The callback function for refresh data on failure event (optional).\n"
            + "*/";

    @ScriptFunction(jsDoc = REQUERY_JSDOC, params = {"onSuccess", "onFailure"})
    public void requery(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        invalidate();
        internalExecute(aOnSuccess != null ? (Void v) -> {
            aOnSuccess.call(null, new Object[]{});
        } : null, aOnFailure != null ? (Exception ex) -> {
            aOnFailure.call(null, new Object[]{ex.getMessage()});
        } : null);
    }

    // modify interface
    private static final String INSERT_JSDOC = ""
            + "/**\n"
            + "* Inserts new row in the rowset and sets cursor on this row. @see push.\n"
            + "* @param pairs the fields value pairs, in a form of key-values pairs, where the key is the property object (e.g. entity.schema.propName) and the value for this property (optional).\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = INSERT_JSDOC, params = {"pairs"})
     public void insert(Object... requiredFields) throws Exception {
     if (requiredFields != null) {
     for (int i = 0; i < requiredFields.length; i++) {
     requiredFields[i] = ScriptUtils.toJava(requiredFields[i]);
     }
     }
     rowset.insert(requiredFields);
     }
     */

    private static final String INSERT_AT_JSDOC = ""
            + "/**\n"
            + "* Inserts new row in the rowset and sets cursor on this row. @see push.\n"
            + "* @param index The new row will be inserted at. 1 - based.\n"
            + "* @param pairs The fields value pairs, in a form of key-values pairs, where the key is the property object (e.g. entity.schema.propName) and the value for this property.\n"
            + "*/";
    /*
     @ScriptFunction(jsDoc = INSERT_AT_JSDOC, params = {"index", "pairs"})
     public void insertAt(int aIndex, Object... requiredFields) throws Exception {
     if (requiredFields != null) {
     for (int i = 0; i < requiredFields.length; i++) {
     requiredFields[i] = ScriptUtils.toJava(requiredFields[i]);
     }
     }
     rowset.insertAt(aIndex, false, requiredFields);
     }
     */

    public boolean delete() throws Exception {
        int oldCount = rowset.size();
        if (oldCount > 0) {
            rowset.delete();
        }
        return oldCount != rowset.size();
    }
    private static final String REMOVE_ALL_JSDOC = ""
            + "/**\n"
            + "* Deletes all rows in the rowset.\n"
            + "*/";

    @ScriptFunction(jsDoc = REMOVE_ALL_JSDOC)
    public boolean removeAll() throws Exception {
        rowset.deleteAll();
        return rowset.isEmpty();
    }

    private static final String REMOVE_JSDOC = ""
            + "/**\n"
            + " * Deletes a object by cursor position or by object itself.\n"
            + " * @param aCursorPosOrInstance Object position in terms of cursor API (1-based)"
            + "| object instance itself. Note! If no cursor position or instance is passed,"
            + "then object at current cursor position will be deleted.\n"
            + " */";

    @ScriptFunction(jsDoc = REMOVE_JSDOC, params = {"aCursorPosOrInstance"})
    public boolean remove(Object aCursorPosOrInstance) throws Exception {
        if (aCursorPosOrInstance instanceof Row) {
            return deleteRow((Row) aCursorPosOrInstance);
        } else if (aCursorPosOrInstance instanceof Number) {
            return deleteRow(((Number) aCursorPosOrInstance).intValue());
        } else {
            return delete();
        }
    }

    public boolean deleteRow(int aCursorIndex) throws Exception {
        if (aCursorIndex >= 1 && aCursorIndex <= rowset.size()) {
            rowset.deleteAt(aCursorIndex);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteRow(Row aRow) throws Exception {
        if (aRow != null) {
            int oldSize = rowset.size();
            rowset.delete(Collections.singleton(aRow));
            int newSize = rowset.size();
            return oldSize > newSize;
        } else {
            return false;
        }
    }

    private static final String INSTANCE_CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + "* Experimental. The constructor funciton for the entity's data array elements.\n"
            + "*/";

    @ScriptFunction(jsDoc = INSTANCE_CONSTRUCTOR_JSDOC)
    public JSObject getElementClass() {
        return rowset.getFields().getInstanceConstructor();
    }

    @ScriptFunction
    public void setElementClass(JSObject aValue) {
        rowset.getFields().setInstanceConstructor(aValue);
    }

    private static final String ON_CHANGED_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured after the entity data change.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_CHANGED_JSDOC)
    @EventMethod(eventClass = EntityInstanceChangeEvent.class)
    public JSObject getOnChanged() {
        return getOnAfterChange();
    }

    @ScriptFunction
    public void setOnChanged(JSObject aValue) {
        setOnAfterChange(aValue);
    }
    private static final String ON_DELETED_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured after an entity row has been deleted.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_DELETED_JSDOC)
    @EventMethod(eventClass = EntityInstanceDeleteEvent.class)
    public JSObject getOnDeleted() {
        return getOnAfterDelete();
    }

    @ScriptFunction
    public void setOnDeleted(JSObject aValue) {
        setOnAfterDelete(aValue);
    }
    private static final String ON_INSERTED_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured after an entity row has been inserted.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_INSERTED_JSDOC)
    @EventMethod(eventClass = EntityInstanceInsertEvent.class)
    public JSObject getOnInserted() {
        return getOnAfterInsert();
    }

    @ScriptFunction
    public void setOnInserted(JSObject aValue) {
        setOnAfterInsert(aValue);
    }
    private static final String ON_SCROLLED_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured after the cursor position changed.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_SCROLLED_JSDOC)
    @EventMethod(eventClass = CursorPositionChangedEvent.class)
    public JSObject getOnScrolled() {
        return getOnAfterScroll();
    }

    @ScriptFunction
    public void setOnScrolled(JSObject aValue) {
        setOnAfterScroll(aValue);
    }
    private static final String WILL_CHANGE_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured before the entity data change.\n"
            + "*/";

    @ScriptFunction(jsDoc = WILL_CHANGE_JSDOC)
    @EventMethod(eventClass = EntityInstanceChangeEvent.class)
    public JSObject getWillChange() {
        return getOnBeforeChange();
    }

    @ScriptFunction
    public void setWillChange(JSObject aValue) {
        setOnBeforeChange(aValue);
    }
    private static final String WILL_DELETE_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured before an entity row has been deleted.\n"
            + "*/";

    @ScriptFunction(jsDoc = WILL_DELETE_JSDOC)
    @EventMethod(eventClass = EntityInstanceDeleteEvent.class)
    public JSObject getWillDelete() {
        return getOnBeforeDelete();
    }

    @ScriptFunction
    public void setWillDelete(JSObject aValue) {
        setOnBeforeDelete(aValue);
    }
    private static final String WILL_INSERT_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured before an entity row has been inserted.\n"
            + "*/";

    @ScriptFunction(jsDoc = WILL_INSERT_JSDOC)
    @EventMethod(eventClass = EntityInstanceInsertEvent.class)
    public JSObject getWillInsert() {
        return getOnBeforeInsert();
    }

    @ScriptFunction
    public void setWillInsert(JSObject aValue) {
        setOnBeforeInsert(aValue);
    }
    private static final String WILL_SCROLL_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured before the cursor position changed.\n"
            + "*/";

    @ScriptFunction(jsDoc = WILL_SCROLL_JSDOC)
    @EventMethod(eventClass = CursorPositionWillChangeEvent.class)
    public JSObject getWillScroll() {
        return getOnBeforeScroll();
    }

    @ScriptFunction
    public void setWillScroll(JSObject aValue) {
        setOnBeforeScroll(aValue);
    }

    public void putOrmDefinition(String aName, JSObject aDefinition) {
        if (aName != null && !aName.isEmpty() && aDefinition != null) {
            Map<String, Object> defs = rowset.getFields().getOrmDefinitions();
            if (!defs.containsKey(aName)) {
                rowset.getFields().putOrmDefinition(aName, aDefinition);
            } else {
                Logger.getLogger(ApplicationEntity.class.getName()).log(Level.FINE, String.format("ORM property %s redefinition attempt on entity %s %s.", aName, name != null && !name.isEmpty() ? name : "", title != null && !title.isEmpty() ? "[" + title + "]" : ""));
            }
        }
    }

    public Map<String, Object> getOrmDefinitions() {
        return rowset.getFields().getOrmDefinitions();
    }

    @Override
    public void setPublished(Object aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    /**
     * Gets cursor substitute.
     *
     * @return Cursor substitute entity.
     */
    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Returns cursor-substitute entity.\n"
            + " * Sunstitute's cursor is used when in original entity's cursor some field's value is null.\n"
            + " */")
    /*
     public E getSubstitute() {
     return substitute;
     }
     */

    /**
     * Sets cursor substitute. Use this function carefully. Circular references
     * may occur
     *
     * @param aValue Cursor substitute entity to be set.
     */
    /*
     @ScriptFunction
     public void setSubstitute(E aValue) {
     if (aValue != this) {
     substitute = aValue;
     }
     }

     public Object getSubstituteRowsetObject(String aFieldName) throws Exception {
     E lsubstitute = substitute;
     while (lsubstitute != null) {
     Rowset sRowset = lsubstitute.getRowset();
     if (sRowset != null && !sRowset.isBeforeFirst() && !sRowset.isAfterLast()) {
     Object value = sRowset.getObject(sRowset.getFields().find(aFieldName));
     if (value != null) {
     return value;
     }
     }
     lsubstitute = lsubstitute.getSubstitute();
     }
     return null;
     }
     */
    /**
     * Returns change log for this entity. In some cases, we might have several
     * change logs in one model. Several databases is the case.
     *
     * @throws java.lang.Exception
     * @return
     */
    protected abstract List<Change> getChangeLog() throws Exception;

    public JSObject getOnAfterChange() {
        return onAfterChange;
    }

    public JSObject getOnAfterDelete() {
        return onAfterDelete;
    }

    public JSObject getOnAfterInsert() {
        return onAfterInsert;
    }

    public JSObject getOnAfterScroll() {
        return onAfterScroll;
    }

    public JSObject getOnBeforeChange() {
        return onBeforeChange;
    }

    public JSObject getOnBeforeDelete() {
        return onBeforeDelete;
    }

    public JSObject getOnBeforeInsert() {
        return onBeforeInsert;
    }

    public JSObject getOnBeforeScroll() {
        return onBeforeScroll;
    }

    private static final String ON_FILTERED_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured after the entity's data have been filtered.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_FILTERED_JSDOC)
    @EventMethod(eventClass = PublishedSourcedEvent.class)
    public JSObject getOnFiltered() {
        return onFiltered;
    }

    private static final String ON_REQUIRED_JSDOC = ""
            + "/**\n"
            + "* The handler function for the event occured after the entity's data have been requeried.\n"
            + "*/";

    @ScriptFunction(jsDoc = ON_REQUIRED_JSDOC)
    @EventMethod(eventClass = PublishedSourcedEvent.class)
    public JSObject getOnRequeried() {
        return onRequeried;
    }

    public void setOnAfterChange(JSObject aValue) {
        JSObject oldValue = onAfterChange;
        onAfterChange = aValue;
        changeSupport.firePropertyChange("onAfterChange", oldValue, aValue);
    }

    public void setOnAfterDelete(JSObject aValue) {
        JSObject oldValue = onAfterDelete;
        onAfterDelete = aValue;
        changeSupport.firePropertyChange("onAfterDelete", oldValue, aValue);
    }

    public void setOnAfterInsert(JSObject aValue) {
        JSObject oldValue = onAfterInsert;
        onAfterInsert = aValue;
        changeSupport.firePropertyChange("onAfterInsert", oldValue, aValue);
    }

    public void setOnAfterScroll(JSObject aValue) {
        JSObject oldValue = onAfterScroll;
        onAfterScroll = aValue;
        changeSupport.firePropertyChange("onAfterScroll", oldValue, aValue);
    }

    public void setOnBeforeChange(JSObject aValue) {
        JSObject oldValue = onBeforeChange;
        onBeforeChange = aValue;
        changeSupport.firePropertyChange("onBeforeChange", oldValue, aValue);
    }

    public void setOnBeforeDelete(JSObject aValue) {
        JSObject oldValue = onBeforeDelete;
        onBeforeDelete = aValue;
        changeSupport.firePropertyChange("onBeforeDelete", oldValue, aValue);
    }

    public void setOnBeforeInsert(JSObject aValue) {
        JSObject oldValue = onBeforeInsert;
        onBeforeInsert = aValue;
        changeSupport.firePropertyChange("onBeforeInsert", oldValue, aValue);
    }

    public void setOnBeforeScroll(JSObject aValue) {
        JSObject oldValue = onBeforeScroll;
        onBeforeScroll = aValue;
        changeSupport.firePropertyChange("onBeforeScroll", oldValue, aValue);
    }

    @ScriptFunction
    @EventMethod(eventClass = PublishedSourcedEvent.class)
    public void setOnFiltered(JSObject aValue) {
        JSObject oldValue = onFiltered;
        onFiltered = aValue;
        changeSupport.firePropertyChange("onFiltered", oldValue, aValue);
    }

    @ScriptFunction
    public void setOnRequeried(JSObject aValue) {
        JSObject oldValue = onRequeried;
        onRequeried = aValue;
        changeSupport.firePropertyChange("onRequeried", oldValue, aValue);
    }

    private void silentFirst() throws InvalidCursorPositionException {
        rowset.removeRowsetListener(this);
        try {
            rowset.first();
        } finally {
            rowset.addRowsetListener(this);
        }
    }

    public abstract void enqueueUpdate() throws Exception;

    public boolean isRowsetPresent() {
        return rowset != null;
    }

    public Rowset getRowset() throws Exception {
        return rowset;
    }

    protected void internalExecute(final Consumer<Void> aOnSuccess, final Consumer<Exception> aOnFailure) throws Exception {
        if (query == null) {
            throw new IllegalStateException("Query must present. Query name: " + queryName + "; tableName: " + getTableNameForDescription());
        }
        bindQueryParameters();
        if (isValid()) {
            // Since we have no onRequeried event, we have to filter manually here.
            assert rowset != null;
            assert pending == null;
            filterRowset();
            silentFirst();
            if (aOnSuccess != null) {
                aOnSuccess.accept(null);
            }
        } else {
            // Requery if query parameters values have been changed while bindQueryParameters() call
            // or we are forced to refresh the data via requery() call.
            uninstallUserFiltering();
            silentUnpend();
            refreshRowset(aOnSuccess, aOnFailure);
            assert rowset != null;
            assert pending != null || (aOnSuccess == null && model.process == null);
            // filtering will be done while processing onRequeried event in ApplicationEntity code
        }
    }

    protected void unforwardChangeLog() {
        if (rowset != null && rowset.getFlowProvider() instanceof DelegatingFlowProvider) {
            DelegatingFlowProvider dfp = (DelegatingFlowProvider) rowset.getFlowProvider();
            rowset.setFlowProvider(dfp.getDelegate());
        }
    }

    protected void forwardChangeLog() {
        rowset.setFlowProvider(new DelegatingFlowProvider(rowset.getFlowProvider()) {
            @Override
            public List<Change> getChangeLog() {
                try {
                    return ApplicationEntity.this.getChangeLog();
                } catch (Exception ex) {
                    Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }
        });
    }

    protected void uninstallUserFiltering() throws RowsetException {
        if (userFiltering && rowset != null && rowset.getActiveFilter() != null) {
            rowset.getActiveFilter().cancelFilter();
        }
        userFiltering = false;
    }

    protected void internalExecuteChildren(boolean refresh) throws Exception {
        if (updatingCounter == 0) {
            Set<Relation<E>> rels = getOutRelations();
            if (rels != null) {
                Set<E> toExecute = new HashSet<>();
                rels.forEach((Relation<E> outRel) -> {
                    if (outRel != null) {
                        E rEntity = outRel.getRightEntity();
                        if (rEntity != null) {
                            toExecute.add(rEntity);
                        }
                    }
                });
                model.executeEntities(refresh, toExecute);
            }
        }
    }

    protected void internalExecuteChildren(boolean refresh, int aOnlyFieldIndex) throws Exception {
        if (updatingCounter == 0) {
            Set<Relation<E>> rels = getOutRelations();
            if (rels != null) {
                Field onlyField = getFields().get(aOnlyFieldIndex);
                Set<E> toExecute = new HashSet<>();
                rels.forEach((Relation<E> outRel) -> {
                    if (outRel != null) {
                        E rEntity = outRel.getRightEntity();
                        if (rEntity != null && outRel.getLeftField() == onlyField) {
                            toExecute.add(rEntity);
                        }
                    }
                });
                model.executeEntities(refresh, toExecute);
            }
        }
    }

    /**
     * WARNING!!! This method is for external use only. It allows to set a
     * rowset from any environment and so it resets entitiy state e.g. valid
     * flags is resetted, entity is re-signed to rowset's events and fields is
     * resetted with rowset's fields.
     *
     * @param aRowset
     */
    public void setRowset(Rowset aRowset) {
        Rowset oldRowset = rowset;
        if (rowset != null) {
            rowset.removeRowsetListener(this);
        }
        rowset = aRowset;
        valid = true;
        if (rowset != null) {
            rowset.addRowsetListener(this);
            changeSupport.firePropertyChange("rowset", oldRowset, rowset);
        }
    }

    @Override
    public boolean validate() throws Exception {
        Rowset oldRowset = rowset;
        boolean res = super.validate();
        if (!res) {
            rowset = oldRowset;
        }
        return res;
    }

    protected abstract void prepareRowsetByQuery() throws Exception;

    protected abstract void refreshRowset(final Consumer<Void> aOnSuccess, final Consumer<Exception> aOnFailure) throws Exception;

    @Override
    public Fields getFields() {
        Fields fields = super.getFields();
        try {
            Rowset rs = getRowset();
            if (rs != null) {
                fields = rs.getFields();
            }
        } catch (Exception ex) {
            Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fields;
    }

    public boolean isUserFiltering() {
        return userFiltering;
    }

    public void setUserFiltering(boolean aUserFiltering) throws Exception {
        boolean oldUserFiltering = userFiltering;
        userFiltering = aUserFiltering;
        if (oldUserFiltering != userFiltering) {
            if (rowset.getActiveFilter() != null) {
                rowset.getActiveFilter().cancelFilter();
            }
            execute();
        }
    }

    protected boolean isFilterable() throws Exception {
        return rowset != null && !userFiltering && rtInFilterRelations != null && !rtInFilterRelations.isEmpty();
    }

    protected boolean isQueriable() throws Exception {
        return queryName != null || (tableName != null && !tableName.isEmpty());
    }

    public void bindQueryParameters() throws Exception {
        Parameters selfParameters = getQuery().getParameters();
        // Let's correct script evil!!!
        for (int i = 1; i <= selfParameters.getFieldsCount(); i++) {
            Parameter p = selfParameters.get(i);
            boolean oldModified = p.isModified();
            p.setValue(ScriptUtils.toJava(p.getValue()));
            p.setModified(oldModified);
        }
        //
        boolean parametersModified = false;
        Set<Relation<E>> inRels = getInRelations();
        if (inRels != null && !inRels.isEmpty()) {
            for (Relation<E> relation : inRels) {
                if (relation != null && relation.isRightParameter()) {
                    E leftEntity = relation.getLeftEntity();
                    if (leftEntity != null) {
                        Object pValue = null;
                        if (relation.isLeftField()) {
                            // There might be entities - parameters values sources, with no
                            // data in theirs rowsets, so we can't bind query parameters to proper values. In the
                            // such case we initialize parameters values with RowsetUtils.UNDEFINED_SQL_VALUE
                            Rowset leftRowset = leftEntity.getRowset();
                            if (leftRowset != null && !leftRowset.isEmpty() && !leftRowset.isBeforeFirst() && !leftRowset.isAfterLast()) {
                                try {
                                    pValue = leftRowset.getObject(leftRowset.getFields().find(relation.getLeftField().getName()));
                                } catch (InvalidColIndexException | InvalidCursorPositionException ex) {
                                    pValue = RowsetUtils.UNDEFINED_SQL_VALUE;
                                    Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, "while assigning parameter:" + relation.getRightParameter() + " in entity: " + getTitle() + " [" + String.valueOf(getEntityId()) + "]", ex);
                                }
                            } else {
                                pValue = RowsetUtils.UNDEFINED_SQL_VALUE;
                            }
                        } else {
                            /*
                             Query<?> leftQuery = leftEntity.getQuery();
                             assert leftQuery != null : "Left query must present (Relation points to query, but query is absent)";
                             Parameters leftParams = leftQuery.getParameters();
                             assert leftParams != null : "Parameters of left query must present (Relation points to query parameter, but query parameters are absent)";
                             */
                            Parameter leftParameter = relation.getLeftParameter();
                            if (leftParameter != null) {
                                pValue = leftParameter.getValue();
                                // Let's correct Rhino evil!!!
                                pValue = ScriptUtils.toJava(pValue);
                                if (pValue == null) {
                                    pValue = leftParameter.getDefaultValue();
                                }
                                pValue = ScriptUtils.toJava(pValue);
                            } else {
                                Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, "Parameter of left query must present (Relation points to query parameter in entity: {0} [{1}], but query parameter is absent)", new Object[]{getTitle(), String.valueOf(getEntityId())});
                            }
                        }
                        Parameter selfPm = relation.getRightParameter();
                        if (selfPm != null) {
                            Object selfValue = selfPm.getValue();
                            if (!SQLUtils.isJdbcEqual(selfValue, pValue)) {
                                selfPm.setValue(pValue);
                            }
                        }
                    } else {
                        Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, "Relation with no left entity detected");
                    }
                }
            }
        }
        for (int i = 1; i <= selfParameters.getFieldsCount(); i++) {
            Parameter param = (Parameter) selfParameters.get(i);
            if (param.isModified()) {
                parametersModified = true;
                param.setModified(false);
            }
        }
        if (parametersModified) {
            invalidate();
        }
    }

    protected void validateInFilterRelations() {
        // never build yet, so build it ...
        if (rtInFilterRelations == null) {
            rtInFilterRelations = new ArrayList<>();
            assert rowset != null;
            Set<Relation<E>> inRels = getInRelations();
            if (inRels != null) {
                inRels.forEach((Relation<E> rel) -> {
                    if (rel != null && rel.isRightField()) {
                        rtInFilterRelations.add(rel);
                    }
                });
            }
        }
    }

    protected void validateFilter() throws RowsetException {
        assert rtInFilterRelations != null;
        assert rowset != null;
        if (rowsetFilter == null && !rtInFilterRelations.isEmpty()) {
            List<Field> constraints = new ArrayList<>();
            // enumerate filtering relations ...
            rtInFilterRelations.forEach((Relation<E> rel) -> {
                assert rel != null && rel.isRightField();
                constraints.add(rel.getRightField());
            });
            if (!constraints.isEmpty()) {
                rowsetFilter = rowset.createFilter();
                rowsetFilter.beginConstrainting();
                try {
                    Fields rFields = rowset.getFields();
                    for (Field field : constraints) {
                        // entity's and rowset's fields may differ.
                        rowsetFilter.addConstraint(rFields.find(field.getName()));
                    }
                } finally {
                    rowsetFilter.endConstrainting();
                }
                rowsetFilter.build();
            }
        }
    }

    public boolean filterRowset() throws Exception {
        validateInFilterRelations();
        if (isFilterable()) {
            validateFilter();
            return applyFilter();
        } else {
            return false;
        }
    }

    public boolean applyFilter() throws Exception {
        try {
            assert !userFiltering : "Can't apply own filter while user filtering";
            assert rowset != null : "Bad requery -> filter chain";
            KeySet filterKeySet = new KeySet();
            if (!rtInFilterRelations.isEmpty()) {
                for (Relation<E> rel : rtInFilterRelations) {
                    // relation must be filtering relation ...
                    assert rel != null && rel.isRightField();
                    E leftEntity = rel.getLeftEntity();
                    assert leftEntity != null;
                    Object fValue = null;
                    if (rel.isLeftField()) {
                        Rowset leftRowset = leftEntity.getRowset();
                        if (leftRowset != null) {
                            try {
                                if (!leftRowset.isEmpty()) {
                                    if (!leftRowset.isBeforeFirst() && !leftRowset.isAfterLast()) {
                                        fValue = leftRowset.getObject(leftRowset.getFields().find(rel.getLeftField().getName()));
                                    } else {
                                        fValue = RowsetUtils.UNDEFINED_SQL_VALUE;
                                        Logger.getLogger(Entity.class.getName()).log(Level.FINE, "Failed to achieve value for filtering field:{0} in entity: {1} [{2}]. The source rowset has bad position (before first or after last).", new Object[]{rel.getRightField(), getTitle(), String.valueOf(getEntityId())});
                                    }
                                } else {
                                    fValue = RowsetUtils.UNDEFINED_SQL_VALUE;
                                    Logger.getLogger(Entity.class.getName()).log(Level.FINE, "Failed to achieve value for filtering field:{0} in entity: {1} [{2}]. The source rowset has no any rows.", new Object[]{rel.getRightField(), getTitle(), String.valueOf(getEntityId())});
                                }
                            } catch (InvalidColIndexException | InvalidCursorPositionException ex) {
                                Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, "while achieving value for filtering field:" + rel.getRightField() + " in entity: " + getTitle() + " [" + String.valueOf(getEntityId()) + "]", ex);
                                throw ex;
                            }
                        } else {
                            fValue = RowsetUtils.UNDEFINED_SQL_VALUE;
                            Logger.getLogger(Entity.class.getName()).log(Level.FINE, "Failed to achieve value for filtering field:{0} in entity: {1} [{2}]. The source rowset is absent.", new Object[]{rel.getRightField(), getTitle(), String.valueOf(getEntityId())});
                        }
                    } else {
                        /*
                         Q leftQuery = leftEntity.getQuery();
                         assert leftQuery != null : "Left query must present (Relation points to query, but query is absent)";
                         Parameters leftParams = leftQuery.getParameters();
                         assert leftParams != null : "Parameters of left query must present (Relation points to query parameter, but query parameters are absent)";
                         */
                        Parameter leftParameter = rel.getLeftParameter();
                        if (leftParameter != null) {
                            fValue = leftParameter.getValue();
                            if (fValue == null) {
                                fValue = leftParameter.getDefaultValue();
                            }
                        } else {
                            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, "Parameter of left query must present (Relation points to query parameter, but query parameter with specified name is absent)");
                        }
                    }
                    Converter conv = rowset.getConverter();
                    Field fieldOfValue = rowset.getFields().get(rel.getRightField().getName());
                    filterKeySet.add(conv.convert2RowsetCompatible(fValue, fieldOfValue.getTypeInfo()));
                }
            }
            Filter activeFilter = rowset.getActiveFilter();
            if (rowsetFilter != null && !rowsetFilter.isEmpty()
                    && (rowsetFilter != activeFilter || !rowsetFilter.getKeysetApplied().equals(filterKeySet))) {
                rowsetFilter.filterRowset(filterKeySet);
                return true;
            } else {
                return false;

            }
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public Object executeScriptEvent(final JSObject aHandler, final PublishedSourcedEvent aEvent) {
        Object res = null;
        if (aHandler != null) {
            try {
                return ScriptUtils.toJava(aHandler.call(getPublished(), new Object[]{aEvent.getPublished()}));
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return res;
    }

    @Override
    public boolean willScroll(final RowsetScrollEvent aEvent) {
        boolean res = true;
        assert aEvent.getRowset() == rowset;
        try {
            // call script method
            Object sRes = executeScriptEvent(onBeforeScroll, new CursorPositionWillChangeEvent(this, aEvent.getOldRowIndex(), aEvent.getNewRowIndex()));
            if (sRes != null && sRes instanceof Boolean) {
                return (Boolean) sRes;
            } else {
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public void rowsetScrolled(RowsetScrollEvent aEvent) {
        Rowset easRs = aEvent.getRowset();
        if (aEvent.getNewRowIndex() >= 0 && aEvent.getNewRowIndex() <= easRs.size() + 1) {
            try {
                // call script method
                executeScriptEvent(onAfterScroll, new CursorPositionChangedEvent(this, aEvent.getOldRowIndex(), aEvent.getNewRowIndex()));
                internalExecuteChildren(false);
            } catch (Exception ex) {
                Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean willChangeRow(final RowChangeEvent aEvent) {
        boolean res = true;
        Fields fmdv = getFields();
        if (fmdv != null) {
            final Field field = fmdv.get(aEvent.getFieldIndex());
            if (field != null) {
                try {
                    // call script method
                    Object sRes = executeScriptEvent(onBeforeChange, new EntityInstanceChangeEvent(aEvent.getChangedRow(), field, aEvent.getOldValue(), aEvent.getNewValue()));
                    if (sRes != null && sRes instanceof Boolean) {
                        return (Boolean) sRes;
                    } else {
                        return true;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return res;
    }

    @Override
    public void rowChanged(final RowChangeEvent aEvent) {
        try {
            Fields lfields = getFields();
            if (lfields != null) {
                final Field field = lfields.get(aEvent.getFieldIndex());
                if (field != null) {
                    executeScriptEvent(onAfterChange, new EntityInstanceChangeEvent(aEvent.getChangedRow(), field, aEvent.getOldValue(), aEvent.getNewValue()));
                }
            }
            internalExecuteChildren(false, aEvent.getFieldIndex());
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean willInsertRow(final RowsetInsertEvent event) {
        boolean res = true;
        // call script method
        try {
            Object sRes = executeScriptEvent(onBeforeInsert, new EntityInstanceInsertEvent(this, event.getRow()));
            if (sRes != null && sRes instanceof Boolean) {
                return (Boolean) sRes;
            } else {
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public boolean willDeleteRow(final RowsetDeleteEvent event) {
        boolean res = true;
        // call script method
        try {
            Object sRes = executeScriptEvent(onBeforeDelete, new EntityInstanceDeleteEvent(this, event.getRow()));
            if (sRes != null && sRes instanceof Boolean) {
                return (Boolean) sRes;
            } else {
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(ApplicationEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public void rowInserted(final RowsetInsertEvent event) {
        try {
            // call script method
            executeScriptEvent(onAfterInsert, new EntityInstanceInsertEvent(this, event.getRow()));
            internalExecuteChildren(false);
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowDeleted(final RowsetDeleteEvent event) {
        try {
            // call script method
            executeScriptEvent(onAfterDelete, new EntityInstanceDeleteEvent(this, event.getRow()));
            internalExecuteChildren(false);
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
        try {
            // call script method
            executeScriptEvent(onFiltered, new PublishedSourcedEvent(this));
            internalExecuteChildren(false);
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowsetSorted(RowsetSortEvent event) {
        try {
            // call script method
            executeScriptEvent(onFiltered, new PublishedSourcedEvent(this));
            internalExecuteChildren(false);
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void beforeRequery(RowsetRequeryEvent rre) {
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        try {
            assert rowset != null;
            filterRowset();
            // call script method
            executeScriptEvent(onRequeried, new PublishedSourcedEvent(this));
            internalExecuteChildren(false);
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        try {
            assert rowset != null;
            filterRowset();
            // call script method
            executeScriptEvent(onRequeried, new PublishedSourcedEvent(this));
            internalExecuteChildren(false);
        } catch (Exception ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rowsetNetError(RowsetNetErrorEvent rnee) {
    }

    @Override
    public void rowsetSaved(RowsetSaveEvent event) {
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
    }

    @Override
    public boolean willFilter(RowsetFilterEvent event) {
        return true;
    }

    @Override
    public boolean willRequery(RowsetRequeryEvent event) {
        return true;
    }

    @Override
    public boolean willSort(RowsetSortEvent event) {
        return true;
    }

    @Override
    public boolean willNextPageFetch(RowsetNextPageEvent event) {
        return true;
    }

    @Override
    protected void assign(E appTarget) throws Exception {
        super.assign(appTarget);
        appTarget.setOnAfterChange(onAfterChange);
        appTarget.setOnAfterDelete(onAfterDelete);
        appTarget.setOnAfterInsert(onAfterInsert);
        appTarget.setOnAfterScroll(onAfterScroll);
        appTarget.setOnFiltered(onFiltered);
        appTarget.setOnRequeried(onRequeried);
        appTarget.setOnBeforeChange(onBeforeChange);
        appTarget.setOnBeforeDelete(onBeforeDelete);
        appTarget.setOnBeforeInsert(onBeforeInsert);
        appTarget.setOnBeforeScroll(onBeforeScroll);
    }

    @Override
    public boolean addInRelation(Relation<E> aRelation) {
        if (aRelation instanceof ReferenceRelation<?>) {
            return false;
        } else {
            return super.addInRelation(aRelation);
        }
    }

    @Override
    public boolean addOutRelation(Relation<E> aRelation) {
        if (aRelation instanceof ReferenceRelation<?>) {
            return false;
        } else {
            return super.addOutRelation(aRelation);
        }
    }
}
