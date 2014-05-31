/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.application;

import com.bearsoft.rowset.Row;
import com.eas.client.events.PublishedSourcedEvent;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;

/**
 * An event for an entity's instance row delete.
 * @author vv
 */
public class EntityInstanceDeleteEvent extends PublishedSourcedEvent {

    protected Row deleted;

    public EntityInstanceDeleteEvent(HasPublished aSource, Row aDeleted) {
        super(aSource);
        deleted = aDeleted;
    }

    private static final String DELETED_JSDOC = ""
            + "/**\n"
            + "* The deleted element.\n"
            + "*/";

    @ScriptFunction(jsDoc = DELETED_JSDOC)
    public Row getDeleted() {
        return deleted;
    }
}
