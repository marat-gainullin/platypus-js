/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.metadata.ApplicationElement;
import com.eas.util.FileUtils;
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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 *
 * @author mg
 */
public class FilesAppCache extends AppElementsCache<Client> {

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
                        for (WatchEvent<?> event : events) {
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
                                Logger.getLogger(FilesAppCache.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
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
                Logger.getLogger(FilesAppCache.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClosedWatchServiceException ex1) {
                //no-op
            }
        }
    }
    protected boolean secondCacheEnabled;//WARNING!!! If you whant to set this flag to true,
    //you have to review server code, that translates an application element to clients.
    //That code shouldn't cache application elements by itself, but in file mode
    //that code utilizes the AppElementsCache code, rather than do things directly.
    //Thus, if the flag will have true value, it will lead to double client and server side caching.
    //It very harmful and you should clearily think before changing this flag.
    //Subjects to review:
    // - FilteredAppElementRequestHandler (PlatypusHttpServlet),
    // - IsAppElementActualRequestHandler (Server),
    // - AppElementRequestHandler (Server).
    protected String srcPathName;
    protected Thread watchDog;
    protected Map<WatchKey, Path> watchKey2Directory = new HashMap<>();
    protected Map<String, String> id2Path = new HashMap<>();
    protected Map<String, String> path2Id = new HashMap<>();
    protected Map<String, AppElementFiles> families = new HashMap<>();
    protected WatchService service;

    public FilesAppCache(String aSrcPathName) throws Exception {
        super(null);
        srcPathName = aSrcPathName;
        File srcDirectory = checkRootDirectory();
        scanSource(srcDirectory);
    }

    public String getSrcPathName() {
        return srcPathName;
    }

    private File checkRootDirectory() throws IllegalArgumentException {
        File srcDirectory = new File(srcPathName);
        if (!srcDirectory.exists() || !srcDirectory.isDirectory()) {
            throw new IllegalArgumentException(String.format("%s doesn't point to a directory.", srcPathName));
        }
        return srcDirectory;
    }

    @Override
    protected void initializeFileCache() throws Exception {
        if (secondCacheEnabled) {
            super.initializeFileCache();
        }
    }

    @Override
    protected ApplicationElement getFromFileCache(String aId) throws Exception {
        if (secondCacheEnabled) {
            return super.getFromFileCache(aId);
        } else {
            return null;
        }
    }

    @Override
    protected void putToFileCache(ApplicationElement aAppElement) {
        if (secondCacheEnabled) {
            super.putToFileCache(aAppElement);
        }
    }

    public synchronized void watch() throws Exception {
        service = FileSystems.getDefault().newWatchService();
        if (isWindows()) {
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

    @Override
    public boolean isActual(String aId, long aTxtContentLength, long aTxtCrc32) throws Exception {
        ApplicationElement appElement = get(aId);
        if (appElement != null) {
            Long ourSize = appElement.getTxtContentLength();
            Long ourCrc32 = appElement.getTxtCrc32();
            ourSize = ourSize == null ? 0 : ourSize;
            ourCrc32 = ourCrc32 == null ? 0 : ourCrc32;
            long thiersSize = aTxtContentLength;
            long thiersCrc32 = aTxtCrc32;
            return thiersSize == ourSize && thiersCrc32 == ourCrc32;
        } else {
            return false;
        }
    }

    @Override
    protected synchronized ApplicationElement achieveAppElement(String aId) throws Exception {
        String familyPath = id2Path.get(aId);
        if (familyPath != null) {
            AppElementFiles family = families.get(familyPath);
            if (family != null && family.getAppElementType() != null) {
                return family.getApplicationElement();
            }
        }
        File resFile = new File((srcPathName + File.separatorChar + aId).replace('/', File.separatorChar));
        if (resFile.exists()) {
            String ext = FileUtils.getFileExtension(resFile);
            String fileName = resFile.getPath();
            String withoutExt = ext != null && !ext.isEmpty() ? fileName.substring(0, fileName.length() - ext.length() - 1) : fileName;
            AppElementFiles family = families.get(withoutExt);
            if (family != null && family.hasExtension(ext) && family.getAppElementType() != null) {
                return null;// application elements are not allowed to download partially!
            } else {
                ApplicationElement appElement = new ApplicationElement();
                appElement.setId(aId);
                appElement.setName(resFile.getName());
                appElement.setType(ClientConstants.ET_RESOURCE);
                //appElement.setParentId(aParentDirectoryAppElementId);
                appElement.setBinaryContent(FileUtils.readBytes(resFile));
                // hack, but it works fine.
                appElement.setTxtContentLength((long) appElement.getBinaryContent().length);
                CRC32 crc = new CRC32();
                crc.update(appElement.getBinaryContent());
                appElement.setTxtCrc32(crc.getValue());
                return appElement;
            }
        }
        return null;
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        // windows
        return (os.indexOf("win") >= 0);

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
                        Logger.getLogger(FilesAppCache.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex) {
            // Files.walkFileTree may fail due to some programs activity
            Logger.getLogger(FilesAppCache.class.getName()).log(Level.SEVERE, null, ex);
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
                        Logger.getLogger(FilesAppCache.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex) {
            // Files.walkFileTree may fail due to some programs activity
            Logger.getLogger(FilesAppCache.class.getName()).log(Level.SEVERE, null, ex);
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
                    family = new AppElementFiles(null);
                    families.put(familyPath, family);
                }
                Integer type1 = family.getAppElementType();
                String id1 = type1 != null ? family.getAppElementId(type1) : null;
                family.addFile(aFile);
                Integer type2 = family.getAppElementType();
                String id2 = type2 != null ? family.getAppElementId(type2) : null;
                // remove old values
                id2Path.remove(path2Id.remove(familyPath));
                if (id2 != null) {
                    path2Id.put(familyPath, id2);
                    id2Path.put(id2, familyPath);
                }
                remove(id1);// force the cache to refresh application element's content
                remove(id2);// force the cache to refresh application element's content
            }
        } catch (Exception ex) {
            Logger.getLogger(FilesAppCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected synchronized void removeFileFromIndex(File aFile) {
        try {
            String familyPath = fileNameWithoutExtension(aFile);
            if (familyPath != null) {
                AppElementFiles family = families.get(familyPath);
                if (family != null) {
                    String id1 = path2Id.get(familyPath);
                    family.removeFile(aFile);
                    // force the cache to refresh application element's content                    
                    if (id1 != null) {
                        remove(id1);
                    } else {
                        String id = aFile.getPath().substring(srcPathName.length() + 1).replace(File.separatorChar, '/');
                        assert !id.startsWith("/") : "Cahced platypus application element's id can't start with /. Id from absolute path is not caching subject.";
                        remove(id);
                    }
                    Integer type2 = family.getAppElementType();
                    if (type2 == null) {
                        path2Id.remove(familyPath);
                        id2Path.remove(id1);
                    }
                    if (family.isEmpty()) {
                        families.remove(familyPath);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(FilesAppCache.class.getName()).log(Level.SEVERE, null, ex);
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
                id2Path.remove(id);
                families.remove(familyPath);
            }
        }
    }
}
