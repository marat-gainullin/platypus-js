/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import com.google.gwt.core.client.GWT;

public class RadioCell extends CheckBoxCell {

  public interface RadioAppearance extends CheckBoxAppearance {

  }

  protected final RadioAppearance appearance;

  public RadioCell() {
    this(GWT.<RadioAppearance> create(RadioAppearance.class));
  }

  public RadioCell(RadioAppearance appearance) {
    super(appearance);
    this.appearance = appearance;
  }

}
