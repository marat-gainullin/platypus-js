/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;

/**
 *
 * @author mg
 */
@ScriptObj(jsDoc = "/**\n"
        + "* Orientation types constants.\n"
        + "*/")
public class Orientation {

    @ScriptFunction
    public static int getHORIZONTAL() {
        return HORIZONTAL;
    }
    
    @ScriptFunction
    public static int getVERTICAL() {
        return VERTICAL;
    }
    
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
}
