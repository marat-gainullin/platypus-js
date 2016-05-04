/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
}
