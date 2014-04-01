/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid.cells;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import java.util.Date;

/**
 * 
 * @author mg
 */
public class DateEditorCell extends RenderedPopupEditorCell<Date> {

	protected DateTimeFormat format = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);

	public DateEditorCell() {
		super(new DateBox());
	}

	public DateEditorCell(Widget aEditor) {
		super(aEditor);
	}

	public DateEditorCell(Widget aEditor, DateTimeFormat aFormat) {
		super(aEditor);
		format = aFormat;
	}

	public DateTimeFormat getFormat() {
		return format;
	}

	public void setFormat(DateTimeFormat aValue) {
		format = aValue;
	}

	@Override
	protected void renderCell(Context context, Date value, SafeHtmlBuilder sb) {
		sb.append(SafeHtmlUtils.fromTrustedString(value != null ? format.format(value) : ""));
	}
}
