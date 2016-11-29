/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.components.rt;

import com.eas.script.ScriptFunction;

/**
 *
 * @author Марат
 */
public interface HasEmptyText {

    public static final String EMPTY_TEXT_JSDOC = ""
            + "/**\n"
            + " * The text to be shown when component's value is absent.\n"
            + " */";

    @ScriptFunction(jsDoc = EMPTY_TEXT_JSDOC)
    public String getEmptyText();

    @ScriptFunction
    public void setEmptyText(String aValue);
}
