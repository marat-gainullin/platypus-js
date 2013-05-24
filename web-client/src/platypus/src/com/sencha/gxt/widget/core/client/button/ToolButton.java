/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiConstructor;
import com.sencha.gxt.widget.core.client.button.Tools.ToolStyle;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * A {@link IconButton} that supports a set of predefined styles, see
 * {@link IconButton.IconConfig}.
 */
public class ToolButton extends IconButton {

  private static ToolStyle style = GWT.<Tools> create(Tools.class).icons();

  public static IconConfig CLOSE = new IconConfig(style.close(), style.closeOver());
  public static IconConfig COLLAPSE = new IconConfig(style.collapse(), style.collapseOver());
  public static IconConfig DOUBLEDOWN = new IconConfig(style.doubleDown(), style.doubleDownOver());
  public static IconConfig DOUBLELEFT = new IconConfig(style.doubleLeft(), style.doubleLeftOver());
  public static IconConfig DOUBLERIGHT = new IconConfig(style.doubleRight(), style.doubleRightOver());
  public static IconConfig DOUBLEUP = new IconConfig(style.doubleUp(), style.doubleUpOver());
  public static IconConfig DOWN = new IconConfig(style.down(), style.downOver());
  public static IconConfig EXPAND = new IconConfig(style.expand(), style.expandOver());
  public static IconConfig GEAR = new IconConfig(style.gear(), style.gearOver());
  public static IconConfig LEFT = new IconConfig(style.left(), style.leftOver());
  public static IconConfig MAXIMIZE = new IconConfig(style.maximize(), style.maximizeOver());
  public static IconConfig MINIMIZE = new IconConfig(style.minimize(), style.minimizeOver());
  public static IconConfig MINUS = new IconConfig(style.minus(), style.minusOver());
  public static IconConfig PIN = new IconConfig(style.pin(), style.pinOver());
  public static IconConfig UNPIN = new IconConfig(style.unpin(), style.unpinOver());
  public static IconConfig PLUS = new IconConfig(style.plus(), style.plusOver());
  public static IconConfig PRINT = new IconConfig(style.print(), style.printOver());
  public static IconConfig QUESTION = new IconConfig(style.question(), style.questionOver());
  public static IconConfig REFRESH = new IconConfig(style.refresh(), style.refreshOver());
  public static IconConfig RESTORE = new IconConfig(style.restore(), style.restoreOver());
  public static IconConfig RIGHT = new IconConfig(style.right(), style.rightOver());
  public static IconConfig SAVE = new IconConfig(style.save(), style.saveOver());
  public static IconConfig SEARCH = new IconConfig(style.search(), style.searchOver());
  public static IconConfig UP = new IconConfig(style.up(), style.upOver());

  /**
   * Creates a new tool button.
   * 
   * @param appearance the button appearance
   * @param config the icon configuration
   */
  public ToolButton(IconButtonAppearance appearance, IconConfig config) {
    super(appearance, config);
  }

  /**
   * Creates a new icon button.
   * 
   * @param config the icon configuration
   */
  @UiConstructor
  public ToolButton(IconConfig config) {
    super(config);
  }

  /**
   * Creates a new tool button.
   * 
   * @param config the icon configuration
   * @param handler the select handler
   */
  public ToolButton(IconConfig config, SelectHandler handler) {
    super(config);
    addSelectHandler(handler);
  }

  /**
   * Creates a new tool button. The 'over' style and 'disabled' style names
   * determined by adding '-over' and '-disabled' to the base style name.
   * 
   * @param style the style name
   */
  public ToolButton(String style) {
    this(new IconConfig(style));
  }

}
