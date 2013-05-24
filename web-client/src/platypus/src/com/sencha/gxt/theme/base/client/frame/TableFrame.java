/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.frame;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.core.client.util.Size;

/**
 * <code>Frame</code> implementation that creates the frame using a 3 x 3 HTML
 * TABLE. Sides and corners are rendered using images. See TableFrame.html and
 * TableFrame.css.
 */
public class TableFrame implements Frame {

  public interface TableFrameResources {

    ImageResource bottomBorder();

    ImageResource bottomLeftBorder();

    ImageResource bottomRightBorder();

    ImageResource leftBorder();

    ImageResource rightBorder();

    TableFrameStyle style();

    ImageResource topBorder();

    ImageResource topLeftBorder();

    ImageResource topRightBorder();
  }

  public interface TableFrameStyle extends CssResource {

    String bottom();

    String bottomLeft();

    String bottomRight();

    String content();

    String contentArea();

    String focus();

    String frame();

    String left();

    String over();

    String pressed();

    String right();

    String top();

    String topLeft();

    String topRight();

  }

  public interface Template extends XTemplates {
    @XTemplate(source = "TableFrame.html")
    SafeHtml render(TableFrameStyle style, FrameOptions options, SafeHtml content);
  }

  private Template template = GWT.create(Template.class);
  private final TableFrameResources resources;
  private TableFrameStyle style;

  public TableFrame(TableFrameResources resources) {
    this.resources = resources;
    this.style = resources.style();

    StyleInjectorHelper.ensureInjected(this.style, true);
  }

  @Override
  public XElement getContentElem(XElement parent) {
    return parent.selectNode("." + style.content());
  }

  public XElement getHeaderElem(XElement parent) {
    return parent.selectNode("." + style.top());
  }

  public TableFrameResources getResources() {
    return resources;
  }

  @Override
  public void onFocus(XElement parent, boolean focus, NativeEvent event) {
    parent.setClassName(style.focus(), focus);
  }

  @Override
  public void onOver(XElement parent, boolean over, NativeEvent event) {
    parent.setClassName(style.over(), over);
  }

  @Override
  public void onPress(XElement parent, boolean pressed, NativeEvent event) {
    parent.setClassName(style.pressed(), pressed);
  }

  @Override
  public void render(SafeHtmlBuilder builder, FrameOptions options, SafeHtml content) {
    builder.append(template.render(style, options, content));
  }

  @Override
  public String pressedClass() {
    return style.pressed();
  }

  @Override
  public Size getFrameSize() {
    return new Size(resources.topBorder().getHeight(), resources.rightBorder().getWidth());
  }

  @Override
  public void onHideHeader(XElement parent, boolean hide) {
    XElement header = getHeaderElem(parent);
    if (header != null && header.hasChildNodes()) {
      NodeList<Node> children = header.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Node node = children.getItem(i);
        if (Element.is(node)) {
          Element.as(node).getStyle().setDisplay(hide ? Display.NONE : Display.BLOCK);
        }
      }
    }
  }

  @Override
  public String overClass() {
    return style.over();
  }

}
