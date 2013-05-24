/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.menu;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.sencha.gxt.core.client.Style.HideMode;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.menu.Menu;

public abstract class MenuBaseAppearance implements Menu.MenuAppearance {

  public interface MenuResources {

    MenuStyle style();

  }

  public interface MenuStyle extends CssResource {

    String dateMenu();

    String menu();

    String menuItemActive();

    String menuList();

    String menuListItem();

    String menuListItemIndent();

    String menuRadioGroup();

    String menuScroller();

    String menuScrollerActive();

    String menuScrollerBottom();

    String menuScrollerTop();
    
    String noSeparator();
    
    String plain();

  }

  public interface BaseMenuTemplate extends XTemplates {

    @XTemplate(source = "Menu.html")
    SafeHtml template(MenuStyle style, String ignoreClass);

  }

  protected final MenuResources resources;
  protected final MenuStyle style;

  private BaseMenuTemplate template;

  public MenuBaseAppearance(MenuResources resources, BaseMenuTemplate template) {
    this.resources = resources;
    this.style = resources.style();
    this.template = template;

    StyleInjectorHelper.ensureInjected(this.style, true);
  }

  public void applyDateMenu(XElement element) {
    element.addClassName(style.dateMenu());
  }

  public XElement createItem(XElement parent, String childId, boolean needsIndent) {
    XElement div = XElement.as(DOM.createDiv());
    div.setId("x-menu-el-" + childId);
    // div.setClassName(style.menuListItem());
    if (needsIndent) div.setClassName(style.menuListItemIndent());
    return div;
  }

  public XElement getBottomScroller(XElement parent) {
    // Check whether scroller already exists
    Node firstChild = parent.getLastChild();
    if (firstChild != null && Element.is(firstChild)) {
      XElement firstChildXElement = XElement.as(firstChild);
      if (firstChildXElement.is("." + style.menuScrollerTop())) {
        // Found scroller
        return firstChildXElement;
      }
    }

    // Scroller does not already exist; create it
    XElement scroller = XElement.as(DOM.createDiv());
    scroller.addClassName(style.menuScroller(), style.menuScrollerBottom());
    scroller.setInnerHTML("&nbsp;");
    parent.appendChild(scroller);
    return scroller;
  }

  public XElement getGroup(XElement parent, String id, String groupName) {
    XElement groupElement = parent.selectNode("#" + id + "-" + groupName);
    return groupElement != null ? groupElement : createGroup(parent, id, groupName);
  }

  public NodeList<Element> getGroups(XElement parent) {
    return parent.select("." + style.menuRadioGroup());
  }

  public XElement getMenuList(XElement element) {
    return element.selectNode("." + style.menuList());
  }

  public NodeList<Element> getScrollers(XElement parent) {
    return parent.select("." + style.menuScroller());
  }

  public XElement getTopScroller(XElement parent) {
    // Check whether scroller already exists
    Element firstChild = parent.getFirstChildElement();
    if (firstChild != null) {
      XElement firstChildXElement = XElement.as(firstChild);
      if (firstChildXElement.is("." + style.menuScrollerTop())) {
        // Found scroller
        return firstChildXElement;
      }
    }

    // Scroller does not already exist; create it
    XElement scroller = XElement.as(DOM.createDiv());
    scroller.addClassName(style.menuScroller(), style.menuScrollerTop());
    scroller.setInnerHTML("&nbsp;");
    parent.insertFirst(scroller);
    return scroller;
  }

  public boolean hasScrollers(XElement parent) {
    return parent.select("." + style.menuScroller()).getLength() > 0;
  }

  public void onScrollerOut(XElement target) {
    target.removeClassName(style.menuItemActive(), style.menuScrollerActive());
  }

  public void render(SafeHtmlBuilder result) {
    result.append(template.template(style, CommonStyles.get().ignore()));
  }

  private XElement createGroup(XElement parent, String id, String groupName) {
    XElement groupElement = XElement.createElement("div");
    groupElement.makePositionable(true);
    groupElement.addClassName(HideMode.OFFSETS.value());
    groupElement.addClassName(style.menuRadioGroup());
    groupElement.setId(id + "-" + groupName);
    parent.appendChild(groupElement);
    return groupElement;
  }

  @Override
  public String noSeparatorClass() {
    return style.noSeparator();
  }

  @Override
  public String plainClass() {
    return style.plain();
  }

}
