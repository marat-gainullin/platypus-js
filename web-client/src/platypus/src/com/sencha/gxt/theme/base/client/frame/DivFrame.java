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
 * <code>Frame</code> implementation that renders its frame using 9 absolutely
 * positioned DIVs.
 * <p/>
 * See <a
 * href='http://code.google.com/p/google-web-toolkit/wiki/CssResourceCookbook'>
 * CssResourceCookbook </a> for more information.
 */
public class DivFrame implements Frame {

  public interface DivFrameResources {

    ImageResource bottomBorder();

    ImageResource bottomLeftBorder();

    ImageResource bottomRightBorder();

    ImageResource leftBorder();

    ImageResource rightBorder();

    DivFrameStyle style();

    ImageResource topBorder();

    ImageResource topLeftBorder();

    ImageResource topRightBorder();
  }

  public interface DivFrameStyle extends CssResource {

    String bottom();

    String bottomLeft();

    String bottomRight();

    String content();

    String contentArea();

    String left();

    String over();

    String pressed();

    String right();

    String top();

    String topLeft();

    String topRight();

  }

  public interface Template extends XTemplates {
    @XTemplate(source = "DivFrame.html")
    SafeHtml render(DivFrameStyle style, SafeHtml content);
  }

  private final DivFrameStyle style;
  private Template template = GWT.create(Template.class);
  private final DivFrameResources resources;

  public DivFrame(DivFrameResources resources) {
    this.resources = resources;
    this.style = resources.style();

    StyleInjectorHelper.ensureInjected(this.style, true);
  }

  @Override
  public XElement getContentElem(XElement parent) {
    return parent.selectNode("." + style.content());
  }

  @Override
  public Size getFrameSize() {
    return new Size(resources.leftBorder().getWidth(), resources.rightBorder().getWidth());
  }

  @Override
  public XElement getHeaderElem(XElement parent) {
    return parent.selectNode("." + style.top());
  }

  public DivFrameResources getResources() {
    return resources;
  }

  @Override
  public void onFocus(XElement parent, boolean focus, NativeEvent event) {

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
  public void onOver(XElement parent, boolean over, NativeEvent event) {
    parent.setClassName(style.over(), over);
  }

  @Override
  public void onPress(XElement parent, boolean pressed, NativeEvent event) {
    parent.setClassName(style.pressed(), pressed);
  }

  @Override
  public String overClass() {
    return style.over();
  }

  @Override
  public String pressedClass() {
    return style.pressed();
  }

  @Override
  public void render(SafeHtmlBuilder builder, FrameOptions options, SafeHtml content) {
    builder.append(template.render(style, content));
  }

}
