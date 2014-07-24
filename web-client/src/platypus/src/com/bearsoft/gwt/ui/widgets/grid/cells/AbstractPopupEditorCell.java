/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid.cells;

import java.util.Set;

import com.bearsoft.gwt.ui.widgets.grid.Grid;
import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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

		public ViewData(String aId, ValueUpdater<T> aUpdater) {
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
		Widget oldParent = editor.getParent();
		if (oldParent == null) {
			final UpdaterRef<C> updaterRef = new UpdaterRef<>(valueUpdater);
			final PopupPanel pp = new PopupPanel(true) {

				protected void autoHide() {
					super.hide(true);
				}

				@Override
				public void hide(boolean autoClosed) {
					if (autoClosed) {
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {
							@Override
							public void execute() {
								autoHide();
							}
						});
					} else {
						super.hide(autoClosed);
					}
				}

			};
			pp.getElement().setClassName("grid-cell-editor-popup");
			pp.getElement().getStyle().setPaddingLeft(Grid.LEFT_RIGHT_CELL_PADDING, Style.Unit.PX);
			pp.getElement().getStyle().setPaddingRight(Grid.LEFT_RIGHT_CELL_PADDING, Style.Unit.PX);
			pp.getElement().getStyle().setFontSize(0, Style.Unit.PT);
			//pp.setAnimationEnabled(true);
			valueHost.setValue(value);
			pp.setWidget(editor);
			final HandlerRegistration valueChangeReg = valueHost.addValueChangeHandler(new ValueChangeHandler<C>() {

				@Override
				public void onValueChange(ValueChangeEvent<C> event) {
					if (updaterRef.valueUpdater != null) {
						updaterRef.valueUpdater.update(valueHost.getValue());
					}
				}

			});
			final Element cellElement = parent;
			pp.setWidth(cellElement.getClientWidth() + "px");
			pp.setHeight(cellElement.getClientHeight() + "px");
			pp.setPopupPosition(cellElement.getAbsoluteLeft() - Grid.LEFT_RIGHT_CELL_PADDING, cellElement.getAbsoluteTop());
			final HandlerRegistration editorKeyDownReg = editor.addDomHandler(new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
						updaterRef.valueUpdater = null;
						valueHost.setValue(null);
						pp.hide();
					} else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {

							@Override
							public void execute() {
								updaterRef.valueUpdater = null;
								valueHost.setValue(null);
								pp.hide();
							}
						});
					}
				}
			}, KeyDownEvent.getType());
			pp.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					valueChangeReg.removeHandler();
					editorKeyDownReg.removeHandler();
					pp.setWidget(null);
					editor.removeFromParent();
					setViewData(context.getKey(), null);
					if (onEditorClose != null)
						onEditorClose.run();
				}
			});
			pp.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					if (editor instanceof RequiresResize) {
						((RequiresResize) editor).onResize();
					}
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
}
