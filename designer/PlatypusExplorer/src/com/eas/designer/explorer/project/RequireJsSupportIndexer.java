package com.eas.designer.explorer.project;

import com.eas.designer.application.indexer.PlatypusPathRecognizer;
import com.eas.designer.application.project.PlatypusProject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexer;
import org.netbeans.modules.parsing.spi.indexing.EmbeddingIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.support.IndexDocument;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author mg
 */
public class RequireJsSupportIndexer extends EmbeddingIndexer {

    public static final String FIELD_EXPOSED_TYPES = "et"; //NOI18N
    public static final String FIELD_MODULE_NAME = "mn"; //NOI18N
    public static final String REQUIREJS_BASE_PATH = "bp"; //NOI18N

    @Override
    protected void index(Indexable indexable, Parser.Result parserResult, Context context) {
        FileObject fo = parserResult.getSnapshot().getSource().getFileObject();
        if (fo != null) {
            try {
                Project project = FileOwnerQuery.getOwner(fo);
                if (project instanceof PlatypusProject) {
                    PlatypusProject pProject = (PlatypusProject) project;
                    FileObject projectDir = pProject.getProjectDirectory();
                    FileObject srcDir = pProject.getSrcRoot();
                    FileObject apiDir = pProject.getApiRoot();
                    String srcRootName = FileUtil.getRelativePath(projectDir, srcDir);
                    String apiRootName = FileUtil.getRelativePath(projectDir, apiDir);
                    IndexingSupport support = IndexingSupport.getInstance(context);
                    IndexDocument doc = support.createDocument(fo);
                    doc.addPair(REQUIREJS_BASE_PATH, srcRootName, true, true);
                    doc.addPair(REQUIREJS_BASE_PATH, apiRootName, true, true);
                    support.addDocument(doc);
                }
            } catch (IOException ioe) {
                Logger.getLogger(RequireJsSupportIndexer.class.getName()).log(Level.WARNING, null, ioe);
            }
        }
    }

    public static final class Factory extends EmbeddingIndexerFactory {

        public static final String NAME = "requirejs"; // NOI18N
        public static final int VERSION = 1;
        private static final int PRIORITY = 220;

        @Override
        public EmbeddingIndexer createIndexer(Indexable indexable, Snapshot snapshot) {
            if (isIndexable(indexable, snapshot)) {
                return new RequireJsSupportIndexer();
            } else {
                return null;
            }
        }

        @Override
        public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
            try {
                IndexingSupport is = IndexingSupport.getInstance(context);
                for (Indexable i : deleted) {
                    is.removeDocuments(i);
                }
            } catch (IOException ioe) {
                Logger.getLogger(RequireJsSupportIndexer.class.getName()).log(Level.WARNING, null, ioe);
            }
        }

        @Override
        public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
        }

        @Override
        public String getIndexerName() {
            return NAME;
        }

        @Override
        public int getIndexVersion() {
            return VERSION;
        }

        private boolean isIndexable(Indexable indexable, Snapshot snapshot) {
            return PlatypusPathRecognizer.JAVASCRIPT_MIME_TYPE.equals(snapshot.getMimeType());
        }

        @Override
        public boolean scanStarted(Context context) {
            return super.scanStarted(context);
        }

        @Override
        public void scanFinished(Context context) {
            super.scanFinished(context);
        }

        @Override
        public int getPriority() {
            return PRIORITY;
        }
    }
}
