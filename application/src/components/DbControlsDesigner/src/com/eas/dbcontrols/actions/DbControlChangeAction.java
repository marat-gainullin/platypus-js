/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.actions;

import com.eas.dbcontrols.DbControlsDesignUtils;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.undo.UndoableEditSupport;

/**
 *
 * @author mg
 */
public abstract class DbControlChangeAction extends AbstractAction {

    protected UndoableEditSupport undoSupport;

    public DbControlChangeAction() {
        super();
        putValue(Action.SHORT_DESCRIPTION, DbControlsDesignUtils.getLocalizedString(getClass().getSimpleName()));
    }

    public UndoableEditSupport getUndoSupport() {
        return undoSupport;
    }

    public void setUndoSupport(UndoableEditSupport aSupport) {
        undoSupport = aSupport;
    }
}
