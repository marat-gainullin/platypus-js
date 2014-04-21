/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid.cells;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 * @param <C>
 */
public abstract class AbstractPopupEditorCell<C> extends AbstractEditableCell<C, AbstractPopupEditorCell.ViewData<C>> {

	protected static class ViewData<T> {
		String id;
		ValueUpdater<T> updater;
		
		public ViewData(String aId, ValueUpdater<T> aUpdater){
			id = aId;
			updater = aUpdater;
		}
	}
	
	protected Widget editor;
	protected HasValue<C> valueHost;
	protected Focusable focusHost;

	public AbstractPopupEditorCell(Widget aEditor, String... consumedEvents) {
		super(consumedEvents);
		setEditor(aEditor);
	}

	public AbstractPopupEditorCell(Widget aEditor, Set<String> consumedEvents) {
		super(consumedEvents);
		setEditor(aEditor);
	}

	public Widget getEditor() {
		return editor;
	}

	public void setEditor(Widget aEditor) {
		editor = aEditor;
		if (editor instanceof HasValue<?>) {
			valueHost = (HasValue<C>) editor;
		} else {
			throw new IllegalArgumentException("Editor must implement interface HasValue<C>");
		}
		if (editor instanceof Focusable) {
			focusHost = (Focusable) editor;
			focusHost.setTabIndex(1);
		}
		editor.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
	}

	protected static class UpdaterRef<C> {

		public ValueUpdater<C> valueUpdater;

		public UpdaterRef(ValueUpdater<C> aValueUpdater) {
			valueUpdater = aValueUpdater;
		}
	}

	public boolean isEditing(Cell.Context context, Element parent, C value) {
		ViewData<C> viewId = getViewData(context.getKey());
		return viewId != null;
	}

	public void startEditing(final Cell.Context context, final Element parent, final C value, ValueUpdater<C> valueUpdater, final Runnable onEditorClose) {
		final UpdaterRef<C> updaterRef = new UpdaterRef<>(valueUpdater);
		final PopupPanel pp = new PopupPanel(true);
		pp.getElement().setClassName("grid-cell-editor-popup");
		pp.setAnimationEnabled(true);
		valueHost.setValue(value);
		pp.setWidget(editor);
		final Element cellElement = parent;
		pp.setWidth(cellElement.getClientWidth() + "px");
		pp.setHeight(cellElement.getClientHeight() + "px");
		pp.setPopupPosition(cellElement.getAbsoluteLeft(), cellElement.getAbsoluteTop());
		final HandlerRegistration editorKeyUp = editor.addDomHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				boolean enter = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER;
				boolean escape = event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE;
				if (enter || escape) {
					if (escape) {
						updaterRef.valueUpdater = null;
					}
					pp.hide();
				}
			}
		}, KeyUpEvent.getType());
		pp.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				try {
					if (updaterRef.valueUpdater != null) {
						updaterRef.valueUpdater.update(valueHost.getValue());
					}
				} catch (Exception ex) {
					Logger.getLogger(AbstractPopupEditorCell.class.getName()).log(Level.SEVERE, null, ex);
				} finally {
					editorKeyUp.removeHandler();
					pp.setWidget(null);
					editor.removeFromParent();
					setViewData(context.getKey(), null);
					if (updaterRef.valueUpdater == null) {
						setValue(context, parent, value);
					}
					if (onEditorClose != null)
						onEditorClose.run();
				}
			}
		});
		pp.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				if (editor instanceof RequiresResize)
					((RequiresResize) editor).onResize();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					@Override
					public void execute() {
						if (editor.isAttached() && focusHost != null) {
							focusHost.setFocus(true);
						}
					}

				});
			}
		});
	}
}
