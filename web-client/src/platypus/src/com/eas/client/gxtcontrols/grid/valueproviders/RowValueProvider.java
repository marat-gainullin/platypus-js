package com.eas.client.gxtcontrols.grid.valueproviders;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.eas.client.gxtcontrols.converters.RowValueConverter;
import com.eas.client.gxtcontrols.model.ModelElementRef;
import com.eas.client.model.Entity;
import com.sencha.gxt.core.client.ValueProvider;

public class RowValueProvider<N> implements ValueProvider<Row, N>, ChangesHost {

	protected Entity rowsEntity;
	protected ModelElementRef columnRef;
	protected RowValueConverter<N> converter;

	public RowValueProvider(Entity aRowsEntity, ModelElementRef aColumnRef,
			RowValueConverter<N> aConverter) {
		super();
		rowsEntity = aRowsEntity;
		columnRef = aColumnRef;
		converter = aConverter;
	}

	public boolean isChanged(Row aRow) {
		if (columnRef != null && columnRef.getColIndex() > 0) {
			try {
				if (rowsEntity == columnRef.entity) {
					return aRow.isColumnUpdated(columnRef.getColIndex());
				} else {
					if (rowsEntity.scrollTo(aRow)
							&& columnRef.entity.getRowset() != null) {
						return columnRef.entity.getRowset().getCurrentRow()
								.isColumnUpdated(columnRef.getColIndex());
					}
				}
			} catch (Exception e) {
				Logger.getLogger(RowValueProvider.class.getName()).log(
						Level.SEVERE, e.getMessage());
			}
		}
		return false;
	}

	@Override
	public N getValue(Row aRow) {
		if (columnRef != null && columnRef.getColIndex() > 0) {
			try {
				if (rowsEntity == columnRef.entity) {
					return converter.convert(aRow.getColumnObject(columnRef
							.getColIndex()));
				} else {
					if (rowsEntity.scrollTo(aRow)
							&& columnRef.entity.getRowset() != null) {
						Object value = columnRef.entity.getRowset().getObject(
								columnRef.getColIndex());
						return converter.convert(value);
					}
				}
			} catch (Exception e) {
				Logger.getLogger(RowValueProvider.class.getName()).log(
						Level.SEVERE, e.getMessage());
			}
		}
		return null;
	}

	@Override
	public void setValue(Row aRow, N value) {
		if (columnRef != null && columnRef.getColIndex() > 0) {
			try {
				if (rowsEntity == columnRef.entity) {
					aRow.setColumnObject(columnRef.getColIndex(), value);
				} else {
					if (rowsEntity.scrollTo(aRow)
							&& columnRef.entity.getRowset() != null) {
						columnRef.entity.getRowset().updateObject(
								columnRef.getColIndex(), value);
					}
				}
			} catch (Exception e) {
				Logger.getLogger(RowValueProvider.class.getName()).log(
						Level.SEVERE, e.getMessage());
			}
		}
	}

	@Override
	public String getPath() {
		return null;
	}
}
