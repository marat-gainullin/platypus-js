/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.script;

/**
 * An exception to notify a publisher absence error.
 * @author vv
 */
public class NoPublisherException extends IllegalStateException {
    
    public NoPublisherException() {
        super("JavaScript API integrity check failed. No publisher function.");
    }
}
