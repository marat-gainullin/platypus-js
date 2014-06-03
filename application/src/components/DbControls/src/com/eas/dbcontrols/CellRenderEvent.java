/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.rowset.Row;
import com.eas.client.events.PublishedSourcedEvent;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class CellRenderEvent extends PublishedSourcedEvent {

    protected Object id;
    protected Object columnId;
    protected CellData cell;
    protected Row object;

    public CellRenderEvent(HasPublished source, Object id, Object columnId, CellData cell, Row object) {
        super(source);
        this.columnId = columnId;
        this.id = id;
        this.cell = cell;
        this.object = object;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The primary key of the data object.\n"
            + " */")
    public Object getId() {
        return id;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The cell's column ID.\n"
            + " */")
    public Object getColumnId() {
        return columnId;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The \"abstract\" cell.\n"
            + " */")
    public CellData getCell() {
        return cell;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The cell's row object.\n"
            + " */")
    public Row getObject() {
        return object;
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }

}
