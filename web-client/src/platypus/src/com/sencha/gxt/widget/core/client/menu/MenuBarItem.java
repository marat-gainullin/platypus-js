/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasHTML;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Component;

public class MenuBarItem extends Component implements HasHTML {

  public static interface MenuBarItemAppearance {

    XElement getTextElement(XElement parent);

    void onOver(XElement parent, boolean over);

    void onActive(XElement parent, boolean active);

    void render(SafeHtmlBuilder builder);
  }

  protected final MenuBarItemAppearance appearance;
  protected Menu menu;
  protected boolean expanded;

  @UiConstructor
  public MenuBarItem(String text) {
    this(text, null);
  }

  public MenuBarItem(String text, Menu menu) {
    this(text, menu, GWT.<MenuBarItemAppearance> create(MenuBarItemAppearance.class));
  }

  public MenuBarItem(String text, Menu menu, MenuBarItemAppearance appearance) {
    this.appearance = appearance;

    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    appearance.render(builder);

    setElement(XDOM.create(builder.toSafeHtml()));
    sinkEvents(Event.MOUSEEVENTS);

    setText(text);
    setMenu(menu);
  }

  public MenuBarItemAppearance getAppearance() {
    return appearance;
  }

  public Menu getMenu() {
    return menu;
  }

  @UiChild(limit = 1, tagname = "menu")
  public void setMenu(Menu menu) {
    this.menu = menu;
  }

  @Override
  public String getHTML() {
    return appearance.getTextElement(getElement()).getInnerHTML();
  }

  @Override
  public String getText() {
    return appearance.getTextElement(getElement()).getInnerText();
  }

  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);

    switch (event.getTypeInt()) {
      case Event.ONMOUSEOVER:
        appearance.onOver(getElement(), true);
        break;

      case Event.ONMOUSEOUT:
        appearance.onOver(getElement(), false);
        break;
    }
  }

  @Override
  public void setHTML(String html) {
    appearance.getTextElement(getElement()).setInnerHTML(html);
  }

  @Override
  public void setText(String text) {
    appearance.getTextElement(getElement()).setInnerText(text);
  }

}
