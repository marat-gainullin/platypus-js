/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.rt.veers;

import com.eas.dbcontrols.grid.rt.columns.model.FieldModelColumn;
import javax.swing.table.TableModel;

/**
 * Event, occuring when some of model columns are removed or added to inner model columns.
 * @author Gala
 */
public class ColumnsChangedEvent {

    protected TableModel source;
    protected FieldModelColumn beginColumn = null;
    protected FieldModelColumn endColumn = null;

    /**
     * Constructs event, signaling about changes in table model's columns.
     * @param aSource Table model, serving as changes source.
     * @param aBeginColumn FieldModelColumn instance, at the begining of changing interval.
     * @param aEndColumn FieldModelColumn instance, at the end of changing interval.
     * @see TableModel
     * @see FieldModelColumn
     */
    public ColumnsChangedEvent(TableModel aSource, FieldModelColumn aBeginColumn, FieldModelColumn aEndColumn)
    {
        super();
        source = aSource;
        beginColumn = aBeginColumn;
        endColumn = aEndColumn;
    }

    /**
     * Returns the change source.
     * @return Change source. It is TableModel instance.
     * @see TableModel
     */
    public TableModel getSource() {
        return source;
    }

    /**
     * Returns model column at the begining of the change interval.
     * @return First model column.
     * @see FieldModelColumn
     */
    public FieldModelColumn getBeginColumn() {
        return beginColumn;
    }

    /**
     * Returns model column at the end of the change interval.
     * @return Last model column.
     * @see FieldModelColumn
     */
    public FieldModelColumn getEndColumn() {
        return endColumn;
    }
}
