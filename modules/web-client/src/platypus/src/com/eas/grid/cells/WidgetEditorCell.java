/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid.cells;

import java.util.Set;

import com.eas.grid.Grid;
import com.eas.ui.CommonResources;
import com.eas.widgets.boxes.AutoCloseBox;
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
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 * @param <C>
 */
public abstract class WidgetEditorCell<C> extends AbstractEditableCell<C, WidgetEditorCell.ViewData<C>> {

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

	public WidgetEditorCell(Widget aEditor, String... consumedEvents) {
		super(consumedEvents);
		setEditor(aEditor);
	}

	public WidgetEditorCell(Widget aEditor, Set<String> consumedEvents) {
		super(consumedEvents);
		setEditor(aEditor);
	}

	public Widget getEditor() {
		return editor;
	}

	public void setEditor(Widget aEditor) {
		editor = aEditor;
		if (editor == null || ((editor instanceof HasValue<?>) && (editor instanceof HasText))) {
			valueHost = (HasValue<C>) editor;
		} else {
			throw new IllegalArgumentException("Editor must implement interfaces HasValue<?> and HasText");
		}
		if (editor instanceof Focusable) {
			focusHost = (Focusable) editor;
			focusHost.setTabIndex(1);
		}
		if (editor != null){
			CommonResources.INSTANCE.commons().ensureInjected();
			editor.getElement().addClassName(CommonResources.INSTANCE.commons().borderSized());
		}
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

	public void startEditing(final Cell.Context context, final Element aBoxPositionTemplate, final Element aBoxParent, final C value, ValueUpdater<C> valueUpdater, final Runnable onEditorClose) {
		Widget oldParent = editor.getParent();
		if (oldParent == null) {
			final UpdaterRef<C> updaterRef = new UpdaterRef<>(valueUpdater);
			final AutoCloseBox editorBox = new AutoCloseBox() {

				protected void superClose() {
					super.close();
				}

				@Override
				public void close() {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {

						@Override
						public void execute() {
							superClose();
						}
					});
				}

			};
			valueHost.setValue(value);
			editorBox.setWidget(editor);
			final HandlerRegistration valueChangeReg = valueHost.addValueChangeHandler(new ValueChangeHandler<C>() {

				@Override
				public void onValueChange(ValueChangeEvent<C> event) {
					if (updaterRef.valueUpdater != null) {
						updaterRef.valueUpdater.update(valueHost.getValue());
					}
				}

			});
			editorBox.getElement().setClassName("grid-cell-editor-popup");
			editorBox.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
			editorBox.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			editorBox.getElement().getStyle().setWidth(aBoxPositionTemplate.getClientWidth(), Style.Unit.PX);
			editorBox.getElement().getStyle().setHeight(aBoxPositionTemplate.getClientHeight(), Style.Unit.PX);
			editorBox.getElement().getStyle().setLeft(aBoxPositionTemplate.getAbsoluteLeft() - aBoxParent.getAbsoluteLeft(), Style.Unit.PX);
			editorBox.getElement().getStyle().setTop(aBoxPositionTemplate.getAbsoluteTop() - aBoxParent.getAbsoluteTop(), Style.Unit.PX);
			final HandlerRegistration editorKeyDownReg = editor.addDomHandler(new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
						updaterRef.valueUpdater = null;
						valueHost.setValue(null);
						editorBox.close();
					} else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {

							@Override
							public void execute() {
								updaterRef.valueUpdater = null;
								valueHost.setValue(null);
								editorBox.close();
							}
						});
					}
				}
			}, KeyDownEvent.getType());
			editorBox.addCloseHandler(new CloseHandler<AutoCloseBox>() {
				@Override
				public void onClose(CloseEvent<AutoCloseBox> event) {
					valueChangeReg.removeHandler();
					editorKeyDownReg.removeHandler();
					editorBox.setWidget(null);
					editor.removeFromParent();
					setViewData(context.getKey(), null);
					if (onEditorClose != null)
						onEditorClose.run();
				}
			});
			editorBox.show(aBoxParent);
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					if (editor.isAttached() && focusHost != null) {
						focusHost.setFocus(true);
					}
				}

			});
		}
	}
}
