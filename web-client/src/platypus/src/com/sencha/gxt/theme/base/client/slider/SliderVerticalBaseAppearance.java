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

public abstract class SliderVerticalBaseAppearance extends SliderBaseAppearance {

  public interface BaseSliderVerticalStyle extends SliderStyle {
  }

  public interface SliderVerticalResources extends SliderResources {

    BaseSliderVerticalStyle style();

  }

  public interface SliderVerticalTemplate extends XTemplates {

    @XTemplate(source = "SliderVertical.html")
    SafeHtml template(SliderStyle style, SafeStyles offset, SafeStyles innerHeight);

  }

  protected final SliderVerticalTemplate template;

  private final SliderVerticalResources resources;

  public SliderVerticalBaseAppearance(SliderVerticalResources resources, SliderVerticalTemplate template) {
    super(resources);
    this.resources = resources;
    this.template = template;
  }

  @Override
  public int getClickedValue(Context context, Element parent, NativeEvent event) {
    Element innerEl = getInnerEl(parent);
    return event.getClientY() - XElement.as(innerEl).getTop(false);
  }

  @Override
  public int getSliderLength(XElement parent) {
    return getInnerEl(parent).getOffsetHeight();
  }

  @Override
  public boolean isVertical() {
    return true;
  }

  public void render(double fractionalValue, int width, int height, SafeHtmlBuilder sb) {
    if (height == -1) {
      // default
      height = 200;
    }

    int halfThumb = resources.style().halfThumb();
    int innerHeight = height;
    int offset = (int) (innerHeight - ((fractionalValue * innerHeight) - halfThumb));

    offset = innerHeight - offset;

    SafeStyles heightStyle = SafeStylesUtils.fromTrustedString("");

    // ends
    height -= 14;
    heightStyle = SafeStylesUtils.fromTrustedString("height: " + height + "px;");

    SafeStyles offsetStyles = SafeStylesUtils.fromTrustedString("bottom:" + offset + "px;");
    sb.append(template.template(resources.style(), offsetStyles, heightStyle));
  }

  @Override
  public void setThumbPosition(Element parent, int pos) {
    XElement thumbElement = XElement.as(getThumb(parent));
    thumbElement.getStyle().setBottom(pos, Unit.PX);
  }

}
