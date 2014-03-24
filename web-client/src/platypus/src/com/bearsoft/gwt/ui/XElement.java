/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;

/**
 * 
 * @author mg
 */
public class XElement extends Element {

	protected XElement() {
		super();
	}

	public final native double getSubPixelComputedWidth() /*-{
		if ($wnd.getComputedStyle)
			return parseFloat($wnd.getComputedStyle(this).width);
		else
			return -1;
	}-*/;

	public final native double getSubPixelComputedHeight() /*-{
		if ($wnd.getComputedStyle)
			return parseFloat($wnd.getComputedStyle(this).height);
		else
			return -1;
	}-*/;

	public final int getContentWidth() {
		Element ruler = DOM.createDiv();
		ruler.getStyle().setMargin(0, Style.Unit.PX);
		ruler.getStyle().setPadding(0, Style.Unit.PX);
		ruler.getStyle().setBorderWidth(0, Style.Unit.PX);
		ruler.getStyle().setPosition(Style.Position.RELATIVE);
		ruler.getStyle().setWidth(100, Style.Unit.PCT);
		insertFirst(ruler);
		try {
			double computedWidth = ruler.<XElement> cast().getSubPixelComputedWidth();
			if (computedWidth != -1) {
				return (int) Math.floor(computedWidth);
			} else
				return ruler.getOffsetWidth();
		} finally {
			ruler.removeFromParent();
		}
	}

	public final int getContentHeight() {
		Element ruler = DOM.createDiv();
		ruler.getStyle().setMargin(0, Style.Unit.PX);
		ruler.getStyle().setPadding(0, Style.Unit.PX);
		ruler.getStyle().setBorderWidth(0, Style.Unit.PX);
		ruler.getStyle().setPosition(Style.Position.RELATIVE);
		ruler.getStyle().setHeight(100, Style.Unit.PCT);
		insertFirst(ruler);
		try {
			double computedHeight = ruler.<XElement> cast().getSubPixelComputedHeight();
			if (computedHeight != -1) {
				return (int) Math.floor(computedHeight);
			} else
				return ruler.getOffsetHeight();
		} finally {
			ruler.removeFromParent();
		}
	}

	/**
	 * Selects a single child at any depth below this element based on the
	 * passed CSS selector.
	 * 
	 * @param selector
	 *            the css selector
	 * @return the child element
	 */
	public final XElement child(String selector) {
		Element child = childElement(selector);
		return child == null ? null : child.<XElement> cast();
	}

	/**
	 * Selects a single child at any depth below this element based on the
	 * passed CSS selector.
	 * 
	 * @param selector
	 *            the css selector
	 * @return the child element
	 */
	public final Element childElement(String selector) {
		return null;
	}

	/**
	 * Selects child nodes based on the passed CSS selector (the selector should
	 * not contain an id).
	 * 
	 * @param selector
	 *            the selector/xpath query
	 * @return the matching elements
	 */
	public final NodeList<Element> select(String selector) {
		return null;
	}

	/**
	 * Puts a mask over this element to disable user interaction.
	 * 
	 * @param message
	 *            a message to display in the mask
	 */
	public final void mask(String message) {
	}

	/**
	 * Removes a mask over this element to disable user interaction.
	 * 
	 */
	public final void unmask() {
	}

	public int getChildIndex(Element aChild){
		return -1;
	}
	
	/**
	 * Generates a native dom click on the element.
	 */
	public final native void click() /*-{
		var el = this;
		if (el.click) {
			el.click();
		} else {
			var event = $doc.createEvent("MouseEvents");
			event.initEvent('click', true, true, $wnd, 0, 0, 0, 0, 0, false, false, false, false, 1, el);
			el.dispatchEvent(event);
		}
	}-*/;

}
