/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid;

import com.eas.grid.columns.header.HeaderNode;
import com.eas.ui.PublishedColor;
import com.eas.ui.PublishedFont;
import com.eas.ui.XDataTransfer;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;

public class DraggableHeader<T> extends Header<String> implements HasColumn<T> {

	protected String title;
	protected PublishedColor background;
	protected PublishedColor foreground;
	protected PublishedFont font;
	protected GridSection<T> table;
	protected Element hostElement;
	protected Column<T, ?> column;
	protected HeaderNode<T> headerNode;
	protected boolean moveable = true;
	protected boolean resizable = true;

	public DraggableHeader(String aTitle, GridSection<T> aTable, Column<T, ?> aColumn, HeaderNode<T> aHeaderNode) {
		this(aTitle, aTable, aColumn, aTable != null ? aTable.getElement() : null, aHeaderNode);
	}

	protected DraggableHeader(String aTitle, GridSection<T> aTable, Column<T, ?> aColumn, Element aHostElement, HeaderNode<T> aHeaderNode) {
		super(new HeaderCell());
		if (aTitle == null || aColumn == null) {
			throw new NullPointerException();
		}
		title = aTitle;
		column = aColumn;
		table = aTable;
		hostElement = aHostElement;
		headerNode = aHeaderNode;
	}

	@Override
	public Column<T, ?> getColumn() {
		return column;
	}

	public HeaderNode<T> getHeaderNode() {
		return headerNode;
	}

	public AbstractCellTable<T> getTable() {
		return table;
	}

	public void setTable(GridSection<T> aValue) {
		table = aValue;
		hostElement = table != null ? table.getElement() : null;
	}

	@Override
	public String getValue() {
		return title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String aValue) {
		title = aValue;
	}

	public PublishedColor getBackground() {
		return background;
	}

	public void setBackground(PublishedColor aValue) {
		background = aValue;
	}

	public PublishedColor getForeground() {
		return foreground;
	}

	public void setForeground(PublishedColor aValue) {
		foreground = aValue;
	}

	public PublishedFont getFont() {
		return font;
	}

	public void setFont(PublishedFont aValue) {
		font = aValue;
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean aValue) {
		resizable = aValue;
	}

	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean aValue) {
		moveable = aValue;
	}

	@Override
	public void onBrowserEvent(Context context, Element targetCell, NativeEvent event) {
		if (BrowserEvents.DRAGSTART.equals(event.getType())) {
			event.stopPropagation();
			EventTarget et = event.getEventTarget();
			if (Element.is(et)) {
				DraggedColumn<?> col = new DraggedColumn<>(column, this, table, targetCell, Element.as(et));
				if ((col.isMove() && moveable) || (col.isResize() && resizable)) {
					event.getDataTransfer().<XDataTransfer> cast().setEffectAllowed("move");
					DraggedColumn.instance = col;
					event.getDataTransfer().setData(
					        "Text",
					        (table != null ? "grid-section-" + table.hashCode() : "not leaf") + "; column-" + (DraggedColumn.instance.isMove() ? "moved" : "resized")
					                + (table != null ? ":" + table.getColumnIndex(column) : ""));
				} else {
					event.getDataTransfer().<XDataTransfer> cast().setEffectAllowed("none");
				}
			}
		}
		super.onBrowserEvent(context, targetCell, event);
	}

	public interface GridResources extends ClientBundle {

		static final GridResources instance = GWT.create(GridResources.class);

		public GridStyles header();

	}

	public interface GridStyles extends CssResource {

		public String gridHeaderMover();

		public String gridHeaderResizer();

	}

	public static GridStyles headerStyles = GridResources.instance.header();

	private static class HeaderCell extends AbstractCell<String> {

		public HeaderCell() {
			super(BrowserEvents.DRAGSTART);
		}

		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb) {
			headerStyles.ensureInjected();// ondragenter=\"event.preventDefault();\"
			                              // ondragover=\"event.preventDefault();\"
			                              // ondrop=\"event.preventDefault();\"
			sb.append(SafeHtmlUtils.fromTrustedString("<div class=\"grid-column-header-content\"; style=\"position:relative;\">"))
			        .append(value.startsWith("<html>") ? SafeHtmlUtils.fromTrustedString(value.substring(6)) : SafeHtmlUtils.fromString(value)).append(SafeHtmlUtils.fromTrustedString("</div>"))
			        .append(SafeHtmlUtils.fromTrustedString("<span draggable=\"true\" class=\"" + headerStyles.gridHeaderMover() + " grid-header-mover\"></span>"))
			        .append(SafeHtmlUtils.fromTrustedString("<span draggable=\"true\" class=\"" + headerStyles.gridHeaderResizer() + " grid-header-resizer\"></span>"));
		}

	}
}
