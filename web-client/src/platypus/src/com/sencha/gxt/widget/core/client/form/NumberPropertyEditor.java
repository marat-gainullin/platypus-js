/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.math.BigDecimal;
import java.text.ParseException;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.constants.NumberConstants;

/**
 * A property editor that converts typed numbers to strings and strings back to
 * numbers. Also handles incrementing and decrementing the typed numbers.
 * 
 * @param <N> the number type
 */
public abstract class NumberPropertyEditor<N extends Number> extends PropertyEditor<N> {

  /**
   * A number property editor for use with {@link BigDecimal}.
   */
  public static class BigDecimalPropertyEditor extends NumberPropertyEditor<BigDecimal> {

    /**
     * Creates a number property editor for use with {@link BigDecimal}.
     */
    public BigDecimalPropertyEditor() {
      super(new BigDecimal(1));
    }

    /**
     * Creates a number property editor for use with {@link BigDecimal} that
     * uses the specified format when converting to (or from) a string.
     * 
     * @param format the format to use when converting to (or from) a string
     */
    public BigDecimalPropertyEditor(NumberFormat format) {
      super(format, new BigDecimal(1));
    }

    @Override
    protected BigDecimal doDecr(BigDecimal value) {
      return value.subtract(getIncrement());
    }

    @Override
    protected BigDecimal doIncr(BigDecimal value) {
      return value.add(getIncrement());
    }

    @Override
    protected BigDecimal parseString(String string) {
      // may throw exception
      return new BigDecimal(string);
    }

    @Override
    protected BigDecimal returnTypedValue(Number number) {
      if (number instanceof BigDecimal) {
        return (BigDecimal) number;
      } else {
        return new BigDecimal(number.doubleValue());
      }
    }

  }

  /**
   * A number property editor for use with {@link Double}.
   */
  public static class DoublePropertyEditor extends NumberPropertyEditor<Double> {
    /**
     * Creates a number property editor for use with {@link Double}.
     */
    public DoublePropertyEditor() {
      super(1d);
    }

    /**
     * Creates a number property editor for use with {@link Double} that uses
     * the specified format when converting to (or from) a string.
     * 
     * @param format the format to use when converting to (or from) a string
     */
    public DoublePropertyEditor(NumberFormat format) {
      super(format, 1d);
    }

    @Override
    public Double doDecr(Double value) {
      return value - getIncrement();
    }

    @Override
    public Double doIncr(Double value) {
      return value + getIncrement();
    }

    @Override
    protected Double parseString(String string) {
      return Double.parseDouble(string);
    }

    @Override
    protected Double returnTypedValue(Number number) {
      return number.doubleValue();
    }
  }

  /**
   * A number property editor for use with {@link Float}.
   */
  public static class FloatPropertyEditor extends NumberPropertyEditor<Float> {
    /**
     * Creates a number property editor for use with {@link Float}.
     */
    public FloatPropertyEditor() {
      super(1f);
    }

    /**
     * Creates a number property editor for use with {@link Float} that uses the
     * specified format when converting to (or from) a string.
     * 
     * @param format the format to use when converting to (or from) a string
     */
    public FloatPropertyEditor(NumberFormat format) {
      super(format, 1f);
    }

    @Override
    public Float doDecr(Float value) {
      return value - getIncrement();
    }

    @Override
    public Float doIncr(Float value) {
      return value + getIncrement();
    }

    @Override
    protected Float parseString(String string) {
      return Float.parseFloat(string);
    }

    @Override
    protected Float returnTypedValue(Number number) {
      return number.floatValue();
    }
  }

  /**
   * A number property editor for use with {@link Integer}.
   */
  public static class IntegerPropertyEditor extends NumberPropertyEditor<Integer> {
    /**
     * Creates a number property editor for with {@link Integer}.
     */
    public IntegerPropertyEditor() {
      super(1);
    }

    /**
     * Creates a number property editor for use with {@link Integer} that uses
     * the specified format when converting to (or from) a string.
     * 
     * @param format the format to use when converting to (or from) a string
     */
    public IntegerPropertyEditor(NumberFormat format) {
      super(format, 1);
    }

    @Override
    public Integer doDecr(Integer value) {
      return value - getIncrement();
    }

    @Override
    public Integer doIncr(Integer value) {
      return value + getIncrement();
    }

    @Override
    protected Integer parseString(String string) {
      return Integer.parseInt(string);
    }

    @Override
    protected Integer returnTypedValue(Number number) {
      return number.intValue();
    }
  }
  /**
   * A number property editor for use with {@link Long}.
   */
  public static class LongPropertyEditor extends NumberPropertyEditor<Long> {
    /**
     * Creates a number property editor for use with {@link Long}.
     */
    public LongPropertyEditor() {
      super(1l);
    }

    /**
     * Creates a number property editor for use with {@link Long} that uses the
     * specified format when converting to (or from) a string.
     * 
     * @param format the format to use when converting to (or from) a string
     */
    public LongPropertyEditor(NumberFormat format) {
      super(format, 1l);
    }

    @Override
    public Long doDecr(Long value) {
      return value - getIncrement();
    }

    @Override
    public Long doIncr(Long value) {
      return value + getIncrement();
    }

    @Override
    protected Long parseString(String string) {
      return Long.parseLong(string);
    }

    @Override
    protected Long returnTypedValue(Number number) {
      return number.longValue();
    }
  }
  /**
   * A number property editor for use with {@link Short}.
   */
  public static class ShortPropertyEditor extends NumberPropertyEditor<Short> {

    /**
     * Creates a number property editor for use with {@link Short}.
     */
    public ShortPropertyEditor() {
      super((short) 1);
    }

    /**
     * Creates a number property editor for use with {@link Short} that uses the
     * specified format when converting to (or from) a string.
     * 
     * @param format the format to use when converting to (or from) a string
     */
    public ShortPropertyEditor(NumberFormat format) {
      super(format, (short) 1);
    }

    @Override
    public Short doDecr(Short value) {
      return (short) (value - getIncrement());
    }

    @Override
    public Short doIncr(Short value) {
      return (short) (value + getIncrement());
    }

    @Override
    protected Short parseString(String string) {
      return Short.parseShort(string);
    }

    @Override
    protected Short returnTypedValue(Number number) {
      return number.shortValue();
    }
  }

  protected NumberConstants numbers;
  protected NumberFormat format;
  protected String alphaRegex = "[a-zA-Z]";
  protected String currencySymbolRegex = "\\$";
  protected String groupSeparator;

  private N incrAmount;

  private boolean stripCurrencySymbol;
  private boolean stripAlphas;
  private boolean stripGroupSeparator;

  /**
   * Creates a new number property editor with the default number type (Double).
   */
  public NumberPropertyEditor(N incrAmount) {
    this.incrAmount = incrAmount;
    numbers = LocaleInfo.getCurrentLocale().getNumberConstants();
    groupSeparator = numbers.groupingSeparator();
  }

  /**
   * Creates a new number property editor.
   * 
   * @param format the number format
   */
  public NumberPropertyEditor(NumberFormat format, N incrAmount) {
    this(incrAmount);
    this.format = format;
  }

  /**
   * Creates a new number property editor.
   * 
   * @param pattern the number format pattern
   */
  public NumberPropertyEditor(String pattern, N incrAmount) {
    this(NumberFormat.getFormat(pattern), incrAmount);
  }

  /**
   * Decrements a value by the current increment amount.
   * 
   * @param value the value to decrement
   * @return the decremented value
   */
  public N decr(N value) {
    if (value == null) {
      return doDecr(doDecr(getIncrement()));
    }
    return doDecr(value);
  }

  /**
   * Returns the editor's format.
   * 
   * @return the number format
   */
  public NumberFormat getFormat() {
    return format;
  }

  /**
   * Gets the current increment amount.
   * 
   * @return the current increment amount.
   */
  public N getIncrement() {
    return incrAmount;
  }

  /**
   * Increments a value by the current increment amount.
   * 
   * @param value the value to increment
   * @return the incremented value
   */
  public N incr(N value) {
    if (value == null) {
      return getIncrement();
    }
    return doIncr(value);
  }

  @Override
  public N parse(CharSequence text) throws ParseException {
    String value = text.toString();

    // workaround as GWT NumberFormat stripping -- and not throwing exception
    if (value.length() >= 2 && value.startsWith("--")) {
      throw new ParseException(value + " is not a valid number", 0);
    }

    // first try to create a typed value directly from the raw text
    try {
      return parseString(value);
    } catch (Exception e) {
    }

    // second, strip all unwanted characters
    String stripValue = stripValue(value);
    try {
      return parseString(stripValue);
    } catch (Exception e) {
    }

    try {
      // third try parsing with the formatter
      if (format != null) {
        Double d = format.parse(value);
        return (N) returnTypedValue(d);
      } else {
        Double d = NumberFormat.getDecimalFormat().parse(value);
        return (N) returnTypedValue(d);
      }
    } catch (Exception ex) {
      throw new ParseException(ex.getMessage(), 0);
    }
  }

  @Override
  public String render(Number value) {
    if (format != null) {
      return format.format(value.doubleValue());
    }
    return value.toString();
  }

  /**
   * Sets the editor's format.
   * 
   * @param format the format
   */
  public void setFormat(NumberFormat format) {
    this.format = format;
  }

  /**
   * Sets the increment amount (defaults to zero).
   * 
   * @param value the new increment amount
   */
  public void setIncrement(N value) {
    this.incrAmount = value;
  }

  protected abstract N doDecr(N value);

  protected abstract N doIncr(N value);

  protected abstract N parseString(String string);

  protected abstract N returnTypedValue(Number number);

  protected String stripValue(String value) {
    if (stripCurrencySymbol) {
      value = value.replaceAll(currencySymbolRegex, "");
    }
    if (stripAlphas) {
      value = value.replaceAll(alphaRegex, "");
    }
    if (stripGroupSeparator) {
      value = value.replaceAll(groupSeparator, "");
    }
    return value;
  }
}
