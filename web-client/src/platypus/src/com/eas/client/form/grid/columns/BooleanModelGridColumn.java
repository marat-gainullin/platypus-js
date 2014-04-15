package com.eas.client.form.grid.columns;

import com.bearsoft.gwt.ui.widgets.grid.cells.TreeExpandableCell;
import com.bearsoft.rowset.Row;
import com.eas.client.converters.BooleanRowValueConverter;
import com.google.gwt.cell.client.CheckboxCell;

public class BooleanModelGridColumn extends ModelGridColumn<Boolean> {

	public BooleanModelGridColumn(String aName) {
		super(new TreeExpandableCell<Row, Boolean>(new CheckboxCell()), aName, null, null, new BooleanRowValueConverter());
	}
}
