/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.events;

import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class MouseEvent extends Event<java.awt.event.MouseEvent> {

    protected MouseEvent(java.awt.event.MouseEvent aDelegate) {
        super(aDelegate);
    }

    private static final String X_JS_DOC = ""
            + "/**\n"
            + " * X cursor coordinate in component's space.\n"
            + " */";

    @ScriptFunction(jsDoc = X_JS_DOC)
    public int getX() {
        return delegate.getX();
    }

    private static final String Y_JS_DOC = ""
            + "/**\n"
            + " * Y cursor coordinate in component's space.\n"
            + " */";

    @ScriptFunction(jsDoc = Y_JS_DOC)
    public int getY() {
        return delegate.getY();
    }

    private static final String BUTTON_JS_DOC = ""
            + "/**\n"
            + " * Which, if any, of the mouse buttons has changed state.\n"
            + " * Values: 0 - no button, 1 - button 1, 2 - button 2, 3 - button 3.\n"
            + " */";

    @ScriptFunction(jsDoc = BUTTON_JS_DOC)
    public int getButton() {
        return delegate.getButton();
    }

    private static final String CLICK_COUNT_JS_DOC = ""
            + "/**\n"
            + " * The number of mouse clicks associated with this event.\n"
            + " */";

    @ScriptFunction(jsDoc = CLICK_COUNT_JS_DOC)
    public int getClickCount() {
        return delegate.getClickCount();
    }

    private static final String ALT_DOWN_JS_DOC = ""
            + "/**\n"
            + " * Alt key is down on this event.\n"
            + " */";

    @ScriptFunction(jsDoc = ALT_DOWN_JS_DOC)
    public boolean isAltDown() {
        return delegate.isAltDown() || delegate.isAltGraphDown();
    }

    private static final String CONTROL_DOWN_JS_DOC = ""
            + "/**\n"
            + " * Ctrl key is down on this event.\n"
            + " */";

    @ScriptFunction(jsDoc = CONTROL_DOWN_JS_DOC)
    public boolean isControlDown() {
        return delegate.isControlDown();
    }

    private static final String SHIFT_DOWN_JS_DOC = ""
            + "/**\n"
            + " * Shift key is down on this event.\n"
            + " */";

    @ScriptFunction(jsDoc = SHIFT_DOWN_JS_DOC)
    public boolean isShiftDown() {
        return delegate.isShiftDown();
    }

    private static final String META_DOWN_JS_DOC = ""
            + "/**\n"
            + " * Meta key is down on this event.\n"
            + " */";

    @ScriptFunction(jsDoc = META_DOWN_JS_DOC)
    public boolean isMetaDown() {
        return delegate.isMetaDown();
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
