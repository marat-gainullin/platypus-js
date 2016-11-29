package com.eas.widgets.boxes;

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

	protected static class RegExpFormat {
		protected String regexpPattern;

		public RegExpFormat(String aPattern) {
			super();
			regexpPattern = aPattern;
		}

		public String format(String aValue) {
			return aValue;
		}

		public String parse(String aText) throws ParseException {
			if (!aText.matches(regexpPattern))
				throw new ParseException("A text doesn't satisfy a regexp pattern: " + regexpPattern, 0);
			return aText;
		}
	}

	protected static class BypassFormat {

		public BypassFormat() {
			super();
		}

		public String format(String aValue) {
			return aValue;
		}

		public String parse(String aText) {
			return aText;
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
	/**
	 * Regexp format (type).
	 */
	public static final int REGEXP = 6;
	/**
	 * Bypass format (type).
	 */
	public static final int TEXT = 7;

	protected int type = TEXT;
	protected String pattern;

	protected NumberFormat numberFormat;
	protected DateTimeFormat dateFormat;
	protected MaskFormat maskFormat;
	protected RegExpFormat regExpFormat;
	protected BypassFormat bypassFormat;

	public ObjectFormat() {
		super();
	}

	public ObjectFormat(int aType) throws ParseException {
		super();
		setValueType(aType);
	}

	public int getValueType() {
		return type;
	}

	public void setValueType(int aType) throws ParseException {
		type = aType;
		constructFormat();
	}

	public void setFormatTypeByValue(Object aValue) throws ParseException {
		if (aValue instanceof String) {
			type = TEXT;
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
		regExpFormat = null;
		bypassFormat = null;
		if (type == MASK) {
			if (pattern != null && !pattern.isEmpty())
				maskFormat = new MaskFormat(pattern);
		} else if (type == DATE || type == TIME) {
			if (pattern != null && !pattern.isEmpty())
				dateFormat = DateTimeFormat.getFormat(pattern);
		} else if (type == NUMBER) {
			if (pattern != null && !pattern.isEmpty())
				numberFormat = NumberFormat.getFormat(pattern);
			else
				numberFormat = NumberFormat.getDecimalFormat();
		} else if (type == PERCENT) {
			if (pattern != null && !pattern.isEmpty())
				numberFormat = new PercentFormat(pattern);
			else
				numberFormat = NumberFormat.getPercentFormat();
		} else if (type == CURRENCY) {
			if (pattern != null && !pattern.isEmpty())
				numberFormat = NumberFormat.getCurrencyFormat();
			else
				numberFormat = new CurrencyFormat(pattern);
		} else if (type == REGEXP) {
			if (pattern != null && !pattern.isEmpty())
				regExpFormat = new RegExpFormat(pattern);
		} else if (type == TEXT) {
			bypassFormat = new BypassFormat();
		} else {
			assert false;
		}
	}

	public boolean isEmpty() {
		return numberFormat == null && dateFormat == null && maskFormat == null && regExpFormat == null && bypassFormat == null;
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
			else if (regExpFormat != null)
				return regExpFormat.parse(aView);
			else if (bypassFormat != null)
				return bypassFormat.parse(aView);
			else
				return aView;
		} catch (Exception ex) {
			throw new ParseException(ex.getMessage(), 0);
		}
	}

	public String format(Object aValue) throws ParseException {
		if (numberFormat != null) {
			if (aValue instanceof Number)
				return numberFormat.format((Number) aValue);
			else
				return null;
		} else if (dateFormat != null) {
			if (aValue instanceof Date)
				return dateFormat.format((Date) aValue);
			else
				return null;
		} else if (maskFormat != null) {
			return maskFormat.format(aValue);
		} else if (regExpFormat != null) {
			if (aValue instanceof String)
				return regExpFormat.format((String) aValue);
			else
				return null;
		} else if (bypassFormat != null) {
			return aValue != null ? bypassFormat.format(String.valueOf(aValue)) : null;
		} else {
			return aValue != null ? aValue.toString() : null;
		}
	}

}
