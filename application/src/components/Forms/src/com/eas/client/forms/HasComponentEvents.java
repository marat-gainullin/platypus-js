/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.forms.events.ActionEvent;
import com.eas.client.forms.events.ComponentEvent;
import com.eas.client.forms.events.MouseEvent;
import com.eas.script.EventMethod;
import com.eas.script.ScriptFunction;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author Марат
 */
public interface HasComponentEvents {

    public static final String ON_MOUSE_CLICKED_JSDOC = ""
            + "/**\n"
            + " * Mouse clicked event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_MOUSE_CLICKED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    public JSObject getOnMouseClicked();

    @ScriptFunction
    public void setOnMouseClicked(JSObject aValue);
    public static final String ON_MOUSE_DRAGGED_JSDOC = ""
            + "/**\n"
            + " * Mouse dragged event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_MOUSE_DRAGGED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    public JSObject getOnMouseDragged();

    @ScriptFunction
    public void setOnMouseDragged(JSObject aValue);

    public static final String ON_MOUSE_ENTERED_JSDOC = ""
            + "/**\n"
            + " * Mouse entered over the component event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_MOUSE_ENTERED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    public JSObject getOnMouseEntered();

    @ScriptFunction
    public void setOnMouseEntered(JSObject aValue);
    public static final String ON_MOUSE_EXITED_JSDOC = ""
            + "/**\n"
            + " * Mouse exited over the component event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_MOUSE_EXITED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    public JSObject getOnMouseExited();

    @ScriptFunction
    public void setOnMouseExited(JSObject aValue);

    public static final String ON_MOUSE_MOVED_JSDOC = ""
            + "/**\n"
            + " * Mouse moved event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_MOUSE_MOVED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    public JSObject getOnMouseMoved();

    @ScriptFunction
    public void setOnMouseMoved(JSObject aValue);

    public static final String ON_MOUSE_PRESSED_JSDOC = ""
            + "/**\n"
            + " * Mouse pressed event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_MOUSE_PRESSED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    public JSObject getOnMousePressed();

    @ScriptFunction
    public void setOnMousePressed(JSObject aValue);

    public static final String ON_MOUSE_RELEASED_JSDOC = ""
            + "/**\n"
            + " * Mouse released event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_MOUSE_RELEASED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    public JSObject getOnMouseReleased();

    @ScriptFunction
    public void setOnMouseReleased(JSObject aValue);

    public static final String ON_MOUSE_WHEEL_MOVED_JSDOC = ""
            + "/**\n"
            + " * Mouse wheel moved event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_MOUSE_WHEEL_MOVED_JSDOC)
    @EventMethod(eventClass = MouseEvent.class)
    public JSObject getOnMouseWheelMoved();

    @ScriptFunction
    public void setOnMouseWheelMoved(JSObject aValue);

    public static final String ON_ACTION_PERFORMED_JSDOC = ""
            + "/**\n"
            + " * Main action performed event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_ACTION_PERFORMED_JSDOC)
    @EventMethod(eventClass = ActionEvent.class)
    public JSObject getOnActionPerformed();

    @ScriptFunction
    public void setOnActionPerformed(JSObject aValue);

    public static final String ON_COMPONENT_HIDDEN_JSDOC = ""
            + "/**\n"
            + " * Component hidden event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_COMPONENT_HIDDEN_JSDOC)
    @EventMethod(eventClass = ComponentEvent.class)
    public JSObject getOnComponentHidden();

    @ScriptFunction
    public void setOnComponentHidden(JSObject aValue);

    public static final String ON_COMPONENT_MOVED_JSDOC = ""
            + "/**\n"
            + " * Component moved event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_COMPONENT_MOVED_JSDOC)
    @EventMethod(eventClass = ComponentEvent.class)
    public JSObject getOnComponentMoved();

    @ScriptFunction
    public void setOnComponentMoved(JSObject aValue);

    public static final String ON_COMPONENT_RESIZED_JSDOC = ""
            + "/**\n"
            + " * Component resized event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_COMPONENT_RESIZED_JSDOC)
    @EventMethod(eventClass = ComponentEvent.class)
    public JSObject getOnComponentResized();

    @ScriptFunction
    public void setOnComponentResized(JSObject aValue);

    public static final String ON_COMPONENT_SHOWN_JSDOC = ""
            + "/**\n"
            + " * Component shown event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_COMPONENT_SHOWN_JSDOC)
    @EventMethod(eventClass = ComponentEvent.class)
    public JSObject getOnComponentShown();

    @ScriptFunction
    public void setOnComponentShown(JSObject aValue);

    public static final String ON_FOCUS_GAINED_JSDOC = ""
            + "/**\n"
            + " * Keyboard focus gained by the component event.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_FOCUS_GAINED_JSDOC)
    @EventMethod(eventClass = FocusEvent.class)
    public JSObject getOnFocusGained();

    @ScriptFunction
    public void setOnFocusGained(JSObject aValue);

    public static final String ON_FOCUS_LOST_JSDOC = ""
            + "/**\n"
            + " * Keyboard focus lost by the component event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_FOCUS_LOST_JSDOC)
    @EventMethod(eventClass = FocusEvent.class)
    public JSObject getOnFocusLost();

    @ScriptFunction
    public void setOnFocusLost(JSObject aValue);

    public static final String ON_KEY_PRESSED_JSDOC = ""
            + "/**\n"
            + " * Key pressed event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_KEY_PRESSED_JSDOC)
    @EventMethod(eventClass = KeyEvent.class)
    public JSObject getOnKeyPressed();

    @ScriptFunction
    public void setOnKeyPressed(JSObject aValue);

    public static final String ON_KEY_RELEASED_JSDOC = ""
            + "/**\n"
            + " * Key released event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_KEY_RELEASED_JSDOC)
    @EventMethod(eventClass = KeyEvent.class)
    public JSObject getOnKeyReleased();

    @ScriptFunction
    public void setOnKeyReleased(JSObject aValue);

    public static final String ON_KEY_TYPED_JSDOC = ""
            + "/**\n"
            + " * Key typed event handler function.\n"
            + " */";

    @ScriptFunction(jsDoc = ON_KEY_TYPED_JSDOC)
    @EventMethod(eventClass = KeyEvent.class)
    public JSObject getOnKeyTyped();

    @ScriptFunction
    public void setOnKeyTyped(JSObject aValue);
}
