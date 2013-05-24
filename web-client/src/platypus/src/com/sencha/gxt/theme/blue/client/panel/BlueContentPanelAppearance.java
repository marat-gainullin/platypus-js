/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.blue.client.panel;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.panel.ContentPanelBaseAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;

public class BlueContentPanelAppearance extends ContentPanelBaseAppearance {

  public interface BlueContentPanelResources extends ContentPanelResources {

    @Source({"com/sencha/gxt/theme/base/client/panel/ContentPanel.css", "BlueContentPanel.css"})
    @Override
    BlueContentPanelStyle style();

  }

  public interface BlueContentPanelStyle extends ContentPanelStyle {

  }

  public BlueContentPanelAppearance() {
    super(GWT.<BlueContentPanelResources> create(BlueContentPanelResources.class),
        GWT.<ContentPanelTemplate> create(ContentPanelTemplate.class));
  }

  public BlueContentPanelAppearance(BlueContentPanelResources resources) {
    super(resources, GWT.<ContentPanelTemplate> create(ContentPanelTemplate.class));
  }

  @Override
  public HeaderDefaultAppearance getHeaderAppearance() {
    return new BlueHeaderAppearance();
  }

}
