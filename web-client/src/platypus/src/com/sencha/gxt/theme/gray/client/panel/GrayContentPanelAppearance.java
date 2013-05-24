/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.panel;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.theme.base.client.panel.ContentPanelBaseAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;

public class GrayContentPanelAppearance extends ContentPanelBaseAppearance {

  public interface GrayContentPanelResources extends ContentPanelResources {

    @Source({"com/sencha/gxt/theme/base/client/panel/ContentPanel.css", "GrayContentPanel.css"})
    @Override
    GrayContentPanelStyle style();

  }

  public interface GrayContentPanelStyle extends ContentPanelStyle {

  }

  public GrayContentPanelAppearance() {
    super(GWT.<GrayContentPanelResources> create(GrayContentPanelResources.class),
        GWT.<ContentPanelTemplate> create(ContentPanelTemplate.class));
  }

  public GrayContentPanelAppearance(GrayContentPanelResources resources) {
    super(resources, GWT.<ContentPanelTemplate> create(ContentPanelTemplate.class));
  }

  @Override
  public HeaderDefaultAppearance getHeaderAppearance() {
    return new GrayHeaderAppearance();
  }

}
