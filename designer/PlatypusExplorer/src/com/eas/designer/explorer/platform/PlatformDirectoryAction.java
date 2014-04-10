/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.platform;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;

@ActionID(
    category = "File",
id = "com.eas.designer.explorer.platform.PlatformDirectoryAction")
@ActionRegistration(
    displayName = "#CTL_Platypus_Platform_Title")
@ActionReference(path = "Menu/Tools", position = 805, separatorBefore = 802, separatorAfter = 807)
public final class PlatformDirectoryAction implements ActionListener {
    
    @Override
    public void actionPerformed(ActionEvent e) {
        PlatypusPlatformDialog.showPlatformHomeDialog();
    }
}
