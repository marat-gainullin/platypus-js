/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.events;

import com.eas.script.ScriptFunction;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author vv
 */
public class ScriptSourcedEvent {

    protected Scriptable source;

    public ScriptSourcedEvent(Scriptable source) {
        this.source = source;
    }

    private static final String SOURCE_JS_DOC = "/**\n"
            + "* The source of the event.\n"
            + "*/";
    @ScriptFunction(jsDoc = SOURCE_JS_DOC)
    public Scriptable getSource() {
        return source;
    }
}
