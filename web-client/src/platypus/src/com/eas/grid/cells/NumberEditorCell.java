package com.eas.grid.cells;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 * @param <T>
 */
public class NumberEditorCell<T extends Number> extends RenderedEditorCell<T> {

	protected NumberFormat format = NumberFormat.getDecimalFormat();

	public NumberEditorCell(Widget aEditor) {
		super(aEditor);
	}

	public NumberEditorCell(Widget aEditor, NumberFormat aFormat) {
		super(aEditor);
		format = aFormat;
	}

	public NumberFormat getFormat() {
		return format;
	}

	public void setFormat(NumberFormat aValue) {
		format = aValue;
	}

	@Override
	protected void renderCell(Context context, T value, SafeHtmlBuilder sb) {
		sb.append(SafeHtmlUtils.fromTrustedString(value != null ? format.format(value) : ""));
	}
}
