/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.gui;

import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;

/**
 *
 * @author mg
 */
@ScriptObj(jsDoc = "/**\n"
        + "* Font decoration attributes object.\n"
        + "*/")
public class FontStyle {

    @ScriptFunction
    public static int getNORMAL() {
        return NORMAL;
    }
    
    @ScriptFunction
    public static int getBOLD() {
        return BOLD;
    }
    
    @ScriptFunction
    public static int getITALIC() {
        return ITALIC;
    }
    
    @ScriptFunction
    public static int getBOLD_ITALIC() {
        return BOLD_ITALIC;
    }
    
    public static final int NORMAL = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int BOLD_ITALIC = 3;
}
