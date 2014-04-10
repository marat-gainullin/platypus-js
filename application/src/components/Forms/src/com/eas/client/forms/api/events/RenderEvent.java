/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.events;

import com.eas.gui.CascadedStyle;
import com.eas.script.ScriptFunction;

/**
 *
 * @author vv
 */
public class RenderEvent extends Event<java.awt.event.ContainerEvent> {

    protected RenderEvent(java.awt.event.ContainerEvent aEvent) {
        super(aEvent);
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The primary key of the data object.\n"
            + " */")
    public String getId() {
        return null;
    }

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * The \"abstract\" cell.\n"
            + " */")
    public Cell getCell() {
        return new Cell();
    }

    /**
     * The stub class to mimic script API.
     */
    public class Cell {

        @ScriptFunction(jsDoc = ""
                + "/**\n"
                + " * The displayed text.\n"
                + " */")
        public String getDisplay() {
            return null;
        }
        
        @ScriptFunction(jsDoc = ""
                + "/**\n"
                + " * The cell's style.\n"
                + " */")
        public CascadedStyle getStyle() {
            return new CascadedStyle();
        }
    }
}
