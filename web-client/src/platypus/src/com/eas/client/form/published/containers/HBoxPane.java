package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.containers.HorizontalBoxPanel;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

public class HBoxPane extends HorizontalBoxPanel implements HasPublished {

	protected JavaScriptObject published;

	public HBoxPane() {
		super();
	}

	public HBoxPane(int aHGap) {
		super();
		setHgap(aHGap);
	}

	public void ajustWidth(Widget aChild, int aValue) {
		if (aChild != null) {
			XElement xwe = aChild.getElement().<XElement> cast();
			int hDelta = xwe.getOffsetWidth() - xwe.getContentWidth();
			xwe.getStyle().setWidth(aValue - hDelta, Style.Unit.PX);
			onResize();
		}
	}

	@Override
	public JavaScriptObject getPublished() {
		return published;
	}

	@Override
	public void setPublished(JavaScriptObject aValue) {
		if (published != aValue) {
			published = aValue;
			if (published != null) {
				publish(this, aValue);
			}
		}
	}

	private native static void publish(HasPublished aWidget, JavaScriptObject aPublished)/*-{
	}-*/;
}
