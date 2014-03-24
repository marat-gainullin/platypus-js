/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.platform;

import org.openide.util.NbBundle;

/**
 * Exception means what platform path isn't properly configured.
 * 
 * @author vv
 */
public class EmptyPlatformHomePathException extends Exception {

    public EmptyPlatformHomePathException() {
        super(NbBundle.getMessage(EmptyPlatformHomePathException.class, "LBL_Platform_Home_Not_Set"));
    }
}
