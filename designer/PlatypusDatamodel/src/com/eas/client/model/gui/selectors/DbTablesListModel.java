/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.eas.client.ClientConstants;
import com.eas.client.DatabaseMdCache;
import com.eas.client.DatabasesClient;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.dataflow.ColumnsIndicies;
import com.eas.client.sqldrivers.SqlDriver;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author mg
 */
public class DbTablesListModel implements ListModel<String> {

    protected List<String> tables;
    protected Set<ListDataListener> listeners = new HashSet<>();
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
            setTablesRowset(fetchTables());
        } catch (Exception ex) {
            Logger.getLogger(DbTablesListModel.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    protected final List<String> fetchTables() {
        if (schema != null && !schema.isEmpty()) {
            try {
                SqlDriver driver = mdCache.getConnectionDriver();
                String sql4Tables = driver.getSql4TablesEnumeration(schema);
                SqlCompiledQuery query = new SqlCompiledQuery(basesProxy, datasourceName, sql4Tables);
                return query.executeQuery((ResultSet r) -> {
                    List<String> _tables = new ArrayList<>();
                    ColumnsIndicies idxs = new ColumnsIndicies(r.getMetaData());
                    int schemaColIndex = idxs.find(ClientConstants.JDBCCOLS_TABLE_SCHEM);
                    int tableColIndex = idxs.find(ClientConstants.JDBCCOLS_TABLE_NAME);
                    while (r.next()) {
                        String schemaName = r.getString(schemaColIndex);
                        String tableName = r.getString(tableColIndex);
                        _tables.add(schemaName != null && !schemaName.isEmpty() ? schemaName + "." + tableName : tableName);
                    }
                    return _tables;
                }, null, null, null);
            } catch (Exception ex) {
                Logger.getLogger(DbTablesListModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    protected final void setTablesRowset(List<String> aTablesRowset) {
        tables = aTablesRowset;
    }

    public DatabaseMdCache getMdCache() {
        return mdCache;
    }

    public void setSchema(String aSchema) throws Exception {
        if (schema == null ? aSchema != null : !schema.equals(aSchema)) {
            schema = aSchema;
            setTablesRowset(fetchTables());
        }
        fireDataChanged();
    }

    public String getSchema() {
        return schema;
    }

    @Override
    public int getSize() {
        if (tables != null) {
            return tables.size();
        } else {
            return 0;
        }
    }

    @Override
    public String getElementAt(int index) {
        if (index >= 0 && index < getSize()) {
            return tables.get(index);
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
/*
    public int findTable(String aScheme, String aTable) {
        String pattern = "";
        if (aScheme != null && !aScheme.isEmpty()) {
            pattern = aScheme + ".";
        }
        pattern += aTable;
        if (tables != null && pattern != null && !pattern.isEmpty()) {
            int i = -1;
            for (String r : tables) {
                if (pattern.toLowerCase().equalsIgnoreCase(r)) {
                    return i;
                }
            }
        }
        return -1;
    }
    */
}
