/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.script.EventMethod;
import com.eas.script.ScriptFunction;
import java.awt.event.ContainerEvent;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author Марат
 */
public interface HasContainerEvents extends HasComponentEvents {

    public static final String ON_COMPONENT_ADDED_JSDOC = ""
            + "/**\n"
            + " * Component added event hanler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_COMPONENT_ADDED_JSDOC)
    @EventMethod(eventClass = ContainerEvent.class)
    public JSObject getOnComponentAdded();

    @ScriptFunction
    public void setOnComponentAdded(JSObject aValue);

    public static final String ON_COMPONENT_REMOVED_JSDOC = ""
            + "/**\n"
            + " * Component removed event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_COMPONENT_REMOVED_JSDOC)
    @EventMethod(eventClass = ContainerEvent.class)
    public JSObject getOnComponentRemoved();

    @ScriptFunction
    public void setOnComponentRemoved(JSObject aValue);

}
