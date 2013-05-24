/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.event.ArrowSelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

public class SplitButtonCell extends TextButtonCell {

  public SplitButtonCell() {
    this(GWT.<ButtonCellAppearance<String>> create(ButtonCellAppearance.class));
  }

  public SplitButtonCell(ButtonCellAppearance<String> appearance) {
    super(appearance);
  }

  @Override
  protected void onClick(Context context, XElement p, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
    event.preventDefault();
    // focus();
    // hideToolTip();

    if (!isDisableEvents() && fireCancellableEvent(context, new BeforeSelectEvent(context))) {

      if (isClickOnArrow(p, event)) {
        if (menu != null && !menu.isVisible()) {
          showMenu(p);
        }
        fireEvent(context, new ArrowSelectEvent(context, menu));
      } else {
        fireEvent(context, new SelectEvent(context));
      }
    }
  }

  public boolean isClickOnArrow(XElement p, NativeEvent e) {
    XElement buttonEl = appearance.getButtonElement(p);
    return (getArrowAlign() == ButtonArrowAlign.BOTTOM) ? e.getClientY() > buttonEl.getRegion().getBottom() - 14
        : e.getClientX() > buttonEl.getRegion().getRight() - 14;
  }

}
