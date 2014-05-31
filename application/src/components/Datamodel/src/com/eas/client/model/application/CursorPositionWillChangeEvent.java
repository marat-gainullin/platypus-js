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
 *
 * @author vv
 */
public class CursorPositionWillChangeEvent extends PublishedSourcedEvent {

    protected int oldIndex;
    protected int newIndex;

    public CursorPositionWillChangeEvent(HasPublished aSource, int aOldIndex, int aNewIndex) {
        super(aSource);
        oldIndex = aOldIndex;
        newIndex = aNewIndex;
    }


    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + "* Cursor position the cursor is still on.\n"
            + "*/")
    public int getOldIndex() {
        return oldIndex;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + "* Cursor position the cursor will be set on.\n"
            + "*/")
    public int getNewIndex() {
        return newIndex;
    }

}
