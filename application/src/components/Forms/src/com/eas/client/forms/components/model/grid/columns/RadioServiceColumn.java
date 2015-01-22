/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

/**
 *
 * @author mg
 */
public class RadioServiceColumn extends ServiceColumn {

    public RadioServiceColumn() {
        super("rows-header-radiobuttons", new RadioCellRenderer(), new RadioCellEditor());
    }
}
