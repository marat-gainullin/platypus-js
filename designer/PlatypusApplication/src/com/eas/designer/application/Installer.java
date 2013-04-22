/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application;

import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {
    
    private static final String ALERT_LOG_MIN_LEVEL = "950"; //NOI18N
    private static final String ALERT_LOG_MIN_LEVEL_PROPERTY_NAME = "netbeans.exception.alert.min.level"; //NOI18N
    private static final String REPORT_LOG_MIN_LEVEL_PROPERTY_NAME = "netbeans.exception.report.min.level"; //NOI18N
   
    @Override
    public void restored() {
        //Disable Netbeans alert dialog for WARNING level exceptions but enable it for SEVERE level
        System.setProperty(ALERT_LOG_MIN_LEVEL_PROPERTY_NAME, ALERT_LOG_MIN_LEVEL);
        System.setProperty(REPORT_LOG_MIN_LEVEL_PROPERTY_NAME, ALERT_LOG_MIN_LEVEL);
    }
}
