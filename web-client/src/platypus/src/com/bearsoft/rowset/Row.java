package com.bearsoft.rowset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bearsoft.rowset.Utils.JsObject;
import com.bearsoft.rowset.beans.HasPropertyListeners;
import com.bearsoft.rowset.beans.PropertyChangeEvent;
import com.bearsoft.rowset.beans.PropertyChangeListener;
import com.bearsoft.rowset.beans.PropertyChangeSupport;
import com.bearsoft.rowset.beans.VetoableChangeListener;
import com.bearsoft.rowset.beans.VetoableChangeSupport;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.Insert;
import com.bearsoft.rowset.changes.Update;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * This class serves as the values holder.
 * 
 * @author mg
 */
public class Row implements HasPropertyListeners {

	protected String entityName;
	protected List<Change> log;
	protected Fields fields;
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	protected VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);
	protected Set<Integer> updated = new HashSet<>();
	protected boolean deleted = false;
	protected boolean inserted = false;
	protected List<Object> originalValues = new ArrayList<>();
	protected List<Object> currentValues = new ArrayList<>();
	protected Insert insertChange;

	/**
	 * Constructs the row with column count equals to colCount values vectors
	 * allocated.
	 * 
	 * @param colCount
	 *            - column count that you whant to be in this row.
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Fires vetoable change events, without sending reverting corresponding
	 * events.
	 * 
	 * @param event
	 *            An event to fire.
	 * @return True if no one of listeners has vetoed the change. False
	 *         otherwise.
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
		} catch (Exception ex) {
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

	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		return propertyChangeSupport.getPropertyChangeListeners();
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
		for (int i = 1; i <= fields.getFieldsCount(); i++) {
			updated.add(i);
		}
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
		for (int i = 0; i < getColumnCount(); i++) {
			originalValues.set(i, currentValues.get(i));
		}
		clearUpdated();
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

	public void setFieldObject(String aFieldName, Object aFieldValue) throws Exception {
		int colIndex = fields.find(aFieldName);
		setColumnObject(colIndex, Utils.toJava(aFieldValue));
	}

	/**
	 * Sets current value of particular column.
	 * 
	 * @param aColIndex
	 *            ordinal position of the column. It's value lies within the
	 *            range of [1: <code>getColumnCount()</code>].
	 * @param aValue
	 *            value that you want to be setted to the column as the current
	 *            column value.
	 * @throws InvalidColIndexException
	 *             if colIndex < 1 or colIndex > <code>getColumnCount()</code>
	 */
	public void setColumnObject(int aColIndex, Object aValue) throws RowsetException {
		if (aColIndex >= 1 && aColIndex <= getColumnCount()) {
			Field field = fields.get(aColIndex);
			aValue = Converter.convert2RowsetCompatible(aValue, field.getTypeInfo());
			if (!smartEquals(getColumnObject(aColIndex), aValue)) {
				Object oldValue = currentValues.get(aColIndex - 1);
				PropertyChangeEvent event = new PropertyChangeEvent(this, field.getName(), oldValue, aValue);
				event.setPropagationId(aColIndex);
				if (checkChange(event)) {
					Fields.OrmDef expanding = fields.getOrmScalarExpandings().get(field.getName());
					JavaScriptObject expandingOldValue = expanding != null && expanding.getName() != null && !expanding.getName().isEmpty() ? jsPublished.getJs(expanding.getName()) : null;
					Set<Runnable> oppositeOldScalars = field.isPk() ? gatherOppositeScalarsChangesFirerers() : null;
					currentValues.set(aColIndex - 1, aValue);
					updated.add(aColIndex);
					generateUpdate(aColIndex, oldValue, aValue);
					propertyChangeSupport.firePropertyChange(event);
					if (expanding != null && expanding.getName() != null && !expanding.getName().isEmpty() && expanding.getOppositeName() != null && !expanding.getOppositeName().isEmpty()) {
						JavaScriptObject expandingNewValue = jsPublished.getJs(expanding.getName());
						propertyChangeSupport.firePropertyChange(expanding.getName(), expandingOldValue, expandingNewValue);
						fireChangeOfOppositeCollection(expandingOldValue, expanding.getOppositeName(), expanding.getJsDef());
						fireChangeOfOppositeCollection(expandingNewValue, expanding.getOppositeName(), expanding.getJsDef());
					}
					if (field.isPk()) {
						for (Runnable fire : oppositeOldScalars) {
							fire.run();
						}
						;
						Set<Runnable> oppositeNewScalars = gatherOppositeScalarsChangesFirerers();
						for (Runnable fire : oppositeNewScalars) {
							fire.run();
						}
						;
						fireChangeOfSelfCollections();
					}
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
	}

	protected void generateUpdate(int colIndex, Object oldValue, Object newValue) {
		if (fields != null && log != null) {
			Field field = fields.get(colIndex);
			boolean insertComplemented = tryToComplementInsert(field, newValue);
			if (!insertComplemented) {
				Update update = new Update(entityName);
				update.data = new Change.Value[] { new Change.Value(field.getName(), newValue, field.getTypeInfo()) };
				update.keys = generateChangeLogKeys(colIndex, this, oldValue);
				log.add(update);
			}
		}
	}

	private boolean tryToComplementInsert(Field field, Object newValue) {
		boolean insertComplemented = false;
		if (insertChange != null && !field.isNullable()) {
			boolean met = false;
			for (Change.Value value : insertChange.data) {
				if (value.name.equalsIgnoreCase(field.getName())) {
					met = true;
					break;
				}
			}
			if (!met) {
				Change.Value[] newdata = new Change.Value[insertChange.data.length + 1];
				newdata[newdata.length - 1] = new Change.Value(field.getName(), newValue, field.getTypeInfo());
				System.arraycopy(insertChange.data, 0, newdata, 0, insertChange.data.length);
				insertChange.data = newdata;
				insertComplemented = true;
			}
		}
		return insertComplemented;
	}

	public static Change.Value[] generateChangeLogKeys(int colIndex, Row aRow, Object oldValue) {
		Fields fields = aRow.getFields();
		if (fields != null) {
			List<Change.Value> keys = new ArrayList<>();
			for (int i = 1; i <= fields.getFieldsCount(); i++) {
				Field field = fields.get(i);
				// Some tricky processing of primary key modification case ...
				if (field.isPk()) {
					Object value = aRow.getCurrentValues()[i - 1];
					if (i == colIndex) {
						value = oldValue;
					}
					keys.add(new Change.Value(field.getName(), value, field.getTypeInfo()));
				}
			}
			return keys.toArray(new Change.Value[] {});
		} else {
			return null;
		}
	}

	public Object getFieldObject(String aFieldName) throws Exception {
		int colIndex = fields.find(aFieldName);
		return Utils.toJs(getColumnObject(colIndex));
	}

	/**
	 * Returns current column value.
	 * 
	 * @param colIndex
	 *            ordinal position of the column. It's value lies within the
	 *            range of [1: <code>getColumnCount()</code>].
	 * @return The column's current value.
	 * @throws InvalidColIndexException
	 *             if colIndex < 1 or colIndex > <code>getColumnCount()</code>
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
	 * Returns original column value.
	 * 
	 * @param colIndex
	 *            ordinal position of the column. It's value lies within the
	 *            range of [1: <code>getColumnCount()</code>].
	 * @return The column's original value.
	 * @throws InvalidColIndexException
	 *             if colIndex < 1 or colIndex > <code>getColumnCount()</code>
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
	 * @param aColIndex
	 *            ordinal position of the column. Index is 1-based.
	 * @return Whether aprticular column is updated.
	 * @see #setColumnObject(int, Object)
	 * @see #getUpdatedColumns()
	 * @see #isUpdated()
	 * @see #clearUpdated()
	 * @see #setUpdated()
	 * @see #setColumnUpdated(int)
	 */
	public boolean isColumnUpdated(int aColIndex) {
		return updated.contains(aColIndex);
	}

	/**
	 * Sets particular column updated state.
	 * 
	 * @param colIndex
	 *            ordinal position of the column. Index is 1-based.
	 * @see #setColumnObject(int, Object)
	 * @see #getUpdatedColumns()
	 * @see #isUpdated()
	 * @see #clearUpdated()
	 * @see #setUpdated()
	 * @see #isColumnUpdated(int)
	 */
	public void setColumnUpdated(int colIndex) {
		updated.add(colIndex);
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
		return updated;
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
		Object[] lcurrentValues = getCurrentValues();
		List<Integer> pkIndicies = fields.getPrimaryKeysIndicies();
		List<Object> pkValues = new ArrayList<>();
		for (Integer pkIdx : pkIndicies) {
			assert pkIdx >= 1 && pkIdx <= lcurrentValues.length;
			pkValues.add(lcurrentValues[pkIdx - 1]);
		}
		return pkValues.toArray();
	}

	/**
	 * Returns an internal representation of row's data. It's not recomended to
	 * add or remove any elements from the list returned. Furthermore, if you
	 * have changed some elements of the list, than you must set internal
	 * updated flags in some way.
	 * 
	 * @return Internal row's data values list.
	 */
	public List<Object> getInternalCurrentValues() {
		return currentValues;
	}

	/**
	 * Tests if two values are equal to each other. It casts both values to big
	 * decimal format if they are numbers.
	 * 
	 * @param aFirstValue
	 *            First value to be compared.
	 * @param aOtherValue
	 *            Second value to be compared.
	 * @return If two values are equal to each other.
	 */
	public static boolean smartEquals(Object aFirstValue, Object aOtherValue) {
		if (aFirstValue != null) {
			if (aFirstValue instanceof Number && aOtherValue instanceof Number) {
				Double bd1 = ((Number) aFirstValue).doubleValue();
				Double bd2 = ((Number) aOtherValue).doubleValue();
				return bd1.compareTo(bd2) == 0;
			} else {
				return aFirstValue.equals(aOtherValue);
			}
		} else {
			return aOtherValue == null;
		}
	}

	protected void fireChangeOfSelfCollections() {
		for (String selfCollectionName : fields.getOrmCollectionsDefinitions().keySet()) {
			propertyChangeSupport.firePropertyChange(selfCollectionName, null, new Object());
		}
	}

	protected Set<Runnable> gatherOppositeScalarsChangesFirerers() {
		Set<Runnable> firerers = new HashSet<>();
		if (jsPublished != null) {
			for (Map.Entry<String, Fields.OrmDef> entry : fields.getOrmCollectionsDefinitions().entrySet()) {
				String collectionName = entry.getKey();
				final String oppositeName = entry.getValue().getOppositeName();
				if (collectionName != null && !collectionName.isEmpty() && oppositeName != null && !oppositeName.isEmpty()) {
					JavaScriptObject jsCollection = jsPublished.getJs(collectionName);
					if (jsCollection != null) {
						Object oLength = jsCollection.<JsObject> cast().getJava("length");
						int length = oLength instanceof Number ? ((Number) oLength).intValue() : 0;
						for (int i = 0; i < length; i++) {
							JavaScriptObject oRowFacade = jsCollection.<JsObject> cast().getSlot(i);
							if (oRowFacade != null) {
								JsObject jsRowFacade = oRowFacade.<JsObject> cast();
								JavaScriptObject oUnwrap = jsRowFacade.getJs("unwrap");
								if (oUnwrap != null) {
									JsObject unwrap = oUnwrap.<JsObject> cast();
									Object oRow = unwrap.apply(jsRowFacade, null);
									if (oRow instanceof Row) {
										final Row oppositeRow = (Row) oRow;
										firerers.add(new Runnable() {
											public void run() {
												oppositeRow.getChangeSupport().firePropertyChange(oppositeName, null, new Object());
											}
										});
									}
								}
							}
						}
					}
				}
			}
			;
		}
		return firerers;
	}

	public void fireChangesOfOppositeScalars() {
		Set<Runnable> oppositeNewScalars = gatherOppositeScalarsChangesFirerers();
		for (Runnable fire : oppositeNewScalars) {
			fire.run();
		}
		;
	}

	public void fireChangesOfOppositeCollections() {
		for (Fields.OrmDef expanding : fields.getOrmScalarExpandings().values()) {
			JavaScriptObject expandingValue = expanding != null && expanding.getName() != null && !expanding.getName().isEmpty() ? jsPublished.getJs(expanding.getName()) : null;
			if (expanding != null && expanding.getName() != null && !expanding.getName().isEmpty() && expanding.getOppositeName() != null && !expanding.getOppositeName().isEmpty()) {
				fireChangeOfOppositeCollection(expandingValue, expanding.getOppositeName(), expanding.getJsDef());
			}
		}
		;
	}

	private void fireChangeOfOppositeCollection(JavaScriptObject oExpanding, String oppositeName, JavaScriptObject def) {
		if (oExpanding != null) {
			JsObject jsExpanding = oExpanding.cast();
			if (jsExpanding.has("unwrap")) {
				JavaScriptObject unwrap = jsExpanding.getJs("unwrap");
				if (unwrap != null) {
					Object oRow = unwrap.<JsObject> cast().apply(jsExpanding, null);
					if (oRow instanceof Row) {
						Row row = (Row) oRow;
						row.getChangeSupport().firePropertyChange(oppositeName, null, new Object());
					}
				}
			}
		}
	}

	protected JsObject jsPublished;

	public void setPublished(JavaScriptObject aPublished) {
		jsPublished = aPublished != null ? aPublished.<JsObject> cast() : null;
	}

	public JavaScriptObject getPublished() {
		return jsPublished;
	}

	public void publishOrmProps(JavaScriptObject aTarget) {
		for (Map.Entry<String, Fields.OrmDef> entry : fields.getOrmScalarDefinitions().entrySet()) {
			aTarget.<JsObject> cast().defineProperty(entry.getKey(), entry.getValue().getJsDef());
		}
		for (Map.Entry<String, Fields.OrmDef> entry : fields.getOrmCollectionsDefinitions().entrySet()) {
			aTarget.<JsObject> cast().defineProperty(entry.getKey(), entry.getValue().getJsDef());
		}
	}

	public static native JavaScriptObject publishFacade(Row aRow, JavaScriptObject aTarget)/*-{
		var published = aRow.@com.bearsoft.rowset.Row::getPublished()();
		if(published == null){
			var nFields = aRow.@com.bearsoft.rowset.Row::getFields()();
			if(aTarget){
				published = aTarget;
			}else{
				var elClass = nFields.@com.bearsoft.rowset.metadata.Fields::getInstanceConstructor()();
				if(elClass != null && typeof elClass == "function")
					published = new elClass();
				else
					published = {};
			} 
			Object.defineProperty(published, "unwrap", { get : function(){
				return function() {
					return aRow;
				}
			}});
			var fieldsCount = nFields.@com.bearsoft.rowset.metadata.Fields::getFieldsCount()();
			var schema = @com.bearsoft.rowset.metadata.Fields::publishFacade(Lcom/bearsoft/rowset/metadata/Fields;)(nFields);
			for(var i = 0; i < fieldsCount; i++){
				(function(){
					var _i = i;
					var propDesc = {
						 get : function(){ return $wnd.P.boxAsJs(aRow.@com.bearsoft.rowset.Row::getFieldObject(Ljava/lang/String;)(schema[_i].name)); },
						 set : function(aValue){ aRow.@com.bearsoft.rowset.Row::setFieldObject(Ljava/lang/String;Ljava/lang/Object;)(schema[_i].name, $wnd.P.boxAsJava(aValue)); }
					};
					Object.defineProperty(published, schema[_i].name, propDesc);
					Object.defineProperty(published, (_i+""),     propDesc);
				})();
			}
			aRow.@com.bearsoft.rowset.Row::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			aRow.@com.bearsoft.rowset.Row::publishOrmProps(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
		}
		return published;
	}-*/;
}
