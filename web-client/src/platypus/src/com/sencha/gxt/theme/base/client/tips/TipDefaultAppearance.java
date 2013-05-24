/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.tips;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.theme.base.client.frame.Frame;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame.NestedDivFrameResources;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame.NestedDivFrameStyle;
import com.sencha.gxt.widget.core.client.tips.Tip.TipAppearance;

public class TipDefaultAppearance implements TipAppearance {

  public interface TipDefaultTemplate extends XTemplates {

    @XTemplate(source = "TipDefault.html")
    SafeHtml render(TipStyle style);

  }

  public interface TipDivFrameResources extends ClientBundle, NestedDivFrameResources {

    ImageResource anchorBottom();

    ImageResource anchorLeft();

    ImageResource anchorRight();

    ImageResource anchorTop();

    @Source("background.gif")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource background();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Override
    ImageResource bottomBorder();

    @Override
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource bottomLeftBorder();

    @Override
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource bottomRightBorder();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    @Override
    ImageResource leftBorder();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Override
    ImageResource rightBorder();

    @Source({"com/sencha/gxt/theme/base/client/frame/NestedDivFrame.css", "TipDivFrame.css"})
    @Override
    TipNestedDivFrameStyle style();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Override
    ImageResource topBorder();

    @Override
    ImageResource topLeftBorder();

    @Override
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource topRightBorder();

  }

  public interface TipNestedDivFrameStyle extends NestedDivFrameStyle {

  }

  public interface TipResources extends ClientBundle {

    ImageResource anchorBottom();

    ImageResource anchorLeft();

    ImageResource anchorRight();

    ImageResource anchorTop();

    @Source("TipDefault.css")
    TipStyle style();

  }

  public interface TipStyle extends CssResource {

    String anchor();

    String anchorBottom();

    String anchorLeft();

    String anchorRight();

    String heading();

    String text();

    String tools();

    String tip();

  }

  protected final TipResources resources;
  protected final TipStyle style;
  protected TipDefaultTemplate template;

  protected Frame frame;

  public TipDefaultAppearance() {
    this(GWT.<TipResources> create(TipResources.class));
  }

  public TipDefaultAppearance(TipResources resources) {
    this(resources, GWT.<TipDefaultTemplate> create(TipDefaultTemplate.class));
  }

  public TipDefaultAppearance(TipResources resources, TipDefaultTemplate template) {
    this.resources = resources;
    this.style = resources.style();

    StyleInjectorHelper.ensureInjected(style, true);

    this.template = template;

    frame = new NestedDivFrame(GWT.<TipDivFrameResources> create(TipDivFrameResources.class));
  }

  @Override
  public void applyAnchorDirectionStyle(XElement anchorEl, Side anchor) {
    switch (anchor) {
      case BOTTOM:
        anchorEl.addClassName(style.anchorBottom());
        break;
      case LEFT:
        anchorEl.addClassName(style.anchorLeft());
        break;
      case RIGHT:
        anchorEl.addClassName(style.anchorRight());
        break;
    }
  }

  @Override
  public void applyAnchorStyle(XElement anchorEl) {
    anchorEl.addClassName(style.anchor());
  }

  public XElement getHeaderElement(XElement parent) {
    return parent.selectNode("." + style.heading());
  }

  @Override
  public XElement getTextElement(XElement parent) {
    return parent.selectNode("." + style.text());
  }

  @Override
  public XElement getToolsElement(XElement parent) {
    return parent.selectNode("." + style.tools());
  }

  @Override
  public void removeAnchorStyle(XElement anchorEl) {
    anchorEl.removeClassName(style.anchor());
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    frame.render(sb, Frame.EMPTY_FRAME, template.render(style));
  }

  protected TipDivFrameResources getDivFrameResources() {
    return GWT.create(TipDivFrameResources.class);
  }

  @Override
  public int autoWidth(XElement parent, int minWidth, int maxWidth) {
    int tw = getTextElement(parent).getTextWidth();
    int hw = getHeaderElement(parent).getTextWidth();
    
    int w = Math.max(tw, hw);
    // framing
    w += 10;
    
    w += getToolsElement(parent).getOffsetWidth();
        
    return Util.constrain(w, minWidth, maxWidth);
  }

  @Override
  public void updateContent(XElement parent, String heading, String text) {
    XElement header = getHeaderElement(parent);
    if (heading != null && !heading.equals("")) {
      header.setInnerHTML(heading);
      header.getParentElement().getStyle().setDisplay(Display.BLOCK);
    } else {
      header.getParentElement().getStyle().setDisplay(Display.NONE);
    }
    
    getTextElement(parent).setInnerHTML(text);
  }

}
