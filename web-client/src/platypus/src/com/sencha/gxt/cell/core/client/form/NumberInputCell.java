/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.sencha.gxt.widget.core.client.event.XEvent;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;

public class NumberInputCell<N extends Number> extends TwinTriggerFieldCell<N> {

  protected List<Character> allowed;
  protected String decimalSeparator = LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();

  private boolean allowDecimals = true;
  private boolean allowNegative = true;
  private int lastKeyCode;
  private String baseChars = "0123456789";

  public NumberInputCell(NumberPropertyEditor<N> propertyEditor) {
    this(propertyEditor, GWT.<TwinTriggerFieldAppearance> create(TwinTriggerFieldAppearance.class));
  }

  public NumberInputCell(NumberPropertyEditor<N> propertyEditor, TwinTriggerFieldAppearance appearance) {
    super(appearance);
    setPropertyEditor(propertyEditor);

    allowed = new ArrayList<Character>();
    for (int i = 0; i < baseChars.length(); i++) {
      allowed.add(baseChars.charAt(i));
    }
    
    setAllowDecimals(true);
    setAllowNegative(true);
  }

  /**
   * Returns the base characters.
   * 
   * @return the base characters
   */
  public String getBaseChars() {
    return baseChars;
  }

  /**
   * Returns the decimal separator.
   * 
   * @return the decimal separator
   */
  public String getDecimalSeparator() {
    return decimalSeparator;
  }

  @Override
  public NumberPropertyEditor<N> getPropertyEditor() {
    return (NumberPropertyEditor<N>) super.getPropertyEditor();
  }

  /**
   * Returns true of decimal values are allowed.
   * 
   * @return the allow decimal state
   */
  public boolean isAllowDecimals() {
    return allowDecimals;
  }

  /**
   * Returns true if negative values are allowed.
   * 
   * @return the allow negative value state
   */
  public boolean isAllowNegative() {
    return allowNegative;
  }

  /**
   * Sets whether decimal value are allowed (defaults to true).
   * 
   * @param allowDecimals true to allow negative values
   */
  public void setAllowDecimals(boolean allowDecimals) {
    this.allowDecimals = allowDecimals;
    if (allowDecimals) {
      for (int i = 0; i < decimalSeparator.length(); i++) {
        if (!allowed.contains(decimalSeparator.charAt(i))) {
          allowed.add(decimalSeparator.charAt(i));
        }
      }
    } else {
      for (int i = 0; i < decimalSeparator.length(); i++) {
        allowed.remove(Character.valueOf(decimalSeparator.charAt(i)));
      }
    }
  }

  /**
   * Sets whether negative value are allowed to be entered into the field (defaults to true).
   * 
   * @param allowNegative true to allow negative values
   */
  public void setAllowNegative(boolean allowNegative) {
    this.allowNegative = allowNegative;
    if (allowNegative && !allowed.contains('-')) {
      allowed.add('-');
    } else if (!allowNegative) {
      allowed.remove(Character.valueOf('-'));
    }
  }

  /**
   * Sets the base set of characters to evaluate as valid numbers (defaults to
   * '0123456789').
   * 
   * @param baseChars the base characters
   */
  public void setBaseChars(String baseChars) {
    for (int i = 0; i < this.baseChars.length(); i++) {
      if (allowed.contains(this.baseChars.charAt(i))) {
        allowed.remove(allowed.indexOf(this.baseChars.charAt(i)));
      }
    }
    this.baseChars = baseChars;
    for (int i = 0; i < baseChars.length(); i++) {
      allowed.add(baseChars.charAt(i));
    }
  }

  /**
   * Sets the decimal separator (defaults to
   * LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator()).
   * 
   * @param decimalSeparator the decimal separator
   */
  public void setDecimalSeparator(String decimalSeparator) {
    this.decimalSeparator = decimalSeparator;
  }

  @Override
  public void setPropertyEditor(PropertyEditor<N> propertyEditor) {
    assert propertyEditor instanceof NumberPropertyEditor;
    super.setPropertyEditor(propertyEditor);
  }

  @Override
  protected void onKeyDown(Context context, Element parent, N value, NativeEvent event, ValueUpdater<N> valueUpdater) {
    super.onKeyDown(context, parent, value, event, valueUpdater);
    lastKeyCode = event.getKeyCode();
  }

  @Override
  protected void onKeyPress(Context context, Element parent, N value, NativeEvent event, ValueUpdater<N> valueUpdate) {
    super.onKeyPress(context, parent, value, event, valueUpdate);

    char key = (char) event.getCharCode();

    if (event.<XEvent> cast().isSpecialKey(lastKeyCode) || event.getCtrlKey()) {
      return;
    }

    if (!allowed.contains(key)) {
      event.stopPropagation();
      event.preventDefault();
    }
  }
}
