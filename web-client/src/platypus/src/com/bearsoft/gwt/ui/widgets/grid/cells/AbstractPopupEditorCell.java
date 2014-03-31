/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author mg
 * @param <C>
 */
public abstract class AbstractPopupEditorCell<C> extends AbstractCell<C> {

	protected Widget editor;
	protected HasValue<C> valueHost;
	protected HasBlurHandlers blurHost;
	protected Focusable focusHost;

	public AbstractPopupEditorCell(Widget aEditor, String... consumedEvents) {
		super(consumedEvents);
		editor = aEditor;
		if (editor instanceof HasValue<?>) {
			valueHost = (HasValue<C>) editor;
		} else {
			throw new IllegalArgumentException("aEditor must implement interface HasValue<C>");
		}
		if (editor instanceof HasBlurHandlers) {
			blurHost = (HasBlurHandlers) editor;
		}
		if (editor instanceof Focusable) {
			focusHost = (Focusable) editor;
		}
	}

	public AbstractPopupEditorCell(Widget aEditor, Set<String> consumedEvents) {
		super(consumedEvents);
		editor = aEditor;
		if (editor instanceof HasValue<?>) {
			valueHost = (HasValue<C>) editor;
		} else {
			throw new IllegalArgumentException("aEditor must implement interface HasValue<C>");
		}
		if (editor instanceof HasBlurHandlers) {
			blurHost = (HasBlurHandlers) editor;
		}
		if (editor instanceof Focusable) {
			focusHost = (Focusable) editor;
		}
	}

	public Widget getEditor() {
		return editor;
	}

	public void setEditor(Widget aEditor) {
		editor = aEditor;
	}

	protected static class UpdaterRef<C> {

		public ValueUpdater<C> valueUpdater;

		public UpdaterRef(ValueUpdater<C> aValueUpdater) {
			valueUpdater = aValueUpdater;
		}
	}

	public void startEditing(Element parent, final C value, ValueUpdater<C> valueUpdater) {
		final UpdaterRef<C> updaterRef = new UpdaterRef<>(valueUpdater);
		final PopupPanel pp = new PopupPanel(true);
		pp.getElement().setClassName("grid-cell-editor-popup");
		pp.setAnimationEnabled(true);
		valueHost.setValue(value);
		pp.setWidget(editor);
		final Element cellElement = parent.getParentElement();
		pp.setWidth(cellElement.getOffsetWidth() + "px");
		pp.setHeight(cellElement.getOffsetHeight() + "px");
		pp.setPopupPosition(cellElement.getAbsoluteLeft(), cellElement.getAbsoluteTop());
		final HandlerRegistration editorBlur = blurHost != null ? blurHost.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				pp.hide();
			}
		}) : null;
		final HandlerRegistration editorKeyUp = editor.addDomHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					pp.hide();
				} else if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
					updaterRef.valueUpdater = null;
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
					if (editorBlur != null) {
						editorBlur.removeHandler();
					}
					editorKeyUp.removeHandler();
					pp.setWidget(null);
					editor.removeFromParent();
				}
			}
		});
		pp.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				int dx = offsetWidth - cellElement.getOffsetWidth();
				int dy = offsetHeight - cellElement.getOffsetHeight();
				int w = cellElement.getOffsetWidth() - dx;
				int h = cellElement.getOffsetHeight() - dy;
				pp.setWidth((w >= 0 ? w : 0) + "px");
				pp.setHeight((h >= 0 ? h : 0) + "px");
				if (pp.isAnimationEnabled()) {
					Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
						@Override
						public void execute() {
							if (editor.isAttached() && focusHost != null) {
								focusHost.setFocus(true);
							}
						}
					});
				} else {
					if (editor.isAttached() && focusHost != null) {
						focusHost.setFocus(true);
					}
				}
			}
		});
	}
}
