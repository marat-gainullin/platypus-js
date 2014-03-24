/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid.builders;

import com.bearsoft.gwt.ui.widgets.grid.ThemedGridResources;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.Header;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mg
 * @param <T>
 */
public class ThemedHeaderOrFooterBuilder<T> extends AbstractHeaderOrFooterBuilder<T> {

    protected int rowsHeight = 18;
    protected SpanningSectionsBuilder groupsBuilder;

    public ThemedHeaderOrFooterBuilder(AbstractCellTable<T> table, boolean isFooter) {
        this(table, isFooter, null);
    }
    
    public ThemedHeaderOrFooterBuilder(AbstractCellTable<T> table, boolean isFooter, SpanningSectionsBuilder aGroupsBuilder) {
        super(table, isFooter);
        groupsBuilder = aGroupsBuilder;
    }

    public ThemedHeaderOrFooterBuilder(AbstractCellTable<T> table, boolean isFooter, SpanningSectionsBuilder aGroupsBuilder, int aRowsHeight) {
        super(table, isFooter);
        rowsHeight = aRowsHeight;
        groupsBuilder = aGroupsBuilder;
    }

    public int getRowsHeight() {
        return rowsHeight;
    }

    public void setRowsHeight(int aValue) {
        rowsHeight = aValue;
    }

    public TableRowBuilder newRow(){
        return startRow();
    }
    
    @Override
    protected boolean buildHeaderOrFooterImpl() {
        AbstractCellTable<T> table = getTable();
        boolean isFooter = isBuildingFooter();

        // Check if there any columns to render
        int columnCount = table.getColumnCount();
        if (columnCount > 0) {
            // Early exit if there aren't any headers or footers in the columns to render.
            boolean hasHeadersOrFooters = false;
            for (int i = 0; i < columnCount; i++) {
                if (getHeader(i) != null) {
                    hasHeadersOrFooters = true;
                    break;
                }
            }
            if (hasHeadersOrFooters) {
                // Get information about the sorted column.
                ColumnSortList sortList = table.getColumnSortList();
                Map<Column<T, ?>, ColumnSortList.ColumnSortInfo> sortedColumns = new HashMap<>();
                if (sortList != null) {
                    for (int i = 0; i < sortList.size(); i++) {
                        ColumnSortList.ColumnSortInfo sInfo = sortList.get(i);
                        sortedColumns.put((Column<T, ?>) sInfo.getColumn(), sInfo);
                    }
                }
                // Get the common style names.
                String className = isBuildingFooter() ? ThemedGridResources.instance.cellTableStyle().cellTableFooter() : ThemedGridResources.instance.cellTableStyle().cellTableHeader();
                String sortableStyle = ThemedGridResources.instance.cellTableStyle().cellTableSortableHeader();
                String sortedAscStyle = ThemedGridResources.instance.cellTableStyle().cellTableSortedHeaderAscending();
                String sortedDescStyle = ThemedGridResources.instance.cellTableStyle().cellTableSortedHeaderDescending();
                if (groupsBuilder != null) {
                    groupsBuilder.buildSections(className, this, columnCount);
                }
                TableRowBuilder tr = startRow();
                // Loop through all column headers.
                for (int curColumn = 0; curColumn < columnCount; curColumn++) {
                    Header<?> headerOrFooter = getHeader(curColumn);
                    Column<T, ?> column = getTable().getColumn(curColumn);
                    boolean isSortable = !isFooter && column.isSortable();
                    ColumnSortList.ColumnSortInfo sortedInfo = sortedColumns.get(column);
                    boolean isSorted = sortedInfo != null;
                    StringBuilder classesBuilder = new StringBuilder(className);
                    boolean isSortAscending = isSortable && sortedInfo != null ? sortedInfo.isAscending() : false;
                    if (isSortable) {
                        if (classesBuilder.length() > 0) {
                            classesBuilder.append(" ");
                        }
                        classesBuilder.append(sortableStyle);
                        if (isSorted) {
                            if (classesBuilder.length() > 0) {
                                classesBuilder.append(" ");
                            }
                            classesBuilder.append(isSortAscending ? sortedAscStyle : sortedDescStyle);
                        }
                    }
                    // Render the header or footer.
                    TableCellBuilder th
                            = tr.startTH().className(classesBuilder.toString());
                    if (headerOrFooter != null) {
                        appendExtraStyles(headerOrFooter, classesBuilder);
                        enableColumnHandlers(th, column);
                        // Build the header.
                        Cell.Context context = new Cell.Context(0, curColumn, headerOrFooter.getKey());
                        // Add div element with aria button role
                        if (isSortable) {
                            // TODO: Figure out aria-label and translation of label text
                            th.attribute("role", "button");
                            th.tabIndex(-1);
                        }
                        renderSortableHeader(th, context, headerOrFooter, isSorted, isSortAscending);
                    }
                    th.endTH();
                }
                // End the row.
                tr.endTR();
                return true;
            } else {
                // No headers or footers are defined
                return false;
            }
        } else {
            // Nothing to render;
            return false;
        }
    }

    /**
     * Append the extra style names for the header.
     *
     * @param header the header that may contain extra styles, it can be null
     * @param classesBuilder the string builder for the TD classes
     */
    private <H> void appendExtraStyles(Header<H> header, StringBuilder classesBuilder) {
        if (header == null) {
            return;
        }
        String headerStyleNames = header.getHeaderStyleNames();
        if (headerStyleNames != null) {
            classesBuilder.append(" ");
            classesBuilder.append(headerStyleNames);
        }
    }
}
