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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.db.explorer.JDBCDriver;
import org.netbeans.api.db.explorer.JDBCDriverManager;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;

/**
 * A class managing access to the platform's runtime installation.
 *
 * @author vv
 */
public class PlatypusPlatform {

    public static final String PLATYPUS_DIR_NAME = "Platypus"; //NOI18N
    public static final String PLATYPUS_FILE_NAME = "platform"; //NOI18N
    public static final String PLATFORM_HOME_PATH_ATTR_NAME = "path"; //NOI18N
    public static final String BIN_DIRECTORY_NAME = "bin"; //NOI18N
    public static final String LIB_DIRECTORY_NAME = "lib"; //NOI18N
    public static final String UPDATES_DIRECTORY_NAME = "updates"; //NOI18N
    public static final String JS_API_DIRECTORY_NAME = "api"; //NOI18N
    public static final String JAR_FILE_EXTENSION = "jar"; //NOI18N
    public static final String THIRDPARTY_LIB_DIRECTORY_NAME = "thirdparty"; //NOI18N
    private static final String PLATFORM_ERROR_MSG = "Platform executables home does not exist or not a directory.";
    private static final String LINUX_UPDATE_EXECUTABLE = "update.sh";
    private static final String MAC_UPDATE_EXECUTABLE = "update-mac.sh";
    private static final String WINDOWS_UPDATE_EXECUTABLE = "update.exe";

    private static final Map<String, File> jarsCache = new HashMap<>();

    static {
        String platformPath = getPlatformHomePath();
        if (platformPath != null
                && new File(platformPath).exists()
                && new File(platformPath).isDirectory()) {

            String executableName;

            if (Utilities.isWindows()) {
                executableName = WINDOWS_UPDATE_EXECUTABLE;
            } else if (Utilities.isMac()) {
                executableName = MAC_UPDATE_EXECUTABLE;
            } else {
                executableName = LINUX_UPDATE_EXECUTABLE;
            }

            String command;
            String updaterPath = platformPath + File.separator + UPDATES_DIRECTORY_NAME + File.separator + executableName;
            if (Utilities.isWindows()) {
                command = "cmd.exe /C " + "\"" + updaterPath + "\"";
            } else {
                command = updaterPath;
            }

            try {
                Runtime.getRuntime().exec(command);
            } catch (IOException ex) {
                Logger.getLogger(PlatypusPlatform.class.getName())
                        .log(Level.SEVERE, null, ex); // NOI18N
            }
        } else {
            Logger.getLogger(PlatypusPlatform.class.getName())
                    .log(Level.INFO, PLATFORM_ERROR_MSG); // NOI18N
        }
    }

    /**
     * Gets the platform home directory.
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
     * Sets the platform home directory.
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
     * The platform's main jars directory.
     *
     * @return bin directory path
     * @throws PlatformHomePathException if the platform isn't properly
     * configured
     */
    public static File getPlatformBinDirectory() throws PlatformHomePathException {
        File platformBinDir = new File(getPlatformHomeDir(), BIN_DIRECTORY_NAME);
        if (!platformBinDir.exists() || !platformBinDir.isDirectory()) {
            throw new PlatformHomePathException(PLATFORM_ERROR_MSG);
        }
        return platformBinDir;
    }

    /**
     * The platform's main jars directory.
     *
     * @return bin directory path
     * @throws PlatformHomePathException if the platform isn't properly
     * configured
     */
    public static File getPlatformLibDirectory() throws PlatformHomePathException {
        return getPlatformLibDirectory(getPlatformHomeDir());
    }

    /**
     * The platform's lib jars directory.
     *
     * @param platfromHomeDir
     * @return bin directory path
     * @throws PlatformHomePathException if the platform isn't properly
     * configured
     */
    public static File getPlatformLibDirectory(File platfromHomeDir) throws PlatformHomePathException {
        File platformLibDir = new File(platfromHomeDir, LIB_DIRECTORY_NAME);
        if (!platformLibDir.exists() || !platformLibDir.isDirectory()) {
            throw new PlatformHomePathException(PLATFORM_ERROR_MSG);
        }
        return platformLibDir;
    }

    /**
     * Gets the platform's JavaScript API directory.
     *
     * @return bin directory path
     * @throws PlatformHomePathException if the platform isn't properly
     * configured
     */
    public static File getPlatformApiDirectory() throws PlatformHomePathException {
        return PlatypusPlatform.getPlatformApiDirectory(getPlatformHomeDir());
    }

    /**
     * Gets the platform's JavaScript API directory.
     *
     * @param platfromHomeDir
     * @return bin directory path
     * @throws PlatformHomePathException if the platform isn't properly
     * configured
     */
    public static File getPlatformApiDirectory(File platfromHomeDir) throws PlatformHomePathException {
        File platformApiDir = new File(platfromHomeDir, JS_API_DIRECTORY_NAME);
        if (!platformApiDir.exists() || !platformApiDir.isDirectory()) {
            throw new PlatformHomePathException(PLATFORM_ERROR_MSG);
        }
        return platformApiDir;
    }

    /**
     * The platform's lib jars directory.
     *
     * @return if the platform isn't properly configured
     * @throws PlatformHomePathException if the platform isn't properly
     * configured
     */
    public static File getThirdpartyLibDirectory() throws PlatformHomePathException {
        return getThirdpartyLibDirectory(getPlatformLibDirectory());
    }

    /**
     * Platform's third party jars subdirectory.
     *
     * @param platformLibDirectory
     * @return lib directory path
     * @throws PlatformHomePathException if platform isn't properly configured
     */
    public static File getThirdpartyLibDirectory(File platformLibDirectory) throws PlatformHomePathException {
        File tpLibDir = new File(platformLibDirectory, THIRDPARTY_LIB_DIRECTORY_NAME);
        if (!tpLibDir.exists() || !tpLibDir.isDirectory()) {
            throw new PlatformHomePathException(PLATFORM_ERROR_MSG);
        }
        return tpLibDir;
    }

    /**
     * Finds a jar file for some class.
     *
     * @param className a class name to find
     * @return a jar file
     * @throws PlatformHomePathException when the platform is not found or not
     * correct
     * @throws IOException on some IO problems
     */
    public synchronized static File findThirdpartyJar(String className) throws PlatformHomePathException, IOException {
        return findThirdpartyJar(getPlatformHomeDir(), className);
    }

    /**
     * Finds jar file in the platform's lib directory for provided class name.
     *
     * @param platfromHomeDir
     * @param className full class name to find in jars
     * @return jar file or null if jar is not found
     * @throws PlatformHomePathException when the platform is not found or not
     * correct
     * @throws java.io.IOException on some IO problems
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

    /**
     * Registers the JDBC drivers provided with the platform in the IDE driver's
     * manager.
     *
     * @throws PlatformHomePathException when the platform is not found or not
     * correct
     * @throws IOException on some IO problems
     * @throws DatabaseException on some database problems
     */
    public static void registerJdbcDrivers() throws PlatformHomePathException, IOException, DatabaseException {
        registerJdbcDrivers(getPlatformHomeDir());
    }

    /**
     * Registers the JDBC drivers provided with the platform in the IDE driver's
     * manager.
     *
     * @param platfromHomeDir a platform's installation directory
     * @return a list JDBC of drivers
     * @throws PlatformHomePathException when the platform is not found or not
     * correct
     * @throws IOException on some IO problems
     * @throws DatabaseException on some database problems
     */
    public static List<JDBCDriver> registerJdbcDrivers(File platfromHomeDir) throws PlatformHomePathException, IOException, DatabaseException {
        List<JDBCDriver> drivers = new ArrayList<>();
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
                JDBCDriver drv = JDBCDriver.create(databaseServer.name, databaseServer.name, databaseServer.jdbcClassName, urls.toArray(new URL[0]));
                JDBCDriverManager.getDefault().addDriver(drv);
                drivers.add(drv);
            }
        }
        return Collections.unmodifiableList(drivers);
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
            throw new PlatformHomePathException(PLATFORM_ERROR_MSG);
        }
        return platformHomeDir;
    }
}
