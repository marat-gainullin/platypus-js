/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.indexer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.parsing.spi.indexing.Context;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexer;
import org.netbeans.modules.parsing.spi.indexing.CustomIndexerFactory;
import org.netbeans.modules.parsing.spi.indexing.Indexable;
import org.netbeans.modules.parsing.spi.indexing.support.IndexingSupport;

/**
 *
 * @author vv
 */
public class FileIndexerFactory extends CustomIndexerFactory {

    @Override
    public CustomIndexer createIndexer() {
        return new FileIndexer();
    }

    @Override
    public boolean supportsEmbeddedIndexers() {
        return false;
    }

    @Override
    public void filesDeleted(Iterable<? extends Indexable> deleted, Context context) {
        try {
            IndexingSupport is = IndexingSupport.getInstance(context);
            for (Indexable i : deleted) {
                is.removeDocuments(i);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileIndexerFactory.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    @Override
    public void filesDirty(Iterable<? extends Indexable> dirty, Context context) {
        //no-op
    }

    @Override
    public String getIndexerName() {
        return FileIndexer.INDEXER_NAME;
    }

    @Override
    public int getIndexVersion() {
        return FileIndexer.INDEXER_VERSION;
    }

    @Override
    public boolean scanStarted(final Context ctx) {
        try {
            final IndexingSupport is = IndexingSupport.getInstance(ctx);
            return is.isValid();
        } catch (final IOException ex) {
            Logger.getLogger(FileIndexerFactory.class.getName()).log(Level.WARNING, null, ex);
            return false;
        }
    }
}
