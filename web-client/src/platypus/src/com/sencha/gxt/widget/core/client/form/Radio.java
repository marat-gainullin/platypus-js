/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.sencha.gxt.cell.core.client.form.RadioCell;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.util.ToggleGroup;

/**
 * Single radio field. {@link ValueChangeEvent}s are fired when the checkbox
 * state is changed by the user, instead of waiting for a {@link BlurEvent}.
 * Radio grouping is handled automatically by the browser if you give each radio
 * in a group the same name or use {@link ToggleGroup}.
 */
public class Radio extends CheckBox {

  /**
   * Creates a new radio field.
   */
  public Radio() {
    this(new RadioCell());
  }

  /**
   * Creates a new radio field.
   * 
   * @param cell the radio cell
   */
  public Radio(RadioCell cell) {
    super(cell);
  }

  @Override
  public void setName(String name) {
    // can't change name after input element is created
    if (GXT.isIE6() || GXT.isIE7()) {
      this.name = name;
      redraw();
    } else {
      super.setName(name);
    }
  }

}
