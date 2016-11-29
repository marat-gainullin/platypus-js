/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.cellview.client.Column;

/**
 *
 * @author mg
 * @param <T>
 * @param <C>
 */
public abstract class GridColumn<T, C> extends Column<T, C> {
    
    public GridColumn(Cell<C> aCell) {
        super(aCell);
    }
    
}
