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
public abstract class FontClearAction extends DbControlSnapshotAction {

    public FontClearAction() {
        super();
        putValue(Action.SMALL_ICON, DesignIconCache.getIcon("16x16/delete.png"));
    }

    @Override
    public boolean isEnabled() {
        return designInfo instanceof DbControlDesignInfo && ((DbControlDesignInfo) designInfo).getFont() != null;
    }
}
