package com.eas.grid;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

public class GridSelectionEventManager<T> extends DefaultSelectionEventManager<T> {

	private EventTranslator<T> translator;

	protected GridSelectionEventManager() {
		this(null);
	}

	protected GridSelectionEventManager(EventTranslator<T> aTranslator) {
		super(aTranslator);
		translator = aTranslator;
	}

	/**
	 * Handle an event that could cause a value to be selected for a
	 * {@link MultiSelectionModel}. This overloaded method adds support for both
	 * the control and shift keys. If the shift key is held down, all rows
	 * between the previous selected row and the current row are selected.
	 * 
	 * @param event
	 *            the {@link CellPreviewEvent} that triggered selection
	 * @param action
	 *            the action to handle
	 * @param selectionModel
	 *            the {@link SelectionModel} to update
	 */            
	protected void handleMultiSelectionEvent(CellPreviewEvent<T> event, SelectAction action, MultiSelectionModel<? super T> selectionModel) {
		NativeEvent nativeEvent = event.getNativeEvent();
		String type = nativeEvent.getType();
		if (BrowserEvents.CLICK.equals(type)) {
			
//			  Update selection on click. Selection is toggled only if the user
//			  presses the ctrl key. If the user does not press the control key,
//			  selection is additive.
			 
			boolean shift = nativeEvent.getShiftKey();
			boolean ctrlOrMeta = nativeEvent.getCtrlKey() || nativeEvent.getMetaKey();
			boolean clearOthers = (translator == null) ? !ctrlOrMeta : translator.clearCurrentSelection(event);
			if (action == null || action == SelectAction.DEFAULT) {
				action = ctrlOrMeta ? SelectAction.TOGGLE : SelectAction.SELECT;
			}
			doMultiSelection(selectionModel, event.getDisplay(), event.getIndex(), event.getValue(), action, shift, clearOthers);
		} else if (BrowserEvents.KEYDOWN.equals(type)) {
			boolean shift = nativeEvent.getShiftKey();
			boolean ctrlOrMeta = nativeEvent.getCtrlKey() || nativeEvent.getMetaKey();
			int keyCode = nativeEvent.getKeyCode();
			if (keyCode == KeyCodes.KEY_SPACE) {				
//				  Update selection when the space bar is pressed. The spacebar
//				  always toggles selection, regardless of whether the control
//				  key is pressed.				 
				boolean clearOthers = (translator == null) ? false : translator.clearCurrentSelection(event);
				if (action == null || action == SelectAction.DEFAULT) {
					action = SelectAction.TOGGLE;
				}
				doMultiSelection(selectionModel, event.getDisplay(), event.getIndex(), event.getValue(), action, shift, clearOthers);
			} else if (keyCode == KeyCodes.KEY_UP) {				
//				  Update selection when the up key is pressed. 				 
				boolean clearOthers = (translator == null) ? !ctrlOrMeta : translator.clearCurrentSelection(event);
				if (action == null || action == SelectAction.DEFAULT) {
					action = ctrlOrMeta ? SelectAction.TOGGLE : SelectAction.SELECT;
				}
				int idxToSelect = event.getIndex() - 1;
				if (idxToSelect >= 0 && idxToSelect < event.getDisplay().getRowCount()) {
					int visibleIdxToSelect = idxToSelect - event.getDisplay().getVisibleRange().getStart();
					if(visibleIdxToSelect >=0 && visibleIdxToSelect < event.getDisplay().getVisibleRange().getLength()){
						T itemToSelect = event.getDisplay().getVisibleItem(visibleIdxToSelect);
						doMultiSelection(selectionModel, event.getDisplay(), idxToSelect, itemToSelect, action, shift, clearOthers);
					}
				}
			} else if (keyCode == KeyCodes.KEY_DOWN) {				
//				 Update selection when the down key is pressed.				 
				boolean clearOthers = (translator == null) ? !ctrlOrMeta : translator.clearCurrentSelection(event);
				if (action == null || action == SelectAction.DEFAULT) {
					action = ctrlOrMeta ? SelectAction.TOGGLE : SelectAction.SELECT;
				}
				int idxToSelect = event.getIndex() + 1;
				if (idxToSelect >= 0 && idxToSelect < event.getDisplay().getRowCount()) {
					int visibleIdxToSelect = idxToSelect - event.getDisplay().getVisibleRange().getStart();
					if(visibleIdxToSelect >= 0 && visibleIdxToSelect < event.getDisplay().getVisibleRange().getLength()){
						T itemToSelect = event.getDisplay().getVisibleItem(visibleIdxToSelect);
						doMultiSelection(selectionModel, event.getDisplay(), idxToSelect, itemToSelect, action, shift, clearOthers);
					}
				}
			}else
				super.handleMultiSelectionEvent(event, action, selectionModel);
		}
	}

	public static <T> GridSelectionEventManager<T> create(EventTranslator<T> translator) {
		return new GridSelectionEventManager<>(translator);
	}

	public static <T> GridSelectionEventManager<T> create() {
		return new GridSelectionEventManager<>();
	}
}
