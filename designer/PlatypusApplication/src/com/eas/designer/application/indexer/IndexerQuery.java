/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.Project;
import org.netbeans.modules.parsing.spi.indexing.support.IndexResult;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.openide.filesystems.FileObject;

/**
 *
 * @author vv
 */
public class IndexerQuery {
    
    /**
     * Gets FileObject for application element by application element name from index
     * @param appElementId application element name
     * @return primary file for application element
     */
    public static FileObject appElementId2File(Project project, String appElementId) {
        try {
            final Collection<FileObject> roots = new ArrayList<>(QuerySupport.findRoots(project, null, Collections.<String>emptyList(), Collections.<String>emptyList()));
            QuerySupport q = QuerySupport.forRoots(FileIndexer.INDEXER_NAME, FileIndexer.INDEXER_VERSION, roots.toArray(new FileObject[roots.size()]));
            Collection<? extends IndexResult> results = q.query(FileIndexer.APP_ELEMENT_NAME, appElementId, QuerySupport.Kind.EXACT);
            if (!results.isEmpty()) {
                IndexResult result = results.iterator().next();
                return result.getFile();
            }
        } catch (IOException ex) {
            Logger.getLogger(IndexerQuery.class.getName()).log(Level.WARNING, null, ex);
        }
        return null;
    }
    
    /**
     * Gets application element name for full path of it's primary file from index
     * @param aFile Primary file object.
     * @return application element name
     */
    public static String file2AppElementId(FileObject aFile) {
        try {
            final Collection<FileObject> roots = new ArrayList<>(QuerySupport.findRoots((Project) null, null, Collections.<String>emptyList(), Collections.<String>emptyList()));
            QuerySupport q = QuerySupport.forRoots(FileIndexer.INDEXER_NAME, FileIndexer.INDEXER_VERSION, roots.toArray(new FileObject[roots.size()]));
            Collection<? extends IndexResult> results = q.query(FileIndexer.FULL_PATH, aFile.getPath(), QuerySupport.Kind.EXACT);
            if (results.size() == 1) {
                IndexResult result = results.iterator().next();
                return result.getValue(FileIndexer.APP_ELEMENT_NAME);
            }
        } catch (IOException ex) {
            Logger.getLogger(IndexerQuery.class.getName()).log(Level.WARNING, null, ex);
        }
        return null;
    }
}
