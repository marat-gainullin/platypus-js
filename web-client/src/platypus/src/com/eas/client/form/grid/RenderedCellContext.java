package com.eas.client.form.grid;

import com.eas.client.form.published.PublishedCell;
import com.google.gwt.cell.client.Cell;

public class RenderedCellContext extends Cell.Context {

	protected PublishedCell cell;

	public RenderedCellContext(int aIndex, int aColumn, Object aKey) {
		super(aIndex, aColumn, aKey);
	}

	public PublishedCell getPublishedCell() {
		return cell;
	}

	public void setPublishedCell(PublishedCell aCell) {
		cell = aCell;
	}
}
