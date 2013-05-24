/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.box;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.sencha.gxt.theme.gray.client.window.GrayWindowAppearance;

public class GrayMessageBoxAppearance extends GrayWindowAppearance {

  public interface GrayMessageBoxResources extends GrayWindowResources, ClientBundle {

    @Source({
        "com/sencha/gxt/theme/base/client/panel/ContentPanel.css",
        "com/sencha/gxt/theme/gray/client/window/GrayWindow.css"})
    @Override
    GrayWindowStyle style();

  }

  public GrayMessageBoxAppearance() {
    super((GrayMessageBoxResources) GWT.create(GrayMessageBoxResources.class));
  }
}
