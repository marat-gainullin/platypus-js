/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.field;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.cell.core.client.form.ValueBaseInputCell.ValueBaseFieldAppearance;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.form.Field.FieldStyles;

public abstract class ValueBaseFieldDefaultAppearance implements ValueBaseFieldAppearance {

  public interface ValueBaseFieldResources {

    ValueBaseFieldStyle css();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal, preventInlining = true)
    ImageResource invalidLine();

  }

  public interface ValueBaseFieldStyle extends CssResource, FieldStyles {

    String empty();

    String field();

    String readonly();

    String wrap();

  }

  private final ValueBaseFieldResources res;
  private final ValueBaseFieldStyle style;

  public ValueBaseFieldDefaultAppearance(ValueBaseFieldResources resources) {
    this.res = resources;
    this.style = res.css();

    StyleInjectorHelper.ensureInjected(this.style, true);
  }

  @Override
  public void onEmpty(Element parent, boolean empty) {
    getInputElement(parent).setClassName(style.empty(), empty);
  }

  @Override
  public void onFocus(Element parent, boolean focus) {
    getInputElement(parent).setClassName(style.focus(), focus);
  }

  @Override
  public void onValid(Element parent, boolean valid) {
    getInputElement(parent).setClassName(style.invalid(), !valid);
  }

  @Override
  public void setReadOnly(Element parent, boolean readOnly) {
    getInputElement(parent).<InputElement> cast().setReadOnly(readOnly);
    getInputElement(parent).setClassName(style.readonly(), readOnly);
  }

}
