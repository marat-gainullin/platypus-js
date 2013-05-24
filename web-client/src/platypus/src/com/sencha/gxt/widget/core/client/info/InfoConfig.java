/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.info;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;

/**
 * Abstract base class for configuration settings for {@link Info}.
 */
public abstract class InfoConfig {

  private int display = 2500;
  private int width = 225;
  private int height = -1;

  private ShowHandler showHandler;
  private HideHandler hideHandler;

  public int getDisplay() {
    return display;
  }

  public int getHeight() {
    return height;
  }

  public HideHandler getHideHandler() {
    return hideHandler;
  }

  public ShowHandler getShowHandler() {
    return showHandler;
  }

  public int getWidth() {
    return width;
  }

  public void setDisplay(int display) {
    this.display = display;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setHideHandler(HideHandler hideHandler) {
    this.hideHandler = hideHandler;
  }

  public void setShowHandler(ShowHandler showHandler) {
    this.showHandler = showHandler;
  }

  public void setWidth(int width) {
    this.width = width;
  }
  
  protected abstract SafeHtml render(Info info);

}
