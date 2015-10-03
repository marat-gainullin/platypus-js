package com.eas.grid.selection;

import com.eas.grid.columns.CheckServiceColumn;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager.EventTranslator;
import com.google.gwt.view.client.DefaultSelectionEventManager.SelectAction;

public class CheckBoxesEventTranslator<T> implements EventTranslator<T> {

	public CheckBoxesEventTranslator() {
		super();
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
				if(table.getColumn(event.getColumn()) instanceof CheckServiceColumn){
					return true;
				}
			}
		}
		return false;
	}
}
