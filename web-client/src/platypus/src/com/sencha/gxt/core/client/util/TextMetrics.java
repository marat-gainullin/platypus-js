/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Visibility;
import com.sencha.gxt.core.client.dom.XElement;

/**
 * Provides precise pixel measurements for blocks of text so that you can
 * determine exactly how high and wide, in pixels, a given block of text will
 * be.
 */
public class TextMetrics {

  private static TextMetrics instance;

  /**
   * Returns the singleton instance.
   * 
   * @return the text metrics instance
   */
  public static TextMetrics get() {
    if (instance == null) {
      instance = new TextMetrics();
    }
    return instance;
  }

  private XElement el;

  private TextMetrics() {
    el = XElement.createElement("div");
    Document.get().getBody().appendChild(el);
    el.makePositionable(true);
    el.setLeftTop(-10000, -10000);
    el.getStyle().setVisibility(Visibility.HIDDEN);
  }

  /**
   * Binds this TextMetrics instance to an element from which to copy existing
   * CSS styles that can affect the size of the rendered text.
   * 
   * @param el the element
   */
  public void bind(Element el) {
    bind(XElement.as(el));
  }

  /**
   * Binds this TextMetrics instance to an element from which to copy existing
   * CSS styles that can affect the size of the rendered text.
   * 
   * @param el the element
   */
  public void bind(XElement el) {   
    //needed sometimes to force a refresh
    el.repaint();
    List<String> l = new ArrayList<String>();
    l.add("fontSize");
    l.add("fontWeight");
    l.add("fontStyle");
    l.add("fontFamily");
    l.add("lineHeight");
    l.add("textTransform");
    l.add("letterSpacing");

   
    Map<String, String> map = el.getComputedStyle(l);
    for (String key : map.keySet()) {
      this.el.getStyle().setProperty(key, map.get(key));
    }
  }

  public void bind(String className) {
    this.el.setClassName(className);
  }

  /**
   * Returns the measured height of the specified text. For multiline text, be
   * sure to call {@link #setFixedWidth} if necessary.
   * 
   * @param text the text to be measured
   * @return the height in pixels
   */
  public int getHeight(String text) {
    return getSize(text).getHeight();
  }

  /**
   * Returns the size of the specified text based on the internal element's
   * style and width properties.
   * 
   * @param text the text to measure
   * @return the size
   */
  public Size getSize(String text) {
    el.setInnerHTML(text);
    Size size = el.getSize();
    el.setInnerHTML("");
    return size;
  }

  /**
   * Returns the measured width of the specified text.
   * 
   * @param text the text to measure
   * @return the width in pixels
   */
  public int getWidth(String text) {
    el.getStyle().setProperty("width", "auto");
    return getSize(text).getWidth();
  }

  /**
   * Sets a fixed width on the internal measurement element. If the text will be
   * multiline, you have to set a fixed width in order to accurately measure the
   * text height.
   * 
   * @param width the width to set on the element
   */
  public void setFixedWidth(int width) {
    el.setWidth(width);
  }

}
