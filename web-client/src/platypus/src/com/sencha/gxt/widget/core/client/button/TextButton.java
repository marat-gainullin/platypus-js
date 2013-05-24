/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.button;

import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * A text button.
 */
public class TextButton extends CellButtonBase<String> {

  protected TextButtonCell cell;

  /**
   * Creates a new text button.
   */
  public TextButton() {
    this(new TextButtonCell());
  }

  /**
   * Creates a new text button.
   * 
   * @param text the button's text
   */
  public TextButton(String text) {
    this(new TextButtonCell(), text);
  }

  /**
   * Creates a new text button.
   * 
   * @param cell the button cell
   * @param text the button's text
   */
  public TextButton(TextButtonCell cell, String text) {
    super(cell, text);
    setText(text);
  }

  /**
   * Creates a new text button.
   * 
   * @param text the button's text
   * @param handler the select handler
   */
  public TextButton(String text, SelectHandler handler) {
    this(text);
    addSelectHandler(handler);
  }

  /**
   * Creates a new text button.
   * 
   * @param text the button's text
   * @param icon the button's icon
   */
  public TextButton(String text, ImageResource icon) {
    this(text);
    setIcon(icon);
  }

  /**
   * Creates a new text button.
   * 
   * @param cell the button's cell
   */
  public TextButton(TextButtonCell cell) {
    super(cell, null);
    this.cell = cell;
  }

}
