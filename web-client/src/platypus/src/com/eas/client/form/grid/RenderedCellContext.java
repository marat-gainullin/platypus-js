package com.eas.client.form.grid;

import com.eas.client.form.published.PublishedStyle;
import com.google.gwt.cell.client.Cell;

public class RenderedCellContext extends Cell.Context {

	protected PublishedStyle style;

	public RenderedCellContext(int aIndex, int aColumn, Object aKey) {
		super(aIndex, aColumn, aKey);
	}

	public PublishedStyle getStyle() {
		return style;
	}

	public void setStyle(PublishedStyle aStyle) {
		style = aStyle;
	}
}
