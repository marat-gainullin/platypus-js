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
import com.sencha.gxt.theme.base.client.menu.CheckMenuItemBaseAppearance;
import com.sencha.gxt.theme.gray.client.menu.GrayMenuItemAppearance.GrayMenuItemResources;

public class GrayCheckMenuItemAppearance extends CheckMenuItemBaseAppearance {

  public interface GrayCheckMenuItemResources extends CheckMenuItemResources, GrayMenuItemResources, ClientBundle {

    @Source({"com/sencha/gxt/theme/base/client/menu/CheckMenuItem.css", "GrayCheckMenuItem.css"})
    GrayCheckMenuItemStyle checkStyle();

  }

  public interface GrayCheckMenuItemStyle extends CheckMenuItemStyle {
  }

  public GrayCheckMenuItemAppearance() {
    this(GWT.<GrayCheckMenuItemResources> create(GrayCheckMenuItemResources.class),
        GWT.<MenuItemTemplate> create(MenuItemTemplate.class));
  }

  public GrayCheckMenuItemAppearance(GrayCheckMenuItemResources resources, MenuItemTemplate template) {
    super(resources, template);
  }

}
