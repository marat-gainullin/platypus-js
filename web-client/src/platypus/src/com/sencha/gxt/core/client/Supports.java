/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

/**
 * Determines information about the current platform the application is running on.
 */
public class Supports {

  static {
    Element div = DOM.createDiv();
    
    StringBuilder sb = new StringBuilder();
    sb.append("<div style='height:30px;width:50px'>");
    sb.append("<div style='height:20px;width:20px;'></div>");
    sb.append("</div>");
    sb.append("<div style='float:left; background-color:transparent;'></div>");
    
    div.setInnerHTML(sb.toString());

    css3BorderRadius = hasCss3BorderRadiusTest();
    css3LinearGradient = hasCss3LinearGradientInternal(div);
  }

  private static boolean css3BorderRadius;
  private static boolean css3LinearGradient;

  /**
   * Returns true if the device supports CSS3 linear gradients.
   * 
   * @return true for CSS3 linear gradients
   */
  public static boolean hasCss3BorderRadius() {
    return css3BorderRadius;
  }

  /**
   * Returns true if the device supports CSS3 border radius.
   * 
   * @return true for CSS3 border radius
   */
  public static boolean hasCss3LinearGradient() {
    return css3LinearGradient;
  }

  private static native boolean hasCss3BorderRadiusTest() /*-{
		var domPrefixes = [ 'borderRadius', 'BorderRadius', 'MozBorderRadius',
				'WebkitBorderRadius', 'OBorderRadius', 'KhtmlBorderRadius' ], pass = false, i;

		for (i = 0; i < domPrefixes.length; i++) {
			if (document.body.style[domPrefixes[i]] !== undefined) {
				return true;
			}
		}
		return pass;
  }-*/;

  private static native boolean hasCss3LinearGradientInternal(Element div) /*-{
		var property = 'background-image:', webkit = '-webkit-gradient(linear, left top, right bottom, from(black), to(white))', w3c = 'linear-gradient(left top, black, white)', moz = '-moz-'
				+ w3c, options = [ property + webkit, property + w3c,
				property + moz ];

		div.style.cssText = options.join(';');

		return ("" + div.style.backgroundImage).indexOf('gradient') !== -1;

  }-*/;
}
