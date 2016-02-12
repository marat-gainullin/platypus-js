package com.eas.client.cache;

import com.eas.script.JsDoc;
import com.eas.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class ApplicationSourceIndexer implements PlatypusIndexer {

    public interface ScanCallback {

        public void moduleScanned(String aModuleName, ScriptDocument.ModuleDocument aModule, File aFile);
    }

    protected Path projectPath;
    protected Path appPath;
    protected Map<String, File> id2Paths = new HashMap<>();
    protected WatchService service;
    protected ScanCallback scanCallback;
    protected boolean autoScan = true;
    protected ScriptsConfigs scriptsConfigs;

    public ApplicationSourceIndexer(Path aProjectPath, ScriptsConfigs aScriptConfigs) throws Exception {
        this(aProjectPath, aScriptConfigs, true, null);
    }

    public ApplicationSourceIndexer(Path aProjectPath, ScriptsConfigs aScriptConfigs, ScanCallback aScanCallback) throws Exception {
        this(aProjectPath, aScriptConfigs, true, aScanCallback);
    }

    public ApplicationSourceIndexer(Path aProjectPath, ScriptsConfigs aScriptConfigs, boolean aAutoScan, ScanCallback aScanCallback) throws Exception {
        super();
        autoScan = aAutoScan;
        projectPath = aProjectPath;
        scriptsConfigs = aScriptConfigs;
        appPath = projectPath.resolve(PlatypusFiles.PLATYPUS_PROJECT_APP_ROOT);
        scanCallback = aScanCallback;
        if (autoScan) {
            File srcDirectory = checkRootDirectory();
            scanSource(srcDirectory);
        }
    }

    private File checkRootDirectory() throws IllegalArgumentException {
        File srcDirectory = appPath.toFile();
        if (!srcDirectory.exists() || !srcDirectory.isDirectory()) {
            throw new IllegalArgumentException(String.format("%s doesn't point to a directory.", appPath.toString()));
        }
        return srcDirectory;
    }

    public void watch() throws Exception {
        service = FileSystems.getDefault().newWatchService();
    }

    public void unwatch() throws Exception {
        assert service != null;
        service.close();
        service = null;
    }

    public void rescan() {
        id2Paths.clear();
        File srcDirectory = checkRootDirectory();
        scanSource(srcDirectory);
    }

    private void scanSource(File aDirectory) {
        assert aDirectory.exists() && aDirectory.isDirectory();
        try {
            Files.walkFileTree(Paths.get(aDirectory.toURI()), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path aFilePath, BasicFileAttributes attrs) throws IOException {
                    try {
                        add(aFilePath.toFile());
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

    protected void add(File aFile) throws Exception {
        if (aFile.getName().endsWith(PlatypusFiles.JAVASCRIPT_FILE_END)) {
            String defaultModuleName = getDefaultModuleName(aFile);
            ScriptDocument scriptDoc = scriptsConfigs.get(defaultModuleName, aFile);
            Set<Map.Entry<String, ScriptDocument.ModuleDocument>> modulesDocs = scriptDoc.getModules().entrySet();
            modulesDocs.forEach((Map.Entry<String, ScriptDocument.ModuleDocument> aModuleDocEntry) -> {
                id2Paths.put(aModuleDocEntry.getKey(), aFile);
                if (scanCallback != null) {
                    scanCallback.moduleScanned(aModuleDocEntry.getKey(), aModuleDocEntry.getValue(), aFile);
                }
            });
        } else if (aFile.getName().endsWith(PlatypusFiles.SQL_FILE_END)) {
            String fileContent = FileUtils.readString(aFile, PlatypusFiles.DEFAULT_ENCODING);
            String queryName = PlatypusFilesSupport.getAnnotationValue(fileContent, JsDoc.Tag.NAME_TAG);
            if (queryName != null && !queryName.isEmpty()) {
                id2Paths.put(queryName, aFile);
            }
        }
    }

    @Override
    public String getDefaultModuleName(File aFile) {
        String defaultModuleName = appPath.relativize(Paths.get(aFile.toURI())).toString().replace(File.separator, "/");
        defaultModuleName = defaultModuleName.substring(0, defaultModuleName.length() - PlatypusFiles.JAVASCRIPT_FILE_END.length());
        return defaultModuleName;
    }

    public Path getProjectPath() {
        return projectPath;
    }

    public Path getAppPath() {
        return appPath;
    }

    /**
     * Resolves an application element name to a path of local file.
     *
     * @param aName
     * @return
     * @throws Exception
     */
    @Override
    public File nameToFile(String aName) throws Exception {
        if (aName != null) {
            File file = id2Paths.get(aName);
            if (file != null) {
                return file;
            } else {
                String filyName = aName.replace('/', File.separatorChar);
                Path appResource = appPath.resolve(filyName);
                if (appResource.toFile().exists()) {// plain resource relative 'app' directory
                    return appResource.toFile();
                } else {
                    Path appJsResource = appPath.resolve(filyName + PlatypusFiles.JAVASCRIPT_FILE_END);
                    if (appJsResource.toFile().exists()) {// *.js resource relative 'app' directory
                        return appJsResource.toFile();
                    } else {
                        File absoluteResource = new File(filyName);// plain resource by absolute path
                        if (absoluteResource.exists()) {
                            return absoluteResource;
                        } else {
                            File absoluteJsResource = new File(filyName + PlatypusFiles.JAVASCRIPT_FILE_END);
                            if (absoluteJsResource.exists()) {
                                return absoluteJsResource;
                            } else {
                                return null;
                            }
                        }
                    }
                }
            }
        } else {
            return null;
        }
    }
}
