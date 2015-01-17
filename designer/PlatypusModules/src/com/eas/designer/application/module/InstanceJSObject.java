/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module;

import jdk.nashorn.api.scripting.AbstractJSObject;

/**
 *
 * @author mg
 */
public class InstanceJSObject extends AbstractJSObject {

    public static final Object fakeMember = new Object(){

        @Override
        public String toString() {
            return "";
        }
    
    };

    public InstanceJSObject() {
        super();
    }

    @Override
    public boolean hasMember(String name) {
        return true;
    }

    @Override
    public boolean hasSlot(int slot) {
        return false;
    }

    @Override
    public Object getMember(String name) {
        return fakeMember;
    }
}
