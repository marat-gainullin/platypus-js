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
public abstract class DmElementClearAction extends DbControlSnapshotAction {

    public DmElementClearAction() {
        super();
        putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/delete.png"));
    }

    @Override
    public boolean isEnabled() {
        if (designInfo != null && designInfo instanceof DbControlDesignInfo) {
            return ((DbControlDesignInfo) designInfo).getDatamodelElement() != null;
        }
        return super.isEnabled();
    }
}
