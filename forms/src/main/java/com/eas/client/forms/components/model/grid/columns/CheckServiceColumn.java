/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

/**
 *
 * @author mg
 */
public class CheckServiceColumn extends ServiceColumn {

    public CheckServiceColumn() {
        super("rows-header-checkboxes", new CheckCellRenderer(), new CheckCellEditor());
    }
}
