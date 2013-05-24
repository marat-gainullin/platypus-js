/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.cell.CellComponent;

/**
 * Interface for cells that can be sized.
 * 
 * <p />
 * {@link CellComponent} will "size" any cells which implement this interface.
 */
public interface ResizableCell {

  /**
   * Returns the height.
   * 
   * @return the height
   */
  public int getHeight();

  /**
   * Returns the width.
   * 
   * @return the width
   */
  public int getWidth();

  /**
   * Determines if the cell should be redrawn when resized by @link
   * {@link CellComponent}. If true, {@link CellComponent#redraw()} will be
   * called.
   * 
   * @return true to force a redraw
   */
  public boolean redrawOnResize();

  /**
   * Sets the height.
   * 
   * @param height the height in pixels
   */
  public void setHeight(int height);

  /**
   * Sets the cell size.
   * 
   * @param width the width in pixels
   * @param height the height in pixels
   */
  public void setSize(int width, int height);

  /**
   * Sets the size of the cell without requiring a redraw. This method is called
   * by {@link CellComponent} when {@link #redrawOnResize()} returns false.
   * 
   * @param parent the parent element
   * @param width the width
   * @param height the height
   */
  public void setSize(XElement parent, int width, int height);

  /**
   * Sets the width.
   * 
   * @param width the width in pixels
   */
  public void setWidth(int width);
}
