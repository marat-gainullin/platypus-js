/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid.filters;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.NumberFilterHandler;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.grid.filters.RangeMenu.RangeItem;

/**
 * Filter class for numeric fields. by default, converts data to {@code double}
 * before comparing, but this behavior can be change by overriding
 * {@link #equals(Number, Number)} and {@link #greaterThan(Number, Number)}. See
 * {@link Filter} for more information.
 * 
 * @param <M> the model in the store and in each grid row
 * @param <V> the numeric type in the column to filter
 */
public class NumericFilter<M, V extends Number> extends Filter<M, V> {

  /**
   * The default locale-sensitive messages used by this class.
   */
  public class DefaultNumericFilterMessages implements NumericFilterMessages {
    @Override
    public String emptyText() {
      return DefaultMessages.getMessages().numericFilter_emptyText();
    }
  }

  /**
   * The locale-sensitive messages used by this class.
   */
  public interface NumericFilterMessages {
    String emptyText();
  }

  private final NumberPropertyEditor<V> propertyEditor;
  private List<RangeItem> rangeItems = new ArrayList<RangeItem>();
  private RangeMenu<M, V> rangeMenu;
  private NumericFilterMessages messages = new DefaultNumericFilterMessages();
  private int width = 125;

  /**
   * Creates a numeric filter for the specified value provider. See
   * {@link Filter#Filter(ValueProvider)} for more information.
   * 
   * @param valueProvider the value provider
   * @param propertyEditor property editor for numeric type {@code <V>}
   */
  public NumericFilter(ValueProvider<? super M, V> valueProvider, NumberPropertyEditor<V> propertyEditor) {
    super(valueProvider);

    this.propertyEditor = propertyEditor;

    setHandler(new NumberFilterHandler<V>(propertyEditor));

    rangeItems.add(RangeItem.LESSTHAN);
    rangeItems.add(RangeItem.GREATERTHAN);
    rangeItems.add(RangeItem.EQUAL);

    rangeMenu = new RangeMenu<M, V>(this);
    menu = rangeMenu;
    rangeMenu.setRangeItems(rangeItems);

    setWidth(getWidth());
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<FilterConfig> getFilterConfig() {
    return (List<FilterConfig>) getValue();
  }

  /**
   * Returns the locale-sensitive messages used by this class.
   * 
   * @return the local-sensitive messages used by this class.
   */
  public NumericFilterMessages getMessages() {
    return messages;
  }

  @Override
  public Object getValue() {
    return rangeMenu.getValue();
  }

  /**
   * Returns the width used for the range sub-menu.
   * 
   * @return the width used for the range sub-menu
   */
  public int getWidth() {
    return width;
  }

  @Override
  public boolean isActivatable() {
    if (rangeMenu.eq != null && rangeMenu.eq.getCurrentValue() != null) {
      return true;
    }
    if (rangeMenu.lt != null && rangeMenu.lt.getCurrentValue() != null) {
      return true;
    }
    if (rangeMenu.gt != null && rangeMenu.gt.getCurrentValue() != null) {
      return true;
    }
    return false;
  }

  public void setMessages(NumericFilterMessages messages) {
    this.messages = messages;
    rangeMenu.setEmptyText(messages.emptyText());
  }

  /**
   * Sets the width to use for the range sub-menu (defaults to 125).
   * 
   * @param width the width used for the range sub-menu.
   */
  public void setWidth(int width) {
    this.width = width;
    rangeMenu.setFieldWidth(width);
  }

  /**
   * Compares the two values for equality. Can be overridden to provide an
   * equality check that allows for floating point approximation issues, such as
   * using an epsilon value to allow them to be the same within a few decimal
   * points:
   * 
   * <code><pre>
return Math.abs(val.doubleValue() - userVal.doubleValue()) < epsilon * val.doubleValue();
</code></pre>
   * 
   * where {@code epsilon} would represent the magnitude of difference between
   * the two values. If the magnitude of the values is known, this could instead
   * be <code><pre>
return Math.abs(val.doubleValue() - userVal.doubleValue()) < epsilon;
</code></pre> where {@code epsilon} is instead the maximum difference
   * allowed.
   * 
   * @param a
   * @param b
   * @return true if the two values should be considered to be equal for the
   *         purposes of filtering
   */
  protected boolean equals(V a, V b) {
    return a.equals(b.doubleValue());
  }

  protected NumberPropertyEditor<V> getPropertyEditor() {
    return propertyEditor;
  }

  @Override
  protected Class<V> getType() {
    return null;
  }

  /**
   * Compares the values given, and returns true if the first is greater than
   * the second. Default implementation converts to {@code double} before
   * comparing.
   * 
   * @param a
   * @param b
   * @return true if the second parameter is greater than the first parameter
   */
  protected boolean greaterThan(V a, V b) {
    return Double.compare(a.doubleValue(), b.doubleValue()) > 0;
  }

  protected boolean validateModel(M model) {
    V val = getValueProvider().getValue(model);

    if (rangeMenu.eq != null) {
      V userVal = rangeMenu.eq.getCurrentValue();
      if (userVal != null && (val == null || !equals(val, userVal))) {
        return false;
      }
    }

    if (rangeMenu.lt != null) {
      V userVal = rangeMenu.lt.getCurrentValue();
      if (userVal != null && (val == null || greaterThan(val, userVal))) {
        return false;
      }
    }

    if (rangeMenu.gt != null) {
      V userVal = rangeMenu.gt.getCurrentValue();
      if (userVal != null && (val == null || greaterThan(userVal, val))) {
        return false;
      }
    }
    return true;
  }

}
