/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.blue.client.box;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.sencha.gxt.theme.blue.client.window.BlueWindowAppearance;

public class BlueMessageBoxAppearance extends BlueWindowAppearance {

  public interface BlueMessageBoxResources extends BlueWindowResources, ClientBundle {

    @Source({
        "com/sencha/gxt/theme/base/client/panel/ContentPanel.css",
        "com/sencha/gxt/theme/blue/client/window/BlueWindow.css"})
    @Override
    BlueWindowStyle style();

  }

  public BlueMessageBoxAppearance() {
    super((BlueMessageBoxResources) GWT.create(BlueMessageBoxResources.class));
  }
}
