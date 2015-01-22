package com.eas.client.converters;

import java.util.Date;

public class DateValueConverter implements ValueConverter<Date> {

	@Override
	public Date convert(Object aValue) {
		if (aValue instanceof Number) {
			return new Date(((Number) aValue).longValue());
		} else if (aValue instanceof String) {
			return new Date(Long.valueOf((String) aValue));
		} else if (aValue instanceof Boolean) {
			return new Date(Long.valueOf(((Boolean) aValue) ? 1 : 0));
		} else if (aValue instanceof Date) {
			return ((Date) aValue);
		} else {
			return null;
		}
	}
}
