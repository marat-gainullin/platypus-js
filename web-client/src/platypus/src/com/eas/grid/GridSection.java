/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableColElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Event;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

/**
 * 
 * @author mg
 * @param <T>
 */
public class GridSection<T> extends CellTable<T> {

	protected static final String HIDDEN_COLUMN_WIDTH = "0.00000000000000000001px";

	protected AbstractCellTable<T>[] columnsPartners;
	protected WidthCallback widthPropagator;
	protected ColumnsRemover columnsRemover;
	protected AbstractCellTable<T> headerSource;
	protected AbstractCellTable<T> footerSource;
	protected boolean draggableRows;

	public GridSection(ProvidesKey<T> keyProvider) {
		super(15, ThemedGridResources.instance, keyProvider, null, true, false);
		setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.CURRENT_PAGE);
		setLoadingIndicator(null);
		setEmptyTableWidget(null);
		getElement().getStyle().setProperty("borderCollapse", "collapse");
		setKeyboardSelectionHandler(new CellTableKeyboardSelectionHandler<T>(this) {

			@Override
			public void onCellPreview(CellPreviewEvent<T> event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				String eventType = event.getNativeEvent().getType();
				if (BrowserEvents.KEYDOWN.equals(eventType) && !event.isCellEditing()) {
					/*
					 * Handle keyboard navigation, unless the cell is being
					 * edited. If the cell is being edited, we do not want to
					 * change rows.
					 * 
					 * Prevent default on navigation events to prevent default
					 * scrollbar behavior.
					 */
					int oldRow = GridSection.this.getKeyboardSelectedRow();
					int oldColumn = GridSection.this.getKeyboardSelectedColumn();
					boolean isRtl = LocaleInfo.getCurrentLocale().isRTL();
					int keyCodeLineEnd = isRtl ? KeyCodes.KEY_LEFT : KeyCodes.KEY_RIGHT;
					int keyCodeLineStart = isRtl ? KeyCodes.KEY_RIGHT : KeyCodes.KEY_LEFT;
					int keyCode = nativeEvent.getKeyCode();
					super.onCellPreview(event);
					if (keyCode == keyCodeLineEnd) {
						GridSection.this.setKeyboardSelectedRow(oldRow);
						if (GridSection.this.getKeyboardSelectedColumn() < oldColumn)
							GridSection.this.setKeyboardSelectedColumn(oldColumn);
					} else if (keyCode == keyCodeLineStart) {
						GridSection.this.setKeyboardSelectedRow(oldRow);
						if (GridSection.this.getKeyboardSelectedColumn() > oldColumn)
							GridSection.this.setKeyboardSelectedColumn(oldColumn);
					}
				} else
					super.onCellPreview(event);
			}
		});
	}

	public boolean isDraggableRows() {
		return draggableRows;
	}

	public void setDraggableRows(boolean aValue) {
		if (draggableRows != aValue) {
			draggableRows = aValue;
			if (isAttached())
				redraw();
		}
	}

	protected boolean ctrlKey;

	@Override
	protected void onBrowserEvent2(Event event) {
		ctrlKey = event.getCtrlKey();
		super.onBrowserEvent2(event);
	}

	public boolean isCtrlKey() {
		return ctrlKey;
	}

	public AbstractCellTable<T>[] getColumnsPartners() {
		return columnsPartners;
	}

	@Override
	public void setKeyboardSelectedRow(int row, int subrow, boolean stealFocus) {
		onBlur();// When selecting cells with shift key, focused cell is not
		         // cleared without such hack.
		super.setKeyboardSelectedRow(row, subrow, stealFocus);
	}

	@Override
	public void setKeyboardSelectedColumn(int column, boolean stealFocus) {
		onBlur();// When selecting cells with shift key, focused cell is not
		         // cleared without such hack.
		super.setKeyboardSelectedColumn(column, stealFocus);
	}

	public void setColumnsPartners(AbstractCellTable<T>[] aPartners) {
		columnsPartners = aPartners;
	}

	public WidthCallback getWidthPropagator() {
		return widthPropagator;
	}

	public void setWidthPropagator(WidthCallback aValue) {
		widthPropagator = aValue;
	}

	@Override
	public Header<?> getHeader(int index) {
		return headerSource != null ? headerSource.getHeader(index) : super.getHeader(index);
	}

	@Override
	public Header<?> getFooter(int index) {
		return footerSource != null ? footerSource.getFooter(index) : super.getFooter(index);
	}

	public ColumnsRemover getColumnsRemover() {
		return columnsRemover;
	}

	public void setColumnsRemover(ColumnsRemover aValue) {
		columnsRemover = aValue;
	}

	@Override
	public void removeColumn(int index) {
		if (columnsRemover != null) {
			columnsRemover.removeColumn(index);
		} else {
			Column<T, ?> col = getColumn(index);
			hiddenColumns.remove(col);
			super.removeColumn(index);
			NodeList<Element> colGroups = getElement().getElementsByTagName("colgroup");
			if (colGroups != null && colGroups.getLength() == 1) {
				TableColElement colGroup = colGroups.getItem(0).cast();
				if (getColumnCount() < colGroup.getChildCount()) {
					// It seems, that GWT's bug is still here.
					if (index >= 0 && index < colGroup.getChildCount()) {
						colGroup.removeChild(colGroup.getChild(index));
					}
				}
			}
		}
	}

	public AbstractCellTable<T> getHeaderSource() {
		return headerSource;
	}

	public void setHeaderSource(AbstractCellTable<T> aValue) {
		headerSource = aValue;
	}

	public AbstractCellTable<T> getFooterSource() {
		return footerSource;
	}

	public void setFooterSource(AbstractCellTable<T> aValue) {
		footerSource = aValue;
	}

	protected Map<Column<T, ?>, String> hiddenColumns = new HashMap<>();

	public boolean isColumnHidden(Column<T, ?> aColumn) {
		return hiddenColumns.containsKey(aColumn);
	}

	public boolean hideColumn(Column<T, ?> aColumn) {
		if (!isColumnHidden(aColumn)) {
			hiddenColumns.put(aColumn, getColumnWidth(aColumn));
			super.setColumnWidth(aColumn, HIDDEN_COLUMN_WIDTH);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setColumnWidth(Column<T, ?> aColumn, String width) {
		if (isColumnHidden(aColumn)) {
			super.setColumnWidth(aColumn, HIDDEN_COLUMN_WIDTH);
		} else {
			super.setColumnWidth(aColumn, width);
		}
	}

	public boolean showColumn(Column<T, ?> aColumn) {
		if (isColumnHidden(aColumn)) {
			String wasWidth = hiddenColumns.remove(aColumn);
			super.setColumnWidth(aColumn, wasWidth);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void doSetColumnWidth(int index, String aValue) {
		if (index >= 0 && index < getColumnCount()) {
			super.doSetColumnWidth(index, aValue);
			if (columnsPartners != null) {
				for (AbstractCellTable<T> partner : columnsPartners) {
					if (index >= 0 && index < partner.getColumnCount()) {
						Column<T, ?> col = partner.getColumn(index);
						if (aValue == null) {
							partner.clearColumnWidth(col);
						} else {
							partner.setColumnWidth(col, aValue);
						}
					}
				}
			}
			widthChanged();
		}
	}

	protected void widthChanged() {
		if (widthPropagator != null) {
			widthPropagator.changed();
		}
	}

	@Override
	public TableSectionElement getTableHeadElement() {
		return super.getTableHeadElement();
	}

	public TableCellElement getCell(int aRow, int aCol) {
		NodeList<TableRowElement> rows = getTableBodyElement().getRows();
		if (aRow >= 0 && aRow < rows.getLength()) {
			TableRowElement row = rows.getItem(aRow);
			NodeList<TableCellElement> cells = row.getCells();
			if (aCol >= 0 && aCol < cells.getLength()) {
				return cells.getItem(aCol);
			}
		}
		return null;
	}

	@Override
	public Element getKeyboardSelectedElement() {
		return super.getKeyboardSelectedElement();
	}

	public void focusCell(int aRow, int aCol) {
		setKeyboardSelectedColumn(aCol);
		setKeyboardSelectedRow(aRow);
		setFocus(true);
	}

	public <C> void redrawAllRowsInColumn(int aIndex, ListDataProvider<T> aDataProvider) {
		if (aIndex >= 0 && aIndex < getColumnCount()) {
			int start = getVisibleRange().getStart();
			Column<T, C> column = (Column<T, C>) getColumn(aIndex);
			Cell<C> cell = column.getCell();
			List<T> data = aDataProvider.getList();
			ProvidesKey<T> keys = getKeyProvider();
			NodeList<TableRowElement> rows = getTableBodyElement().getRows();
			for (int i = 0; i < rows.getLength(); i++) {
				TableRowElement row = rows.getItem(i);
				NodeList<TableCellElement> cells = row.getCells();
				if (aIndex >= 0 && aIndex < cells.getLength()) {
					TableCellElement toRerender = cells.getItem(aIndex);
					if (toRerender != null) {
						SafeHtmlBuilder sb = new SafeHtmlBuilder();
						int dataIdx = start + i;
						if (dataIdx >= 0 && dataIdx < data.size()) {
							T object = data.get(dataIdx);
							Cell.Context cx = new Cell.Context(start + i, aIndex, keys.getKey(object));
							cell.render(cx, column.getValue(object), sb);
							// Take into account, that cell builder supports
							// some
							// maps
							// to cells' divs
							// and generates them. So we have to work with first
							// <div>
							// in <td>.
							toRerender.getFirstChildElement().setInnerSafeHtml(sb.toSafeHtml());
						}
					}
				}
			}
		}
	}

	@Override
	public TableSectionElement getTableFootElement() {
		return super.getTableFootElement();
	}

	public String getColumnWidth(Column<T, ?> col, boolean withHidden) {
		if (col != null) {
			if (withHidden) {
				if (hiddenColumns.containsKey(col)) {
					return hiddenColumns.get(col);
				} else {
					return getColumnWidth(col);
				}
			} else {
				return getColumnWidth(col);
			}
		} else {
			return null;
		}
	}

	protected static Map<Element, GridSection<?>> sections = new HashMap<>();

	public static GridSection<?> getInstance(Element aTableElement) {
		return sections.get(aTableElement);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		sections.put(getElement(), this);
	}

	@Override
	protected void onDetach() {
		sections.remove(getElement());
		super.onDetach();
	}

	@Override
	public void redraw() {
		super.redraw();
	}

	@Override
	public void redrawFooters() {
		super.redrawFooters();
	}

	@Override
	public void redrawHeaders() {
		super.redrawHeaders();
	}

	@Override
	public void redrawRow(int absRowIndex) {
		super.redrawRow(absRowIndex);
	}
}
