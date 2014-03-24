package com.eas.client.form.grid.columns;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.grid.GridColumn;
import com.bearsoft.rowset.Row;
import com.eas.client.form.combo.ValueLookup;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.model.Entity;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;

public class RowRowValueGridColumn extends GridColumn<Row, Row> implements FieldUpdater<Row, Row>, ChangesHost {

	protected ValueLookup lookup;

	protected Entity rowsEntity;
	protected ModelElementRef columnRef;

	protected ModelElementRef lookupValueRef;
	protected ModelElementRef displayValueRef;

	public RowRowValueGridColumn(Cell<Row> aCell, Entity aRowsEntity, ModelElementRef aColumnRef, ModelElementRef aLookupValueRef, ModelElementRef aDisplayValueRef) {
		super(aCell);
		rowsEntity = aRowsEntity;
		columnRef = aColumnRef;
		lookupValueRef = aLookupValueRef;
		displayValueRef = aDisplayValueRef;
		setFieldUpdater(this);
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
		if (aRow != null && columnRef.getColIndex() > 0)
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
				Logger.getLogger(RowRowValueGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		return false;
	}

	@Override
	public Row getValue(Row aRow) {
		init();
		if (aRow != null && columnRef.getColIndex() > 0)
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
				Logger.getLogger(RowRowValueGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		return null;
	}

	@Override
	public void update(int aIndex, Row aRow, Row value) {
		init();
		if (aRow != null && columnRef.getColIndex() > 0){
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
				Logger.getLogger(RowRowValueGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}
}
