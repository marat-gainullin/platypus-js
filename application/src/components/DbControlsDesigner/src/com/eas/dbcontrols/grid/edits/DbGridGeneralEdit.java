/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.grid.edits;

import com.eas.dbcontrols.DbControlEdit;
import com.eas.dbcontrols.grid.DbGridDesignInfo;

/**
 *
 * @author mg
 */
public abstract class DbGridGeneralEdit extends DbControlEdit
{
    protected DbGridDesignInfo gridInfo = null;

    public DbGridGeneralEdit(DbGridDesignInfo aGridInfo)
    {
        super();
        gridInfo = aGridInfo;
    }

    @Override
    public void die()
    {
        gridInfo = null;
        super.die();
    }
}
