package com.eas.client.converters;

import java.util.Date;

public class BooleanValueConverter implements ValueConverter<Boolean> {

	@Override
	public Boolean convert(Object aValue) {
		if (aValue instanceof Number) {
			return ((Number) aValue).intValue() != 0;
		} else if (aValue instanceof String) {
			String s = (String) aValue;
			return !s.isEmpty();
		} else if (aValue instanceof Boolean) {
			return ((Boolean) aValue);
		} else if (aValue instanceof Date) {
			return !((Date) aValue).equals(new Date(0));
		} else
			return null;
	}
}
