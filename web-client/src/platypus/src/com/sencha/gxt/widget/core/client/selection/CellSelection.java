/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.selection;

public class CellSelection<M> {
  private int cell;
  private M model;
  private int row;

  public CellSelection(M model, int row, int cell) {
    this.model = model;
    this.row = row;
    this.cell = cell;
  }

  public int getCell() {
    return cell;
  }

  public M getModel() {
    return model;
  }

  public int getRow() {
    return row;
  }
  
  
}
