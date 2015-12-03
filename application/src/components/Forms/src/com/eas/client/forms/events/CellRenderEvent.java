/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events;

import com.bearsoft.gui.grid.data.CellData;
import com.eas.client.events.PublishedSourcedEvent;
import com.eas.client.forms.components.model.grid.columns.ModelColumn;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class CellRenderEvent extends PublishedSourcedEvent {

    protected ModelColumn column;
    protected CellData cell;
    protected JSObject object;

    public CellRenderEvent(HasPublished source, ModelColumn aColumn, CellData aCell, JSObject anElement) {
        super(source);
        column = aColumn;
        cell = aCell;
        object = anElement;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The cell's column.\n"
            + " */")
    public ModelColumn getColumn() {
        return column;
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
            + " * The cell's object.\n"
            + " */")
    public JSObject getObject() {
        return object;
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }
}
