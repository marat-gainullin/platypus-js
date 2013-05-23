/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.platform;

import com.eas.util.FileUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.prefs.Preferences;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author vv
 */
public class PlatypusPlatform {

    public static final String HOME_DIRECTORY_PATH_KEY = "platypus.platform.home"; //NOI18N
    public static final String BIN_DIRECTORY_NAME = "bin"; //NOI18N
    public static final String LIB_DIRECTORY_NAME = "lib"; //NOI18N
    public static final String JAR_FILE_EXTENSION = "jar"; //NOI18N
    public static final String THIRDPARTY_LIB_DIRECTORY_NAME = "thirdparty"; //NOI18N
    
    private static Map<String, File> jarsCache = new HashMap<>();

    /**
     * Gets Platypus platform home directory.
     *
     * @return home directory or null if this path isn't defined
     */
    public static String getPlatformHomePath() {
        Preferences node = NbPreferences.forModule(PlatypusPlatform.class);
        return node.get(PlatypusPlatform.HOME_DIRECTORY_PATH_KEY, null);
    }

    /**
     * Sets Platypus platform home directory.
     *
     * @param path home directory path
     */
    public static void setPlatformHomePath(String path) {
        Preferences node = NbPreferences.forModule(PlatypusPlatform.class);
        node.put(PlatypusPlatform.HOME_DIRECTORY_PATH_KEY, path);
        jarsCache.clear();
    }

    /**
     * Platform's bin directory.
     *
     * @return bin directory path
     * @throws EmptyPlatformHomePathException if platform isn't properly
     * configured
     */
    public static File getPlatformBinDirectory() throws EmptyPlatformHomePathException {
        File platformBinDir = new File(getPlatformHomeDir(), BIN_DIRECTORY_NAME);
        if (!platformBinDir.exists() || !platformBinDir.isDirectory()) {
            throw new IllegalStateException("Platform executables home not exists or not a directory.");
        }
        return platformBinDir;
    }

    /**
     * Platform's bin directory.
     *
     * @return bin directory path
     * @throws EmptyPlatformHomePathException if platform isn't properly
     * configured
     */
    public static File getPlatformLibDirectory() throws EmptyPlatformHomePathException {
        File platformLibDir = new File(getPlatformHomeDir(), LIB_DIRECTORY_NAME);
        if (!platformLibDir.exists() || !platformLibDir.isDirectory()) {
            throw new IllegalStateException("Platform executables home not exists or not a directory.");
        }
        return platformLibDir;
    }
    
    /**
     * Platform's third party subdirectory.
     *
     * @return lib directory path
     * @throws EmptyPlatformHomePathException if platform isn't properly
     * configured
     */
    public static File getThirdpartyLibDirectory() throws EmptyPlatformHomePathException {
        File tpLibDir = new File(getPlatformLibDirectory(), THIRDPARTY_LIB_DIRECTORY_NAME);
        if (!tpLibDir.exists() || !tpLibDir.isDirectory()) {
            throw new IllegalStateException("Platform's third party libs home not exists or not a directory.");
        }
        return tpLibDir;
    }

    /**
     * Finds jar file in the platform's lib directory for provided class name.
     *
     * @param className full class name to find in jars
     * @return jar file or null if jar is not found
     */
    public synchronized static File findThirdpartyJar(String className) throws EmptyPlatformHomePathException, IOException {
        File jarFile = jarsCache.get(className);
        if (jarFile == null) {
            jarFile = findJar(FileUtil.toFileObject(getThirdpartyLibDirectory()), className);
            jarsCache.put(className, jarFile);
        }
        return jarFile;
    }
    
    private static File findJar(FileObject dir, String className) throws IOException {
        List<File> files = new ArrayList<>();
        Enumeration<? extends FileObject> e = dir.getChildren(true);
        while (e.hasMoreElements()) {
            FileObject fo = e.nextElement();
            if (JAR_FILE_EXTENSION.equalsIgnoreCase(fo.getExt())) {
                files.add(FileUtil.toFile(fo));
            }
        }
        return findClassJar(files, className);
    }

    private static File findClassJar(Collection<File> classpath, String className) throws IOException {
        String classFilePath = className.replace('.', '/') + ".class"; // NOI18N
        for (File file : classpath) {
            if (file.isFile() && JAR_FILE_EXTENSION.equalsIgnoreCase(FileUtils.getFileExtension(file))) {
                try (JarFile jf = new JarFile(file)) {
                    Enumeration entries = jf.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = (JarEntry) entries.nextElement();
                        if (classFilePath.equals(entry.getName())) {
                            return file;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static File getPlatformHomeDir() throws EmptyPlatformHomePathException {
        if (PlatypusPlatform.getPlatformHomePath() == null || PlatypusPlatform.getPlatformHomePath().isEmpty()) {
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
