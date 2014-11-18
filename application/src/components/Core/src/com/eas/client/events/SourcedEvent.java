/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.events;

import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public interface SourcedEvent extends HasPublished {

    public static final String SOURCE_JS_DOC = ""
            + "/**\n"
            + " * The source object of the event.\n"
            + " */";

    @ScriptFunction(jsDoc = SOURCE_JS_DOC)
    public HasPublished getSource();

}
