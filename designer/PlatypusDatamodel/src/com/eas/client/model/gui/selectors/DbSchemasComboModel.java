/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.bearsoft.rowset.Rowset;
import com.eas.client.ClientConstants;
import com.eas.client.DbClient;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.sqldrivers.SqlDriver;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author mg
 */
public class DbSchemasComboModel implements ComboBoxModel<String> {

    protected DbClient client;
    protected String dbId;
    protected String[] schemas = new String[]{};
    protected DbTablesListModel tablesModel;
    protected Set<ListDataListener> listeners = new HashSet<>();

    public DbSchemasComboModel(DbClient aClient, String aDbId, DbTablesListModel aTablesModel) {
        super();
        client = aClient;
        dbId = aDbId;
        tablesModel = aTablesModel;
        achieveSchemas();
    }

    protected final Rowset createRowset() {
        try {
        } catch (Exception ex) {
            Logger.getLogger(DbSchemasComboModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected final void achieveSchemas() {
        try {
            SqlDriver driver = client.getDbMetadataCache(dbId).getConnectionDriver();
            String sql4Schemas = driver.getSql4SchemasEnumeration();
            SqlCompiledQuery query = new SqlCompiledQuery(client, dbId, sql4Schemas);
            Rowset schemasRowset = query.executeQuery();
            int schemacolIndex = schemasRowset.getFields().find(ClientConstants.JDBCCOLS_TABLE_SCHEM);
            if (schemasRowset != null) {
                Map<String, String> sch = new TreeMap<>();
                for (int i = 0; i < schemasRowset.size(); i++) {
                    if (schemasRowset.absolute(i + 1)) {
                        String schemaName = schemasRowset.getString(schemacolIndex);
                        if (!sch.containsKey(schemaName)) {
                            sch.put(schemaName, schemaName);
                        }
                    }
                }
                Collection<String> schemasCol = sch.values();
                if (schemasCol != null) {
                    schemas = new String[schemasCol.size()];
                    Iterator<String> schIt = schemasCol.iterator();
                    if (schIt != null) {
                        int i = 0;
                        while (schIt.hasNext()) {
                            schemas[i++] = (String) schIt.next();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DbSchemasComboModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem != null && anItem instanceof String) {
            try {
                tablesModel.setSchema((String) anItem);
            } catch (Exception ex) {
                Logger.getLogger(DbSchemasComboModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public Object getSelectedItem() {
        return tablesModel.getSchema();
    }

    @Override
    public int getSize() {
        return schemas.length;
    }

    @Override
    public String getElementAt(int index) {
        if (index >= 0 && index < schemas.length) {
            return schemas[index];
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
}
