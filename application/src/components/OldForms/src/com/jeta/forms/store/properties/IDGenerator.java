/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.store.properties;

/**
 *
 * @author Marat
 */
public class IDGenerator {
    public final static Long rndIDPart = 100000L;
    public static Long genID() {
        return System.currentTimeMillis()*rndIDPart+Math.round(Math.random()*rndIDPart);
    }
    
}