/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api;

import static com.eas.client.forms.api.VerticalPosition.TOP;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;

/**
 *
 * @author mg
 */
@ScriptObj(jsDoc = "/**\n"
        + "* Horizontal position constants.\n"
        + "*/")
public class HorizontalPosition {

    @ScriptFunction
    public static int getLEFT() {
        return LEFT;
    }
    
    @ScriptFunction
    public static int getCENTER() {
        return CENTER;
    }
    
    @ScriptFunction
    public static int getRIGHT() {
        return RIGHT;
    }
    
    public static final int LEFT = 2;
    public static final int CENTER = 0;
    public static final int RIGHT = 4;
}