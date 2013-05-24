/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer;

/**
 * A framed panel that is managed by a {@link PortalLayoutContainer}.
 */
public class Portlet extends FramedPanel {

  private boolean pinned = false;

  /**
   * Creates a portlet with the default appearance.
   */
  public Portlet() {
    this(GWT.<FramedPanelAppearance> create(FramedPanelAppearance.class));
  }

  /**
   * Creates a portlet with the specified appearance.
   * 
   * @param appearance the portlet appearance
   */
  public Portlet(FramedPanelAppearance appearance) {
    super(appearance);
    setPinned(false);
  }

  /**
   * Returns true if the portal is pinned.
   * 
   * @return true if pinned
   */
  public boolean isPinned() {
    return pinned;
  }

  /**
   * Sets the pinned state of a portlet (whether the portlet is draggable).
   * 
   * @param pinned true to pin the portlet so that it is not draggable.
   */
  public void setPinned(boolean pinned) {
    this.pinned = pinned;
    Cursor c = pinned ? Cursor.DEFAULT : Cursor.MOVE;
    getHeader().getElement().getStyle().setCursor(c);

    if (getData("gxt.draggable") != null) {
      Draggable d = (Draggable) getData("gxt.draggable");
      d.setEnabled(!pinned);
    }
  }
}
