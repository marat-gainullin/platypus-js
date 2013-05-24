/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.tabs;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.core.client.util.IconHelper;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelAppearance;

public abstract class TabPanelBaseAppearance implements TabPanelAppearance {

  public interface ItemTemplate extends XTemplates {
    @XTemplate(source = "TabItem.html")
    SafeHtml render(TabPanelStyle style, TabItemConfig config);
  }

  public interface TabPanelResources {

    TabPanelStyle style();

  }

  public interface TabPanelStyle extends CssResource {

    String tab();

    String tabBbar();

    String tabBbarNoborder();

    String tabBody();

    String tabBodyBottom();

    String tabBodyNoborder();

    String tabBodyTop();

    String tabBwrap();

    String tabEdge();

    String tabFooter();

    String tabFooterNoborder();

    String tabHeader();

    String tabHeaderNoborder();

    String tabImage();

    String tabLeft();

    String tabNoborder();

    String tabRight();

    String tabScrollerLeft();

    String tabScrollerLeftDisabled();

    String tabScrollerLeftOver();

    String tabScrollerRight();

    String tabScrollerRightDisabled();

    String tabScrollerRightOver();

    String tabScrolling();

    String tabScrollingBottom();

    String tabsText();

    String tabStrip();

    String tabStripActive();

    String tabStripBottom();

    String tabStripClosable();

    String tabStripClose();

    String tabStripDisabled();

    String tabStripInner();

    String tabStripOver();

    String tabStripText();

    String tabStripTop();

    String tabStripWrap();

    String tabTbar();

    String tabTbarNoborder();

    String tabWithIcon();

    String xToolbar();

  }

  public interface Template extends XTemplates {
    @XTemplate(source = "TabPanel.html")
    SafeHtml render(TabPanelStyle style);
  }

  protected ItemTemplate itemTemplate;
  protected final TabPanelStyle style;
  protected Template template;

  private static final String ITEM_SELECTOR = "li";

  public TabPanelBaseAppearance(TabPanelResources resources, Template template, ItemTemplate itemTemplate) {
    this.style = resources.style();

    StyleInjectorHelper.ensureInjected(this.style, true);

    this.template = template;
    this.itemTemplate = itemTemplate;
  }

  public void createScrollers(XElement parent) {
    int h = getStripWrap(parent).getOffsetHeight();
    XElement scrollLeft = getBar(parent).insertFirst("<div class='" + style.tabScrollerLeft() + "'></div>");
    scrollLeft.setId(XDOM.getUniqueId());
    scrollLeft.setHeight(h);

    XElement scrollRight = getBar(parent).insertFirst("<div class='" + style.tabScrollerRight() + "'></div>");
    scrollRight.setId(XDOM.getUniqueId());
    scrollRight.setHeight(h);
  }

  public XElement getBar(XElement parent) {
    return parent.selectNode("." + style.tabHeader());
  }

  @Override
  public XElement getBody(XElement parent) {
    return parent.selectNode("." + style.tabBody());
  }

  @Override
  public String getItemSelector() {
    return ITEM_SELECTOR;
  }

  public XElement getScrollLeft(XElement parent) {
    return getBar(parent).selectNode("." + style.tabScrollerLeft());
  }

  public XElement getScrollRight(XElement parent) {
    return getBar(parent).selectNode("." + style.tabScrollerRight());
  }

  public XElement getStrip(XElement parent) {
    return parent.selectNode("." + style.tabStrip());
  }

  public XElement getStripEdge(XElement parent) {
    return parent.selectNode("." + style.tabEdge());
  }

  public XElement getStripWrap(XElement parent) {
    return parent.selectNode("." + style.tabStripWrap());
  }

  public void insert(XElement parent, TabItemConfig config, int index) {
    XElement item = XDOM.create(itemTemplate.render(style, config).asString());
    item.setClassName(CommonStyles.get().disabled(), !config.isEnabled());
    
    if (config.isHTML()) {
      XElement textEl = item.selectNode("." + style.tabStripText());
      textEl.setInnerHTML(config.getHTML());
    }
    
    getStrip(parent).insertChild(item, index);

    if (config.getIcon() != null) {
      setItemIcon(item, config.getIcon());
    }

    if (config.isClosable()) {
      item.addClassName(style.tabStripClosable());
    }
  }

  @Override
  public boolean isClose(XElement target) {
    return target.is("." + style.tabStripClose());
  }

  @Override
  public void onDeselect(Element item) {
    item.removeClassName(style.tabStripActive());
  }

  public void onMouseOut(XElement parent, Event event) {
    NodeList<Element> nodeList = parent.select("." + style.tabStripOver());
    for (int i = 0; i < nodeList.getLength(); i++) {
      nodeList.getItem(i).removeClassName(style.tabStripOver());
    }
    XElement target = event.getEventTarget().cast();
    if (target.is("." + style.tabScrollerLeft())) {
      target.removeClassName(style.tabScrollerLeftOver());
    } else if (target.is("." + style.tabScrollerRight())) {
      target.removeClassName(style.tabScrollerRightOver());
    }
  }

  public void onMouseOver(Event event) {
    XElement target = event.getEventTarget().cast();
    Element item = findItem(event.getEventTarget().<Element> cast());
    if (item != null) {
      item.addClassName(style.tabStripOver());
    } else if (target.is("." + style.tabScrollerLeft())) {
      target.addClassName(style.tabScrollerLeftOver());
    } else if (target.is("." + style.tabScrollerRight())) {
      target.addClassName(style.tabScrollerRightOver());
    }
  }

  @Override
  public void onScrolling(XElement parent, boolean scrolling) {
    parent.selectNode("." + style.tabHeader()).setClassName(style.tabScrolling(), scrolling);
  }

  @Override
  public void onSelect(Element item) {
    item.addClassName(style.tabStripActive());
  }

  public void render(SafeHtmlBuilder builder) {
    builder.append(template.render(style));
  }

  @Override
  public void setItemWidth(XElement element, int width) {
    XElement inner = element.selectNode("." + style.tabStripInner());
    int tw = element.getOffsetWidth();
    int iw = inner.getOffsetWidth();
    inner.setWidth(width - (tw - iw));
  }

  @Override
  public void updateItem(XElement item, TabItemConfig config) {
    XElement textEl = item.selectNode("." + style.tabStripText());
    
    if (config.isHTML()) {
      textEl.setInnerHTML(config.getHTML());
    } else {
      textEl.setInnerText(config.getText());
    }

    setItemIcon(item, config.getIcon());

    item.setClassName(CommonStyles.get().disabled(), !config.isEnabled());
  }

  public void updateScrollButtons(XElement parent) {
    int pos = getScrollPos(parent);
    getScrollLeft(parent).setClassName(style.tabScrollerLeftDisabled(), pos == 0);
    getScrollRight(parent).setClassName(style.tabScrollerRightDisabled(),
        pos >= (getScrollWidth(parent) - getScrollArea(parent) - 2));
  }

  protected Element findItem(Element target) {
    return target.<XElement> cast().findParentElement(ITEM_SELECTOR, 5);
  }

  protected void setItemIcon(XElement item, ImageResource icon) {
    XElement node = item.selectNode("." + style.tabImage());
    if (node != null) {
      node.removeFromParent();
    }
    if (icon != null) {
      Element e = IconHelper.getElement(icon);
      e.setClassName(style.tabImage());
      item.appendChild(e);
    }
    item.setClassName(style.tabWithIcon(), icon != null);
  }

  private int getScrollPos(XElement parent) {
    return getStripWrap(parent).getScrollLeft();
  }

  private int getScrollArea(XElement parent) {
    return Math.max(0, getStripWrap(parent).getClientWidth());
  }

  private int getScrollWidth(XElement parent) {
    return getStripEdge(parent).getOffsetsTo(getStripWrap(parent)).getX() + getScrollPos(parent);
  }

}
