package com.eas.client.gxtcontrols.grid.valueproviders;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.eas.client.gxtcontrols.combo.ValueLookup;
import com.eas.client.gxtcontrols.model.ModelElementRef;
import com.eas.client.model.Entity;
import com.sencha.gxt.core.client.ValueProvider;

public class RowRowValueProvider implements ValueProvider<Row, Row>, ChangesHost {

	protected ValueLookup lookup;

	protected Entity rowsEntity;
	protected ModelElementRef columnRef;

	protected ModelElementRef lookupValueRef;
	protected ModelElementRef displayValueRef;

	public RowRowValueProvider(Entity aRowsEntity, ModelElementRef aColumnRef, ModelElementRef aLookupValueRef, ModelElementRef aDisplayValueRef) {
		super();
		rowsEntity = aRowsEntity;
		columnRef = aColumnRef;
		lookupValueRef = aLookupValueRef;
		displayValueRef = aDisplayValueRef;
	}

	protected void init() {
		if (lookup == null)
			lookup = new ValueLookup(columnRef.getColIndex(), lookupValueRef, displayValueRef);
	}

	public ValueLookup getLookup() {
		init();
		return lookup;
	}

	public boolean tryInit() {
		if (columnRef.entity.getRowset() != null && lookupValueRef.entity.getRowset() != null && displayValueRef.entity.getRowset() != null) {
			ValueLookup lookup = getLookup();
			return lookup != null && lookup.tryInit();
		}
		return false;
	}

	public boolean isChanged(Row aRow) {
		init();
		if (columnRef.getColIndex() > 0)
			try {
				if (rowsEntity == columnRef.entity) {
					return aRow.isColumnUpdated(columnRef.getColIndex());
				} else {
					if (rowsEntity.scrollTo(aRow) && columnRef.entity.getRowset() != null) {
						Row lRow = columnRef.entity.getRowset().getCurrentRow();
						return lRow.isColumnUpdated(columnRef.getColIndex());
					}
				}
			} catch (Exception e) {
				Logger.getLogger(RowValueProvider.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		return false;
	}

	@Override
	public Row getValue(Row aRow) {
		init();
		if (columnRef.getColIndex() > 0)
			try {
				if (rowsEntity == columnRef.entity) {
					return lookup.lookupRow(aRow);
				} else {
					if (rowsEntity.scrollTo(aRow) && columnRef.entity.getRowset() != null) {
						Row lRow = columnRef.entity.getRowset().getCurrentRow();
						return lookup.lookupRow(lRow);
					}
				}
			} catch (Exception e) {
				Logger.getLogger(RowValueProvider.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		return null;
	}

	@Override
	public void setValue(Row aRow, Row value) {
		init();
		if (columnRef.getColIndex() > 0)
			try {
				if (rowsEntity == columnRef.entity) {
					Object lookupKeyValue = value != null ? value.getColumnObject(lookup.getLookupValueRef().getColIndex()) : null;
					aRow.setColumnObject(columnRef.getColIndex(), lookupKeyValue);
				} else {
					if (rowsEntity.scrollTo(aRow) && columnRef.entity.getRowset() != null) {
						Object lookupKeyValue = value != null ? columnRef.entity.getRowset().getObject(lookup.getLookupValueRef().getColIndex()) : null;
						columnRef.entity.getRowset().updateObject(columnRef.getColIndex(), lookupKeyValue);
					}
				}
			} catch (Exception e) {
				Logger.getLogger(RowValueProvider.class.getName()).log(Level.SEVERE, e.getMessage());
			}
	}

	@Override
	public String getPath() {
		if (columnRef != null && columnRef.entity != null && columnRef.getColIndex() > 0) {
			return columnRef.entity.getEntityId() + "/" + columnRef.getColIndex();
		} else
			return null;
	}
}
