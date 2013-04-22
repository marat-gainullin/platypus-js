/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model.gui.edits;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author Marat
 */
public abstract class DatamodelEdit extends Object implements UndoableEdit {

    protected boolean alive = true;

    protected abstract void redoWork();

    protected abstract void undoWork();

    protected DatamodelEdit() {
        super();
    }

    public boolean isNeedConnectors2Reroute() {
        return false;
    }

    @Override
    public void undo() throws CannotUndoException {
        if (canUndo()) {
            undoWork();
        } else {
            throw (new CannotUndoException());
        }
    }

    @Override
    public boolean canUndo() {
        return alive;
    }

    @Override
    public void redo() throws CannotRedoException {
        if (canRedo()) {
            redoWork();
        } else {
            throw (new CannotRedoException());
        }
    }

    @Override
    public boolean canRedo() {
        return alive;
    }

    @Override
    public void die() {
        alive = false;
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
        return "";
    }

    @Override
    public String getUndoPresentationName() {
        return "";
    }

    @Override
    public String getRedoPresentationName() {
        return "";
    }
}

