/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasHTML;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Util;

/**
 * A menu item for headings. Typically, places as the first item in a Menu.
 */
public class HeaderMenuItem extends Item implements HasHTML, HasSafeHtml {

  public interface HeaderMenuItemAppearance extends ItemAppearance {

    public void applyItemStyle(Element element);

  }

  /**
   * Creates a new header menu item.
   */
  public HeaderMenuItem(HeaderMenuItemAppearance appearance) {
    super(appearance);
    setHideOnClick(false);

    Element span = DOM.createSpan();
    appearance.applyItemStyle(XElement.as(span));
    setElement(span);
  }

  public HeaderMenuItem() {
    this(GWT.<HeaderMenuItemAppearance> create(HeaderMenuItemAppearance.class));
  }

  /**
   * Creates a new header menu item.
   * 
   * @param html the text as HTML
   */
  public HeaderMenuItem(String html, HeaderMenuItemAppearance appearance) {
    this(appearance);
    setHTML(html);
  }

  public HeaderMenuItem(SafeHtml html, HeaderMenuItemAppearance appearance) {
    this(appearance);
    setHTML(html);
  }

  public HeaderMenuItem(String html) {
    this(GWT.<HeaderMenuItemAppearance> create(HeaderMenuItemAppearance.class));
    setHTML(html);
  }

  public HeaderMenuItem(SafeHtml html) {
    this(GWT.<HeaderMenuItemAppearance> create(HeaderMenuItemAppearance.class));
    setHTML(html);
  }

  public String getHTML() {
    return getElement().getInnerHTML();
  }

  @Override
  public String getText() {
    return getElement().getInnerText();
  }

  @Override
  public void setHTML(SafeHtml html) {
    setHTML(html.asString());
  }

  public void setHTML(String html) {
    getElement().setInnerHTML(Util.isEmptyString(html) ? "&#160;" : html);
  }

  public void setText(String text) {
    getElement().setInnerText(Util.isEmptyString(text) ? "" : text);
  }

}
