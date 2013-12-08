/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.completion;

import com.eas.client.model.gui.IconCache;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class StoredQuerySqlCompletionItem extends SqlCompletionItem {

    protected PlatypusQueryDataObject query;

    public StoredQuerySqlCompletionItem(PlatypusQueryDataObject aQuery, int aStartOffset, int aEndOffset) {
        super(IndexerQuery.file2AppElementId(aQuery.getPrimaryFile()), aQuery.getName(), aStartOffset, aEndOffset);
        query = aQuery;
        icon = IconCache.getIcon("query.png");
        rightText = NbBundle.getMessage(StoredQuerySqlCompletionItem.class, "query");
    }

    public StoredQuerySqlCompletionItem(String aAlias, PlatypusQueryDataObject aQuery, int aStartOffset, int aEndOffset) {
        super(aAlias, aQuery.getName(), aStartOffset, aEndOffset);
        query = aQuery;
        icon = IconCache.getIcon("query.png");
        rightText = NbBundle.getMessage(StoredQuerySqlCompletionItem.class, "aliasToQuery") + " " + IndexerQuery.file2AppElementId(aQuery.getPrimaryFile());
    }
}
