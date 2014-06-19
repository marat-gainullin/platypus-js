/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.containers;

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
	public void add(Widget w) {
		w.getElement().getStyle().setMarginLeft(hgap, Style.Unit.PX);
		w.getElement().getStyle().setMarginTop(vgap, Style.Unit.PX);
		super.add(w);
	}

	@Override
	public void onResize() {
		// reserved for future use.
	}
}
