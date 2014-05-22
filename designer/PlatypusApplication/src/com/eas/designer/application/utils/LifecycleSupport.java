/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.application.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import org.openide.LifecycleManager;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class LifecycleSupport {
    
    private final static LifecycleSupport INSTANCE = new LifecycleSupport();
    private final ImageIcon RESTART_ICON = ImageUtilities.loadImageIcon("com/eas/designer/application/utils/restart.png", false);//NOI18N
    
    private LifecycleSupport() {
    }
    
    public static LifecycleSupport getInstance() {
        return INSTANCE;
    }
    
    public void notifyRestartNeeded() {
        ActionListener restartAction = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LifecycleManager.getDefault().markForRestart();
                LifecycleManager.getDefault().exit();
            };
        };
        NotificationDisplayer.getDefault().notify(NbBundle.getMessage(LifecycleSupport.class, "RestartNeeded_Tooltip"),//NOI18N
                RESTART_ICON, 
                NbBundle.getMessage(LifecycleSupport.class, "RestartNeeded_Details"),//NOI18N,
                restartAction,
                NotificationDisplayer.Priority.HIGH,
                NotificationDisplayer.Category.WARNING);
    }
}
