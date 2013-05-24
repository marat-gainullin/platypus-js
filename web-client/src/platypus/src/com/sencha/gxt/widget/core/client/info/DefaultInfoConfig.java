/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * Configuration settings for {@link Info} which supports a title and text.
 */
public class DefaultInfoConfig extends InfoConfig {

  public interface DefaultInfoConfigAppearance {

    void render(SafeHtmlBuilder sb, String title, String message);
  }

  private DefaultInfoConfigAppearance appearance;
  private String title;
  private String message;

  /**
   * Creates a new config.
   * 
   * @param title the title as HTML
   * @param message the message as HTML
   */
  public DefaultInfoConfig(String title, String message) {
    this((DefaultInfoConfigAppearance) GWT.create(DefaultInfoConfigAppearance.class), title, message);
  }

  public DefaultInfoConfig(DefaultInfoConfigAppearance appearance, String title, String message) {
    this.appearance = appearance;
    this.title = title;
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public String getTitle() {
    return title;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  protected SafeHtml render(Info info) {
    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    appearance.render(builder, title, message);
    return builder.toSafeHtml();
  }

}
