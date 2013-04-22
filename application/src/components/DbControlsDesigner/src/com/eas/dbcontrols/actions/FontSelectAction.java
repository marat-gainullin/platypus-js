/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols.actions;

import com.eas.dbcontrols.*;
import javax.swing.Action;

/**
 *
 * @author mg
 */
public abstract class FontSelectAction extends DbControlSnapshotAction
{
    public FontSelectAction()
    {
        super();
        putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/font.png"));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
