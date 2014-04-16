/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableColElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Event;
import com.google.gwt.view.client.ProvidesKey;

/**
 * 
 * @author mg
 * @param <T>
 */
public class GridSection<T> extends CellTable<T> {

	protected AbstractCellTable<T>[] columnsPartners;
	protected WidthCallback widthPropagator;
	protected ColumnsRemover columnsRemover;
	protected AbstractCellTable<T> headerSource;
	protected AbstractCellTable<T> footerSource;

	public GridSection(ProvidesKey<T> keyProvider) {
		super(15, ThemedGridResources.instance, keyProvider, null, true, false);
		setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.CURRENT_PAGE);
		setLoadingIndicator(null);
		setEmptyTableWidget(null);
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

	/*
	 * @Override public void setKeyboardSelectedRow(int row, int subrow, boolean
	 * stealFocus) { // TODO: review keyboard cells focusing algorithm in multi
	 * table context if (row < 0) { } else if (row >=
	 * getVisibleRange().getLength()) { } else {
	 * super.setKeyboardSelectedRow(row, subrow, stealFocus); } }
	 */

	public AbstractCellTable<T>[] getColumnsPartners() {
		return columnsPartners;
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
			clearColumnWidth(aColumn);
			return true;
		} else {
			return false;
		}
	}

	public boolean showColumn(Column<T, ?> aColumn) {
		if (isColumnHidden(aColumn)) {
			String wasWidth = hiddenColumns.remove(aColumn);
			setColumnWidth(aColumn, wasWidth);
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
