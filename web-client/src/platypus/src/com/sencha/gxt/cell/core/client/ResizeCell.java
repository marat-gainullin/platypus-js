/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import java.util.Set;

import com.sencha.gxt.core.client.dom.XElement;

/**
 * <code>AbstractEventCell</code> subclass which implements
 * <code>ResizableCell</code>.
 * 
 * @param <C> the cell data type
 */
public abstract class ResizeCell<C> extends AbstractEventCell<C> implements ResizableCell {

  protected int width = -1;
  protected int height = -1;

  public ResizeCell(Set<String> consumedEvents) {
    super(consumedEvents);
  }

  public ResizeCell(String... consumedEvents) {
    super(consumedEvents);
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public void setWidth(int width) {
    this.width = width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public void setHeight(int height) {
    this.height = height;
  }

  @Override
  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
  }
  
  @Override
  public boolean redrawOnResize() {
    return true;
  }

  @Override
  public void setSize(XElement parent, int width, int height) {
    setWidth(width);
    setHeight(height);
  }
}
