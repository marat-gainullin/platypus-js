package com.eas.client.converters;

import com.bearsoft.rowset.Row;

public class RowRowValueConverter implements RowValueConverter<Row> {

	@Override
	public Row convert(Object aValue) {
		return aValue instanceof Row ? (Row) aValue : null;
	}
}
