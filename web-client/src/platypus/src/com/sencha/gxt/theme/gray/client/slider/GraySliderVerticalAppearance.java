/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.slider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.cell.core.client.SliderCell.VerticalSliderAppearance;
import com.sencha.gxt.theme.base.client.slider.SliderVerticalBaseAppearance;

public class GraySliderVerticalAppearance extends SliderVerticalBaseAppearance implements VerticalSliderAppearance {

  public static class GraySliderVerticalAppearanceHelper {

    public static String getTrackVerticalBottom() {
      return new StringBuilder("url(").append(GWT.getModuleBaseURL()).append(
          "/gray/images/slider/trackVerticalBottom.png);").toString();
    }

    public static String getTrackVerticalMiddle() {
      return new StringBuilder("url(").append(GWT.getModuleBaseURL()).append(
          "/gray/images/slider/trackVerticalMiddle.png);").toString();
    }

    public static String getTrackVerticalTop() {
      return new StringBuilder("url(").append(GWT.getModuleBaseURL()).append(
          "/gray/images/slider/trackVerticalTop.png);").toString();
    }

  }

  public interface GraySliderVerticalResources extends SliderVerticalResources, ClientBundle {

    @Source({
        "com/sencha/gxt/theme/base/client/slider/Slider.css",
        "com/sencha/gxt/theme/base/client/slider/SliderVertical.css", "GraySliderVertical.css"})
    GrayVerticalSliderStyle style();

    ImageResource thumbVertical();

    ImageResource thumbVerticalDown();

    ImageResource thumbVerticalOver();

    ImageResource trackVerticalBottom();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource trackVerticalMiddle();

    ImageResource trackVerticalTop();

  }

  public interface GrayVerticalSliderStyle extends BaseSliderVerticalStyle, CssResource {
  }

  public GraySliderVerticalAppearance() {
    this(GWT.<GraySliderVerticalResources> create(GraySliderVerticalResources.class),
        GWT.<SliderVerticalTemplate> create(SliderVerticalTemplate.class));
  }

  public GraySliderVerticalAppearance(GraySliderVerticalResources resources, SliderVerticalTemplate template) {
    super(resources, template);
  }

}
