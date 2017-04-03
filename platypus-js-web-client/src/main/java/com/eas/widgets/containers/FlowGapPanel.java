package com.eas.widgets.containers;

import com.eas.core.XElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class FlowGapPanel extends FlowPanel implements RequiresResize {

	protected int hgap;
	protected int vgap;

	public FlowGapPanel() {
		super();
		getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NORMAL);
		getElement().getStyle().setLineHeight(0, Style.Unit.PX);
		getElement().<XElement>cast().addResizingTransitionEnd(this);
	}

	public int getHgap() {
		return hgap;
	}

	public void setHgap(int aValue) {
		hgap = aValue;
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			w.getElement().getStyle().setMarginLeft(hgap, Style.Unit.PX);
		}
	}

	public int getVgap() {
		return vgap;
	}

	public void setVgap(int aValue) {
		vgap = aValue;
		for (int i = 0; i < getWidgetCount(); i++) {
			Widget w = getWidget(i);
			w.getElement().getStyle().setMarginTop(vgap, Style.Unit.PX);
		}
	}

	@Override
	public void add(Widget aWidget) {
		aWidget.getElement().getStyle().setMarginLeft(hgap, Style.Unit.PX);
		aWidget.getElement().getStyle().setMarginTop(vgap, Style.Unit.PX);
		aWidget.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
		aWidget.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.BOTTOM);
		super.add(aWidget);
	}

	@Override
	public void onResize() {
		// reserved for future use.
	}
}
