/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.button;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.theme.base.client.button.ButtonGroupBaseAppearance.ButtonGroupTableFrameStyle;
import com.sencha.gxt.theme.base.client.frame.TableFrame.TableFrameResources;

public interface ButtonGroupBaseTableFrameResources extends TableFrameResources, ClientBundle {

  @Source({"com/sencha/gxt/theme/base/client/frame/TableFrame.css", "ButtonGroupTableFrame.css"})
  @Override
  ButtonGroupTableFrameStyle style();

  @Source("com/sencha/gxt/theme/base/client/shared/clear.gif")
  @ImageOptions(repeatStyle = RepeatStyle.Both)
  ImageResource background();

  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource backgroundOverBorder();

  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource backgroundPressedBorder();

  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource topOverBorder();

  @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
  ImageResource topPressedBorder();

  @ImageOptions(repeatStyle = RepeatStyle.Vertical)
  ImageResource leftOverBorder();

  @ImageOptions(repeatStyle = RepeatStyle.Vertical)
  ImageResource leftPressedBorder();

  @ImageOptions(repeatStyle = RepeatStyle.Both, preventInlining = true)
  @Override
  ImageResource bottomLeftBorder();

  @ImageOptions(repeatStyle = RepeatStyle.Both, preventInlining = true)
  @Override
  ImageResource bottomRightBorder();

}
