/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.menu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.core.client.util.IconHelper;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public abstract class MenuItemBaseAppearance implements MenuItem.MenuItemAppearance {

  public interface MenuItemResources {

    MenuItemStyle style();

  }

  public interface MenuItemStyle extends CssResource {

    String menuItem();

    String menuItemArrow();

    String menuItemIcon();

    String menuListItem();

  }

  public interface MenuItemTemplate extends XTemplates {

    @XTemplates.XTemplate(source = "MenuItem.html")
    SafeHtml template(MenuItemStyle style);

  }

  private final MenuItemStyle style;
  private MenuItemTemplate template;

  public MenuItemBaseAppearance(MenuItemResources resources, MenuItemTemplate template) {
    style = resources.style();
    this.template = template;

    StyleInjectorHelper.ensureInjected(this.style, true);
  }

  public XElement getAnchor(XElement parent) {
    return XElement.as(parent.getFirstChild());
  }

  public void onAddSubMenu(XElement parent) {
    parent.getFirstChildElement().addClassName(style.menuItemArrow());
  }

  public void onRemoveSubMenu(XElement parent) {
    parent.getFirstChildElement().removeClassName(style.menuItemArrow());
  }

  public void render(SafeHtmlBuilder result) {
    result.append(template.template(style));
  }

  public void setIcon(XElement parent, ImageResource icon) {
    XElement anchor = getAnchor(parent);
    XElement oldIcon = parent.selectNode("." + style.menuItemIcon());
    if (oldIcon != null) {
      oldIcon.removeFromParent();
    }
    if (icon != null) {
      Element e = IconHelper.getElement(icon);
      e.addClassName(style.menuItemIcon());
      anchor.insertChild(e, 0);
    }
  }

  @Override
  public void setText(XElement parent, String text, boolean asHtml) {
    XElement oldIcon = parent.selectNode("." + style.menuItemIcon());
    if (asHtml || Util.isEmptyString(text)) {
      getAnchor(parent).setInnerHTML(Util.isEmptyString(text) ? "&#160;" : text);
    } else {
      getAnchor(parent).setInnerText(text);
    }
    if (oldIcon != null) {
      getAnchor(parent).insertFirst(oldIcon);
    }
  }

  @Override
  public void setWidget(XElement parent, Widget widget) {
    XElement oldIcon = parent.selectNode("." + style.menuItemIcon());

    getAnchor(parent).setInnerHTML("");
    getAnchor(parent).appendChild(widget.getElement());

    if (oldIcon != null) {
      getAnchor(parent).insertFirst(oldIcon);
    }
  }

}
