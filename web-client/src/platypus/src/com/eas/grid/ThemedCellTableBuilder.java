/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.builder.shared.DivBuilder;
import com.google.gwt.dom.builder.shared.HtmlBuilderFactory;
import com.google.gwt.dom.builder.shared.HtmlElementBuilderBase;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
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

    protected String dynamicTDClassName;
    protected String dynamicCellClassName;
    protected String dynamicOddRowsClassName;
    protected String dynamicEvenRowsClassName;

    public ThemedCellTableBuilder(GridSection<T> cellTable) {
        this(cellTable, "", "", "", "");
    }
    
    public ThemedCellTableBuilder(GridSection<T> cellTable, String aDynamicTDClassName, String aDynamicCellClassName, String aDynamicOddRowsClassName, String aDynamicEvenRowsClassName) {
    	super(cellTable);
        dynamicTDClassName = aDynamicTDClassName;
        dynamicCellClassName = aDynamicCellClassName;
        dynamicOddRowsClassName = aDynamicOddRowsClassName;
        dynamicEvenRowsClassName = aDynamicEvenRowsClassName;
    }
    
    @Override
    public void buildRowImpl(T rowValue, int absRowIndex) {
        // Calculate the row styles.
        SelectionModel<? super T> selectionModel = cellTable.getSelectionModel();
        boolean isSelected
                = (selectionModel == null || rowValue == null) ? false : selectionModel.isSelected(rowValue);
        boolean isOdd = (absRowIndex + 1) % 2 != 0;
        StringBuilder trClasses = new StringBuilder();
    	if(isOdd){
    		if(dynamicOddRowsClassName != null && !dynamicOddRowsClassName.isEmpty())
    			trClasses.append(" ").append(dynamicOddRowsClassName);
    		else
    			trClasses.append(" ").append(ThemedGridResources.instance.cellTableStyle().cellTableOddRow());
    	}else{
    		if(dynamicEvenRowsClassName != null && !dynamicEvenRowsClassName.isEmpty())
    			trClasses.append(" ").append(dynamicEvenRowsClassName);
    		else
    			trClasses.append(" ").append(ThemedGridResources.instance.cellTableStyle().cellTableEvenRow());
    	}
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
        if(((GridSection<T>)cellTable).isDraggableRows())
        	tr.attribute("draggable", "true");
        tr.className(trClasses.toString());

        // Build the columns.
        int columnCount = cellTable.getColumnCount();
        for (int curColumn = 0; curColumn < columnCount; curColumn++) {
            Column<T, ?> column = cellTable.getColumn(curColumn);
            // Create the cell styles.
            StringBuilder tdClasses = new StringBuilder(ThemedGridResources.instance.cellTableStyle().cellTableCell());
            /*
            if(showOddRowsInOtherColor){
            	tdClasses.append(" ").append(isOdd ? ThemedGridResources.instance.cellTableStyle().cellTableOddRowCell() : ThemedGridResources.instance.cellTableStyle().cellTableEvenRowCell());
            }
            */
            /*
            if (curColumn == 0) {
                tdClasses.append(" ").append(ThemedGridResources.instance.cellTableStyle().cellTableFirstColumn());
            }
            */
            if (isSelected) {
                tdClasses.append(" ").append(ThemedGridResources.instance.cellTableStyle().cellTableSelectedRowCell());
            }
            /*
            // The first and last column could be the same column.
            if (curColumn == columnCount - 1) {
                tdClasses.append(" ").append(ThemedGridResources.instance.cellTableStyle().cellTableLastColumn());
            }
            */
            if(dynamicTDClassName != null && !dynamicTDClassName.isEmpty()){
            	tdClasses.append(" ").append(dynamicTDClassName);
            }

            // Add class names specific to the cell.
            Cell.Context context = createCellContext(absRowIndex, curColumn, cellTable.getValueKey(rowValue));
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
            DivBuilder div = HtmlBuilderFactory.get().createDivBuilder(); // td.startDiv();
            div.className(dynamicCellClassName);

            // Render the cell into the div.
            renderCell(div, context, column, rowValue);

            // End the cell.
            div.endDiv();
            
            tdGenerated(td, context);
            td.html(((HtmlElementBuilderBase)div).asSafeHtml());
            td.endTD();
        }

        // End the row.
        tr.endTR();
    }
    
    protected Cell.Context createCellContext(int aIndex, int aColumn, Object aKey){
    	return new Cell.Context(aIndex, aColumn, aKey);
    }
    
    protected void tdGenerated(TableCellBuilder aTd, Cell.Context aContext){    	
    } 
}
