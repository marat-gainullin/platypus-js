/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.model.grid.columns;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author mg
 */
public class ServiceColumn extends ModelColumn {

    public static final int SERVICE_COLUMN_DEFAULT_WIDTH = 22;
    
    public ServiceColumn() {
        this("rows-header-service", new ServiceCellRenderer(), new ServiceCellEditor());
    }

    protected ServiceColumn(String aName, TableCellRenderer aRenderer, TableCellEditor aEditor) {
        super(aName, null, null, true, null, null);
        cellRenderer = aRenderer;
        cellEditor = aEditor;
        setPreferredWidth(SERVICE_COLUMN_DEFAULT_WIDTH);
        setMinWidth(SERVICE_COLUMN_DEFAULT_WIDTH);
        setMaxWidth(SERVICE_COLUMN_DEFAULT_WIDTH);
        setWidth(SERVICE_COLUMN_DEFAULT_WIDTH);
        setResizeable(false);
    }
}
