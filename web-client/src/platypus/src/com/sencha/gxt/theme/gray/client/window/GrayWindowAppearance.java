/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.window;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame.NestedDivFrameStyle;
import com.sencha.gxt.theme.base.client.panel.FramedPanelBaseAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance.HeaderResources;
import com.sencha.gxt.theme.base.client.widget.HeaderDefaultAppearance.HeaderStyle;
import com.sencha.gxt.theme.gray.client.panel.GrayFramedPanelAppearance.FramedPanelStyle;
import com.sencha.gxt.widget.core.client.Window.WindowAppearance;

public class GrayWindowAppearance extends FramedPanelBaseAppearance implements WindowAppearance {

  public interface GrayWindowDivFrameStyle extends NestedDivFrameStyle {

  }

  public interface GrayWindowDivFrameResources extends FramedPanelDivFrameResources, ClientBundle {

    @Source({"com/sencha/gxt/theme/base/client/frame/NestedDivFrame.css", "GrayWindowDivFrame.css"})
    @Override
    GrayWindowDivFrameStyle style();

    @Source("com/sencha/gxt/theme/base/client/shared/clear.gif")
    ImageResource background();

    @Override
    ImageResource topLeftBorder();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Override
    ImageResource topBorder();

    @Override
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource topRightBorder();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    @Override
    ImageResource leftBorder();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Override
    ImageResource rightBorder();

    @Override
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource bottomLeftBorder();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Override
    ImageResource bottomBorder();

    @Override
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource bottomRightBorder();

  }

  public interface GrayWindowStyle extends FramedPanelStyle {
    String ghost();
  }

  public interface GrayHeaderStyle extends HeaderStyle {

  }

  public interface GrayHeaderResources extends HeaderResources {
    @Source({"com/sencha/gxt/theme/base/client/widget/Header.css", "GrayWindowHeader.css"})
    GrayHeaderStyle style();
  }

  public interface GrayWindowResources extends ContentPanelResources, ClientBundle {

    @Source({
        "com/sencha/gxt/theme/base/client/panel/ContentPanel.css",
        "com/sencha/gxt/theme/base/client/window/Window.css", "GrayWindow.css"})
    @Override
    GrayWindowStyle style();

  }

  private GrayWindowStyle style;

  public GrayWindowAppearance() {
    this((GrayWindowResources) GWT.create(GrayWindowResources.class));
  }

  public GrayWindowAppearance(GrayWindowResources resources) {
    super(resources, GWT.<FramedPanelTemplate> create(FramedPanelTemplate.class), new NestedDivFrame(
        GWT.<GrayWindowDivFrameResources> create(GrayWindowDivFrameResources.class)));

    this.style = resources.style();
  }

  @Override
  public HeaderDefaultAppearance getHeaderAppearance() {
    return new HeaderDefaultAppearance(GWT.<GrayHeaderResources> create(GrayHeaderResources.class));
  }

  @Override
  public String ghostClass() {
    return style.ghost();
  }
}
