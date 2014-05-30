package com.eas.client.form.grid.selection;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager.EventTranslator;
import com.google.gwt.view.client.DefaultSelectionEventManager.SelectAction;

public class CheckBoxesEventTranslator<T> implements EventTranslator<T> {

	protected Column<T, ?> column;
	
	/**
	 * Construct a new {@link CheckBoxesEventTranslator} that will trigger
	 * selection when a checkbox in the specified column is selected.
	 */
	public CheckBoxesEventTranslator(Column<T, ?> aColumn) {
		super();
		column = aColumn;
	}

	public boolean clearCurrentSelection(CellPreviewEvent<T> event) {
		NativeEvent ne = event.getNativeEvent();
		boolean addToSelectionKey = ne.getCtrlKey() || ne.getMetaKey() || ne.getShiftKey(); 
		return !checkBoxClick(event) && !addToSelectionKey;
	}

	public SelectAction translateSelectionEvent(CellPreviewEvent<T> event) {
		return checkBoxClick(event) ? SelectAction.TOGGLE : SelectAction.DEFAULT;
	}

	protected boolean checkBoxClick(CellPreviewEvent<T> event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		if (BrowserEvents.CLICK.equals(nativeEvent.getType())) {
			if(event.getDisplay() instanceof CellTable<?>){
				CellTable<T> table = (CellTable<T>)event.getDisplay();
				if(table.getColumn(event.getColumn()) == column){
					return true;
				}
			}
		}
		return false;
	}
}
