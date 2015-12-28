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
public class KeyEvent extends Event<java.awt.event.KeyEvent> {

    protected KeyEvent(java.awt.event.KeyEvent aDelegate) {
        super(aDelegate);
    }

    private static final String KEY_JS_DOC = ""
            + "/**\n"
            + " * Key code associated with this event.\n"
            + " */";

    @ScriptFunction(jsDoc = KEY_JS_DOC)
    public int getKey() {
        return delegate.getKeyCode();
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

    private static final String KEY_CHAR_JS_DOC = ""
            + "/**\n"
            + " * Char associated with this event.\n"
            + " */";

    @ScriptFunction(jsDoc = KEY_CHAR_JS_DOC)
    public String getChar() {
        char res = delegate.getKeyChar();
        return res == java.awt.event.KeyEvent.CHAR_UNDEFINED ? "" : Character.toString(res);
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
