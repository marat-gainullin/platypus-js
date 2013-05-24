/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * A base class for layout containers that can insert widgets and honor the
 * {@link RequiresResize} contract.
 */
public abstract class InsertResizeContainer extends ResizeContainer implements InsertPanel.ForIsWidget {
  @Override
  public void insert(IsWidget w, int beforeIndex) {
    insert(asWidgetOrNull(w), beforeIndex);
  }

  @Override
  public void insert(Widget w, int beforeIndex) {
    super.insert(w, beforeIndex);
  }
}