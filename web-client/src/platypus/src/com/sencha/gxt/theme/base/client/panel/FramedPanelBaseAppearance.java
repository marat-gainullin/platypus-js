/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.panel;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.theme.base.client.frame.CollapsibleFrame;
import com.sencha.gxt.theme.base.client.frame.Frame;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame.NestedDivFrameResources;
import com.sencha.gxt.widget.core.client.FramedPanel.FramedPanelAppearance;

public abstract class FramedPanelBaseAppearance extends ContentPanelBaseAppearance implements FramedPanelAppearance {

  public interface FramedPanelDivFrameResources extends NestedDivFrameResources {

  }

  public interface FramedPanelTemplate extends ContentPanelTemplate {
    @XTemplate(source = "FramedPanel.html")
    SafeHtml render(ContentPanelStyle style);
  }

  protected CollapsibleFrame frame;


  public FramedPanelBaseAppearance(ContentPanelResources resources, FramedPanelTemplate template, CollapsibleFrame frame) {
    super(resources, template);

    this.frame = frame;
  }

  @Override
  public XElement getBodyWrap(XElement parent) {
    return frame.getCollapseElem(parent);
  }

  @Override
  public XElement getContentElem(XElement parent) {
    return parent.selectNode("." + style.body());
  }

  @Override
  public int getFrameHeight(XElement parent) {
    int h = frame.getFrameSize().getHeight();
    h += frame.getContentElem(parent).getFrameSize().getHeight();
    return h;
  }

  @Override
  public int getFrameWidth(XElement parent) {
    int w = frame.getFrameSize().getWidth();

    XElement content = getContentElem(parent);
    w += content.getFrameWidth(Side.LEFT, Side.RIGHT);

    return w;
  }

  @Override
  public XElement getHeaderElem(XElement parent) {
    return frame.getHeaderElem(parent);
  }

  @Override
  public void onBodyBorder(XElement parent, boolean border) {
    getContentElem(parent).setClassName(ThemeStyles.getStyle().border(), border);
  }

  @Override
  public void onHideHeader(XElement parent, boolean hide) {
    parent.setClassName("noheader", hide);
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    frame.render(sb, Frame.EMPTY_FRAME, template.render(style));
  }

}
