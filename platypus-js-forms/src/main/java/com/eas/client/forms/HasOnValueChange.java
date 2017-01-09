/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public interface HasOnValueChange {
    
    public static final String ON_VALUE_CHANED_JSDOC = ""
            + "/**\n"
            + " * Value change handler.\n"
            + " */";
    
    public JSObject getOnValueChange();
    
    public void setOnValueChange(JSObject aValue);
}
