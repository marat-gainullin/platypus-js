/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.rt.columns.view;

import com.eas.dbcontrols.grid.rt.veers.ColumnsSource;

/**
 *
 * @author mg
 */
public class AnchorTableColumn extends DummyTableColumn {

    protected ColumnsSource columnsSource;
    
    public AnchorTableColumn(int aWidth) {
        super(aWidth);
    }

    public ColumnsSource getColumnsSource() {
        return columnsSource;
    }

    public void setColumnsSource(ColumnsSource aValue) {
        columnsSource = aValue;
    }
    
}
