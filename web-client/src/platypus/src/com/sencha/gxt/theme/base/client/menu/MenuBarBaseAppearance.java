/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.menu;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.widget.core.client.menu.MenuBar.MenuBarAppearance;

public abstract class MenuBarBaseAppearance implements MenuBarAppearance {

  public interface MenuBarResources {

    MenuBarStyle css();

  }

  public interface MenuBarStyle extends CssResource {

    String menuBar();

  }

  private final MenuBarResources resources;

  public MenuBarBaseAppearance(MenuBarResources resources) {
    this.resources = resources;
    resources.css().ensureInjected();
  }

  @Override
  public void render(SafeHtmlBuilder builder) {
    builder.appendHtmlConstant("<div class='" + resources.css().menuBar() + " " + CommonStyles.get().noFocusOutline() + "'></div>");
  }

}
