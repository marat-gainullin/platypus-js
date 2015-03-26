package com.bearsoft.rowset;

import com.eas.client.dataflow.RowsetConverter;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.ChangeValue;
import com.bearsoft.rowset.changes.Insert;
import com.bearsoft.rowset.changes.Update;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptUtils;
import java.beans.*;
import java.math.BigDecimal;
import java.util.*;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 * This class serves as the values holder.
 *
 * @author mg
 */
public class Row implements HasPublished {

    private static JSObject publisher;
    //
    protected String entityName;
    protected List<Change> log;
    protected Fields fields;
    protected Converter converter = new RowsetConverter();
    protected BitSet updated = new BitSet(10);
    protected boolean deleted;
    protected boolean inserted;
    protected List<Object> originalValues = new ArrayList<>();
    protected List<Object> currentValues = new ArrayList<>();
    protected Insert insertChange;
    protected JSObject published;
    //
    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    protected VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);

    /**
     * Constructs the row with column count equals to colCount values vectors
     * allocated.
     *
     * @param aEntityName
     * @param aFields
     */
    public Row(String aEntityName, Fields aFields) {
        super();
        entityName = aEntityName;
        setFields(aFields);
    }

    public List<Change> getLog() {
        return log;
    }

    public void setLog(List<Change> aValue) {
        log = aValue;
    }

    public void setEntityName(String aValue) {
        entityName = aValue;
    }

    /**
     * Fires vetoable change events, without sending reverting corresponding
     * events.
     *
     * @param event An event to fire.
     * @return True if no one of listeners has vetoed the change. False
     * otherwise.
     */
    protected boolean checkChange(PropertyChangeEvent event) {
        try {
            VetoableChangeListener[] vls = vetoableChangeSupport.getVetoableChangeListeners();
            for (VetoableChangeListener vl : vls) {
                vetoableChangeSupport.removeVetoableChangeListener(vl);
            }
            try {
                for (VetoableChangeListener vl : vls) {
                    vl.vetoableChange(event);
                }
            } finally {
                for (VetoableChangeListener vl : vls) {
                    vetoableChangeSupport.addVetoableChangeListener(vl);
                }
            }
            return true;
        } catch (PropertyVetoException ex) {
            return false;
        }
    }

    public Fields getFields() {
        return fields;
    }

    public final void setFields(Fields aFields) {
        fields = aFields;
        originalValues.clear();
        currentValues.clear();
        for (int i = 0; i < fields.getFieldsCount(); i++) {
            originalValues.add(i, null);
            currentValues.add(i, null);
        }
    }

    public PropertyChangeSupport getChangeSupport() {
        return propertyChangeSupport;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void addPropertyChangeListener(String aPropertyName, PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(aPropertyName, l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    public void removePropertyChangeListener(String aPropertyName, PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(aPropertyName, l);
    }

    public void addVetoableChangeListener(VetoableChangeListener l) {
        vetoableChangeSupport.addVetoableChangeListener(l);
    }

    public void removeVetoableChangeListener(VetoableChangeListener l) {
        vetoableChangeSupport.removeVetoableChangeListener(l);
    }

    /**
     * Returns whether the row is updated at whole or partially. The updated
     * flag is <code>true</code> if <code>setColumnObject()</code> or
     * <code>setUpdated()</code> methods have been invoked between invocations
     * of <code>currentToOriginal()</code> or <code>originalToCurrent()</code>
     * methods.
     *
     * @return Whether the row is updated.
     * @see #setColumnObject(int, Object)
     */
    public boolean isUpdated() {
        return !updated.isEmpty();
    }

    /**
     * Sets updated flags for all columns.
     */
    public void setUpdated() {
        updated.set(0, updated.length() - 1);
    }

    /**
     * Clears the updated flags for all colmns.
     */
    public void clearUpdated() {
        updated.clear();
    }

    /**
     * Returns whether the row is deleted. The deleted flag is <code>true</code>
     * if <code>setDeleted()</code> method has been invoked.
     *
     * @return Whether the row is deleted.
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets the deleted flag.
     */
    public void setDeleted() {
        deleted = true;
    }

    /**
     * Clears the deleted flag.
     */
    public void clearDeleted() {
        deleted = false;
    }

    /**
     * Returns whether the row is inserted. The inserted flag is
     * <code>true</code> if <code>setInserted()</code> method has been invoked.
     *
     * @return Whether the row is inserted.
     */
    public boolean isInserted() {
        return inserted;
    }

    /**
     * Sets the inserted flag.
     */
    public void setInserted() {
        inserted = true;
    }

    public Insert getInsertChange() {
        return insertChange;
    }

    public void setInserted(Insert aInsert) {
        inserted = true;
        insertChange = aInsert;
    }

    /**
     * Clears the inserted flag.
     */
    public void clearInserted() {
        inserted = false;
        insertChange = null;
    }

    /**
     * Applies current values to this row. After this method has been invoked,
     * the current values become original values. Also it clears updated flag.
     *
     * @see #setUpdated()
     * @see #isUpdated()
     * @see #clearUpdated()
     * @see #originalToCurrent()
     */
    public void currentToOriginal() {
        currentToOriginal(true);
    }

    public void currentToOriginal(boolean aCommited) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (aCommited || !updated.get(i)) {
                originalValues.set(i, currentValues.get(i));
            }
        }
        if (aCommited) {
            clearUpdated();
        }
    }

    /**
     * Cancels current values in this row. After this method has been invoked,
     * the original values become current values. Also it clears updated flag.
     *
     * @see #setUpdated()
     * @see #isUpdated()
     * @see #clearUpdated()
     * @see #currentToOriginal()
     */
    public void originalToCurrent() {
        for (int i = 0; i < getColumnCount(); i++) {
            currentValues.set(i, originalValues.get(i));
        }
        clearUpdated();
    }

    /**
     * Returns the columns count of this row.
     *
     * @return The columns count of this row.
     * @see #Row(int colCount)
     */
    public int getColumnCount() {
        assert currentValues.size() == originalValues.size();
        assert currentValues.size() == fields.getFieldsCount();
        return fields.getFieldsCount();
    }

    /**
     * Returns current column value.
     *
     * @param colIndex ordinal position of the column. It's value lies within
     * the range of [1: <code>getColumnCount()</code>].
     * @return The column's current value.
     * @throws InvalidColIndexException if colIndex < 1 or colIndex >
     * <code>getColumnCount()</code>
     */
    public Object getColumnObject(int colIndex) throws InvalidColIndexException {
        if (colIndex >= 1 && colIndex <= getColumnCount()) {
            return currentValues.get(colIndex - 1);
        } else {
            if (colIndex < 1) {
                throw new InvalidColIndexException("colIndex < 1");
            } else if (colIndex > getColumnCount()) {
                throw new InvalidColIndexException("colIndex > getColumnCount()");
            } else {
                throw new InvalidColIndexException("unexpected");
            }
        }
    }

    /**
     * Sets current value of particular column.
     *
     * @param aColIndex ordinal position of the column. It's value lies within
     * the range of [1: <code>getColumnCount()</code>].
     * @param aValue value that you whant to be setted to the column as the
     * current column value.
     * @return
     * @throws InvalidColIndexException if colIndex < 1 or colIndex >
     * <code>getColumnCount()</code>
     */
    public boolean setColumnObject(int aColIndex, Object aValue) throws RowsetException {
        if (aColIndex >= 1 && aColIndex <= getColumnCount()) {
            Field field = fields.get(aColIndex);
            if (converter != null) {
                aValue = converter.convert2RowsetCompatible(aValue, field.getTypeInfo());
            }
            if (!smartEquals(getColumnObject(aColIndex), aValue)) {
                Object oldValue = currentValues.get(aColIndex - 1);
                PropertyChangeEvent event = new PropertyChangeEvent(this, field.getName(), oldValue, aValue);
                event.setPropagationId(aColIndex);
                if (checkChange(event)) {
                    Fields.OrmDef expanding = fields.getOrmScalarExpandings().get(field.getName());
                    Object expandingOldValue = expanding != null && expanding.getName() != null && !expanding.getName().isEmpty() ? ScriptUtils.toJava(getPublished().getMember(expanding.getName())) : null;
                    Set<Runnable> oppositeOldScalars = field.isPk() ? gatherOppositeScalarsChangesFirerers() : null;
                    currentValues.set(aColIndex - 1, aValue);
                    updated.set(aColIndex - 1, true);
                    generateUpdate(aColIndex, oldValue, aValue);
                    propertyChangeSupport.firePropertyChange(event);
                    if (expanding != null && expanding.getName() != null && !expanding.getName().isEmpty()
                            && expanding.getOppositeName() != null && !expanding.getOppositeName().isEmpty()) {
                        Object expandingNewValue = ScriptUtils.toJava(getPublished().getMember(expanding.getName()));
                        propertyChangeSupport.firePropertyChange(expanding.getName(), expandingOldValue, expandingNewValue);
                        fireChangeOfOppositeCollection(expandingOldValue, expanding.getOppositeName(), expanding.getJsDef());
                        fireChangeOfOppositeCollection(expandingNewValue, expanding.getOppositeName(), expanding.getJsDef());
                    }
                    if (field.isPk()) {
                        oppositeOldScalars.stream().forEach((Runnable fire) -> {
                            fire.run();
                        });
                        Set<Runnable> oppositeNewScalars = gatherOppositeScalarsChangesFirerers();
                        oppositeNewScalars.stream().forEach((Runnable fire) -> {
                            fire.run();
                        });
                        fireChangeOfSelfCollections();
                    }
                    return true;
                }
            }
        } else {
            if (aColIndex < 1) {
                throw new InvalidColIndexException("colIndex < 1");
            }
            if (aColIndex > getColumnCount()) {
                throw new InvalidColIndexException("colIndex > getColumnCount()");
            }
        }
        return false;
    }

    protected void generateUpdate(int colIndex, Object oldValue, Object newValue) {
        if (fields != null && log != null) {
            Field field = fields.get(colIndex);
            boolean insertComplemented = tryToComplementInsert(field, newValue);
            if (!insertComplemented) {
                Update update = new Update(entityName);
                update.data = new ChangeValue[]{new ChangeValue(field.getName(), newValue, field.getTypeInfo())};
                update.keys = generateChangeLogKeys(colIndex, this, oldValue);
                log.add(update);
            }
        }
    }

    private boolean tryToComplementInsert(Field field, Object newValue) {
        boolean insertComplemented = false;
        if (insertChange != null && !insertChange.consumed && !field.isNullable()) {
            boolean met = false;
            for (ChangeValue value : insertChange.data) {
                if (value.name.equalsIgnoreCase(field.getName())) {
                    met = true;
                    break;
                }
            }
            if (!met) {
                ChangeValue[] newdata = new ChangeValue[insertChange.data.length + 1];
                newdata[newdata.length - 1] = new ChangeValue(field.getName(), newValue, field.getTypeInfo());
                System.arraycopy(insertChange.data, 0, newdata, 0, insertChange.data.length);
                insertChange.data = newdata;
                insertComplemented = true;
            }
        }
        return insertComplemented;
    }

    public static ChangeValue[] generateChangeLogKeys(int colIndex, Row aRow, Object oldValue) {
        Fields fields = aRow.getFields();
        if (fields != null) {
            List<ChangeValue> keys = new ArrayList<>();
            for (int i = 1; i <= fields.getFieldsCount(); i++) {
                Field field = fields.get(i);
                // Some tricky processing of primary keys modification case ...
                if (field.isPk()) {
                    Object value = aRow.currentValues.get(i - 1);
                    if (i == colIndex) {
                        value = oldValue;
                    }
                    keys.add(new ChangeValue(field.getName(), value, field.getTypeInfo()));
                }
            }
            return keys.toArray(new ChangeValue[]{});
        } else {
            return null;
        }
    }

    /**
     * Returns original column value.
     *
     * @param colIndex ordinal position of the column. It's value lies within
     * the range of [1: <code>getColumnCount()</code>].
     * @return The column's original value.
     * @throws InvalidColIndexException if colIndex < 1 or colIndex >
     * <code>getColumnCount()</code>
     */
    public Object getOriginalColumnObject(int colIndex) throws InvalidColIndexException {
        if (colIndex >= 1 && colIndex <= getColumnCount()) {
            return originalValues.get(colIndex - 1);
        } else {
            if (colIndex < 1) {
                throw new InvalidColIndexException("colIndex < 1");
            } else if (colIndex > getColumnCount()) {
                throw new InvalidColIndexException("colIndex > getColumnCount()");
            } else {
                throw new InvalidColIndexException("unexpected");
            }
        }
    }

    /**
     * Returns whether particular column is updated.
     *
     * @param colIndex ordinal position of the column. Index is 1-based.
     * @return Whether aprticular column is updated.
     * @see #setColumnObject(int, Object)
     * @see #getUpdatedColumns()
     * @see #isUpdated()
     * @see #clearUpdated()
     * @see #setUpdated()
     * @see #setColumnUpdated(int)
     */
    public boolean isColumnUpdated(int colIndex) {
        return updated.get(colIndex - 1);
    }

    /**
     * Sets particular column updated state.
     *
     * @param colIndex ordinal position of the column. Index is 1-based.
     * @see #setColumnObject(int, Object)
     * @see #getUpdatedColumns()
     * @see #isUpdated()
     * @see #clearUpdated()
     * @see #setUpdated()
     * @see #isColumnUpdated(int)
     */
    public void setColumnUpdated(int colIndex) {
        updated.set(colIndex - 1);
    }

    public void setColumnUpdated(int colIndex, Object aValue) {
        updated.set(colIndex - 1);
        currentValues.set(colIndex - 1, aValue);
    }

    /**
     * Returns an updated columns indicies set.
     *
     * @return An updated columns indicies set.
     * @see #setColumnObject(int, Object)
     * @see #isColumnUpdated(int)
     * @see #isUpdated()
     * @see #clearUpdated()
     * @see #setUpdated()
     */
    public Set<Integer> getUpdatedColumns() {
        Set<Integer> lUpdated = new HashSet<>();
        for (int i = 0; i < currentValues.size(); i++) {
            if (updated.get(i)) {
                lUpdated.add(i + 1);
            }
        }
        return lUpdated;
    }

    /**
     * Returns an array of original values of this row.
     *
     * @return An array of original values of this row.
     * @see #getCurrentValues()
     * @see #originalToCurrent()
     * @see #currentToOriginal()
     */
    public Object[] getOriginalValues() {
        return originalValues.toArray();
    }

    /**
     * Returns an array of current values of this row.
     *
     * @return An array of current values of this row.
     * @see #getOriginalValues()
     * @see #originalToCurrent()
     * @see #currentToOriginal()
     */
    public Object[] getCurrentValues() {
        return currentValues.toArray();
    }

    /**
     * Returns an array of current values of primary-key fields of this row.
     *
     * @return An array of current values of primary-key fields of this row.
     */
    public Object[] getPKValues() {
        List<Integer> pkIndicies = fields.getPrimaryKeysIndicies();
        List<Object> pkValues = new ArrayList<>();
        for (Integer pkIdx : pkIndicies) {
            assert pkIdx >= 1 && pkIdx <= currentValues.size();
            pkValues.add(currentValues.get(pkIdx - 1));
        }
        return pkValues.toArray();
    }

    /**
     * Returns an array of current values of primary-key fields of this row.
     *
     * @param aIndicies An array of column indicies.
     * @return A list of current values of this row at corresponding indicies.
     */
    public List<Object> getCurrentValues(List<Integer> aIndicies) {
        List<Object> values = new ArrayList<>();
        for (Integer colIdx : aIndicies) {
            assert colIdx >= 1 && colIdx <= currentValues.size();
            values.add(currentValues.get(colIdx - 1));
        }
        return values;
    }

    /**
     * Returns an array of original values of primary-key fields of this row.
     *
     * @param aIndicies An array of column indicies.
     * @return A list of original values of this row at corresponding indicies.
     */
    public List<Object> getOriginalValues(List<Integer> aIndicies) {
        List<Object> values = new ArrayList<>();
        for (Integer colIdx : aIndicies) {
            assert colIdx >= 1 && colIdx <= originalValues.size();
            values.add(originalValues.get(colIdx - 1));
        }
        return values;
    }

    /**
     * Tests if two values are equal to each other. It casts both values to big
     * decimal format if they are numbers.
     *
     * @param aFirstValue First value to be compared.
     * @param aOtherValue Second value to be compared.
     * @return If two values are equal to each other.
     */
    public static boolean smartEquals(Object aFirstValue, Object aOtherValue) {
        if (aFirstValue != null) {
            if (aFirstValue instanceof Number && aOtherValue instanceof Number) {
                BigDecimal bd1 = RowsetUtils.number2BigDecimal((Number) aFirstValue);
                BigDecimal bd2 = RowsetUtils.number2BigDecimal((Number) aOtherValue);
                return bd1.equals(bd2);
            } else {
                return aFirstValue.equals(aOtherValue);
            }
        } else {
            return aOtherValue == null;
        }
    }

    public void setConverter(Converter aValue) {
        converter = aValue;
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
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

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

    protected void fireChangeOfSelfCollections() {
        fields.getOrmCollectionsDefinitions().keySet().stream().forEach((selfCollectionName) -> {
            propertyChangeSupport.firePropertyChange(selfCollectionName, null, new Object());
        });
    }

    protected Set<Runnable> gatherOppositeScalarsChangesFirerers() {
        Set<Runnable> firerers = new HashSet<>();
        if (published != null) {
            fields.getOrmCollectionsDefinitions().entrySet().stream().forEach((entry) -> {
                String collectionName = entry.getKey();
                String oppositeName = entry.getValue().getOppositeName();
                if (collectionName != null && !collectionName.isEmpty()
                        && oppositeName != null && !oppositeName.isEmpty()) {
                    Object oCollection = published.getMember(collectionName);
                    if (oCollection instanceof JSObject) {
                        JSObject jsCollection = (JSObject) oCollection;
                        int length = JSType.toInteger(jsCollection.getMember("length"));
                        for (int i = 0; i < length; i++) {
                            Object oRowFacade = jsCollection.getSlot(i);
                            if (oRowFacade instanceof JSObject) {
                                JSObject jsRowFacade = (JSObject) oRowFacade;
                                Object oUnwrap = jsRowFacade.getMember("unwrap");
                                if (oUnwrap instanceof JSObject) {
                                    JSObject unwrap = (JSObject) oUnwrap;
                                    Object oRow = unwrap.call(null, new Object[]{});
                                    if (oRow instanceof Row) {
                                        Row oppositeRow = (Row) oRow;
                                        firerers.add(() -> {
                                            oppositeRow.getChangeSupport().firePropertyChange(oppositeName, null, new Object());
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
        return firerers;
    }

    public void fireChangesOfOppositeScalars() {
        Set<Runnable> oppositeNewScalars = gatherOppositeScalarsChangesFirerers();
        oppositeNewScalars.stream().forEach((Runnable fire) -> {
            fire.run();
        });
    }

    public void fireChangesOfOppositeCollections() {
        fields.getOrmScalarExpandings().values().stream().forEach((expanding) -> {
            Object expandingValue = expanding != null && expanding.getName() != null && !expanding.getName().isEmpty() ? ScriptUtils.toJava(getPublished().getMember(expanding.getName())) : null;
            if (expanding != null && expanding.getName() != null && !expanding.getName().isEmpty()
                    && expanding.getOppositeName() != null && !expanding.getOppositeName().isEmpty()) {
                fireChangeOfOppositeCollection(expandingValue, expanding.getOppositeName(), expanding.getJsDef());
            }
        });
    }

    private void fireChangeOfOppositeCollection(Object aExpandingValue, String oppositeName, JSObject def) {
        if (aExpandingValue instanceof JSObject) {
            JSObject jsExpanding = (JSObject) aExpandingValue;
            if (jsExpanding.hasMember("unwrap")) {
                Object oUnwrap = jsExpanding.getMember("unwrap");
                if (oUnwrap instanceof JSObject) {
                    JSObject unwrap = (JSObject) oUnwrap;
                    Object oRow = unwrap.call(null, new Object[]{});
                    if (oRow instanceof Row) {
                        Row row = (Row) oRow;
                        row.getChangeSupport().firePropertyChange(oppositeName, null, new Object());
                    }
                }
            }
        }
    }
}
