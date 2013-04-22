/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import javax.swing.undo.UndoableEdit;

/**
 *
 * @author mg
 */
public abstract class DbControlEdit implements UndoableEdit {

    public DbControlEdit() {
        super();
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public void die() {
    }

    @Override
    public boolean addEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean replaceEdit(UndoableEdit anEdit) {
        return false;
    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    public String getPresentationName() {
        return DbControlsDesignUtils.getLocalizedString(getClass().getSimpleName());
    }

    @Override
    public String getUndoPresentationName() {
        return DbControlsDesignUtils.getLocalizedString("Undo") + " " + DbControlsDesignUtils.getLocalizedString(getClass().getSimpleName());
    }

    @Override
    public String getRedoPresentationName() {
        return DbControlsDesignUtils.getLocalizedString("Redo") + " " + DbControlsDesignUtils.getLocalizedString(getClass().getSimpleName());
    }
}
