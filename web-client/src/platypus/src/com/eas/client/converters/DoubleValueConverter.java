package com.eas.client.converters;

import java.util.Date;

public class DoubleValueConverter implements ValueConverter<Double> {

	@Override
	public Double convert(Object aValue) {
		// target type - Double
		if (aValue instanceof Number) {
			return Double.valueOf(((Number) aValue).doubleValue());
		} else if (aValue instanceof String) {
			return Double.valueOf((String) aValue);
		} else if (aValue instanceof Boolean) {
			return Double.valueOf(((Boolean) aValue) ? 1 : 0);
		} else if (aValue instanceof Date) {
			return Double.valueOf(((Date) aValue).getTime());
		} else
			return null;
	}

}
