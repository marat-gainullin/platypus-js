/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.slider;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;

public abstract class SliderHorizontalBaseAppearance extends SliderBaseAppearance {

  public interface SliderHorizontalResources extends SliderResources {

    SliderHorizontalStyle style();

  }
  public interface SliderHorizontalStyle extends SliderStyle {
  }

  public interface SliderHorizontalTemplate extends XTemplates {

    @XTemplate(source = "SliderHorizontal.html")
    SafeHtml template(SliderStyle style, SafeStyles width, SafeStyles offset);

  }

  private final SliderHorizontalResources resources;
  private final SliderHorizontalTemplate template;

  public SliderHorizontalBaseAppearance(SliderHorizontalResources resources, SliderHorizontalTemplate template) {
    super(resources);
    this.resources = resources;
    this.template = template;
  }

  @Override
  public int getSliderLength(XElement parent) {
    return getInnerEl(parent).getOffsetWidth();
  }

  @Override
  public int getClickedValue(Context context, Element parent, NativeEvent event) {
    Element innerEl = getInnerEl(parent);
    return event.getClientX() - XElement.as(innerEl).getLeft(false);
  }

  @Override
  public void render(double fractionalValue, int width, int height, SafeHtmlBuilder sb) {
    if (width == -1) {
      // default
      width = 200;
    }
    
    // padding
    width -= 7;
    
    int offset = (int) (fractionalValue * (width - 21)) - 7;
    
    offset = Math.max(-7, offset);
    
    SafeStyles offsetStyles = SafeStylesUtils.fromTrustedString("left:" + offset + "px;");
    SafeStyles widthStyle = SafeStylesUtils.fromTrustedString("");

    widthStyle = SafeStylesUtils.fromTrustedString("width: " + width + "px;");
    sb.append(template.template(resources.style(), widthStyle, offsetStyles));
  }

  @Override
  public void setThumbPosition(Element parent, int pos) {
    XElement thumbElement = XElement.as(getThumb(parent));
    pos = Math.max(-7, pos);
    thumbElement.getStyle().setLeft(pos, Unit.PX);
  }

  @Override
  protected Element getEndEl(Element parent) {
    return parent.getFirstChildElement().getFirstChildElement();
  }

  @Override
  protected Element getInnerEl(Element parent) {
    return parent.getFirstChildElement().getFirstChildElement().getFirstChildElement();
  }

  @Override
  public boolean isVertical() {
    return false;
  }

}
