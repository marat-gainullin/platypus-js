/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.open.support.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Marat
 */
public class VirtualProp extends Object{
    
    Object prop = null;
    Scriptable wrap = null;
            
    VirtualProp(Object aprop, Scriptable awrap){
        super();
        prop = aprop;
        wrap = awrap;
    }

    public Scriptable getScriptable(Scriptable parent) {
        if(wrap == null)
           wrap = (Scriptable)Context.javaToJS(prop, parent); 
        return wrap;
    }
}

