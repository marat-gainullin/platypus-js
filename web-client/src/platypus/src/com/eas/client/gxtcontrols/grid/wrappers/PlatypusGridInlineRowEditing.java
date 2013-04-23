package com.eas.client.gxtcontrols.grid.wrappers;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.eas.client.gxtcontrols.wrappers.component.PlatypusAdapterCellField;
import com.eas.client.model.Entity;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.widget.core.client.form.IsField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class PlatypusGridInlineRowEditing extends PlatypusGridInlineEditing<Row> {

	protected Entity rowsSource;

	public PlatypusGridInlineRowEditing(Grid<Row> editableGrid, Entity aRowsSource) {
		super(editableGrid);
		rowsSource = aRowsSource;
	}

	public Entity getRowsSource() {
		return rowsSource;
	}

	public void setRowsSource(Entity aValue) {
		rowsSource = aValue;
	}

	@Override
	public <N, O> void addEditor(ColumnConfig<Row, N> columnConfig, Converter<N, O> converter, IsField<O> field) {
		super.addEditor(columnConfig, converter, field);
		if (field instanceof PlatypusAdapterCellField<?>) {
			((PlatypusAdapterCellField<?>) field).setGridEditing(this);
		}
	}

	@Override
	public void removeEditor(ColumnConfig<Row, ?> columnConfig) {
		IsField<?> field = getEditor(columnConfig);
		super.removeEditor(columnConfig);
		if (field instanceof PlatypusAdapterCellField<?>) {
			((PlatypusAdapterCellField<?>) field).setGridEditing(null);
		}
	}

	@Override
	public <O> IsField<O> getEditor(ColumnConfig<Row, ?> columnConfig) {
		if (columnConfig instanceof PlatypusColumnConfig<?, ?> && ((PlatypusColumnConfig<?, ?>) columnConfig).isReadonly())
			return null;
		else
			return super.getEditor(columnConfig);
	}

	@Override
	protected void doDelete(Collection<Row> selected) {
		if (rowsSource != null) {
			Rowset rowset = rowsSource.getRowset();
			if (rowset != null) {
				try {
					rowset.delete(selected);
				} catch (RowsetException ex) {
					Logger.getLogger(PlatypusGridInlineRowEditing.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	@Override
	protected void doInsert() {
		if (rowsSource != null) {
			Rowset rowset = rowsSource.getRowset();
			if (rowset != null) {
				try {
					rowset.insert();
				} catch (RowsetException ex) {
					Logger.getLogger(PlatypusGridInlineRowEditing.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

}
