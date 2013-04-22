/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.platform;

import com.eas.designer.explorer.actions.GotoAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

@ActionID(
    category = "File",
id = "com.eas.designer.explorer.platform.PlatformDirectoryAction")
@ActionRegistration(
    displayName = "#CTL_PlatformDirectoryAction")
@ActionReference(path = "Menu/Tools", position = 805, separatorBefore = 802, separatorAfter = 807)
@Messages("CTL_PlatformDirectoryAction=Platypus Platform")
public final class PlatformDirectoryAction implements ActionListener {
    
    @Override
    public void actionPerformed(ActionEvent e) {
        PlatypusPlatform.showPlatformHomeDialog();
    }
}
