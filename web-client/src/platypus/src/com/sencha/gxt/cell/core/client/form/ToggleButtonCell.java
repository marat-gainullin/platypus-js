/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.sencha.gxt.cell.core.client.ButtonCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

public class ToggleButtonCell extends ButtonCell<Boolean> {

  protected boolean allowDepress = true;

  /**
   * Creates a toggle button cell with default appearance.
   */
  public ToggleButtonCell() {
    this(GWT.<ButtonCellAppearance<Boolean>> create(ButtonCellAppearance.class));
  }

  /**
   * Creates a toggle button cell with the specified appearance.
   * 
   * @param appearance the appearance of the toggle button cell.
   */
  public ToggleButtonCell(ButtonCellAppearance<Boolean> appearance) {
    super(appearance);
  }

  /**
   * Returns the allow depress state.
   * 
   * @return the allow depress state
   */
  public boolean isAllowDepress() {
    return allowDepress;
  }

  /**
   * True to allow a toggle item to be depressed (defaults to true).
   * 
   * @param allowDepress true to allow depressing
   */
  public void setAllowDepress(boolean allowDepress) {
    this.allowDepress = allowDepress;
  }

  @Override
  protected void onClick(final Context context, final XElement p, final Boolean value, NativeEvent event,
      ValueUpdater<Boolean> valueUpdater) {
    if (!isDisableEvents() && fireCancellableEvent(context, new BeforeSelectEvent(context))) {

      if (allowDepress || !value) {
        valueUpdater.update(!value);

        getAppearance().onToggle(p, !value);
        // this call causing focus to be lost and therefore, blur not firing
        // so we change toggle state via appearance without redraw
        // setValue(context, p, !value);
      }

      if (menu != null) {
        showMenu(p);
      }
      fireEvent(context, new SelectEvent(context));
    }
  }

  @Override
  protected void onMouseDown(XElement parent, NativeEvent event) {
    // do nothing
  }
}