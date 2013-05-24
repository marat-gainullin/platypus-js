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
import com.sencha.gxt.theme.base.client.menu.ItemBaseAppearance;

public class GrayItemAppearance extends ItemBaseAppearance {

  public interface GrayItemResources extends ItemBaseAppearance.ItemResources, ClientBundle {

    @Source({"com/sencha/gxt/theme/base/client/menu/Item.css", "GrayItem.css"})
    GrayItemStyle style();

    ImageResource itemOver();

  }

  public interface GrayItemStyle extends ItemStyle {

    String active();

  }

  public GrayItemAppearance() {
    this(GWT.<GrayItemResources> create(GrayItemResources.class));
  }

  public GrayItemAppearance(GrayItemResources resources) {
    super(resources);
  }

}
