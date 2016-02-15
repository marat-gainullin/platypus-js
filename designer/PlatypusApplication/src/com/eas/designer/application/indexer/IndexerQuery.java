/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
     * Gets FileObject for application element by application element name from
     * index
     *
     * @param project a project the application element belongs
     * @param appElementName application element name
     * @return primary file for application element
     */
    public static FileObject appElementId2File(Project project, String appElementName) {
        try {
            final Collection<FileObject> roots = new ArrayList<>(QuerySupport.findRoots(project, null, Collections.<String>emptyList(), Collections.<String>emptyList()));
            QuerySupport q = QuerySupport.forRoots(FileIndexer.INDEXER_NAME, FileIndexer.INDEXER_VERSION, roots.toArray(new FileObject[]{}));
            Collection<? extends IndexResult> results = q.query(FileIndexer.APP_ELEMENT_NAME, appElementName, QuerySupport.Kind.EXACT);
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
     * Gets application element name for full path of it's primary file from
     * index
     *
     * @param aFile Primary file object.
     * @return application element name
     */
    public static String file2AppElementId(FileObject aFile) {
        if (aFile != null) {
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
        }
        return null;
    }

    /**
     * Finds application elements files by its identifier prefix
     *
     * @param project project a project the application element belongs
     * @param prefix application element prefix
     * @return application elements primary files collection
     */
    public static Collection<AppElementInfo> appElementsByPrefix(Project project, String prefix) {
        if (prefix != null) {
            try {
                final Collection<FileObject> roots = new ArrayList<>(QuerySupport.findRoots(project, null, Collections.<String>emptyList(), Collections.<String>emptyList()));
                QuerySupport q = QuerySupport.forRoots(FileIndexer.INDEXER_NAME, FileIndexer.INDEXER_VERSION, roots.toArray(new FileObject[roots.size()]));
                //Hack! I could not make case insensitive prefix query work, at this moment I've decided to retrieve all elements to filter them later
                Collection<? extends IndexResult> queryResults = q.query(FileIndexer.APP_ELEMENT_NAME, "", QuerySupport.Kind.CASE_INSENSITIVE_PREFIX);//NOI18N
                List<AppElementInfo> results = new ArrayList<>();
                for (IndexResult queryResult : queryResults) {
                    String appElementName = queryResult.getValue(FileIndexer.APP_ELEMENT_NAME);
                    if (appElementName != null && appElementName.toLowerCase().startsWith(prefix.toLowerCase())) {
                        results.add(new AppElementInfo(appElementName, queryResult.getFile()));
                    }
                }
                return results;
            } catch (IOException ex) {
                Logger.getLogger(IndexerQuery.class.getName()).log(Level.WARNING, null, ex);
            }
            return null;
        } else {
            return Collections.EMPTY_LIST;
        }
    }
}
