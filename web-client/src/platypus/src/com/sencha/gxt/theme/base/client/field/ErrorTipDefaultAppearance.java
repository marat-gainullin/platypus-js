/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.base.client.frame.Frame;
import com.sencha.gxt.theme.base.client.frame.NestedDivFrame;
import com.sencha.gxt.theme.base.client.tips.TipDefaultAppearance;

public class ErrorTipDefaultAppearance extends TipDefaultAppearance {

  public interface ErrorTipFrameResources extends ClientBundle, TipDivFrameResources {

    @Source("com/sencha/gxt/theme/base/client/shared/clear.gif")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource background();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("errorTipBottomBorder.gif")
    @Override
    ImageResource bottomBorder();

    @Override
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("errorTipBottomLeftBorder.gif")
    ImageResource bottomLeftBorder();

    @Override
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("errorTipBottomRightBorder.gif")
    ImageResource bottomRightBorder();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    @Source("errorTipLeftBorder.gif")
    @Override
    ImageResource leftBorder();

    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("errorTipRightBorder.gif")
    @Override
    ImageResource rightBorder();

    @Source({"com/sencha/gxt/theme/base/client/frame/NestedDivFrame.css", "ErrorTipFrame.css"})
    @Override
    ErrorTipNestedDivFrameStyle style();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    @Source("errorTipTopBorder.gif")
    @Override
    ImageResource topBorder();

    @Source("errorTipTopLeftBorder.gif")
    @Override
    ImageResource topLeftBorder();

    @Override
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    @Source("errorTipTopRightBorder.gif")
    ImageResource topRightBorder();

  }

  public interface ErrorTipNestedDivFrameStyle extends TipNestedDivFrameStyle {

  }

  public interface ErrorTipResources extends TipResources {
    @Source("exclamation.gif")
    @ImageOptions(preventInlining = true)
    ImageResource errorIcon();

    @Source({"com/sencha/gxt/theme/base/client/tips/TipDefault.css", "ErrorTip.css"})
    ErrorTipStyle style();
  }

  public interface ErrorTipStyle extends TipStyle {
    String textWrap();
  }

  public interface ErrorTipTemplate extends XTemplates {
    @XTemplate(source = "ErrorTipDefault.html")
    SafeHtml render(ErrorTipStyle style);
  }

  private ErrorTipTemplate template;

  public ErrorTipDefaultAppearance() {
    super(GWT.<ErrorTipResources> create(ErrorTipResources.class));

    template = GWT.create(ErrorTipTemplate.class);
    frame = new NestedDivFrame(GWT.<TipDivFrameResources> create(ErrorTipFrameResources.class));
  }

  @Override
  public int autoWidth(XElement parent, int minWidth, int maxWidth) {
    // add icon space
    return super.autoWidth(parent, minWidth, maxWidth) + 25;
  }

  
  @Override
  public void render(SafeHtmlBuilder sb) {
    frame.render(sb, Frame.EMPTY_FRAME, template.render((ErrorTipStyle) style));
  }
}
