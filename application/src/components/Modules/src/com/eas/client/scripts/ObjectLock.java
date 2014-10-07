/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

/**
 *
 * @author mg
 */
public class ObjectLock {

    protected String name;

    public ObjectLock(String aName) {
        super();
        name = aName;
    }

    @Override
    public String toString() {
        return "Lock object for " + name + " instance (" + super.toString() + ").";
    }

}
