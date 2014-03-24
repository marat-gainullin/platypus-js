package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.containers.VerticalBoxPanel;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

public class VBoxPane extends VerticalBoxPanel implements HasPublished {

	protected JavaScriptObject published;

	public VBoxPane() {
		super();
	}

	public VBoxPane(int aVGap) {
		super();
		setVgap(aVGap);
	}

	public void ajustHeight(Widget aChild, int aValue) {
		if (aChild != null) {
			XElement xwe = aChild.getElement().<XElement>cast();
			int hDelta = xwe.getOffsetHeight() - xwe.getContentHeight();
			xwe.getStyle().setHeight(aValue - hDelta, Style.Unit.PX);
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
