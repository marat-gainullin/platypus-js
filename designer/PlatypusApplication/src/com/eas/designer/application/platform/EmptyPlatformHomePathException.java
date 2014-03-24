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
public class EmptyPlatformHomePathException extends Exception {

    public EmptyPlatformHomePathException() {
        super("Platform home directory is not set.");//NOI18N
    }
}
