/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableColElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Event;
import com.google.gwt.view.client.ProvidesKey;
import java.util.HashMap;
import java.util.Map;

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
    private final ColumnSortList sortList = new ColumnSortList();

    public GridSection(ProvidesKey<T> keyProvider) {
        super(15, ThemedGridResources.instance, keyProvider);
        init();
    }

    private void init() {  
        super.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.CURRENT_PAGE);
        super.getColumnSortList().setLimit(1);
        super.addColumnSortHandler(new ColumnSortEvent.Handler() {

            @Override
            public void onColumnSort(ColumnSortEvent event) {
                boolean contains = false;
                int containsAt = -1;
                for (int i = 0; i < sortList.size(); i++) {
                    if (sortList.get(i).getColumn() == event.getColumn()) {
                        contains = true;
                        containsAt = i;
                        break;
                    }
                }
                if (!contains) {
                    if (!isCtrlKey) {
                        sortList.clear();
                    }
                    sortList.insert(sortList.size(), new ColumnSortList.ColumnSortInfo(event.getColumn(), true));
                } else {
                    boolean wasAscending = sortList.get(containsAt).isAscending();
                    if (!isCtrlKey) {
                        sortList.clear();
                        if (wasAscending) {
                            sortList.push(new ColumnSortList.ColumnSortInfo(event.getColumn(), false));
                        }
                    } else {
                        sortList.remove(sortList.get(containsAt));
                        if (wasAscending) {
                            sortList.insert(containsAt, new ColumnSortList.ColumnSortInfo(event.getColumn(), false));
                        }
                    }
                }
                ColumnSortEvent.fire(new HasHandlers() {

                    @Override
                    public void fireEvent(GwtEvent<?> event) {
                        _ensureHandlers().fireEvent(event);
                    }
                }, sortList);
            }
        });
    }

    protected HandlerManager _handlerManager;

    /**
     * Add a handler to handle {@link ColumnSortEvent}s.
     *
     * @param handler the {@link ColumnSortEvent.Handler} to add
     * @return a {@link HandlerRegistration} to remove the handler
     */
    @Override
    public HandlerRegistration addColumnSortHandler(ColumnSortEvent.Handler handler) {
        return _ensureHandlers().addHandler(ColumnSortEvent.getType(), handler);
    }

    HandlerManager _ensureHandlers() {
        return _handlerManager == null ? _handlerManager = createHandlerManager()
                : _handlerManager;
    }

    @Override
    public ColumnSortList getColumnSortList() {
        return sortList;
    }

    protected boolean isCtrlKey;

    @Override
    protected void onBrowserEvent2(Event event) {
        isCtrlKey = event.getCtrlKey();
        super.onBrowserEvent2(event);
    }

    /*
    @Override
    public void setKeyboardSelectedRow(int row, int subrow, boolean stealFocus) {
        // TODO: review keyboard cells focusing algorithm in multi table context
        if (row < 0) {
        } else if (row >= getVisibleRange().getLength()) {
        } else {
            super.setKeyboardSelectedRow(row, subrow, stealFocus);
        }
    }
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
                    if (aValue == null) {
                        partner.clearColumnWidth(partner.getColumn(index));
                    } else {
                        partner.setColumnWidth(partner.getColumn(index), aValue);
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

}
