/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.theme.base.client.frame.DivFrame;
import com.sencha.gxt.theme.base.client.frame.DivFrame.DivFrameResources;
import com.sencha.gxt.theme.base.client.frame.DivFrame.DivFrameStyle;
import com.sencha.gxt.theme.base.client.frame.Frame;
import com.sencha.gxt.theme.base.client.frame.TableFrame;
import com.sencha.gxt.theme.base.client.frame.TableFrame.TableFrameResources;
import com.sencha.gxt.theme.base.client.frame.TableFrame.TableFrameStyle;
import com.sencha.gxt.widget.core.client.info.Info.InfoAppearance;

public class InfoDefaultAppearance implements InfoAppearance {

  public interface InfoDefaultStyle extends CssResource {

    String info();

    String infoMessage();

    String infoTitle();

  }

  public interface InfoDivFrameResources extends DivFrameResources, ClientBundle {

    @Source("background.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource background();

    @Source("bottomBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource bottomBorder();

    @Source("bottomLeftBorder.png")
    ImageResource bottomLeftBorder();

    @Source("bottomRightBorder.png")
    ImageResource bottomRightBorder();

    @Source("leftBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource leftBorder();

    @Source("rightBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource rightBorder();

    @Source("com/sencha/gxt/theme/base/client/frame/DivFrame.css")
    InfoDivFrameStyle style();

    @Source("topBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource topBorder();

    @Source("topLeftBorder.png")
    ImageResource topLeftBorder();

    @Source("topRightBorder.png")
    ImageResource topRightBorder();
  }

  public interface InfoDivFrameStyle extends DivFrameStyle {

  }

  public interface InfoResources extends ClientBundle {

    @Source("InfoDefault.css")
    InfoDefaultStyle style();

  }

  public interface InfoTableFrameResources extends TableFrameResources, ClientBundle {

    // RepeatStyle set to "both" to prevent sprite sheeting in IE6

    @Source("background.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource background();

    @Source("bottomBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource bottomBorder();

    @Source("bottomLeftBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource bottomLeftBorder();

    @Source("bottomRightBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource bottomRightBorder();

    @Source("leftBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource leftBorder();

    @Source("rightBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource rightBorder();

    @Source({"com/sencha/gxt/theme/base/client/frame/TableFrame.css", "InfoTableFrame.css"})
    InfoTableFrameStyle style();

    @Source("topBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource topBorder();

    @Source("topLeftBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource topLeftBorder();

    @Source("topRightBorder.png")
    @ImageOptions(repeatStyle = RepeatStyle.Both)
    ImageResource topRightBorder();

  }

  public interface InfoTableFrameStyle extends TableFrameStyle {

  }

  interface Template extends XTemplates {
    @XTemplate(source = "InfoDefault.html")
    SafeHtml render(InfoDefaultStyle style);
  }

  private Template template;
  private InfoResources resources = GWT.create(InfoResources.class);
  private Frame frame;
  private InfoDefaultStyle style;

  public InfoDefaultAppearance() {
    this.style = resources.style();
    this.style.ensureInjected();

    this.template = GWT.create(Template.class);

    if (GXT.isIE6()) {
      frame = new TableFrame((TableFrameResources) GWT.create(InfoTableFrameResources.class));
    } else {
      frame = new DivFrame((DivFrameResources) GWT.create(InfoDivFrameResources.class));
    }
  }

  @Override
  public XElement getContentElement(XElement parent) {
    return parent.selectNode("." + style.info());
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    frame.render(sb, Frame.EMPTY_FRAME, template.render(style));
  }

}