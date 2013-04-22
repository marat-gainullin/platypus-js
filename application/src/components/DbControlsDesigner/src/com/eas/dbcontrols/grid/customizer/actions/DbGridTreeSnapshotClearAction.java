/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.customizer.actions;

import com.eas.dbcontrols.grid.customizer.DbGridCustomizer;

/**
 *
 * @author mg
 */
public abstract class DbGridTreeSnapshotClearAction extends DbGridTreeSnapshotAction
{
    public DbGridTreeSnapshotClearAction(DbGridCustomizer aCustomizer)
    {
        super(aCustomizer);
    }

    @Override
    protected String getIconName()
    {
        return "16x16/delete.png";
    }
}
