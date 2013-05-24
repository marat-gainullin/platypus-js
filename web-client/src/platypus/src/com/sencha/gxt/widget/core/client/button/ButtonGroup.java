/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public class ButtonGroup extends SimpleContainer {

  public interface ButtonGroupAppearance {
    void render(SafeHtmlBuilder sb);

    void updateText(XElement parent, String text);
    
    void onHideHeader(XElement parent, boolean hide);

    XElement getHeaderElement(XElement parent);

    XElement getContentElem(XElement parent);

    int getFrameHeight(XElement parent);

    int getFrameWidth(XElement parent);
  }

  private final ButtonGroupAppearance appearance;
  private String heading;

  public ButtonGroup() {
    this(GWT.<ButtonGroupAppearance> create(ButtonGroupAppearance.class));
  }

  public ButtonGroup(ButtonGroupAppearance appearance) {
    super(true);
    this.appearance = appearance;

    setDeferHeight(true);

    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    this.appearance.render(builder);

    setElement(XDOM.create(builder.toSafeHtml()));

//    addStyleName("x-toolbar-mark");
  }

  public void setHeadingText(String text) {
    this.heading = text;
    appearance.updateText(getElement(), text);
  }

  public String getHeadingText() {
    return heading;
  }
  
  public void setHeaderVisible(boolean visible) {
    appearance.onHideHeader(getElement(), !visible);
  }

  @Override
  protected void onResize(int width, int height) {
    Size frameSize = getFrameSize();

    if (isAutoWidth()) {
      getContainerTarget().getStyle().clearWidth();
    } else {
      width -= frameSize.getWidth();
      getContainerTarget().setWidth(width - frameSize.getWidth(), true);

    }

    if (isAutoHeight()) {
      getContainerTarget().getStyle().clearHeight();
    } else {
      height -= frameSize.getHeight();
      height -= appearance.getHeaderElement(getElement()).getOffsetHeight();
      getContainerTarget().setHeight(height - frameSize.getHeight(), true);
    }

    super.onResize(width, height);
  }

  @Override
  protected XElement getContainerTarget() {
    return appearance.getContentElem(getElement());
  }

  protected Size getFrameSize() {
    return new Size(appearance.getFrameWidth(getElement()), appearance.getFrameHeight(getElement()));
  }

}
