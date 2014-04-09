/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid.cells;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public abstract class RenderedPopupEditorCell<T> extends AbstractPopupEditorCell<T> {

	protected CellRenderer<T> renderer;
	protected CellHasReadonly readonly;

	public RenderedPopupEditorCell(Widget aEditor) {
		super(aEditor, BrowserEvents.CLICK, BrowserEvents.DBLCLICK, BrowserEvents.KEYUP);
	}

	public CellRenderer<T> getRenderer() {
		return renderer;
	}

	public void setRenderer(CellRenderer<T> aRenderer) {
		renderer = aRenderer;
	}

	public CellHasReadonly getReadonly() {
		return readonly;
	}

	public void setReadonly(CellHasReadonly readonly) {
		this.readonly = readonly;
	}

	@Override
	public void render(Context context, T value, SafeHtmlBuilder sb) {
		if (renderer == null || !renderer.render(context, value, sb)) {
			renderCell(context, value, sb);
		}
	}

	public void startEditing(Element parent, T value, com.google.gwt.cell.client.ValueUpdater<T> valueUpdater) {
		if (readonly == null || !readonly.isReadonly())
			super.startEditing(parent, value, valueUpdater);
	}

	protected abstract void renderCell(Context context, T value, SafeHtmlBuilder sb);

	@Override
	public void onBrowserEvent(Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
		switch (event.getType()) {
		case BrowserEvents.DBLCLICK:
			startEditing(parent, value, valueUpdater);
			break;
		case BrowserEvents.KEYUP:
			if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
				startEditing(parent, value, valueUpdater);
			} else {
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
			}
			break;
		default:
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
			break;
		}
	}
}
