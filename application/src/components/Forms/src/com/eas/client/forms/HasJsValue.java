/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

/**
 *
 * @author mg
 */
public interface HasJsValue {

    public static final String JS_VALUE_JSDOC = ""
            + "/**\n"
            + " * Widget's value.\n"
            + " */";

    public Object getJsValue();

    public void setJsValue(Object aValue);
}
