/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.platform;

import com.eas.designer.application.platform.PlatypusPlatform;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;

@ActionID(
    category = "File",
id = "com.eas.designer.explorer.platform.CheckPlatformUpdateAction")
@ActionRegistration(
    displayName = "#CTL_Check_Platform_Update_Title")
@ActionReference(path = "Menu/Tools", position = 806, separatorBefore = 802, separatorAfter = 807)
public final class CheckPlatformUpdateAction implements ActionListener {
    
    @Override
    public void actionPerformed(ActionEvent e) {
        PlatypusPlatform.checkPlatypusJsUpdates();
    }
}
