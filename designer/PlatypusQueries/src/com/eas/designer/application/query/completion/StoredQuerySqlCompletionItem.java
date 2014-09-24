/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.completion;

import com.eas.client.ClientConstants;
import com.eas.client.model.gui.IconCache;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.application.query.editing.QueryDocumentEditsComplementor;
import java.util.Map;
import net.sf.jsqlparser.TablesFinder;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class StoredQuerySqlCompletionItem extends SqlCompletionItem {

    protected PlatypusQueryDataObject editedQueryDataObject;
    protected String alias;

    public StoredQuerySqlCompletionItem(String aAppElementId, PlatypusQueryDataObject aEditedQuery, PlatypusQueryDataObject aSubjectQuery, int aStartOffset, int aEndOffset) {
        super(aAppElementId, aSubjectQuery.getName(), aStartOffset, aEndOffset);
        text = ClientConstants.STORED_QUERY_REF_PREFIX + text;
        editedQueryDataObject = aEditedQuery;
        icon = IconCache.getIcon("query.png");
        rightText = NbBundle.getMessage(StoredQuerySqlCompletionItem.class, "query");
    }

    public StoredQuerySqlCompletionItem(PlatypusQueryDataObject aEditedQuery, PlatypusQueryDataObject aSubjectQuery, String aAlias, int aStartOffset, int aEndOffset) {
        super(aAlias, aSubjectQuery.getName(), aStartOffset, aEndOffset);
        editedQueryDataObject = aEditedQuery;
        alias = aAlias;
        icon = IconCache.getIcon("query.png");
        rightText = NbBundle.getMessage(StoredQuerySqlCompletionItem.class, "aliasToQuery") + " " + IndexerQuery.file2AppElementId(aEditedQuery.getPrimaryFile());
    }

    @Override
    protected String getTestToInsert() throws Exception {
        if (alias == null) {
            return super.getTestToInsert() + " " + findFreeAliasName();
        } else {
            return super.getTestToInsert();
        }
    }

    private String findFreeAliasName() throws Exception {
        Statement statement = editedQueryDataObject.getCommitedStatement() != null ? editedQueryDataObject.getCommitedStatement() : editedQueryDataObject.getStatement();
        Map<String, Table> tables = TablesFinder.getTablesMap(TablesFinder.TO_CASE.LOWER, statement, true);
        // Take care of pretty queries alias names.
        return QueryDocumentEditsComplementor.findFreeAliasName(tables, QueryDocumentEditsComplementor.QUERY_ALIAS_PREFIX);
    }
}
