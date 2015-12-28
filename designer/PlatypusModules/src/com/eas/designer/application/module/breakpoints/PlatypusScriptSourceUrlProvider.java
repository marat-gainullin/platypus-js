/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.module.breakpoints;

import com.eas.client.cache.PlatypusFiles;
import com.sun.jdi.AbsentInformationException;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
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
public class PlatypusScriptSourceUrlProvider extends SourcePathProvider {

    private static final String[] NO_SOURCE_ROOTS = new String[]{};

    private static final String PATH_PREFIX = "jdk/nashorn/internal/scripts/";   // NOI18N

    private final ContextProvider contextProvider;

    public PlatypusScriptSourceUrlProvider(ContextProvider aContextProvider) {
        super();
        contextProvider = aContextProvider;
    }

    @Override
    public String getURL(String relativePath, boolean global) {
        if (relativePath.startsWith(PATH_PREFIX)) {
            relativePath = relativePath.substring(PATH_PREFIX.length());
            return resolveURL(relativePath);
        } else {
            return null;
        }
    }

    protected String resolveURL(String relativePath) {
        List<? extends SourcePathProvider> spps = contextProvider.lookup(null, SourcePathProvider.class);
        for (SourcePathProvider spp : spps) {
            if (spp != this) {
                String[] sourceRoots = spp.getSourceRoots();
                for (String srcRoot : sourceRoots) {
                    FileObject foRoot = FileUtil.toFileObject(new File(srcRoot));
                    if (foRoot != null) {
                        FileObject fo = foRoot.getFileObject(relativePath.endsWith(PlatypusFiles.JAVASCRIPT_FILE_END) ? relativePath : relativePath + PlatypusFiles.JAVASCRIPT_FILE_END);
                        if (fo != null) {
                            return fo.toURL().toExternalForm();
                        }
                    }
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

}
