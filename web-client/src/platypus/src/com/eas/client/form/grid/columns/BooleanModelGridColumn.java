package com.eas.client.form.grid.columns;

import com.eas.client.converters.BooleanRowValueConverter;
import com.google.gwt.cell.client.CheckboxCell;

public class BooleanModelGridColumn extends ModelGridColumn<Boolean> {

	public BooleanModelGridColumn(String aName) {
		super(new CheckboxCell(), aName, null, null, new BooleanRowValueConverter());
	}
}
