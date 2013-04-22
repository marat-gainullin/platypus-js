/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.bearsoft.gui.grid.data.CellData;
import com.eas.client.events.ScriptSourcedEvent;
import com.eas.client.model.script.RowHostObject;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class CellRenderEvent extends ScriptSourcedEvent {

    protected Object id;
    protected Object columnId;
    protected CellData cell;
    protected RowHostObject object;

    public CellRenderEvent(Scriptable source, Object id, Object columnId, CellData cell, RowHostObject object) {
        super(source);
        this.columnId = columnId;
        this.id = id;
        this.cell = cell;
        this.object = object;
    }

    public Object getId() {
        return id;
    }

    public Object getColumnId() {
        return columnId;
    }

    public CellData getCell() {
        return cell;
    }

    public RowHostObject getObject() {
        return object;
    }
}
