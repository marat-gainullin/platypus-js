package com.eas.client.dbstructure.gui.edits;

import java.util.ArrayList;
import java.util.List;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

public class DbStructureCompoundEdit extends CompoundEdit {

    private List<UndoableEdit> tempEdits = new ArrayList<>();

    @Override
    public void redo() throws CannotRedoException {
        for (int i = 0; i < edits.size(); i++) {
            UndoableEdit edit = edits.get(i);
            if (edit instanceof DbStructureEdit) {
                ((DbStructureEdit) edit).redo();
            }
        }
        for (int i = 0; i < edits.size(); i++) {
            UndoableEdit edit = edits.get(i);
            if (!(edit instanceof DbStructureEdit)) {
                edit.redo();
            }
        }
        tempEdits.addAll(edits);
        edits.clear();
        super.redo();
        edits.addAll(tempEdits);
        tempEdits.clear();
    }

    @Override
    public void undo() throws CannotUndoException {
        for (int i = edits.size() - 1; i >= 0; i--) {
            UndoableEdit edit = edits.get(i);
            if (edit instanceof DbStructureEdit) {
                ((DbStructureEdit) edit).undo();
            }
        }
        for (int i = edits.size() - 1; i >= 0; i--) {
            UndoableEdit edit = edits.get(i);
            if (!(edit instanceof DbStructureEdit)) {
                edit.undo();
            }
        }
        tempEdits.addAll(edits);
        edits.clear();
        super.undo();
        edits.addAll(tempEdits);
        tempEdits.clear();
    }

    public UndoableEdit[] getEdits() {
        UndoableEdit[] lEdits = new UndoableEdit[edits.size()];
        return edits.toArray(lEdits);
    }
}
