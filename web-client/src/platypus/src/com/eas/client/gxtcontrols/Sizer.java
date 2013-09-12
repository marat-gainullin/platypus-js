package com.eas.client.gxtcontrols;

import java.util.Iterator;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.form.FieldSet;

public class Sizer {

	public static int getWidgetLeft(Widget aWidget) {
		Element element = aWidget.getElement();
		if (aWidget.getParent() instanceof FieldSet) {
			element = aWidget.getParent().getElement();// FieldSet trick
		} else if (aWidget.getParent() instanceof BorderLayoutContainer && aWidget.getParent().getParent() instanceof LayoutPanel) {
			element = aWidget.getParent().getElement().getParentElement();// LayoutPanel
			                                                              // trick
		}
		return element.getOffsetLeft();
	}

	public static int getWidgetTop(Widget aWidget) {
		Element element = aWidget.getElement();
		if (aWidget.getParent() instanceof FieldSet) {
			element = aWidget.getParent().getElement();// FieldSet trick
		} else if (aWidget.getParent() instanceof BorderLayoutContainer && aWidget.getParent().getParent() instanceof LayoutPanel) {
			element = aWidget.getParent().getElement().getParentElement();// LayoutPanel
			                                                              // trick
		}
		return element.getOffsetTop();
	}

	public static int getWidgetWidth(Widget aWidget) {
		Element element = aWidget.getElement();
		if (aWidget.getParent() instanceof FieldSet) {
			element = aWidget.getParent().getElement();// FieldSet trick
		} else if (aWidget.getParent() instanceof BorderLayoutContainer && aWidget.getParent().getParent() instanceof LayoutPanel) {
			element = aWidget.getParent().getElement().getParentElement();// LayoutPanel
			                                                              // trick
		}
		return element.getOffsetWidth();
	}

	public static int getWidgetHeight(Widget aWidget) {
		Element element = aWidget.getElement();
		if (aWidget.getParent() instanceof FieldSet) {
			element = aWidget.getParent().getElement();// FieldSet trick
		} else if (aWidget.getParent() instanceof BorderLayoutContainer && aWidget.getParent().getParent() instanceof LayoutPanel) {
			element = aWidget.getParent().getElement().getParentElement();// LayoutPanel
			                                                              // trick
		}
		return element.getOffsetHeight();
	}

	public static Size boxSize(HasWidgets aBox) {
		int minH = 0;
		int maxH = 0;
		int minV = 0;
		int maxV = 0;
		Iterator<Widget> widgets = aBox.iterator();
		while (widgets.hasNext()) {
			Widget w = widgets.next();
			int widgetHeight = getWidgetHeight(w);
			int widgetWidth = getWidgetWidth(w);
			int widgetTop = getWidgetTop(w);
			int widgetLeft = getWidgetLeft(w);
			if (minH > widgetLeft)
				minH = widgetLeft;
			if (maxH < widgetLeft + widgetWidth)
				maxH = widgetLeft + widgetWidth;
			if (minV > widgetTop)
				minV = widgetTop;
			if (maxV < widgetTop + widgetHeight)
				maxV = widgetTop + widgetHeight;
		}
		return new Size(maxH - minH, maxV - minV);
	}
}
