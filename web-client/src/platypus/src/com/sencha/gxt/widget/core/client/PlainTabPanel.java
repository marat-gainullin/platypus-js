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
 * A {@link TabPanel} with a plain tab bar, with no background behind each tab.
 */
public class PlainTabPanel extends TabPanel {

  /**
   * An appearance applicable to a {@link PlainTabPanel}.
   *
   * This interface exists so that the appropriate appearance can be substituted
   * through deferred binding depending on the theme in use. For example, a
   * blue-colored theme could substitute a blue {@code PlainTabPanel}
   * appearance. In functionality, a {@link PlainTabPanelAppearance} is
   * equivalent to a {@link TabPanel.TabPanelAppearance}.
   */
  public interface PlainTabPanelAppearance extends TabPanelAppearance {
  }

  /**
   * Creates a plain tab panel with the default appearance.
   */
  public PlainTabPanel() {
    super(GWT.<PlainTabPanelAppearance> create(PlainTabPanelAppearance.class));
  }

  /**
   * Creates a plain tab panel with the specified appearance.
   * 
   * @param appearance the appearance of the plain tab panel
   */
  public PlainTabPanel(PlainTabPanelAppearance appearance) {
    super(appearance);
  }

}
