package com.bearsoft.gwt.ui.widgets.grid.cells;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 * @param <T>
 */
public class NumberEditorCell<T extends Number> extends AbstractPopupEditorCell<T> {

	protected NumberFormat format = NumberFormat.getDecimalFormat();

	public NumberEditorCell(Widget aEditor) {
		super(aEditor, BrowserEvents.CLICK, BrowserEvents.DBLCLICK);
	}

	public NumberEditorCell(Widget aEditor, NumberFormat aFormat) {
		super(aEditor, BrowserEvents.CLICK, BrowserEvents.DBLCLICK);
		format = aFormat;
	}

	public NumberFormat getFormat() {
		return format;
	}

	public void setFormat(NumberFormat aValue) {
		format = aValue;
	}

	@Override
	public void render(Context context, T value, SafeHtmlBuilder sb) {
		sb.append(SafeHtmlUtils.fromTrustedString(value != null ? format.format(value) : ""));
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
		if (BrowserEvents.DBLCLICK.equals(event.getType())) {
			startEditing(parent, value, valueUpdater);
		} else {
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}
}
