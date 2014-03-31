package com.eas.client.form.grid.columns;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.eas.client.converters.RowRowValueConverter;
import com.eas.client.form.combo.ValueLookup;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.model.Entity;
import com.google.gwt.cell.client.Cell;

public class LookupModelGridColumn extends ModelGridColumn<Row> {

	protected ValueLookup lookup;

	protected ModelElementRef lookupValueRef;
	protected ModelElementRef displayValueRef;

	public LookupModelGridColumn(Cell<Row> aCell, String aName, Entity aRowsEntity, ModelElementRef aColumnModelRef, ModelElementRef aLookupValueRef, ModelElementRef aDisplayValueRef) {
		super(aCell, aName, aRowsEntity, aColumnModelRef, new RowRowValueConverter());
		lookupValueRef = aLookupValueRef;
		displayValueRef = aDisplayValueRef;
	}

	protected void init() {
		if (lookup == null)
			lookup = new ValueLookup(columnModelRef.getColIndex(), lookupValueRef, displayValueRef);
	}

	public ValueLookup getLookup() {
		init();
		return lookup;
	}

	public boolean tryInit() {
		if (columnModelRef.entity.getRowset() != null && lookupValueRef.entity.getRowset() != null && displayValueRef.entity.getRowset() != null) {
			ValueLookup lookup = getLookup();
			return lookup != null && lookup.tryInit();
		}
		return false;
	}

	public boolean isChanged(Row aRow) {
		init();
		if (aRow != null && columnModelRef.getColIndex() > 0)
			try {
				if (rowsEntity == columnModelRef.entity) {
					return aRow.isColumnUpdated(columnModelRef.getColIndex());
				} else {
					if (rowsEntity.scrollTo(aRow) && columnModelRef.entity.getRowset() != null) {
						Row lRow = columnModelRef.entity.getRowset().getCurrentRow();
						return lRow.isColumnUpdated(columnModelRef.getColIndex());
					}
				}
			} catch (Exception e) {
				Logger.getLogger(LookupModelGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		return false;
	}

	@Override
	public Row getValue(Row aRow) {
		init();
		if (aRow != null && columnModelRef.getColIndex() > 0)
			try {
				if (rowsEntity == columnModelRef.entity) {
					return lookup.lookupRow(aRow);
				} else {
					if (rowsEntity.scrollTo(aRow) && columnModelRef.entity.getRowset() != null) {
						Row lRow = columnModelRef.entity.getRowset().getCurrentRow();
						return lookup.lookupRow(lRow);
					}
				}
			} catch (Exception e) {
				Logger.getLogger(LookupModelGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		return null;
	}

	@Override
	public void update(int aIndex, Row aRow, Row value) {
		init();
		if (aRow != null && columnModelRef.getColIndex() > 0){
			try {
				if (rowsEntity == columnModelRef.entity) {
					Object lookupKeyValue = value != null ? value.getColumnObject(lookup.getLookupValueRef().getColIndex()) : null;
					aRow.setColumnObject(columnModelRef.getColIndex(), lookupKeyValue);
				} else {
					if (rowsEntity.scrollTo(aRow) && columnModelRef.entity.getRowset() != null) {
						Object lookupKeyValue = value != null ? columnModelRef.entity.getRowset().getObject(lookup.getLookupValueRef().getColIndex()) : null;
						columnModelRef.entity.getRowset().updateObject(columnModelRef.getColIndex(), lookupKeyValue);
					}
				}
			} catch (Exception e) {
				Logger.getLogger(LookupModelGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}
}
