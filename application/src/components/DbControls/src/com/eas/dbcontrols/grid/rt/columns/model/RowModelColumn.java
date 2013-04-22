/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.columns.model;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.locators.Locator;
import com.eas.dbcontrols.ScalarDbControl;
import com.eas.dbcontrols.grid.rt.HasStyle;
import com.eas.dbcontrols.grid.rt.veers.CellsRowsetsListener;
import java.util.List;
import org.mozilla.javascript.Function;

/**
 * Table's model's column, corresponding to particular row of column's rowset.
 * Column's rowset - rowset that serves as columns source.
 * @author Gala
 */
public class RowModelColumn extends ModelColumn {

    // column info
    protected Locator columnsLocator;
    protected Row row;
    protected int colTitleFieldIndex;
    // cells info
    protected Locator cellsLocator;
    // values rowset
    protected Rowset cellsValuesRowset;
    // values field index
    protected int cellsValuesFieldIndex;
    protected CellsRowsetsListener valuesListener;

    /**
     * Constructs a model's column, related with a particular row of a rowset.
     * @param aColumnsRowsetLocator Columns rowset locator. The locator must be constrainted by primary key. It is used by the column to define the column's actuality.
     * @param aRow Row instance, the column araises from. Row <code>aRow</code> belongs to <code>aColumnsRowsetLocator</code> undelying rowset.
     * @param aColTitleFieldIndex Index of field in the column's rowset. Value of this field would be used to
     * get title of the view's column.
     * @param aCellsLocator Locator instnce, that will be used to achieve cell's values by table's model.
     * @param aCellsValuesRowset Rowset instance, that will be used as actual cells values source.
     * It can be the same with <code>aCellsLocator</code>'s rowset. If it is not, than there is a hope that they are interlinked in some way in client code.
     * According to that hope, model will position cells rowset through locator's <code>first()</code> method to enable that link.
     * If <code>aCellsLocator</code>'s rowset and <code>aCellsValuesRowset</code> are the same, than <code>aCellsLocator</code>'s rowset will not be repositioned.
     * In such case, values will be simply gotten from <code>aCellsValuesRowset</code>.
     * @param aCellsValuesFieldIndex Field index of cells values in <code>aCellsValuesRowset</code>. Index is 1-based.
     * @see Row
     * @see Locator
     * @see Rowset
     * @see #isActual() 
     */
    public RowModelColumn(Locator aColumnsRowsetLocator, Row aRow, int aColTitleFieldIndex, Locator aCellsLocator, Rowset aCellsValuesRowset, int aCellsValuesFieldIndex, Function aCellsHander, Function aSelectHandler, boolean aReadOnly, HasStyle aStyleHost, ScalarDbControl aView, ScalarDbControl aEditor) {
        super(aColumnsRowsetLocator.getRowset(), aCellsHander, aSelectHandler, aReadOnly, aStyleHost, aView, aEditor);
        row = aRow;
        columnsLocator = aColumnsRowsetLocator;
        colTitleFieldIndex = aColTitleFieldIndex;

        cellsLocator = aCellsLocator;
        cellsValuesRowset = aCellsValuesRowset;
        cellsValuesFieldIndex = aCellsValuesFieldIndex;
    }

    /**
     * Returns cells locator that can be used to achieve cells values by table's model.
     * Table's model will use the locator to find cell's value with rows and columns
     * rowsets primary keys values. Locator should be constrainted so, that row key(s) must be specified as first constraint(s) and columns key(s) as second.
     * @return <code>Locator</code> instance. This locator's underlying rowset is neither columns rowset nor rows rowset.
     * @see Locator
     */
    public Locator getCellsLocator() {
        return cellsLocator;
    }

    /**
     * Returns cells values rowset. It can be same as cells locator's rowset.
     * If it is not, than cells locator's rowset will be repositioned.
     * @return Rowset instance, used as cells values source.
     */
    public Rowset getCellsValuesRowset() {
        return cellsValuesRowset;
    }

    /**
     * Returns field index, the cells of this column are bound to.
     * @return Field index in the cells values rowset.
     */
    public int getCellsValuesFieldIndex() {
        return cellsValuesFieldIndex;
    }

    /**
     * Returns a row, this model column is binded to.
     * @return Row instance, this model column arises from.
     * @see Row
     */
    public Row getRow() {
        return row;
    }

    /**
     * Returns field index to be used to achieve column title.
     * @return Column's title field index.
     */
    public int getColTitleFieldIndex() {
        return colTitleFieldIndex;
    }

    /**
     * Retruns actuality of this RowModelColumn instance.
     * @return True if this column presents in column's rowset, i.e. it is actual and false otherwise.
     * @throws Exception 
     * @see #RowModelColumn(com.bearsoft.rowset.locators.Locator, com.bearsoft.rowset.Row, int, com.bearsoft.rowset.locators.Locator) 
     */
    public boolean isActual() throws Exception {
        List<Integer> locatedFields = columnsLocator.getFields();
        Object[] toFind = new Object[columnsLocator.getFields().size()];
        for (int j = 0; j < locatedFields.size(); j++) {
            toFind[j] = row.getColumnObject(locatedFields.get(j));
        }
        return columnsLocator.find(toFind) && columnsLocator.getRow(0) == row;
    }

    public void setValuesListener(CellsRowsetsListener aListener) {
        if (valuesListener != null) {
            cellsValuesRowset.removeRowsetListener(valuesListener);
        }
        valuesListener = aListener;
        if (valuesListener != null) {
            cellsValuesRowset.addRowsetListener(valuesListener);
        }
    }
}
