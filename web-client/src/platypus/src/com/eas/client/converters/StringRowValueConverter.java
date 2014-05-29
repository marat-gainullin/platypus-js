package com.eas.client.converters;

import java.util.Date;

public class StringRowValueConverter implements RowValueConverter<String> {

	@Override
	public String convert(Object aValue) {
		if (aValue instanceof Number) {
			return ((Number) aValue).toString();
		} else if (aValue instanceof String) {
			return (String) aValue;
		} else if (aValue instanceof Boolean) {
			Boolean b = (Boolean) aValue;
			return Boolean.TRUE.equals(b) ? b.toString() : "";
		} else if (aValue instanceof Date)
			return ((Date) aValue).toString();
		else
			return null;
	}

}
