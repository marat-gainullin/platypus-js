/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.script.ScriptUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class PropertyChangeListenerJSAdapter implements PropertyChangeListener {

    protected JSObject handler;

    public PropertyChangeListenerJSAdapter(JSObject aHandler) {
        super();
        handler = aHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        handler.call(null, new Object[]{evt});
    }

}
