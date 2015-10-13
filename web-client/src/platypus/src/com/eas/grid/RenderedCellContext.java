package com.eas.grid;

import com.eas.ui.PublishedCell;
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
