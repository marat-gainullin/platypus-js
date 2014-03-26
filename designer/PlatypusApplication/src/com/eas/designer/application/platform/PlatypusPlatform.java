/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.platform;

import com.eas.designer.application.utils.DatabaseServerType;
import com.eas.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
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
     * @throws PlatformHomePathException if platform isn't properly
     * configured
     */
    public static File getPlatformBinDirectory() throws PlatformHomePathException {
        File platformBinDir = new File(getPlatformHomeDir(), BIN_DIRECTORY_NAME);
        if (!platformBinDir.exists() || !platformBinDir.isDirectory()) {
            throw new IllegalStateException("Platform executables home does not exist or not a directory.");
        }
        return platformBinDir;
    }

    public static File getPlatformLibDirectory() throws PlatformHomePathException {
        return getPlatformLibDirectory(getPlatformHomeDir());
    }

    /**
     * Platform's bin directory.
     *
     * @param platfromHomeDir
     * @return bin directory path
     * @throws PlatformHomePathException if platform isn't properly
     * configured
     */
    public static File getPlatformLibDirectory(File platfromHomeDir) throws PlatformHomePathException {
        File platformLibDir = new File(platfromHomeDir, LIB_DIRECTORY_NAME);
        if (!platformLibDir.exists() || !platformLibDir.isDirectory()) {
            throw new IllegalStateException("Platform executables home does not exist or not a directory.");
        }
        return platformLibDir;
    }

    public static File getThirdpartyLibDirectory() throws PlatformHomePathException {
        return getThirdpartyLibDirectory(getPlatformLibDirectory());
    }

    /**
     * Platform's third party subdirectory.
     *
     * @param platformLibDirectory
     * @return lib directory path
     * @throws PlatformHomePathException if platform isn't properly
     * configured
     */
    public static File getThirdpartyLibDirectory(File platformLibDirectory) throws PlatformHomePathException {
        File tpLibDir = new File(platformLibDirectory, THIRDPARTY_LIB_DIRECTORY_NAME);
        if (!tpLibDir.exists() || !tpLibDir.isDirectory()) {
            throw new IllegalStateException("Platform's third party libs home not exists or not a directory.");
        }
        return tpLibDir;
    }

    public synchronized static File findThirdpartyJar(String className) throws PlatformHomePathException, IOException {
        return findThirdpartyJar(getPlatformHomeDir(), className);
    }

    /**
     * Finds jar file in the platform's lib directory for provided class name.
     *
     * @param platfromHomeDir
     * @param className full class name to find in jars
     * @return jar file or null if jar is not found
     * @throws
     * com.eas.designer.application.platform.PlatformHomePathException
     * @throws java.io.IOException
     */
    public synchronized static File findThirdpartyJar(File platfromHomeDir, String className) throws PlatformHomePathException, IOException {
        File jarFile = jarsCache.get(className);
        if (jarFile == null) {
            File thirdpartyLibDirectory = getThirdpartyLibDirectory(getPlatformLibDirectory(platfromHomeDir));
            jarFile = findJar(thirdpartyLibDirectory, className);
            jarsCache.put(className, jarFile);
        }
        return jarFile;
    }

    public static void registerJdbcDrivers() throws PlatformHomePathException, IOException, DatabaseException {
        registerJdbcDrivers(getPlatformHomeDir());
    }

    public static void registerJdbcDrivers(File platfromHomeDir) throws PlatformHomePathException, IOException, DatabaseException {
        for (DatabaseServerType databaseServer : DatabaseServerType.values()) {
            File mainJar = findThirdpartyJar(platfromHomeDir, databaseServer.jdbcClassName);
            if (mainJar != null) {
                //add urls for the *.jar files in the same directory
                List<URL> urls = new ArrayList<>();
                for (File f : mainJar.getParentFile().listFiles()) {
                    if (JAR_FILE_EXTENSION.equalsIgnoreCase(FileUtils.getFileExtension(f))) {
                        urls.add(f.toURI().toURL());
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

    private static File findJar(File dir, String className) throws IOException {
        final List<File> files = new ArrayList<>();
        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                File f = file.toFile();
                if (JAR_FILE_EXTENSION.equalsIgnoreCase(FileUtils.getFileExtension(f))) {
                    files.add(f);
                }
                return FileVisitResult.CONTINUE;
            }

        });
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

    private static File getPlatformHomeDir() throws PlatformHomePathException {
        if (PlatypusPlatform.getPlatformHomePath() == null || PlatypusPlatform.getPlatformHomePath().isEmpty()) {
            throw new PlatformHomePathException("Platform home directory is not set.");
        }
        File platformHomeDir = new File(PlatypusPlatform.getPlatformHomePath());
        if (!platformHomeDir.exists() || !platformHomeDir.isDirectory()) {
            throw new PlatformHomePathException("Platform home not exists or not a directory.");
        }
        return platformHomeDir;
    }
}
