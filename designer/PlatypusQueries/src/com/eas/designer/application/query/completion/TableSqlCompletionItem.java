/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.completion;

import com.eas.client.metadata.Fields;
import com.eas.client.model.gui.IconCache;
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
public class TableSqlCompletionItem extends SqlCompletionItem {

    protected PlatypusQueryDataObject editedQueryDataObject;
    protected String alias;

    public TableSqlCompletionItem(PlatypusQueryDataObject aEditedQuery, Fields aTable, int aStartOffset, int aEndOffset) {
        super(aTable.get(1).getTableName(), aTable.getTableDescription(), aStartOffset, aEndOffset);
        editedQueryDataObject = aEditedQuery;
        icon = IconCache.getIcon("table.png");
        rightText = NbBundle.getMessage(TableSqlCompletionItem.class, "table");
    }

    public TableSqlCompletionItem(PlatypusQueryDataObject aQuery, String aAlias, Fields aTable, int aStartOffset, int aEndOffset) {
        super(aAlias, aTable != null ? aTable.getTableDescription() : null, aStartOffset, aEndOffset);
        editedQueryDataObject = aQuery;
        alias = aAlias;
        icon = IconCache.getIcon("table.png");
        if (aAlias.equalsIgnoreCase(aTable.get(1).getTableName())) {
            rightText = NbBundle.getMessage(TableSqlCompletionItem.class, "table");
        } else {
            rightText = NbBundle.getMessage(TableSqlCompletionItem.class, "aliasToTable") + " " + aTable.get(1).getTableName();
        }
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
        return QueryDocumentEditsComplementor.findFreeAliasName(tables, QueryDocumentEditsComplementor.TABLE_ALIAS_PREFIX);
    }
}
