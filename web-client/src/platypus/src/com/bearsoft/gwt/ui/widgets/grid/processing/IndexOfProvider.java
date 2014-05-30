package com.bearsoft.gwt.ui.widgets.grid.processing;

public interface IndexOfProvider<T> {

	public int indexOf(T aItem);
	
	public void rescan();
}
