/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.dom.client.Node;

/**
 * 
 * @author mg
 */
public class XElement extends Element {

	public interface Observer {

		public void observe(Element anElement);
	}

	protected XElement() {
		super();
	}

	public final native double getSubPixelComputedWidth() /*-{
		if ($wnd.P.getComputedStyle)
			return parseFloat($wnd.P.getComputedStyle(this).width);
		else
			return -1;
	}-*/;

	public final native double getSubPixelComputedHeight() /*-{
		if ($wnd.P.getComputedStyle)
			return parseFloat($wnd.P.getComputedStyle(this).height);
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
	 * @param aClassName
	 *            the css selector
	 * @return the child element
	 */
	public final XElement child(String aClassName) {
		Element child = childElement(aClassName);
		return child == null ? null : child.<XElement> cast();
	}

	/**
	 * Selects a single child at any depth below this element based on the
	 * passed CSS selector.
	 * 
	 * @param aClassName
	 *            the css selector
	 * @return the child element
	 */
	public final Element childElement(String aClassName) {
		List<Element> result = select(aClassName);
		return result != null && !result.isEmpty() ? result.get(0) : null;
	}

	/**
	 * Selects child nodes based on the passed CSS selector (the selector should
	 * not contain an id).
	 * 
	 * @param aClassName
	 *            the selector/xpath query
	 * @return the matching elements
	 */
	public final List<Element> select(final String aClassName) {
		final List<Element> result = new ArrayList<>();
		iterate(this, new Observer() {
			@Override
			public void observe(Element anElement) {
				if ("*".equals(aClassName)) {
					result.add(anElement);
				} else {
					if (anElement.getClassName() != null && anElement.hasClassName(aClassName))
						result.add(anElement);
				}
			}
		});
		return result;
	}

	public final List<Element> selectByPrefix(final String aClassNamePrefix) {
		final List<Element> result = new ArrayList<>();
		iterate(this, new Observer() {
			@Override
			public void observe(Element anElement) {
				String classesNames = anElement.getClassName();
				if (classesNames != null) {
					String[] classes = classesNames.split(" ");
					for (int i = 0; i < classes.length; i++) {
						if (classes[i].startsWith(aClassNamePrefix)) {
							result.add(anElement);
							break;
						}
					}
				}
			}
		});
		return result;
	}

	protected static void iterate(Element aRoot, Observer aTask) {
		if (aRoot != null) {
			aTask.observe(aRoot);
			NodeList<Node> nl = aRoot.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.getItem(i);
				if (n instanceof Element) {
					iterate((Element) n, aTask);
				}
			}
		}
	}

	/**
	 * Puts a mask over this element to disable user interaction.
	 * 
	 * @param message
	 *            a message to display in the mask
	 */
	public final void errorMask(String message) {
		mask("p-mask-loading-error");
	}

	public final void loadMask() {
		mask("p-mask-loading-start");
	}

	public final void disabledMask() {
		mask("p-mask-disabled");
	}

	public final void mask(String aClassName) {
		Element mask = Document.get().createDivElement();
		mask.getStyle().setLeft(0, Style.Unit.PX);
		mask.getStyle().setTop(0, Style.Unit.PX);
		mask.getStyle().setRight(0, Style.Unit.PX);
		mask.getStyle().setBottom(0, Style.Unit.PX);
		mask.getStyle().setPosition(Style.Position.ABSOLUTE);
		mask.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		mask.setClassName("p-mask");
		Element maskInner = Document.get().createDivElement();
		maskInner.getStyle().setLeft(0, Style.Unit.PX);
		maskInner.getStyle().setTop(0, Style.Unit.PX);
		maskInner.getStyle().setRight(0, Style.Unit.PX);
		maskInner.getStyle().setBottom(0, Style.Unit.PX);
		maskInner.getStyle().setPosition(Style.Position.ABSOLUTE);
		maskInner.getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		maskInner.setClassName(aClassName);
		mask.appendChild(maskInner);
		appendChild(mask);
	}

	/**
	 * Removes a mask over this element to disable user interaction.
	 * 
	 */
	public final void unmask() {
		NodeList<Node> nl = getChildNodes();
		for (int i = nl.getLength() - 1; i >= 0; i--) {
			Node n = nl.getItem(i);
			if (Element.is(n) && Element.as(n).getClassName() != null && Element.as(n).hasClassName("p-mask")) {
				n.removeFromParent();
			}
		}
	}

	public final int getChildIndex(Element aChild) {
		NodeList<Node> nl = getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.getItem(i) == aChild) {
				return i;
			}
		}
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

	public final native JavaScriptObject addResizingTransitionEnd(RequiresResize aTarget)/*-{
		var handler = function(aEvent) {
			if (aEvent.propertyName == "width" || aEvent.propertyName == "height" || aEvent.propertyName == "left" || aEvent.propertyName == "top" || aEvent.propertyName == "right" || aEvent.propertyName == "bottom") {
				aEvent.stopPropagation();
				aTarget.@com.google.gwt.user.client.ui.RequiresResize::onResize()();
			}
		};
		this.addEventListener("transitionend", handler, false);
		this.addEventListener("webkitTransitionEnd", handler, false);
		return handler;
	}-*/;

	public final native void removeTransitionEndListener(JavaScriptObject aListener)/*-{
		this.removeEventListener("transitionend", aListener, false);
		this.removeEventListener("webkitTransitionEnd", aListener, false);
	}-*/;
}
