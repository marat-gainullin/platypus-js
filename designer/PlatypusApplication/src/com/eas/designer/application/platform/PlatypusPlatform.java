/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.platform;

import com.eas.util.FileUtils;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author vv
 */
public class PlatypusPlatform {

    public static final String PLATYPUS_DIR_NAME = "Platypus"; //NOI18N
    public static final String PLATYPUS_FILE_NAME = "platform"; //NOI18N
    public static final String PLATFORM_HOME_PATH_ATTR_NAME = "path"; //NOI18N
    public static final String BIN_DIRECTORY_NAME = "bin"; //NOI18N
    public static final String LIB_DIRECTORY_NAME = "lib"; //NOI18N
    public static final String JAR_FILE_EXTENSION = "jar"; //NOI18N
    public static final String THIRDPARTY_LIB_DIRECTORY_NAME = "thirdparty"; //NOI18N

    public static final String[] JDBC_CLASSES = {"org.h2.jdbcx.JdbcDataSource"}; //NOI18N

    private static final Map<String, File> jarsCache = new HashMap<>();

    /**
     * Gets Platypus platform home directory.
     *
     * @return home directory or null if this path isn't defined
     */
    public static String getPlatformHomePath() {
        FileObject platypusDir = FileUtil.getConfigFile(PLATYPUS_DIR_NAME);
        if (platypusDir == null) {
            Logger.getLogger(PlatypusPlatform.class.getName())
                    .log(Level.INFO, "The config/{0} folder does not exist.", PLATYPUS_DIR_NAME); // NOI18N
            return null;
        }
        FileObject platformFo = platypusDir.getFileObject(PLATYPUS_FILE_NAME);
        if (platformFo == null) {
            Logger.getLogger(PlatypusPlatform.class.getName())
                    .log(Level.INFO, "The {0} configuration file does not exist.", PLATYPUS_FILE_NAME); // NOI18N
            return null;
        }
        return platformFo.getAttribute(PLATFORM_HOME_PATH_ATTR_NAME) != null ? platformFo.getAttribute(PLATFORM_HOME_PATH_ATTR_NAME).toString() : null;
    }

    /**
     * Sets Platypus platform home directory.
     *
     * @param path home directory path
     * @return <code>true</code> if the value have stored successfully
     */
    public static boolean setPlatformHomePath(String path) {
        boolean success;
        try {
            FileObject platypusDir = FileUtil.getConfigFile(PLATYPUS_DIR_NAME);
            if (platypusDir == null) {
                platypusDir = FileUtil.getConfigRoot().createFolder(PLATYPUS_DIR_NAME);
            }
            assert platypusDir != null;
            FileObject platformFo = platypusDir.getFileObject(PLATYPUS_FILE_NAME);
            if (platformFo == null) {
                platformFo = platypusDir.createData(PLATYPUS_FILE_NAME);
            }
            platformFo.setAttribute(PLATFORM_HOME_PATH_ATTR_NAME, path);
            success = true;
        } catch (IOException ex) {
            success = false;
            ErrorManager.getDefault().notify(ex);
        }
        jarsCache.clear();
        return success;
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
            throw new IllegalStateException("Platform executables home does not exist or not a directory.");
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
            throw new IllegalStateException("Platform executables home does not exist or not a directory.");
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
}
