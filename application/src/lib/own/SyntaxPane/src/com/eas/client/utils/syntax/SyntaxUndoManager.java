/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.utils.syntax;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

/**
 *
 * @author Marat
 */
class SyntaxUndoManager extends UndoManager
{

    CompoundEdit sectionBeginEdit = null;
    private int undoSection = 0;
    protected UndoableEditSupport uSupport = new UndoableEditSupport();

    public SyntaxUndoManager()
    {
        super();
    }

    public void enterUndoSection()
    {
        ++undoSection;
        if (undoSection == 1)
        {
            sectionBeginEdit = new CompoundEdit();
        }
    }

    public void leaveUndoSection()
    {
        assert (undoSection > 0);
        --undoSection;
        if (undoSection == 0)
        {
            sectionBeginEdit.end();
            addEdit(sectionBeginEdit);
            //sectionBeginEdit.end();
            sectionBeginEdit = null;
        }
    }

    public boolean isInUndoSection()
    {
        return (undoSection > 0);
    }

    @Override
    public synchronized void discardAllEdits()
    {
        super.discardAllEdits();
        undoSection = 0;
        sectionBeginEdit = null;
    }

    @Override
    public synchronized boolean addEdit(UndoableEdit anEdit)
    {
        if (isInUndoSection())
        {
            assert (sectionBeginEdit != null);
            return sectionBeginEdit.addEdit(anEdit);
        }
        else
        {
            if (super.addEdit(anEdit))
            {
                uSupport.postEdit(anEdit);
                return true;
            }
            return false;
        }
    }

    /**
     * Registers an <code>UndoableEditListener</code>.
     * The listener is notified whenever an edit occurs which can be undone.
     *
     * @param l  an <code>UndoableEditListener</code> object
     * @see #removeUndoableEditListener
     */
    public void addUndoableEditListener(UndoableEditListener l)
    {
        uSupport.addUndoableEditListener(l);
    }

    /**
     * Removes an <code>UndoableEditListener</code>.
     *
     * @param l  the <code>UndoableEditListener</code> object to be removed
     * @see #addUndoableEditListener
     */
    public void removeUndoableEditListener(UndoableEditListener l)
    {
        uSupport.removeUndoableEditListener(l);
    }
}
