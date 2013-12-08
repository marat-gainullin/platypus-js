/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.completion;

import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.gui.IconCache;
import java.util.Map.Entry;
import org.openide.util.NbBundle;

/**
 *
 * @author mg
 */
public class TableSqlCompletionItem extends SqlCompletionItem {

    protected Fields table;

    public TableSqlCompletionItem(Entry<String, Fields> aTableEntry, int aStartOffset, int aEndOffset) {
        super(aTableEntry.getKey(), aTableEntry.getValue().getTableDescription(), aStartOffset, aEndOffset);
        icon = IconCache.getIcon("table.png");
        rightText = NbBundle.getMessage(TableSqlCompletionItem.class, "table");
    }

    public TableSqlCompletionItem(String aAlias, Fields aTable, int aStartOffset, int aEndOffset) {
        super(aAlias, aTable != null ? aTable.getTableDescription() : null, aStartOffset, aEndOffset);
        icon = IconCache.getIcon("table.png");
        if (aAlias.equalsIgnoreCase(aTable.get(1).getTableName())) {
            rightText = NbBundle.getMessage(TableSqlCompletionItem.class, "table");
        } else {
            rightText = NbBundle.getMessage(TableSqlCompletionItem.class, "aliasToTable") + " " + aTable.get(1).getTableName();
        }
    }
}
