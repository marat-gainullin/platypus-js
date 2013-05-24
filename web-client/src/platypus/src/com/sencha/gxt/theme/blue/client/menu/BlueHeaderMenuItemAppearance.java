/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.blue.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Element;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem.HeaderMenuItemAppearance;

public class BlueHeaderMenuItemAppearance extends BlueItemAppearance implements HeaderMenuItemAppearance {

  public interface BlueHeaderMenuItemResources extends BlueItemResources {

    @Source("BlueHeaderMenuItem.css")
    BlueHeaderMenuItemStyle headerStyle();

  }

  public interface BlueHeaderMenuItemStyle extends CssResource {

    public String menuText();

  }

  private BlueHeaderMenuItemStyle headerStyle;

  public BlueHeaderMenuItemAppearance() {
    this(GWT.<BlueHeaderMenuItemResources> create(BlueHeaderMenuItemResources.class));
  }

  public BlueHeaderMenuItemAppearance(BlueHeaderMenuItemResources resources) {
    super(resources);
    headerStyle = resources.headerStyle();
    headerStyle.ensureInjected();
  }

  @Override
  public void applyItemStyle(Element element) {
    element.addClassName(headerStyle.menuText());
  }

}
