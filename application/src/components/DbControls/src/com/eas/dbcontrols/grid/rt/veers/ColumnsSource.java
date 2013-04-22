/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.veers;

import com.bearsoft.gui.grid.columns.TableColumnHandler;
import com.bearsoft.gui.grid.header.GridColumnsGroup;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.locators.Locator;
import com.eas.dbcontrols.ScalarDbControl;
import javax.swing.table.TableColumn;
import org.mozilla.javascript.Function;

/**
 * Class, intended to hold general information about veer columns organization.
 * @author Gala
 */
public class ColumnsSource {

    protected GridColumnsGroup group;
    // columns information
    protected TableColumn anchorViewColumn;
    protected Locator columnsLocator;
    protected int columnsTitleColIndex;
    // cells information
    protected Locator cellsLocator;
    // values rowset
    protected Rowset cellsValuesRowset;
    // values field index
    protected int cellsValuesFieldIndex;
    protected TableColumnHandler rowsColumnsHandler;
    protected Function cellsHandler;
    protected Function selectHandler;

    /**
     * Rows model columns source constructor.
     * @param aGroup <code>GridColumnsGroup</code> the row columns will be placed in.
     * @param aAnchorViewColumn TableColumn instance, columns from this source to glued to.
     * @param aColumnsLocator Locator instance, that will be used to locate column's row in column's rowset.
     * @param aColumnsTitleColIndex Field index in columns rowset the column's title to be gotten from.
     * @param aCellsLocator Locator instnce, that will be used to achieve cell's values by table's model.
     * This locator must be constrainted with rows and than with columns keys.
     * @param aCellsValuesRowset Rowset instance, that will be used as actual cells values source.
     * It can be the same with <code>aCellsLocator</code>'s rowset. If it is not, than there is a hope that they are interlinked in some way in client code.
     * According to that hope, model will position cells rowset through locator's <code>first()</code> method to enable that link.
     * If <code>aCellsLocator</code>'s rowset and <code>aCellsValuesRowset</code> are the same, than <code>aCellsLocator</code>'s rowset will not be repositioned.
     * In such case, values will be simply gotten from <code>aCellsValuesRowset</code>.
     * @param aCellsValuesFieldIndex Field index of cells values in <code>aCellsValuesRowset</code>. Index is 1-based.
     * @param aRowsColumnsHandler Handler of new created table columns. Handling may be any neccesary work, but typically it is cell renderer and cell editor creation.
     * @see TableColumn
     * @see Locator
     * @see Rowset
     * @see TableColumnHandler
     * @see GridColumnsGroup
     */
    public ColumnsSource(GridColumnsGroup aGroup, TableColumn aAnchorViewColumn, Locator aColumnsLocator, int aColumnsTitleColIndex, Locator aCellsLocator, Rowset aCellsValuesRowset, int aCellsValuesFieldIndex, TableColumnHandler aRowsColumnsHandler, Function aCellsHandler, Function aSelectHandler) {
        super();
        group = aGroup;
        anchorViewColumn = aAnchorViewColumn;
        columnsLocator = aColumnsLocator;
        columnsTitleColIndex = aColumnsTitleColIndex;

        cellsLocator = aCellsLocator;
        cellsValuesRowset = aCellsValuesRowset;
        cellsValuesFieldIndex = aCellsValuesFieldIndex;
        rowsColumnsHandler = aRowsColumnsHandler;
        cellsHandler = aCellsHandler;
        selectHandler = aSelectHandler;
    }

    public GridColumnsGroup getGroup() {
        return group;
    }

    public TableColumnHandler getRowsColumnsHandler() {
        return rowsColumnsHandler;
    }

    public TableColumn getPrefirstViewColumn() {
        return anchorViewColumn;
    }

    public Locator getColumnsLocator() {
        return columnsLocator;
    }

    public int getColumnsTitleColIndex() {
        return columnsTitleColIndex;
    }

    public Locator getCellsLocator() {
        return cellsLocator;
    }

    public Rowset getCellsValuesRowset() {
        return cellsValuesRowset;
    }

    public int getCellsValuesFieldIndex() {
        return cellsValuesFieldIndex;
    }

    public Function getCellsHandler() {
        return cellsHandler;
    }

    public Function getSelectHandler() {
        return selectHandler;
    }

    public boolean isReadOnly() {
        return group != null ? group.isReadonly() : false;
    }
}
