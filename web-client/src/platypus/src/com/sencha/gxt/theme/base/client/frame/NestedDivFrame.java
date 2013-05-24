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
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.core.client.util.Size;

/**
 * <code>Frame</code> and <code>CollapsibleFrame</code> implementation that
 * creates its frame using 3 sets of 3 nested DIVs. See NestedDivFrame.html and
 * NestedDivFrame.css.
 */
public class NestedDivFrame implements Frame, CollapsibleFrame {

  public interface NestedDivFrameResources {

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource bottomBorder();

    ImageResource bottomLeftBorder();

    ImageResource bottomRightBorder();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource leftBorder();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource rightBorder();

    NestedDivFrameStyle style();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource topBorder();

    ImageResource topLeftBorder();

    ImageResource topRightBorder();
  }

  public interface NestedDivFrameStyle extends CssResource {

    String bodyWrap();

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
    @XTemplate(source = "NestedDivFrame.html")
    SafeHtml render(NestedDivFrameStyle style, SafeHtml content);
  }

  private NestedDivFrameStyle style;
  private Template template = GWT.create(Template.class);
  private NestedDivFrameResources resources;

  public NestedDivFrame(NestedDivFrameResources resources) {
    this.resources = resources;
    this.style = resources.style();

    StyleInjectorHelper.ensureInjected(this.style, true);
  }

  public XElement getContentElem(XElement parent) {
    return parent.selectNode("." + style.content());
  }

  @Override
  public Size getFrameSize() {
    // we can't get height of topBorder as it is includes the header, using
    // width of topLeftBorder assuming equally rounded corners
    return new Size(resources.leftBorder().getWidth() + resources.rightBorder().getWidth(),
        resources.topLeftBorder().getWidth() + resources.bottomBorder().getHeight());
  }

  public XElement getHeaderElem(XElement parent) {
    return parent.selectNode("." + style.top());
  }

  public NestedDivFrameResources getResources() {
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

  @Override
  public XElement getCollapseElem(XElement parent) {
    return parent.selectNode("." + style.bodyWrap());
  }

}
