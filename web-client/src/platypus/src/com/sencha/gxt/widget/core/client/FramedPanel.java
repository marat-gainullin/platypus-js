/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.core.client.GWT;

/**
 * A ContentPanel that internally uses a @link {@link FramedPanelAppearance}.
 */
public class FramedPanel extends ContentPanel {
  /**
   * The appearance of a framed content panel. A frame content panel is a
   * content panel with a frame.
   */
  public interface FramedPanelAppearance extends ContentPanelAppearance {
  }

  /**
   * Creates a framed content panel with the default appearance.
   */
  public FramedPanel() {
    this(GWT.<FramedPanelAppearance> create(FramedPanelAppearance.class));
  }

  /**
   * Creates a framed content panel with the specified appearance.
   * 
   * @param appearance the appearance of the framed content panel
   */
  public FramedPanel(FramedPanelAppearance appearance) {
    super(appearance);
  }

}
