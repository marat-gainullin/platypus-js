/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.platform;

import com.eas.designer.explorer.project.ProjectRunner;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author vv
 */
public class PlatypusPlatform {
    public static final String HOME_DIRECTORY_PATH_KEY = "platypus.platform.home"; //NOI18N
    private static final String BIN_DIRECTORY_NAME = "bin"; //NOI18N
    private static final String LIB_DIRECTORY_NAME = "lib"; //NOI18N
    private static final String THIRDPARTY_LIB_DIRECTORY_NAME = "thirdparty"; //NOI18N
    
    public static String getPlatformHomePath() {
        Preferences node = NbPreferences.forModule(PlatypusPlatform.class);
        return node.get(PlatypusPlatform.HOME_DIRECTORY_PATH_KEY, "");
    }
    
    public static void setPlatformHomePath(String path) {
        Preferences node = NbPreferences.forModule(PlatypusPlatform.class);
        node.put(PlatypusPlatform.HOME_DIRECTORY_PATH_KEY, path);
    }
    
    public static File getPlatformBinDirectory() throws EmptyPlatformHomePathException {
        File platformBinDir = new File(getPlatformHomeDir(), BIN_DIRECTORY_NAME);
        if (!platformBinDir.exists() || !platformBinDir.isDirectory()) {
            throw new IllegalStateException("Platform executables home not exists or not a directory.");
        }
        return platformBinDir;
    }
    
    public static File getThirdpartyLibDirectory() throws EmptyPlatformHomePathException {
        File platformLibDir = new File(getPlatformHomeDir(), LIB_DIRECTORY_NAME);
        if (!platformLibDir.exists() || !platformLibDir.isDirectory()) {
            throw new IllegalStateException("Platform's libs home not exists or not a directory.");
        }
        File tpLibDir = new File(platformLibDir, THIRDPARTY_LIB_DIRECTORY_NAME);
        if (!tpLibDir.exists() || !tpLibDir.isDirectory()) {
            throw new IllegalStateException("Platform's third party libs home not exists or not a directory.");
        }
        return tpLibDir;
    }
    
    private static File getPlatformHomeDir() throws EmptyPlatformHomePathException {
        assert PlatypusPlatform.getPlatformHomePath() != null;
        if (PlatypusPlatform.getPlatformHomePath().isEmpty()) {
            throw new EmptyPlatformHomePathException();
        }
        File platformHomeDir = new File(PlatypusPlatform.getPlatformHomePath());
        if (!platformHomeDir.exists() || !platformHomeDir.isDirectory()) {
            throw new IllegalStateException("Platform home not exists or not a directory.");
        }
        return platformHomeDir;
    }
    
    public static boolean showPlatformHomeDialog() {
        PlatformDirectoryPanel platformDirectorySelectPanel = new PlatformDirectoryPanel(PlatypusPlatform.getPlatformHomePath());
        DialogDescriptor selectionDialog = new DialogDescriptor(platformDirectorySelectPanel, NbBundle.getMessage(PlatypusPlatform.class, "CTL_Platypus_Platform_Title"), true, new PlatypusPlatform.DialogAcionListener(platformDirectorySelectPanel)); //NOI18N
        return DialogDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(selectionDialog));
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
