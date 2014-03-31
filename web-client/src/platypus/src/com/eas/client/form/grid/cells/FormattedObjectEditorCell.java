/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.form.grid.cells;

import java.text.ParseException;

import com.bearsoft.gwt.ui.widgets.ObjectFormat;
import com.bearsoft.gwt.ui.widgets.grid.cells.AbstractPopupEditorCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class FormattedObjectEditorCell extends AbstractPopupEditorCell<Object> {

	protected ObjectFormat format;

	public FormattedObjectEditorCell() {
		super(new TextBox(), BrowserEvents.CLICK, BrowserEvents.DBLCLICK, BrowserEvents.KEYUP);
	}

	public FormattedObjectEditorCell(Widget aEditor) {
		super(aEditor, BrowserEvents.CLICK, BrowserEvents.DBLCLICK, BrowserEvents.KEYUP);
	}

	public FormattedObjectEditorCell(Widget aEditor, ObjectFormat aFormat) {
		super(aEditor, BrowserEvents.CLICK, BrowserEvents.DBLCLICK, BrowserEvents.KEYUP);
		format = aFormat;
	}

	public ObjectFormat getFormat() {
		return format;
	}

	public void setFormat(ObjectFormat aValue) {
		format = aValue;
	}

	@Override
	public void render(Context context, Object value, SafeHtmlBuilder sb) {
		if (format != null)
			try {
				sb.appendEscaped(format.format(value));
			} catch (ParseException e) {
				sb.appendEscaped(e.getMessage());
			}
		else
			sb.appendEscaped(String.valueOf(value));
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, Object value, NativeEvent event, ValueUpdater<Object> valueUpdater) {
		switch (event.getType()) {
		case BrowserEvents.DBLCLICK:
			startEditing(parent, value, valueUpdater);
			break;
		case BrowserEvents.KEYUP:
			if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
				startEditing(parent, value, valueUpdater);
			} else {
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
			}
			break;
		default:
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
			break;
		}
	}
}
