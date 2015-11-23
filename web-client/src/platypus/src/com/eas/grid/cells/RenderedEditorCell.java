/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid.cells;

import com.eas.grid.RenderedCellContext;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public class RenderedEditorCell<T> extends WidgetEditorCell<T> {

	public interface PaddedCell extends SafeHtmlTemplates {

		public static PaddedCell INSTANCE = GWT.create(PaddedCell.class);

		@Template("<div class=\"grid-cell-anchor\"></div><img class=\"grid-cell-image\" style=\"{0}\" src=\"{1}\"></img><div id=\"{2}\" class=\"grid-cell-text\" style=\"{0}\">{3}</div>")
		public SafeHtml generate(SafeStyles aStyle, SafeUri aImgSrc, String aId, SafeHtml aContent);
		
		@Template("<div class=\"grid-cell-anchor\"></div><img class=\"grid-cell-image\" style=\"{0}\"></img><div id=\"{1}\" class=\"grid-cell-text\" style=\"{0}\">{2}</div>")
		public SafeHtml generate(SafeStyles aStyle, String aId, SafeHtml aContent);
	}

	public interface EditorCloser {

		public void closed(Element aTable);

	}

	protected CellRenderer<T> renderer;
	protected CellHasReadonly readonly;
	protected EditorCloser onEditorClose;

	public RenderedEditorCell(Widget aEditor) {
		super(aEditor, BrowserEvents.DBLCLICK, BrowserEvents.KEYDOWN, BrowserEvents.FOCUS, BrowserEvents.BLUR);
	}

	public EditorCloser getOnEditorClose() {
		return onEditorClose;
	}

	public void setOnEditorClose(EditorCloser aValue) {
		onEditorClose = aValue;
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

	public void setReadonly(CellHasReadonly aValue) {
		readonly = aValue;
	}

	@Override
	public void render(final Context context, final T value, SafeHtmlBuilder sb) {
		String viewDataId = "";
		if (isEditing(context, null, value)) {
			final ViewData<T> viewData = getViewData(context.getKey());
			viewDataId = viewData.id;
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					if (isEditing(context, null, value)) {
						Element identifiedCellTextSection = Document.get().getElementById(viewData.id);
						if (identifiedCellTextSection != null) {
							identifiedCellTextSection.blur();
							Element table = identifiedCellTextSection;
							while (table != null && !"table".equalsIgnoreCase(table.getTagName())) {
								table = table.getParentElement();
							}
							final Element table1 = table;
							if (identifiedCellTextSection.getOwnerDocument() == Document.get()) {
								startEditing(context, identifiedCellTextSection.getParentElement(), table1.getParentElement(), value, viewData.updater, new Runnable() {

									public void run() {
										if (onEditorClose != null && table1 != null) {
											onEditorClose.closed(table1);
										}
									}

								});
							}
						}
					}
				}

			});
		}
		if (renderer == null || !renderer.render(context, viewDataId, value, sb)) {
			SafeHtmlBuilder content = new SafeHtmlBuilder();
			renderCell(context, value, content);
			sb.append(PaddedCell.INSTANCE.generate(new SafeStylesBuilder().toSafeStyles(), viewDataId, content.toSafeHtml()));
		}
	}

	protected void renderCell(Context context, T value, SafeHtmlBuilder sb) {
		if (editor != null) {
			String display = null;
			T oldValue = ((HasValue<T>) editor).getValue();
			((HasValue<T>) editor).setValue(value);
			try {
				display = ((HasText) editor).getText();
			} finally {
				((HasValue<T>) editor).setValue(oldValue);
			}
			sb.appendEscaped(display != null ? display : "");
		} else {
			sb.appendEscaped(value != null ? value.toString() : "");
		}
	}

	public void onBrowserEvent(Cell.Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
		if (readonly == null || !readonly.isReadonly()) {
			if (!isEditing(context, parent, value)) {
				String type = event.getType();
				int keyCode = event.getKeyCode();
				boolean editToggleKeys = BrowserEvents.KEYDOWN.equals(type) && (keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_F2);
				if (BrowserEvents.DBLCLICK.equals(type) || editToggleKeys) {
					// Switch to edit mode.
					ViewData<T> viewData = new ViewData<>(Document.get().createUniqueId(), valueUpdater);
					setViewData(context.getKey(), viewData);
					setValue(new RenderedCellContext(context.getIndex(), context.getColumn(), context.getKey()), parent, value);
				}
			}
		}
	}

}
