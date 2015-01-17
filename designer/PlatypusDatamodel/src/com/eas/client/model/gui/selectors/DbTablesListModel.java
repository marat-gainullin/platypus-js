/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.ClientConstants;
import com.eas.client.DatabaseMdCache;
import com.eas.client.DatabasesClient;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.sqldrivers.SqlDriver;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.openide.util.Exceptions;

/**
 *
 * @author mg
 */
public class DbTablesListModel implements ListModel<String> {

    protected Rowset tablesRowset;
    protected Set<ListDataListener> listeners = new HashSet<>();
    protected int schemaColIndex = -1;
    protected int tableColIndex = -1;
    protected String datasourceName;
    protected DatabasesClient basesProxy;
    protected String schema;
    protected DatabaseMdCache mdCache;

    public DbTablesListModel(DatabasesClient aBasesProxy, String aDatasourceName) {
        super();
        datasourceName = aDatasourceName;
        try {
            basesProxy = aBasesProxy;
            mdCache = basesProxy.getDbMetadataCache(datasourceName);
            schema = mdCache.getConnectionSchema();
            setTablesRowset(createRowset());
        } catch (Exception ex) {
            Logger.getLogger(DbTablesListModel.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    protected final Rowset createRowset() {
        if (schema != null && !schema.isEmpty()) {
            try {
                SqlDriver driver = mdCache.getConnectionDriver();
                String sql4Tables = driver.getSql4TablesEnumeration(schema);
                SqlCompiledQuery query = new SqlCompiledQuery(basesProxy, datasourceName, sql4Tables);
                return query.executeQuery(null, null);
            } catch (Exception ex) {
                Logger.getLogger(DbTablesListModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    protected final void setTablesRowset(Rowset aTablesRowset) {
        tablesRowset = aTablesRowset;
        if (tablesRowset != null) {
            try {
                Fields fields = tablesRowset.getFields();
                schemaColIndex = fields.find(ClientConstants.JDBCCOLS_TABLE_SCHEM);
                tableColIndex = fields.find(ClientConstants.JDBCCOLS_TABLE_NAME);
            } catch (Exception ex) {
                Logger.getLogger(DbTablesListModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public DatabaseMdCache getMdCache() {
        return mdCache;
    }

    public void setSchema(String aSchema) throws Exception {
        if (schema == null ? aSchema != null : !schema.equals(aSchema)) {
            schema = aSchema;
            setTablesRowset(createRowset());
        }
        fireDataChanged();
    }

    public String getSchema() {
        return schema;
    }

    @Override
    public int getSize() {
        if (schemaColIndex != -1 && tableColIndex != -1
                && tablesRowset != null) {
            return tablesRowset.size();
        } else {
            return 0;
        }
    }

    @Override
    public String getElementAt(int index) {
        if (index >= 0 && index < getSize()) {
            try {
                Row r = tablesRowset.getRow(index + 1);
                String schemaName = (String) r.getColumnObject(schemaColIndex);
                String tableName = (String) r.getColumnObject(tableColIndex);
                return schemaName + "." + tableName;
            } catch (InvalidColIndexException ex) {
                Exceptions.printStackTrace(ex);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    protected void fireDataChanged() {
        Iterator<ListDataListener> lIt = listeners.iterator();
        if (lIt != null) {
            while (lIt.hasNext()) {
                ListDataListener l = lIt.next();
                l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
            }
        }
    }

    public int findTable(String aScheme, String aTable) {
        String pattern = "";
        if (aScheme != null && !aScheme.isEmpty()) {
            pattern = aScheme + ".";
        }
        pattern += aTable;
        if (tablesRowset != null && pattern != null && !pattern.isEmpty()) {
            int i = -1;
            try {
                for (Row r : tablesRowset.getCurrent()) {
                    i++;
                    String schemaName = (String) r.getColumnObject(schemaColIndex);
                    String tableName = (String) r.getColumnObject(tableColIndex);
                    if (schemaName != null && !schemaName.isEmpty()) {
                        tableName = schemaName + "." + tableName;
                    }
                    if (pattern.toLowerCase().equalsIgnoreCase(tableName.toLowerCase())) {
                        return i;
                    }
                }
            } catch (InvalidColIndexException ex) {
                Logger.getLogger(DbTablesListModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return -1;
    }
}
