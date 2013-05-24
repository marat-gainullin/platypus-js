/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.dom;

import com.google.gwt.dom.client.Element;

/**
 * Utility class for creating elements from HTML fragments.
 */
public class DomHelper {
  static {
    Ext.loadExt();
    Ext.loadDomHelper();
  }

  /**
   * Creates new DOM element(s) and appends them to el.
   * 
   * @param elem the context element
   * @param html raw HTML fragment
   * @return the new element
   */
  public static native Element append(Element elem, String html) /*-{
		var Ext = @com.sencha.gxt.core.client.dom.Ext::ext;
		return Ext.DomHelper.append(elem, html, false);
  }-*/;

  /**
   * Creates new DOM element(s) and inserts them after el.
   * 
   * @param elem the context element
   * @param html raw HTML fragment
   * @return the new element
   */
  public static native Element insertAfter(Element elem, String html) /*-{
		var Ext = @com.sencha.gxt.core.client.dom.Ext::ext;
		return Ext.DomHelper.doInsert(elem, html, false, "afterEnd",
				"nextSibling");
  }-*/;

  /**
   * Creates new DOM element(s) and inserts them before el.
   * 
   * @param elem the context element
   * @param html raw HTML fragment
   * @return the new element
   */
  public static native Element insertBefore(Element elem, String html) /*-{
		var Ext = @com.sencha.gxt.core.client.dom.Ext::ext;
		return Ext.DomHelper.doInsert(elem, html, false, "beforeBegin");
  }-*/;

  /**
   * Creates new DOM element(s) and inserts them as the first child of el.
   * 
   * @param elem the context element
   * @param html raw HTML fragment
   * @return the new element
   */
  public static native Element insertFirst(Element elem, String html) /*-{
		var Ext = @com.sencha.gxt.core.client.dom.Ext::ext;
		return Ext.DomHelper.doInsert(elem, html, false, "afterBegin",
				"firstChild");
  }-*/;

  /**
   * Inserts an HTML fragment into the DOM.
   * 
   * @param where where to insert the html in relation to el - beforeBegin,
   *          afterBegin, beforeEnd, afterEnd.
   * @param el the context element
   * @param html the HTML fragment
   * @return the inserted node (or nearest related if more than 1 inserted)
   */
  public static native Element insertHtml(String where, Element el, String html) /*-{
		if (!html)
			return el;

		var Ext = @com.sencha.gxt.core.client.dom.Ext::ext;
		return Ext.DomHelper.insertHtml(where, el, html);
  }-*/;

  /**
   * Creates new DOM element(s) and overwrites the contents of el with them.
   * 
   * @param elem the context element
   * @param html raw HTML fragment
   * @return the first new element
   */
  public static native Element overwrite(Element elem, String html) /*-{
		var Ext = @com.sencha.gxt.core.client.dom.Ext::ext;
		return Ext.DomHelper.overwrite(elem, html);
  }-*/;

}
