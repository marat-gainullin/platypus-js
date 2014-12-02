/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid;

import com.bearsoft.gui.grid.header.GridColumnsGroup;
import com.eas.client.forms.components.model.grid.ModelGrid;
import com.eas.dbcontrols.grid.DbGridColumn;

/**
 *
 * @author mg
 */
public class FixedDbGridColumn extends DbGridColumn {

    protected int headerType = ModelGrid.ROWS_HEADER_TYPE_USUAL;

    public FixedDbGridColumn(int aFixedWidth, int aHeaderType) {
        super();
        headerType = aHeaderType;
        setWidth(aFixedWidth);
    }

    public int getHeaderType() {
        return headerType;
    }

    @Override
    public void initializeGridColumnsGroup(GridColumnsGroup group) {
        super.initializeGridColumnsGroup(group);
        group.setMinWidth(getWidth());
        group.setMaxWidth(getWidth());
    }
}
