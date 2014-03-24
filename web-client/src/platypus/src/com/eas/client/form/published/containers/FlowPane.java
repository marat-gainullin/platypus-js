package com.eas.client.form.published.containers;

import com.bearsoft.gwt.ui.XElement;
import com.bearsoft.gwt.ui.containers.FlowGapPanel;
import com.eas.client.form.published.HasPublished;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.Style;

public class FlowPane extends FlowGapPanel implements HasPublished{

	protected JavaScriptObject published;
	
	public FlowPane() {
		super();
	}
	
	public FlowPane(int aVGap, int aHGap) {
		super();
		setHgap(aHGap);
		setVgap(aVGap);
	}
	
	public static void ajustWidth(Widget aChild, int aValue){
		if (aChild != null) {
			XElement xwe = aChild.getElement().<XElement>cast();
			int hDelta = xwe.getOffsetWidth() - xwe.getContentWidth();
			xwe.getStyle().setWidth(aValue - hDelta, Style.Unit.PX);
		}
	}
	
	public static void ajustHeight(Widget aChild, int aValue){
		if (aChild != null) {
			XElement xwe = aChild.getElement().<XElement>cast();
			int hDelta = xwe.getOffsetHeight() - xwe.getContentHeight();
			xwe.getStyle().setHeight(aValue - hDelta, Style.Unit.PX);
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

	private native static void publish(HasPublished aWidget, JavaScriptObject published)/*-{
		published.add = function(toAdd){
			if(toAdd && toAdd.unwrap){
				aComponent.@com.eas.client.form.published.containers.FlowPane::add(Lcom/google/gwt/user/client/ui/Widget;)(toAdd.unwrap());
			}
		}
	}-*/;
}
