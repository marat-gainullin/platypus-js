/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.bearsoft.rowset.Row;
import com.eas.script.HasPublished;

/**
 * Interace for rowset data aware control.
 * Rowset controls views and edits one rowset. Example is DbGrid.
 * @author mg
 */
public interface RowsetDbControl extends DbControl, HasPublished {

    public boolean makeVisible(Row aRow, boolean needToSelect) throws Exception;

    /**
     * Adds a row to processed row set. For example as tree expanded row in background thread.
     * @param aRow A row to add;
     */
    public void addProcessedRow(Row aRow);

    /**
     * Removes a row from processed rows set.
     * @param aRow A row to remove;
     */
    public void removeProcessedRow(Row aRow);

    public Row[] getProcessedRows();

    public boolean isRowProcessed(Row aRow);
}
