/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.ui;

import com.google.gwt.dom.client.DataTransfer;

/**
 *
 * @author mg
 */
public class XDataTransfer extends DataTransfer {

    protected XDataTransfer() {
        super();
    }

    public final native void setDropEffect(String aValue) /*-{
        this.dropEffect = aValue;
    }-*/;
    
    public final native String getDropEffect() /*-{
        return this.dropEffect;
    }-*/;
    
    public final native void setEffectAllowed(String aValue) /*-{
        this.effectAllowed = aValue;
    }-*/;
    
}
