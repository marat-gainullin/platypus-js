/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

/**
 *
 * @author mg
 */
public class RowHeaderTableColumn extends ModelColumn {

    public static final int ROWS_HEADER_TYPE_NONE = 0;
    public static final int ROWS_HEADER_TYPE_USUAL = 1;
    public static final int ROWS_HEADER_TYPE_CHECKBOX = 2;
    public static final int ROWS_HEADER_TYPE_RADIOBUTTON = 3;
    
    public RowHeaderTableColumn(int aWidth) {
        super("rows-header", null, null, true, null, null, null);
        setWidth(aWidth);
    }
}
