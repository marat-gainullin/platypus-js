/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols;

import javax.swing.event.CellEditorListener;

/**
 *
 * @author mg
 */
public interface DbControlEditingListener extends CellEditorListener{
    /**
     * Invoked by cell editors when the editing has been completed,
     * but editing session hasn't been ended.
     * It useful when the cell editor whants set it's value to
     * host(table or tree) model and continue cell editing.
     * It's workaround of swing concept that setting data from cell
     * editor to host model is possible only with terminating editing session.
     */
    public void cellEditingCompleted();
}
