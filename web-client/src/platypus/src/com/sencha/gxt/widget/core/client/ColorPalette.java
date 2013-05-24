/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.cell.CellComponent;

/**
 * A cell component that displays a palette of colors and allows the user to
 * select one.
 * <p/>
 * To be notified when the user selects a color, add a value change handler. See
 * {@link CellComponent#addValueChangeHandler(ValueChangeHandler)} for more
 * information.
 * <p/>
 * To set the currently selected color, see
 * {@link CellComponent#setValue(Object)}.
 * <p/>
 * To get the currently selected color, see {@link CellComponent#getValue()}.
 */
public class ColorPalette extends CellComponent<String> implements HasSelectionHandlers<String> {

  /**
   * Creates a new color palette with default colors.
   */
  public ColorPalette() {
    this(new ColorPaletteCell());
  }

  /**
   * Creates a new color palette {@link CellComponent} with the specified color
   * palette {@link Cell}.
   * 
   * @param cell the color palette appearance
   */
  public ColorPalette(final ColorPaletteCell cell) {
    super(cell);
  }

  /**
   * Creates a new color palette with the specified colors.
   * 
   * @param colors the colors, each consisting of a six digit hex value in
   *          RRGGBB format
   * @param labels the color names, in the same order as <code>colors</code>
   */
  public ColorPalette(String[] colors, String[] labels) {
    this(new ColorPaletteCell(colors, labels));
  }

  @Override
  public HandlerRegistration addSelectionHandler(SelectionHandler<String> handler) {
    return addHandler(handler, SelectionEvent.getType());
  }

}
