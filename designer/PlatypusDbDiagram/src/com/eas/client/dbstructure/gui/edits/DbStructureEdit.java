/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.exceptions.DbActionException;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author mg
 */
public abstract class DbStructureEdit implements UndoableEdit {

    protected SqlActionsController sqlController;

    /**
     * Retrives name of the table, subclasses redos/undos dealing with
     *
     * @return the changing table name
     */
    protected String getChangingTableName() {
        return null;
    }

    protected class DbStructureCannotUndoException extends CannotUndoException {

        protected DbActionException course;

        public DbStructureCannotUndoException(DbActionException ex) {
            super();
            course = ex;
        }

        @Override
        public String getLocalizedMessage() {
            if (course != null) {
                return course.getLocalizedMessage();
            } else {
                return super.getLocalizedMessage();
            }
        }

        @Override
        public String getMessage() {
            if (course != null) {
                return course.getMessage();
            } else {
                return super.getMessage();
            }
        }
    }

    protected class DbStructureCannotRedoException extends CannotRedoException {

        protected DbActionException course;

        public DbStructureCannotRedoException(DbActionException ex) {
            super();
            course = ex;
        }

        @Override
        public String getLocalizedMessage() {
            if (course != null) {
                return course.getLocalizedMessage();
            } else {
                return super.getLocalizedMessage();
            }
        }

        @Override
        public String getMessage() {
            if (course != null) {
                return course.getMessage();
            } else {
                return super.getMessage();
            }
        }
    }

    public DbStructureEdit(SqlActionsController aSqlController) {
        super();
        sqlController = aSqlController;
    }

    @Override
    public void undo() throws CannotUndoException {
        try {
            doUndoWork();
        } catch (Exception ex) {
            if (ex instanceof DbActionException) {
                throw new DbStructureCannotUndoException((DbActionException) ex);
            } else {
                throw new CannotUndoException();
            }
        }
    }

    protected abstract void doUndoWork() throws Exception;

    protected abstract void doRedoWork() throws Exception;

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException {
        try {
            doRedoWork();
        } catch (Exception ex) {
            if (ex instanceof DbActionException) {
                throw new DbStructureCannotRedoException((DbActionException) ex);
            } else {
                throw new CannotRedoException();
            }
        }
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public void die() {
        sqlController = null;
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
        return getPresentationName();
    }

    @Override
    public String getRedoPresentationName() {
        return getPresentationName();
    }
}
