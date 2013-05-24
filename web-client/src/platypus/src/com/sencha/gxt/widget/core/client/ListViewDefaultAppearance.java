/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.ListView.ListViewAppearance;
import com.sencha.gxt.widget.core.client.ListView.ListViewDefaultResources;
import com.sencha.gxt.widget.core.client.ListView.ListViewDefaultStyle;

@SuppressWarnings("javadoc")
public class ListViewDefaultAppearance<M> implements ListViewAppearance<M> {

  protected ListViewDefaultResources resources;
  protected ListViewDefaultStyle style;

  public ListViewDefaultAppearance() {
    this.resources = GWT.create(ListViewDefaultResources.class);
    this.style = resources.css();
    this.style.ensureInjected();
  }

  @Override
  public Element findCellParent(XElement item) {
    return item;
  }

  @Override
  public Element findElement(XElement child) {
    return child.findParentElement("." + style.item(), 10);
  }

  @Override
  public List<Element> findElements(XElement parent) {
    NodeList<Element> nodes = parent.select("." + style.item());
    List<Element> temp = new ArrayList<Element>();
    for (int i = 0; i < nodes.getLength(); i++) {
      temp.add(nodes.getItem(i));
    }

    return temp;
  }

  @Override
  public void onOver(XElement item, boolean over) {
    item.setClassName(style.over(), over);
  }

  @Override
  public void onSelect(XElement item, boolean select) {
    item.setClassName(style.sel(), select);
  }

  @Override
  public void render(SafeHtmlBuilder builder) {
    builder.appendHtmlConstant("<div class=\"" + style.view() + "\"></div>");
  }

  @Override
  public void renderEnd(SafeHtmlBuilder builder) {
  }

  @Override
  public void renderItem(SafeHtmlBuilder sb, SafeHtml content) {
    sb.appendHtmlConstant("<div class='" + style.item() + "'>");
    sb.append(content);
    sb.appendHtmlConstant("</div>");

  }
}