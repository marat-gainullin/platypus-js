/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.base.client.menu.MenuBarBaseAppearance;

public class GrayMenuBarAppearance extends MenuBarBaseAppearance {

  public interface GrayMenuBarResources extends MenuBarResources, ClientBundle {

    @Source({"com/sencha/gxt/theme/base/client/menu/MenuBar.css", "GrayMenuBar.css"})
    GrayMenuBarStyle css();

    @ImageOptions(repeatStyle=RepeatStyle.Horizontal)
    ImageResource background();

  }

  public interface GrayMenuBarStyle extends MenuBarStyle {
  }

  public GrayMenuBarAppearance() {
    this(GWT.<GrayMenuBarResources> create(GrayMenuBarResources.class));
  }

  public GrayMenuBarAppearance(GrayMenuBarResources resources) {
    super(resources);
  }

}
