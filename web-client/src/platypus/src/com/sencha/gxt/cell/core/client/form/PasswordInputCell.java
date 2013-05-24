/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class PasswordInputCell extends TextInputCell {

  @Override
  public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
    ViewData viewData = checkViewData(context, value);
    String s = (viewData != null) ? viewData.getCurrentValue() : value;

    FieldAppearanceOptions options = new FieldAppearanceOptions(getWidth(), getHeight(), isReadOnly(), getEmptyText());
    options.setName(name);
    options.setEmptyText(getEmptyText());
    options.setDisabled(isDisabled());
    appearance.render(sb, "password", s == null ? "" : s, options);
  }
}
