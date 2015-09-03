/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.breakpoints;

import com.sun.jdi.AbsentInformationException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.debugger.jpda.JPDAClassType;
import org.netbeans.spi.debugger.ContextProvider;
import org.netbeans.spi.debugger.jpda.SourcePathProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Martin
 */
@SourcePathProvider.Registration(path = "netbeans-JPDASession")
public class RelativeUrlProvider extends SourcePathProvider {

    private static final String[] NO_SOURCE_ROOTS = new String[]{};

    private static final String pathPrefix = "jdk/nashorn/internal/scripts/";   // NOI18N

    private final ContextProvider contextProvider;
    private SourcePathProvider sourcePath;
    private Set<FileObject> rootDirs;
    private final Object rootDirsLock = new Object();

    public RelativeUrlProvider(ContextProvider aContextProvider) {
        super();
        contextProvider = aContextProvider;
    }

    @Override
    public String getURL(String relativePath, boolean global) {
        if (relativePath.startsWith(pathPrefix)) {
            relativePath = relativePath.substring(pathPrefix.length());
            return resolveURL(relativePath);
        } else {
            return null;
        }
    }

    protected String resolveURL(String relativePath) {
        synchronized (rootDirsLock) {
            if (rootDirs == null) {
                sourcePath = getSourcePathProvider();
                sourcePath.addPropertyChangeListener(new SourcePathListener());
                rootDirs = computeModuleRoots();
            }
            for (FileObject root : rootDirs) {
                FileObject fo = root.getFileObject(relativePath);
                if (fo != null) {
                    return fo.toURL().toExternalForm();
                }
            }
        }
        return null;
    }

    public String getURL(JPDAClassType clazz, String stratum) throws AbsentInformationException {
        String className = clazz.getName();
        if (className.startsWith(PlatypusJSBreakpointsManager.SCRIPTS_CLASS_PREFIX)) {
            try {
                Class.forName(className);
                // Nashorn's own class
                return null;
            } catch (ClassNotFoundException ex) {
                // It seems, that we have Nashorn generated class name
                String sourceName = clazz.getSourceName();
                return resolveURL(sourceName);
            }
        } else {
            return null;
        }
    }

    @Override
    public String getRelativePath(String url, char directorySeparator, boolean includeExtension) {
        return null;
    }

    @Override
    public String[] getSourceRoots() {
        return NO_SOURCE_ROOTS;
    }

    @Override
    public void setSourceRoots(String[] sourceRoots) {
    }

    @Override
    public String[] getOriginalSourceRoots() {
        return NO_SOURCE_ROOTS;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
    }

    private SourcePathProvider getSourcePathProvider() {
        List<? extends SourcePathProvider> spps = contextProvider.lookup(null, SourcePathProvider.class);
        for (SourcePathProvider spp : spps) {
            if (spp != this) {
                return spp;
            }
        }
        throw new RuntimeException("No SourcePathProvider");
    }

    private Set<FileObject> computeModuleRoots() {
        Set<FileObject> dirs = new LinkedHashSet<>();
        String[] sourceRoots = sourcePath.getSourceRoots();
        for (String src : sourceRoots) {
            FileObject fo = getFileObject(src);
            if (fo != null) {
                dirs.add(fo);
            }
        }
        return dirs;
    }

    /**
     * Returns FileObject for given String.
     */
    private static FileObject getFileObject(String file) {
        File f = new File(file);
        FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(f));
        if (fo == null && file.contains("!/")) {
            int index = file.indexOf("!/");
            f = new File(file.substring(0, index));
            fo = FileUtil.toFileObject(f);
        }
        return fo;
    }

    private class SourcePathListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            synchronized (rootDirsLock) {
                if (rootDirs != null) {
                    // If initialized already, recompute:
                    rootDirs = computeModuleRoots();
                }
            }
        }

    }

}
