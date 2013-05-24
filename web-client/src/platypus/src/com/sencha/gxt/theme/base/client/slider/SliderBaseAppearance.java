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
import com.google.gwt.resources.client.CssResource;
import com.sencha.gxt.cell.core.client.SliderCell.SliderAppearance;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;

public abstract class SliderBaseAppearance implements SliderAppearance {

  public interface SliderResources {

    SliderStyle style();

  }

  public interface SliderStyle extends CssResource {
    public String drag();

    public String end();

    public String focus();

    public String inner();

    public String over();

    public String slider();

    public String thumb();

    public int halfThumb();
  }

  private final SliderStyle style;

  public SliderBaseAppearance(SliderResources resources) {
    style = resources.style();

    StyleInjectorHelper.ensureInjected(this.style, true);
  }


  @Override
  public void onEmpty(Element parent, boolean empty) {
    // Not possible to "empty" a slider
  }

  @Override
  public void onFocus(Element parent, boolean focus) {
    // No visible effect of focussing
  }

  @Override
  public void onMouseDown(Context context, Element parent, NativeEvent event) {
    getThumb(parent).addClassName(style.drag());
  }

  @Override
  public void onMouseOut(Context context, Element parent, NativeEvent event) {
    getThumb(parent).removeClassName(style.over());
  }

  @Override
  public void onMouseOver(Context context, Element parent, NativeEvent event) {
    getThumb(parent).addClassName(style.over());
  }

  @Override
  public void onMouseUp(Context context, Element parent, NativeEvent event) {
    getThumb(parent).removeClassName(style.drag());
  }

  @Override
  public void onValid(Element parent, boolean valid) {
    // Always valid
  }

  @Override
  public void setReadOnly(Element parent, boolean readonly) {
    // TODO Not currently disableable
  }

  protected Element getEndEl(Element parent) {
    return parent.getFirstChildElement().getFirstChildElement();
  }

  protected Element getInnerEl(Element parent) {
    return parent.getFirstChildElement().getFirstChildElement().getFirstChildElement();
  }

  @Override
  public Element getThumb(Element parent) {
    return parent.getFirstChildElement().getFirstChildElement().getFirstChildElement().getFirstChildElement();
  }

}
