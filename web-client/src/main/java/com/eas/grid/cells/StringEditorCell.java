/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid.cells;

import java.text.ParseException;

import com.eas.widgets.boxes.MaskFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class StringEditorCell extends RenderedEditorCell<Object> {

	protected MaskFormat format;

	public StringEditorCell() {
		super(new TextBox());
	}

	public StringEditorCell(Widget aEditor) {
		super(aEditor);
	}

	public StringEditorCell(Widget aEditor, MaskFormat aFormat) {
		super(aEditor);
		format = aFormat;
	}

	public MaskFormat getFormat() {
		return format;
	}

	public void setFormat(MaskFormat aValue) {
		format = aValue;
	}

	@Override
	protected void renderCell(Context context, Object value, SafeHtmlBuilder sb) {
		if (format != null) {
			try {
				sb.appendEscaped(format.format(value));
			} catch (ParseException e) {
				sb.appendEscaped(e.getMessage());
			}
		} else {
			sb.appendEscaped(value != null ? value.toString() : "");
		}
	}
}
