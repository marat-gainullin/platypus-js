package com.eas.script;

import jdk.nashorn.api.scripting.AbstractJSObject;

/**
 *
 * @author mg
 */
public abstract class SystemJSCallback extends AbstractJSObject {

    @Override
    public Object getDefaultValue(Class<?> hint) {
        if (String.class.isAssignableFrom(hint)) {
            return super.toString();
        } else {
            return null;
        }
    }

    @Override
    public boolean isFunction() {
        return true;
    }
    
}
