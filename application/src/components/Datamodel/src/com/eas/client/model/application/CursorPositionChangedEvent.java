/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.eas.client.events.PublishedSourcedEvent;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;

/**
 * An event of an entity cursor positioning.
 * @author mg, vv refactoring
 */
public class CursorPositionChangedEvent extends PublishedSourcedEvent {

    protected int oldIndex;
    protected int newIndex;

    public CursorPositionChangedEvent(HasPublished aSource, int aOldIndex, int aNewIndex) {
        super(aSource);
        oldIndex = aOldIndex;
        newIndex = aNewIndex;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Cursor position the cursor was on.\n"
            + " */")
    public int getOldIndex() {
        return oldIndex;
    }

    private static final String NEW_INDEX_JSDOC = ""
            + "/**\n"
            + "* Cursor position the cursor has been set on.\n"
            + "*/";

    @ScriptFunction(jsDoc = NEW_INDEX_JSDOC)
    public int getNewIndex() {
        return newIndex;
    }
}
