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

    public RowHeaderTableColumn(int aWidth) {
        super("rows-header", null, null, true, null, null, null, null);
        setWidth(aWidth);
    }
}
