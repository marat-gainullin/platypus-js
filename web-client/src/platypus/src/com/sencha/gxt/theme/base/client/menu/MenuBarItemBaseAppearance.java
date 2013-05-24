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
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem.MenuBarItemAppearance;

public abstract class MenuBarItemBaseAppearance implements MenuBarItemAppearance {

  public interface MenuBarItemResources {

    MenuBarItemStyle css();

  }

  public interface MenuBarItemStyle extends CssResource {

    String active();

    String menuBarItem();

    String over();

  }

  private final MenuBarItemResources resources;

  public MenuBarItemBaseAppearance(MenuBarItemResources resources) {
    this.resources = resources;
    resources.css().ensureInjected();
  }

  public XElement getTextElement(XElement parent) {
    return parent;
  }

  public void onActive(XElement parent, boolean active) {
    parent.setClassName(resources.css().active(), active);
  }

  public void onOver(XElement parent, boolean over) {
    parent.setClassName(resources.css().over(), over);
  }

  public void render(SafeHtmlBuilder builder) {
    builder.appendHtmlConstant("<div class='" + resources.css().menuBarItem() + "'></div>");
  }

}
