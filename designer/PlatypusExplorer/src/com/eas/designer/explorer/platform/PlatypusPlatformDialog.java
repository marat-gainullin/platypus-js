/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.platform;

import com.eas.designer.application.platform.EmptyPlatformHomePathException;
import com.eas.designer.application.platform.PlatypusPlatform;
import static com.eas.designer.application.platform.PlatypusPlatform.findThirdpartyJar;
import static com.eas.designer.application.platform.PlatypusPlatform.setPlatformHomePath;
import com.eas.designer.explorer.utils.DatabaseServerType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class PlatypusPlatformDialog {

    public static boolean showPlatformHomeDialog() {
        PlatformDirectoryPanel platformDirectorySelectPanel = new PlatformDirectoryPanel(PlatypusPlatform.getPlatformHomePath());
        DialogDescriptor selectionDialog = new DialogDescriptor(platformDirectorySelectPanel, NbBundle.getMessage(PlatypusPlatform.class, "CTL_Platypus_Platform_Title"), true, new DialogAcionListener(platformDirectorySelectPanel)); //NOI18N
        Object result = DialogDisplayer.getDefault().notify(selectionDialog);
        if (DialogDescriptor.OK_OPTION.equals(result)) {
            try {
                registerJdbcDrivers();
                return true;
            } catch (EmptyPlatformHomePathException ex) {
                Logger.getLogger(PlatypusPlatform.class.getName()).log(Level.WARNING, "Platform path error.");
            } catch (IOException | DatabaseException ex) {
                ErrorManager.getDefault().notify(ex);
            }
        }
        return false;
    }

    private static void registerJdbcDrivers() throws EmptyPlatformHomePathException, IOException, DatabaseException {
        for (DatabaseServerType databaseServer : DatabaseServerType.values()) {
            File mainJar = findThirdpartyJar(databaseServer.jdbcClassName);
            if (mainJar != null) {
                FileObject mainJarFo = FileUtil.toFileObject(mainJar);
                Enumeration<? extends FileObject> jdbcDriversEnumeration = mainJarFo.getParent().getChildren(false);
                List<URL> urls = new ArrayList<>();
                while (jdbcDriversEnumeration.hasMoreElements()) {
                    FileObject fo = jdbcDriversEnumeration.nextElement();
                    if (PlatypusPlatform.JAR_FILE_EXTENSION.equalsIgnoreCase(fo.getExt())) {
                        urls.add(fo.toURL());
                    }
                }
                for (JDBCDriver oldDriver : JDBCDriverManager.getDefault().getDrivers(databaseServer.jdbcClassName)) {
                    if (oldDriver.getName().equals(databaseServer.name)) {
                        JDBCDriverManager.getDefault().removeDriver(oldDriver);
                    }
                }
                JDBCDriverManager.getDefault().addDriver(JDBCDriver.create(databaseServer.name, databaseServer.name, databaseServer.jdbcClassName, urls.toArray(new URL[0])));
            }
        }
    }

    static class DialogAcionListener implements ActionListener {

        PlatformDirectoryPanel panel;

        DialogAcionListener(PlatformDirectoryPanel aPanel) {
            panel = aPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == DialogDescriptor.OK_OPTION) {
                setPlatformHomePath(panel.getDirectoryPath());
            }
        }
    }
}
