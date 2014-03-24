package com.eas.client.form.combo;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetCallbackAdapter;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.converters.StringRowValueConverter;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.queries.Query;

public class ComboLabelProvider implements TransactionListener {

	protected ValueLookup lookup;
	protected ModelElementRef targetValueRef;
	protected ModelElementRef lookupValueRef;
	protected ModelElementRef displayValueRef;
	protected StringRowValueConverter converter = new StringRowValueConverter();
	protected Map<Object, String> cachedLabels = new HashMap<>();// change is used
	                                                           // only for query
	                                                           // per row
	                                                           // configuration
	protected Registration transactionListenerRegistration;
	protected Runnable onLabelCacheChange;

	public ComboLabelProvider() {
		super();
	}

	public ModelElementRef getTargetValueRef() {
		return targetValueRef;
	}

	public void setTargetValueRef(ModelElementRef aValue) {
		targetValueRef = aValue;
		init();
	}

	public ModelElementRef getLookupValueRef() {
		return lookupValueRef;
	}

	public void setLookupValueRef(ModelElementRef aValue) {
		lookupValueRef = aValue;
		init();
	}

	public ModelElementRef getDisplayValueRef() {
		return displayValueRef;
	}

	public void setDisplayValueRef(ModelElementRef aValue) {
		displayValueRef = aValue;
		init();
	}

	protected void init() {
		if (lookup == null && lookupValueRef != null && displayValueRef != null && targetValueRef != null && targetValueRef.getColIndex() > 0) {
			lookup = new ValueLookup(targetValueRef.getColIndex(), lookupValueRef, displayValueRef);
		}
	}

	public Runnable getOnLabelCacheChange() {
		return onLabelCacheChange;
	}

	public void setOnLabelCacheChange(Runnable aOnLabelCacheChange) {
		onLabelCacheChange = aOnLabelCacheChange;
	}

	@Override
	public void commited() throws Exception {
		cachedLabels.clear();
		if (transactionListenerRegistration != null)
			transactionListenerRegistration.remove();
		if (onLabelCacheChange != null)
			onLabelCacheChange.run();
	}

	@Override
	public void rolledback() throws Exception {
		cachedLabels.clear();
		if (transactionListenerRegistration != null)
			transactionListenerRegistration.remove();
		if (onLabelCacheChange != null)
			onLabelCacheChange.run();
	}

	public String getLabel(final Object aValue) {
		if (aValue != null) {
			try {
				init();
				if (lookup != null) {
					String toRender = null;
					try {
						Row row = lookup.lookupRow(aValue);
						if (row != null) {
							Object value = row.getColumnObject(displayValueRef.getColIndex());
							if (value != null)
								toRender = converter.convert(value);
						} else if (!lookupValueRef.isField && lookupValueRef.entity == displayValueRef.entity && onLabelCacheChange != null && lookupValueRef.field != null) {
							if (cachedLabels.containsKey(aValue))
								toRender = cachedLabels.get(aValue);
							else {
								Query query = lookupValueRef.entity.getQuery().copy();
								query.setClient(lookupValueRef.entity.getQuery().getClient());
								Parameter ourParam = query.getParameters().get(lookupValueRef.field.getName());
								if (ourParam != null) {
									ourParam.setValue(aValue);
									query.execute(new RowsetCallbackAdapter() {

										@Override
										protected void doWork(Rowset aRowset) throws Exception {
											String displayValue = "";
											if (aRowset.first()) {
												displayValue = aRowset.getString(displayValueRef.getColIndex());
											}
											cachedLabels.put(aValue, displayValue);
											if (onLabelCacheChange != null)
												onLabelCacheChange.run();
											if (transactionListenerRegistration == null)
												transactionListenerRegistration = targetValueRef.entity.getModel().addTransactionListener(ComboLabelProvider.this);
										}

									}, null);
								}
							}
						}
					} catch (Exception e) {
						toRender = e.getMessage();
					}
					return toRender;
				} else
					return null;
			} catch (Exception e) {
				Logger.getLogger(ComboLabelProvider.class.getName()).log(Level.SEVERE, e.getMessage(), e);
				return null;
			}
		} else
			return null;
	}
}
