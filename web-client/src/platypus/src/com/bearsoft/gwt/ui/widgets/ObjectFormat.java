package com.bearsoft.gwt.ui.widgets;

import java.text.ParseException;
import java.util.Date;

import com.google.gwt.i18n.client.CurrencyList;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

public class ObjectFormat {

	protected static class PercentFormat extends NumberFormat {
		public PercentFormat(String aPattern) {
			super(aPattern, CurrencyList.get().getDefault(), true);
		}
	}

	protected static class CurrencyFormat extends NumberFormat {
		public CurrencyFormat(String aPattern) {
			super(aPattern, CurrencyList.get().getDefault(), true);
		}
	}
	
	public static final String DEFAULT_NUMBER_PATTERN = "#,##0.###";
	public static final String DEFAULT_DATE_PATTERN = "dd.MM.yyyy";
	public static final String DEFAULT_TIME_PATTERN = "H:mm:ss";
	public static final String DEFAULT_PERCENT_PATTERN = "#,##0%";
	public static final String DEFAULT_CURRENCY_PATTERN = "#,##0.## ¤";
	public static final String DEFAULT_MASK_PATTERN = "###-####";

	/**
	 * Number format (type).
	 */
	public static final int NUMBER = 0;
	/**
	 * Date format (type).
	 */
	public static final int DATE = 1;
	/**
	 * Time format (type).
	 */
	public static final int TIME = 2;
	/**
	 * Percent format (type).
	 */
	public static final int PERCENT = 3;
	/**
	 * Currency format (type).
	 */
	public static final int CURRENCY = 4;
	/**
	 * Mask format (type).
	 */
	public static final int MASK = 5;

	protected int type = MASK;
	protected String pattern;

	protected NumberFormat numberFormat;
	protected DateTimeFormat dateFormat;
	protected MaskFormat maskFormat;

	public ObjectFormat(int aType, String aPattern) throws ParseException {
		super();
		type = aType;
		pattern = aPattern;
		constructFormat();
	}

	public ObjectFormat(Object aValue) throws ParseException {
		super();
		if (aValue instanceof String) {
			type = MASK;
			pattern = DEFAULT_MASK_PATTERN;
		} else if (aValue instanceof Date) {
			type = DATE;
			pattern = DEFAULT_DATE_PATTERN;
		} else if (aValue instanceof Number) {
			type = NUMBER;
			pattern = DEFAULT_NUMBER_PATTERN;
		}
		constructFormat();
	}

	protected void constructFormat() throws ParseException {
		numberFormat = null;
		dateFormat = null;
		maskFormat = null;
		if (pattern != null && !pattern.isEmpty()) {
			if (type == MASK) {
				maskFormat = new MaskFormat(pattern);
			} else if (type == DATE || type == TIME) {
				dateFormat = DateTimeFormat.getFormat(pattern);
			} else if (type == NUMBER) {
				numberFormat = NumberFormat.getFormat(pattern);
			} else if (type == PERCENT) {
				numberFormat = new PercentFormat(pattern);
			} else if (type == CURRENCY) {
				numberFormat = new CurrencyFormat(pattern);
			} else {
				assert false;
			}
		}
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String aPattern) throws ParseException {
		pattern = aPattern;
		constructFormat();
	}

	public Object parse(String aView) throws ParseException {
		try {
			if (numberFormat != null) {
				double res = numberFormat.parse(aView);
				if (type == PERCENT)
					return res / 100.0;
				else
					return res;
			} else if (dateFormat != null)
				return dateFormat.parse(aView);
			else if (maskFormat != null)
				return maskFormat.parse(aView);
			else
				return aView;
		} catch (Exception ex) {
			throw new ParseException(ex.getMessage(), 0);
		}
	}

	public String format(Object aValue) throws ParseException {
		if (numberFormat != null)
			if (aValue instanceof Number)
				return numberFormat.format((Number) aValue);
			else
				return null;
		else if (dateFormat != null)
			if (aValue instanceof Date)
				return dateFormat.format((Date) aValue);
			else
				return null;
		else if (maskFormat != null)
			return maskFormat.format(aValue);
		else
			return aValue != null ? aValue.toString() : null;
	}

}
