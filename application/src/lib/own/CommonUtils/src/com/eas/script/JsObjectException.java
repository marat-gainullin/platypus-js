/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

/**
 *
 * @author mg
 */
public class JsObjectException extends Exception {

    protected Object data;

    public JsObjectException(Object aData) {
        super(aData != null ? aData.toString() : null);
        data = aData;
    }

    public Object getData() {
        return data;
    }

}
