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
        + "* Vertical position constants.\n"
        + "*/")
public class VerticalPosition {

    @ScriptFunction()
    public static int getTOP() {
        return TOP;
    }
    
    @ScriptFunction
    public static int getCENTER() {
        return CENTER;
    }
    
    @ScriptFunction
    public static int getBOTTOM() {
        return BOTTOM;
    }
    
    public static final int TOP = 1;
    public static final int CENTER = 0;
    public static final int BOTTOM = 3;
}
