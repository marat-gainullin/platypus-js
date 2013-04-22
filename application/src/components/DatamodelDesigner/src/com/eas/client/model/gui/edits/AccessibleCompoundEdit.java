/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.edits;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author mg
 */
public class AccessibleCompoundEdit extends CompoundEdit {

    public UndoableEdit[] getEdits() {
        return edits.toArray(new UndoableEdit[0]);
    }
}
