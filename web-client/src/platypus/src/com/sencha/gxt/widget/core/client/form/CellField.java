/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.google.gwt.dom.client.Element;
import com.google.gwt.view.client.ProvidesKey;
import com.sencha.gxt.cell.core.client.form.FieldCell;
import com.sencha.gxt.widget.core.client.cell.CellComponent;

public class CellField<C> extends CellComponent<C> {

  public CellField(FieldCell<C> cell) {
    super(cell);
  }

  public CellField(FieldCell<C> cell, C initialValue) {
    super(cell, initialValue);
  }

  public CellField(FieldCell<C> cell, C initialValue, ProvidesKey<C> keyProvider) {
    super(cell, initialValue, keyProvider);
  }

  public CellField(FieldCell<C> cell, C initialValue, ProvidesKey<C> keyProvider, Element elem) {
    super(cell, initialValue, keyProvider, elem);
  }

  public CellField(FieldCell<C> cell, ProvidesKey<C> keyProvider) {
    super(cell, keyProvider);
  }

  @Override
  protected void onResize(int width, int height) {
    super.onResize(width, height);
    redraw();
  }

}
