/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.gui.grid.data;

import javax.swing.table.TableModel;

/**
 *
 * @author Gala
 */
public interface TableModelWrapper extends TableModel{

    public TableModel unwrap();
}
