/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gwt.ui.widgets.grid.builders;

import com.bearsoft.gwt.ui.widgets.grid.ThemedGridResources;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.builder.shared.DivBuilder;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.view.client.SelectionModel;

/**
 *
 * @author mg
 * @param <T>
 */
public class ThemedCellTableBuilder<T> extends AbstractCellTableBuilder<T> {

    protected String dynamicCellClassName;

    public ThemedCellTableBuilder(AbstractCellTable<T> cellTable) {
        this(cellTable, "");
    }
    
    public ThemedCellTableBuilder(AbstractCellTable<T> cellTable, String aDynamicCellClassName) {
        super(cellTable);
        dynamicCellClassName = aDynamicCellClassName;
    }

    @Override
    public void buildRowImpl(T rowValue, int absRowIndex) {
        // Calculate the row styles.
        SelectionModel<? super T> selectionModel = cellTable.getSelectionModel();
        boolean isSelected
                = (selectionModel == null || rowValue == null) ? false : selectionModel.isSelected(rowValue);
        boolean isOdd = absRowIndex % 2 != 0;
        StringBuilder trClasses = new StringBuilder(isOdd ? ThemedGridResources.instance.cellTableStyle().cellTableOddRow() : ThemedGridResources.instance.cellTableStyle().cellTableEvenRow());
        if (isSelected) {
            trClasses.append(" ").append(ThemedGridResources.instance.cellTableStyle().cellTableSelectedRow());
        }

        // Add custom row styles.
        RowStyles<T> rowStyles = cellTable.getRowStyles();
        if (rowStyles != null) {
            String extraRowStyles = rowStyles.getStyleNames(rowValue, absRowIndex);
            if (extraRowStyles != null) {
                trClasses.append(" ").append(extraRowStyles);
            }
        }

        // Build the row.
        TableRowBuilder tr = startRow();
        tr.className(trClasses.toString());

        // Build the columns.
        int columnCount = cellTable.getColumnCount();
        for (int curColumn = 0; curColumn < columnCount; curColumn++) {
            Column<T, ?> column = cellTable.getColumn(curColumn);
            // Create the cell styles.
            StringBuilder tdClasses = new StringBuilder(ThemedGridResources.instance.cellTableStyle().cellTableCell());            
            tdClasses.append(" ").append(isOdd ? ThemedGridResources.instance.cellTableStyle().cellTableEvenRowCell() : ThemedGridResources.instance.cellTableStyle().cellTableOddRowCell());
            if (curColumn == 0) {
                tdClasses.append(" ").append(ThemedGridResources.instance.cellTableStyle().cellTableFirstColumn());
            }
            if (isSelected) {
                tdClasses.append(" ").append(ThemedGridResources.instance.cellTableStyle().cellTableSelectedRowCell());
            }
            // The first and last column could be the same column.
            if (curColumn == columnCount - 1) {
                tdClasses.append(" ").append(ThemedGridResources.instance.cellTableStyle().cellTableLastColumn());
            }
            // Add class names specific to the cell.
            Cell.Context context = new Cell.Context(absRowIndex, curColumn, cellTable.getValueKey(rowValue));
            String cellStyles = column.getCellStyleNames(context, rowValue);
            if (cellStyles != null) {
                tdClasses.append(" ").append(cellStyles);
            }
            // Build the cell.
            HasHorizontalAlignment.HorizontalAlignmentConstant hAlign = column.getHorizontalAlignment();
            HasVerticalAlignment.VerticalAlignmentConstant vAlign = column.getVerticalAlignment();
            TableCellBuilder td = tr.startTD();
            td.className(tdClasses.toString());
            if (hAlign != null) {
                td.align(hAlign.getTextAlignString());
            }
            if (vAlign != null) {
                td.vAlign(vAlign.getVerticalAlignString());
            }
            // Add the inner div.
            DivBuilder div = td.startDiv();
            div.style().outlineStyle(Style.OutlineStyle.NONE).endStyle();
            div.className(dynamicCellClassName);

            // Render the cell into the div.
            renderCell(div, context, column, rowValue);

            // End the cell.
            div.endDiv();
            td.endTD();
        }

        // End the row.
        tr.endTR();
    }
}
