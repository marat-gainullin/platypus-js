/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.platform;

//import com.eas.designer.application.indexer.BootClassPathProviderImpl;
import com.eas.designer.application.platform.PlatformHomePathException;
import com.eas.designer.application.platform.PlatypusPlatform;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.db.explorer.DatabaseException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class PlatypusPlatformDialog {

    public static boolean showPlatformHomeDialog() {
        PlatformDirectoryPanel platformDirectorySelectPanel = new PlatformDirectoryPanel(PlatypusPlatform.getPlatformHomePath());
        DialogDescriptor selectionDialog = new DialogDescriptor(platformDirectorySelectPanel, NbBundle.getMessage(PlatypusPlatformDialog.class, "CTL_Platypus_Platform_Title")); //NOI18N
        Object result = DialogDisplayer.getDefault().notify(selectionDialog);
        if (DialogDescriptor.OK_OPTION.equals(result)) {
            try {
                PlatypusPlatform.setPlatformHomePath(platformDirectorySelectPanel.getDirectoryPath());
                PlatypusPlatform.registerJdbcDrivers();
                PlatypusPlatform.checkPlatypusJsUpdates();
                return true;
            } catch (PlatformHomePathException ex) {
                Logger.getLogger(PlatypusPlatform.class.getName()).log(Level.WARNING, ex.getMessage());
            } catch (IOException | DatabaseException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return false;
    }
}
