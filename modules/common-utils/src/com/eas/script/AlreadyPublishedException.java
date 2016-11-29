/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.script;

/**
 * An exception to notify an attempt to set a publisher for an object more than once.
 * @author vv
 */
public class AlreadyPublishedException extends IllegalStateException {
    
    public AlreadyPublishedException() {
        super("API object has to be published only once!");
    }
}
