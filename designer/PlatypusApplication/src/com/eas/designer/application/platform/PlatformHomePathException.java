/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.platform;

/**
 * Exception means what platform path isn't properly configured.
 * 
 * @author vv
 */
public class PlatformHomePathException extends Exception {

    public PlatformHomePathException(String msg) {
        super(msg);
    }
}
