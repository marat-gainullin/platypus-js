/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HasHTML;
import com.sencha.gxt.core.client.util.HasUiAttributes;

/**
 * Config object which controls the content and behavior of a widget in a
 * TabPanel.
 * 
 * <p />
 * When updating the config object after the widget has been inserted, you must
 * call
 * {@link TabPanel#update(com.google.gwt.user.client.ui.Widget, TabItemConfig)}.
 */
public class TabItemConfig implements HasIcon, HasHTML, HasSafeHtml, HasUiAttributes {

  private boolean closable;
  private boolean asHTML = false;
  private String text;
  private ImageResource icon;
  private boolean enabled = true;

  /**
   * Creates a tab item configuration.
   */
  public TabItemConfig() {

  }

  /**
   * Creates a tab item configuration with the specified text.
   * 
   * @param text the text of the tab item.
   */
  public TabItemConfig(String text) {
    this();
    setText(text);
  }

  /**
   * Creates a tab item configuration with the specified properties.
   * 
   * @param text the tab item text
   * @param close true to indicate the tab is closable 
   */
  public TabItemConfig(String text, boolean close) {
    this();
    setText(text);
    setClosable(close);
  }

  @Override
  public String getHTML() {
    return text;
  }

  @Override
  public ImageResource getIcon() {
    return icon;
  }

  @Override
  public String getText() {
    return text;
  }

  /**
   * Returns the item closable state.
   * 
   * @return true if closable
   */
  public boolean isClosable() {
    return closable;
  }

  /**
   * Returns the enable / disable state.
   * 
   * @return true if enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Returns true if the item text is treated as HTML.
   * 
   * @return true for HTML
   */
  public boolean isHTML() {
    return asHTML;
  }

  /**
   * True to allow the item to be closable (defaults to false).
   * 
   * @param closable true for closable
   */
  public void setClosable(boolean closable) {
    this.closable = closable;
  }

  /**
   * True to enable, false to disable (defaults to true).
   * 
   * @param enabled the enabled state
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public void setHTML(SafeHtml html) {
    setHTML(html.asString());
  }

  @Override
  public void setHTML(String html) {
    asHTML = true;
    this.text = html;
  }

  @Override
  public void setIcon(ImageResource icon) {
    this.icon = icon;
  }

  @Override
  public void setText(String text) {
    asHTML = false;
    this.text = text;
  }

}
