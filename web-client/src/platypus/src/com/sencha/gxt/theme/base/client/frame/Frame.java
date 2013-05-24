/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.frame;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Size;

/**
 * Defines the interface for classes which "frame" a given element and support
 * both a header and content section. In general, frames provide support for
 * rounded corners.
 * 
 * @see TableFrame
 * @see DivFrame
 */
public interface Frame {

  /**
   * Render options for Frames.
   */
  public static class FrameOptions {

    private static SafeStyles EMPTY = SafeStylesUtils.fromTrustedString("");

    private String tabIndex = "";
    private SafeStyles frameStyle;
    private String frameClasses = "";

    /**
     * Creates a new frame options instance.
     */
    public FrameOptions() {

    }

    /**
     * Creates a new frame options instance. In many cases, the Frame will be
     * the "outer" or "root" element and will need to have classes and styles
     * defined by content being "wrapped" by the frame.
     * 
     * @param tabIndex the tab index to be applied to frame
     * @param frameClasses a space separated list of CSS class names to be
     *          applied to frame
     * @param frameStyle a safe styles instance to be applied to the frame
     */
    public FrameOptions(Integer tabIndex, String frameClasses, SafeStyles frameStyle) {
      this.tabIndex = tabIndex == null ? "" : tabIndex.toString();
      this.frameClasses = frameClasses;
      this.frameStyle = frameStyle;
    }

    /**
     * Returns the frame classes.
     * 
     * @return the space separated list of CSS class names
     */
    public String getFrameClasses() {
      return frameClasses;
    }

    /**
     * Returns the frame style.
     * 
     * @return the style
     */
    public SafeStyles getFrameStyle() {
      if (frameStyle == null) {
        return EMPTY;
      }
      return frameStyle;
    }

    /**
     * Returns the tab index.
     * 
     * @return the tab index or "" if not specified
     */
    public String getTabIndex() {
      return tabIndex;
    }

    public void setFrameClasses(String frameClasses) {
      this.frameClasses = frameClasses;
    }

    public void setFrameStyle(SafeStyles frameStyle) {
      this.frameStyle = frameStyle;
    }

    public void setTabIndex(String tabIndex) {
      this.tabIndex = tabIndex;
    }

  }

  public static final FrameOptions EMPTY_FRAME = new FrameOptions();

  XElement getContentElem(XElement parent);

  Size getFrameSize();

  XElement getHeaderElem(XElement parent);

  void onFocus(XElement parent, boolean focus, NativeEvent event);

  void onHideHeader(XElement parent, boolean hide);

  void onOver(XElement parent, boolean over, NativeEvent event);

  void onPress(XElement parent, boolean pressed, NativeEvent event);

  String overClass();

  String pressedClass();

  void render(SafeHtmlBuilder builder, FrameOptions options, SafeHtml content);

}
