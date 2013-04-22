/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.edits;

import javax.swing.undo.UndoableEditSupport;

/**
 *
 * @author vv
 */
public class NotSavableUndoableEditSupport extends UndoableEditSupport {

    protected boolean notSavable;

    public synchronized void notSavable() {
        notSavable = true;
    }

    @Override
    public synchronized void endUpdate() {
        notSavable = false;
        super.endUpdate();
    }
}
