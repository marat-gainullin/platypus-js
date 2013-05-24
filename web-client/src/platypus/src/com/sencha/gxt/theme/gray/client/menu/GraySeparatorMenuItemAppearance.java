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
import com.sencha.gxt.theme.base.client.menu.SeparatorMenuItemBaseAppearance;

public class GraySeparatorMenuItemAppearance extends SeparatorMenuItemBaseAppearance {

  public interface GraySeparatorMenuItemResources extends ClientBundle, SeparatorMenuItemResources {

    @Source({"com/sencha/gxt/theme/base/client/menu/SeparatorMenuItem.css", "GraySeparatorMenuItem.css"})
    GraySeparatorMenuItemStyle style();

  }

  public interface GraySeparatorMenuItemStyle extends SeparatorMenuItemStyle {
  }

  public GraySeparatorMenuItemAppearance() {
    this(GWT.<GraySeparatorMenuItemResources> create(GraySeparatorMenuItemResources.class),
        GWT.<SeparatorMenuItemTemplate> create(SeparatorMenuItemTemplate.class));
  }

  public GraySeparatorMenuItemAppearance(GraySeparatorMenuItemResources resources,
      SeparatorMenuItemTemplate template) {
    super(resources, template);
  }

}
