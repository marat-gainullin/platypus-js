/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid.cells;

import com.eas.client.form.grid.RenderedCellContext;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public abstract class RenderedEditorCell<T> extends WidgetEditorCell<T> {

	public interface CellsResources extends ClientBundle {

		public static CellsResources INSTANCE = GWT.create(CellsResources.class);

		public interface CellStyles extends CssResource {

			public String padded();

		}

		public CellStyles tablecell();
	}

	public static int CELL_PADDING = 2;

	public interface PaddedCell extends SafeHtmlTemplates {

		public static PaddedCell INSTANCE = GWT.create(PaddedCell.class);

		@Template("<div id=\"{0}\" class=\"{1}\" style=\"{2}\">{3}</div>")
		public SafeHtml generate(String aId, String aCellClass, SafeStyles aStyle, SafeHtml aContent);
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
		CellsResources.INSTANCE.tablecell().ensureInjected();
		String viewDataId = "";
		if (isEditing(context, null, value)) {
			final ViewData<T> viewData = getViewData(context.getKey());
			viewDataId = viewData.id;
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					if (isEditing(context, null, value)) {
						Element parent = Document.get().getElementById(viewData.id);
						if (parent != null) {
							parent.blur();
							Element table = parent;
							while (table != null && !"table".equalsIgnoreCase(table.getTagName())) {
								table = table.getParentElement();
							}
							final Element table1 = table;
							if (parent.getOwnerDocument() == Document.get()) {
								startEditing(context, parent, table1.getParentElement(), value, viewData.updater, new Runnable() {

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
			sb.append(PaddedCell.INSTANCE.generate(viewDataId, CellsResources.INSTANCE.tablecell().padded(), new SafeStylesBuilder().padding(CELL_PADDING, Style.Unit.PX).toSafeStyles(),
			        content.toSafeHtml()));
		}
	}

	protected abstract void renderCell(Context context, T value, SafeHtmlBuilder sb);

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
