/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid.cells;

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
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author mg
 */
public abstract class RenderedPopupEditorCell<T> extends AbstractPopupEditorCell<T> {

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

	public RenderedPopupEditorCell(Widget aEditor) {
		super(aEditor, BrowserEvents.CLICK, /* BrowserEvents.DBLCLICK, */BrowserEvents.KEYDOWN, BrowserEvents.FOCUS, BrowserEvents.BLUR);
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

	public void setReadonly(CellHasReadonly readonly) {
		this.readonly = readonly;
	}

	@Override
	public void render(final Context context, final T value, SafeHtmlBuilder sb) {
		CellsResources.INSTANCE.tablecell().ensureInjected();
		if (isEditing(context, null, value)) {
			final ViewData<T> viewData = getViewData(context.getKey());
			sb.append(PaddedCell.INSTANCE.generate(viewData.id, CellsResources.INSTANCE.tablecell().padded(), new SafeStylesBuilder().padding(CELL_PADDING, Style.Unit.PX).toSafeStyles(),
			        SafeHtmlUtils.fromTrustedString("&nbsp;")));
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {

				@Override
				public void execute() {
					final Element parent = Document.get().getElementById(viewData.id);
					parent.blur();
					Element table = parent;
					while (table != null && !"table".equalsIgnoreCase(table.getTagName())) {
						table = table.getParentElement();
					}
					final Element table1 = table;
					if (parent != null && parent.getOwnerDocument() == Document.get()) {
						startEditing(context, parent, value, viewData.updater, new Runnable() {

							public void run() {
								if (onEditorClose != null && table1 != null) {
									onEditorClose.closed(table1);
								}
							}

						});
					}
				}

			});
		} else {
			if (renderer == null || !renderer.render(context, value, sb)) {
				SafeHtmlBuilder content = new SafeHtmlBuilder();
				renderCell(context, value, content);
				sb.append(PaddedCell.INSTANCE.generate("", CellsResources.INSTANCE.tablecell().padded(), new SafeStylesBuilder().padding(CELL_PADDING, Style.Unit.PX).toSafeStyles(),
				        content.toSafeHtml()));
			}
		}
	}

	protected abstract void renderCell(Context context, T value, SafeHtmlBuilder sb);

	public void onBrowserEvent(Cell.Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
		if (readonly == null || !readonly.isReadonly()) {
			Object key = context.getKey();
			ViewData<T> viewData = getViewData(key);
			if (viewData == null) {
				String type = event.getType();
				int keyCode = event.getKeyCode();
				boolean editToggleKeys = BrowserEvents.KEYDOWN.equals(type) && (keyCode == KeyCodes.KEY_ENTER || keyCode == KeyCodes.KEY_F2);
				if (BrowserEvents.CLICK.equals(type) || editToggleKeys) {
					// Switch to edit mode.
					viewData = new ViewData<>(Document.get().createUniqueId(), valueUpdater);
					setViewData(key, viewData);
					setValue(context, parent, value);
				}
			}
		}
	}

}
