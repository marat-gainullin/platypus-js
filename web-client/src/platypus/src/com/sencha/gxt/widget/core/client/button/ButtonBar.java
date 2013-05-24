/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.button;

import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * A horizontal row of buttons.
 */
public class ButtonBar extends ToolBar {

  /**
   * Creates a left aligned button bar.
   */
  public ButtonBar() {
    super();
    setSpacing(5);
    removeStyleName("x-toolbar-mark");
  }

}
