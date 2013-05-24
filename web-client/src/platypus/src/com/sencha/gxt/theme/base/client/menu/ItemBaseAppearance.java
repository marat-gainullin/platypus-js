/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.menu;

import com.google.gwt.resources.client.CssResource;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.menu.Item;

public abstract class ItemBaseAppearance implements Item.ItemAppearance {

  public interface ItemResources {

    ItemStyle style();

  }

  public interface ItemStyle extends CssResource {

    String active();

  }

  private final ItemStyle style;

  public ItemBaseAppearance(ItemResources resources) {
    style = resources.style();
    style.ensureInjected();
  }

  public void onActivate(XElement parent) {
    parent.addClassName(style.active());
  }

  public void onDeactivate(XElement parent) {
    parent.removeClassName(style.active());
  }

}
