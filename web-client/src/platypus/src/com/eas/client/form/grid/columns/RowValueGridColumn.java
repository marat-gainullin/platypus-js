package com.eas.client.form.grid.columns;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.gwt.ui.widgets.grid.GridColumn;
import com.bearsoft.rowset.Row;
import com.eas.client.converters.RowValueConverter;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.model.Entity;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;

public class RowValueGridColumn<N> extends GridColumn<Row, N> implements FieldUpdater<Row, N>, ChangesHost {

	protected Entity rowsEntity;
	protected ModelElementRef columnRef;
	protected RowValueConverter<N> converter;

	public RowValueGridColumn(Cell<N> aCell, Entity aRowsEntity, ModelElementRef aColumnRef, RowValueConverter<N> aConverter) {
		super(aCell);
		rowsEntity = aRowsEntity;
		columnRef = aColumnRef;
		converter = aConverter;
		setFieldUpdater(this);
	}

	public boolean isChanged(Row aRow) {
		if (aRow != null && columnRef != null && columnRef.getColIndex() > 0) {
			try {
				if (rowsEntity == columnRef.entity) {
					return aRow.isColumnUpdated(columnRef.getColIndex());
				} else {
					if (rowsEntity.scrollTo(aRow) && columnRef.entity.getRowset() != null) {
						return columnRef.entity.getRowset().getCurrentRow().isColumnUpdated(columnRef.getColIndex());
					}
				}
			} catch (Exception e) {
				Logger.getLogger(RowValueGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
		return false;
	}

	@Override
	public N getValue(Row aRow) {
		if (aRow != null && columnRef != null && columnRef.getColIndex() > 0) {
			try {
				if (rowsEntity == columnRef.entity) {
					return converter.convert(aRow.getColumnObject(columnRef.getColIndex()));
				} else {
					if (rowsEntity.scrollTo(aRow) && columnRef.entity.getRowset() != null) {
						Object value = columnRef.entity.getRowset().getObject(columnRef.getColIndex());
						return converter.convert(value);
					}
				}
			} catch (Exception e) {
				Logger.getLogger(RowValueGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
		return null;
	}

	@Override
	public void update(int aIndex, Row aRow, N value) {
		if (aRow != null && columnRef != null && columnRef.getColIndex() > 0) {
			try {
				if (rowsEntity == columnRef.entity) {
					aRow.setColumnObject(columnRef.getColIndex(), value);
				} else {
					if (rowsEntity.scrollTo(aRow) && columnRef.entity.getRowset() != null) {
						columnRef.entity.getRowset().updateObject(columnRef.getColIndex(), value);
					}
				}
			} catch (Exception e) {
				Logger.getLogger(RowValueGridColumn.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}
}
