package com.eas.client.cache;

import com.eas.client.AppElementFiles;
import com.sun.nio.file.ExtendedWatchEventModifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class ApplicationSourceIndexer {

    public interface ScanCallback {

        public void fileScanned(String aAppElementId, File aFile);
    }

    protected class FilesWatchDog implements Runnable {

        public FilesWatchDog() {
            super();
        }

        @Override
        public void run() {
            try {
                for (;;) {
                    WatchKey keyTaken = service.take();
                    if (keyTaken.isValid() && watchKey2Directory.containsKey(keyTaken)) {
                        Path directoryPathTaken = watchKey2Directory.get(keyTaken);
                        List<WatchEvent<?>> events = keyTaken.pollEvents();
                        events.stream().forEach((event) -> {
                            try {
                                Path path = (Path) event.context();
                                WatchEvent.Kind<Path> kind = (WatchEvent.Kind<Path>) event.kind();
                                if (path != null && directoryPathTaken != null) {
                                    Path resolvedPath = directoryPathTaken.resolve(path);
                                    if (resolvedPath != null) {
                                        File subject = resolvedPath.toFile();
                                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                                            if (subject.exists()) {
                                                if (subject.isDirectory()) {
                                                    scanSource(subject);
                                                } else {
                                                    addFileToIndex(subject);
                                                }
                                            }
                                        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                                            if (subject.isDirectory() || !subject.getName().contains(".")) {
                                                clearFamiliesByPathPrefix(subject.getPath());
                                            } else {
                                                removeFileFromIndex(subject);
                                            }
                                        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                            if (subject.exists() && !subject.isDirectory()) {
                                                readdFileToIndex(subject);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                Logger.getLogger(ApplicationSourceIndexer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    }
                    boolean resetted = keyTaken.reset();
                    if (!keyTaken.isValid() || !resetted) {
                        keyTaken.cancel();
                        Path directoryPathTaken = watchKey2Directory.remove(keyTaken);
                        if (directoryPathTaken != null) {
                            File subject = directoryPathTaken.toFile();
                            clearFamiliesByPathPrefix(subject.getPath());
                        }
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ApplicationSourceIndexer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClosedWatchServiceException ex1) {
                //no-op
            }
        }
    }
    protected String appPathName;
    protected Thread watchDog;
    protected Map<WatchKey, Path> watchKey2Directory = new HashMap<>();
    protected Map<String, Set<String>> id2Paths = new HashMap<>();
    protected Map<String, String> path2Id = new HashMap<>();
    protected Map<String, AppElementFiles> families = new HashMap<>();
    protected WatchService service;
    protected ScanCallback scanCallback;
    protected boolean autoScan;

    public ApplicationSourceIndexer(String aAppPathName) throws Exception {
        this(aAppPathName, true, null);
    }

    public ApplicationSourceIndexer(String aAppPathName, ScanCallback aScanCallback) throws Exception {
        this(aAppPathName, true, aScanCallback);
    }

    public ApplicationSourceIndexer(String aAppPathName, boolean aAutoScan, ScanCallback aScanCallback) throws Exception {
        super();
        autoScan = aAutoScan;
        appPathName = aAppPathName;
        scanCallback = aScanCallback;
        if (autoScan) {
            File srcDirectory = checkRootDirectory();
            scanSource(srcDirectory);
        }
    }

    private File checkRootDirectory() throws IllegalArgumentException {
        String srcPathName = calcSrcPath();
        File srcDirectory = new File(srcPathName);
        if (!srcDirectory.exists() || !srcDirectory.isDirectory()) {
            throw new IllegalArgumentException(String.format("%s doesn't point to a directory.", srcPathName));
        }
        return srcDirectory;
    }

    public synchronized void watch() throws Exception {
        service = FileSystems.getDefault().newWatchService();
        if (isWindows()) {
            String srcPathName = calcSrcPath();
            Path srcPath = Paths.get(new File(srcPathName).toURI());
            WatchKey wk = srcPath.register(service, new WatchEvent.Kind<?>[]{StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY}, ExtendedWatchEventModifier.FILE_TREE);
            watchKey2Directory.put(wk, srcPath);
        } else {
            File srcDirectory = checkRootDirectory();
            registerOnFolders(srcDirectory);
        }
        watchDog = new Thread(new FilesWatchDog());
        watchDog.setDaemon(true);
        watchDog.start();
    }

    public synchronized void unwatch() throws Exception {
        assert service != null && watchDog != null;
        service.close();
        watchDog.join();
        service = null;
        watchDog = null;
        watchKey2Directory.clear();
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        // windows
        return (os.contains("win"));
    }

    public synchronized void rescan() {
        id2Paths.clear();
        path2Id.clear();
        families.clear();
        File srcDirectory = checkRootDirectory();
        scanSource(srcDirectory);
    }

    private synchronized void scanSource(File aDirectory) {
        assert aDirectory.exists() && aDirectory.isDirectory();
        try {
            Files.walkFileTree(Paths.get(aDirectory.toURI()), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path aFilePath, BasicFileAttributes attrs) throws IOException {
                    try {
                        addFileToIndex(aFilePath.toFile());
                    } catch (Exception ex) {
                        Logger.getLogger(ApplicationSourceIndexer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex) {
            // Files.walkFileTree may fail due to some programs activity
            Logger.getLogger(ApplicationSourceIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized void registerOnFolders(File aDirectory) {
        assert aDirectory.exists() && aDirectory.isDirectory();
        try {
            Files.walkFileTree(Paths.get(aDirectory.toURI()), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path aDirectoryPath, BasicFileAttributes attrs) throws IOException {
                    try {
                        assert !isWindows();
                        WatchKey wk = aDirectoryPath.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
                        watchKey2Directory.put(wk, aDirectoryPath);
                    } catch (Exception ex) {
                        Logger.getLogger(ApplicationSourceIndexer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex) {
            // Files.walkFileTree may fail due to some programs activity
            Logger.getLogger(ApplicationSourceIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String fileNameWithoutExtension(File aFile) {
        String filePath = aFile.getPath();
        int dotIndex = filePath.lastIndexOf('.');
        if (!filePath.isEmpty() && dotIndex != -1) {
            return filePath.substring(0, dotIndex);
        } else {
            return null;
        }
    }

    protected synchronized void readdFileToIndex(File aFile) {
        removeFileFromIndex(aFile);
        addFileToIndex(aFile);
    }

    protected synchronized void addFileToIndex(File aFile) {
        try {
            String familyPath = fileNameWithoutExtension(aFile);
            if (familyPath != null) {
                AppElementFiles family = families.get(familyPath);
                if (family == null) {
                    family = new AppElementFiles();
                    families.put(familyPath, family);
                }
                Integer type1 = family.getAppElementType();
                String id1 = type1 != null ? family.getAppElementId(type1) : null;
                family.addFile(aFile);
                Integer type2 = family.getAppElementType();
                String id2 = type2 != null ? family.getAppElementId(type2) : null;
                // remove old values
                String familyId = path2Id.remove(familyPath);
                Set<String> paths = id2Paths.get(familyId);
                if (paths != null) {
                    paths.remove(familyPath);
                    if (paths.isEmpty()) {
                        id2Paths.remove(familyId);
                    }
                }
                if (id2 != null) {
                    path2Id.put(familyPath, id2);
                    Set<String> newPaths = id2Paths.get(id2);
                    if (newPaths == null) {
                        newPaths = new HashSet<>();
                        id2Paths.put(id2, newPaths);
                    }
                    newPaths.add(familyPath);
                }
                if (scanCallback != null && id2 != null) {
                    for (File f : family.getFiles()) {
                        scanCallback.fileScanned(id2, f);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ApplicationSourceIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected synchronized void removeFileFromIndex(File aFile) {
        try {
            String familyPath = fileNameWithoutExtension(aFile);
            if (familyPath != null) {
                AppElementFiles family = families.get(familyPath);
                if (family != null) {
                    family.removeFile(aFile);
                    Integer type2 = family.getAppElementType();
                    if (type2 == null) {
                        String familyId = path2Id.remove(familyPath);
                        Set<String> paths = id2Paths.get(familyId);
                        if (paths != null) {
                            paths.remove(familyPath);
                            if (paths.isEmpty()) {
                                id2Paths.remove(familyId);
                            }
                        }
                    }
                    if (family.isEmpty()) {
                        families.remove(familyPath);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ApplicationSourceIndexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String calcSrcPath() {
        return appPathName + File.separator + PlatypusFiles.PLATYPUS_PROJECT_APP_ROOT;
    }

    /**
     * Resolves an application element name to a path of local file.
     *
     * @param aName
     * @return
     * @throws Exception
     */
    public synchronized AppElementFiles nameToFiles(String aName) throws Exception {
        if (aName != null) {
            File resource = new File(calcSrcPath() + File.separator + aName.replace('/', File.separatorChar));
            if (resource.exists()) {
                AppElementFiles files = new AppElementFiles();
                files.addFile(resource);
                return files;
            } else {
                Set<String> paths = id2Paths.get(aName);
                if (paths != null && !paths.isEmpty()) {
                    AppElementFiles files = families.get(paths.iterator().next());
                    if (files != null) {
                        return files.copy();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    protected synchronized void clearFamiliesByPathPrefix(String aPathPrefix) {
        for (WatchKey wk : watchKey2Directory.keySet().toArray(new WatchKey[]{})) {
            if (!wk.isValid()) {
                watchKey2Directory.remove(wk);
                wk.cancel();
            }
        }
        for (String familyPath : families.keySet().toArray(new String[]{})) {
            if (familyPath.startsWith(aPathPrefix)) {
                String id = path2Id.remove(familyPath);
                id2Paths.remove(id);
                families.remove(familyPath);
            }
        }
    }
}
