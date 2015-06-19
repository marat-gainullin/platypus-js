/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dataflow;

import com.eas.client.changes.ChangeValue;
import com.eas.client.changes.ChangeVisitor;
import com.eas.client.changes.Command;
import com.eas.client.changes.Delete;
import com.eas.client.changes.EntitiesHost;
import com.eas.client.changes.Insert;
import com.eas.client.changes.Update;
import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Parameter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writer for jdbc datasources. Performs writing of a rowset. Writing utilizes
 * converters to produce jdbc-specific data while writing. There are two modes
 * of database updating. The first one "write mode" is update/delete/insert
 * statements preparation and batch execution. The second one "log mode" is
 * logging of statemnents to be executed with parameters values. In log mode no
 * execution is performed.
 *
 * @see Converter
 * @author mg
 */
public class StatementsGenerator implements ChangeVisitor {

    /**
     * Stores short living information about statements, to be executed while
     * jdbc update process. Performs parameterized statements execution.
     */
    public static class StatementsLogEntry {

        protected static final Logger queriesLogger = Logger.getLogger(StatementsLogEntry.class.getName());
        public String clause;
        public List<ChangeValue> parameters = new ArrayList<>();
        public boolean valid = true;

        public StatementsLogEntry() {
            super();
        }

        public int apply(Connection aConnection) throws Exception {
            if (valid) {
                try (PreparedStatement stmt = aConnection.prepareStatement(clause)) {
                    for (int i = 1; i <= parameters.size(); i++) {
                        ChangeValue v = parameters.get(i - 1);
                        Converter.convertAndAssign(v.value, v.type, aConnection, i, stmt);
                    }
                    if (queriesLogger.isLoggable(Level.FINE)) {
                        queriesLogger.log(Level.FINE, "Executing sql with {0} parameters: {1}", new Object[]{parameters.size(), clause});
                    }
                    return stmt.executeUpdate();
                }
            } else {
                Logger.getLogger(StatementsLogEntry.class.getName()).log(Level.INFO, "Invalid StatementsLogEntry occured!");
                return 0;
            }
        }
    }
    protected static final String INSERT_CLAUSE = "insert into %s (%s) values (%s)";
    protected static final String DELETE_CLAUSE = "delete from %s where %s";
    protected static final String UPDATE_CLAUSE = "update %s set %s where %s";
    protected List<StatementsLogEntry> logEntries = new ArrayList<>();
    protected EntitiesHost entitiesHost;
    protected String schemaContextFieldName;
    protected String schemaContext;

    public StatementsGenerator(EntitiesHost aEntitiesHost, String aSchemaContextFieldName, String aSchemaContext) {
        super();
        entitiesHost = aEntitiesHost;
        schemaContextFieldName = aSchemaContextFieldName;
        schemaContext = aSchemaContext;
    }

    public List<StatementsLogEntry> getLogEntries() {
        return logEntries;
    }

    protected String generatePlaceholders(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("?");
        }
        return sb.toString();
    }

    /**
     * Generates sql where clause string for keys array passed in. It's assumed
     * that key columns may not have NULL values. This assumption is made
     * because we use simple "=" operator in WHERE clause.
     *
     * @param aKeys Keys array to deal with.
     * @return
     */
    protected String generateWhereClause(List<ChangeValue> aKeys) {
        StringBuilder whereClause = new StringBuilder();
        for (int i = 0; i < aKeys.size(); i++) {
            if (i > 0) {
                whereClause.append(" and ");
            }
            whereClause.append(aKeys.get(i).name).append(" = ?");
        }
        return whereClause.toString();
    }

    protected class InsertChunk {

        public StatementsLogEntry insert;
        public StringBuilder dataColumnsNames;
        public List<String> keysColumnsNames;
        public boolean contexted;
    }

    @Override
    public void visit(Insert aChange) throws Exception {
        if (!aChange.consumed) {
            Map<String, InsertChunk> inserts = new HashMap<>();
            for (ChangeValue data : aChange.getData()) {
                Field field = entitiesHost.resolveField(aChange.entityName, data.name);
                if (field != null) {
                    data.type = field.getTypeInfo();
                    InsertChunk chunk = inserts.get(field.getTableName());
                    if (chunk == null) {
                        chunk = new InsertChunk();
                        inserts.put(field.getTableName(), chunk);
                        chunk.insert = new StatementsLogEntry();
                        // Adding here is strongly needed. Because of order in wich other and this statememts are added
                        // to the log and therefore applied into a database during a transaction.
                        logEntries.add(chunk.insert);
                        chunk.dataColumnsNames = new StringBuilder();
                        chunk.keysColumnsNames = new ArrayList<>();
                    }
                    if (!chunk.insert.parameters.isEmpty()) {
                        chunk.dataColumnsNames.append(", ");
                    }
                    String dataColumnName = field.getOriginalName() != null ? field.getOriginalName() : field.getName();
                    chunk.dataColumnsNames.append(dataColumnName);
                    //
                    if (schemaContext != null && !schemaContext.isEmpty()
                            && schemaContextFieldName != null && schemaContextFieldName.isEmpty()
                            && dataColumnName.equalsIgnoreCase(schemaContextFieldName)) {
                        chunk.contexted = true;
                        if (data.value == null) {
                            chunk.insert.parameters.add(new ChangeValue(schemaContextFieldName, schemaContext, DataTypeInfo.VARCHAR));
                        } else {
                            chunk.insert.parameters.add(data);
                        }
                    } else {
                        chunk.insert.parameters.add(data);
                    }
                    if (field.isPk()) {
                        chunk.keysColumnsNames.add(dataColumnName);
                    }
                }
            }
            for (String tableName : inserts.keySet()) {
                InsertChunk chunk = inserts.get(tableName);
                //
                if (schemaContext != null && !schemaContext.isEmpty()
                        && schemaContextFieldName != null && !schemaContextFieldName.isEmpty()
                        && !chunk.contexted) {
                    Field contextField = entitiesHost.resolveField(tableName, schemaContextFieldName);
                    if (contextField != null) {
                        if (!chunk.insert.parameters.isEmpty()) {
                            chunk.dataColumnsNames.append(", ");
                        }
                        chunk.dataColumnsNames.append(schemaContextFieldName);
                        chunk.insert.parameters.add(new ChangeValue(schemaContextFieldName, schemaContext, DataTypeInfo.VARCHAR));
                    }
                }
                //
                chunk.insert.clause = String.format(INSERT_CLAUSE, tableName, chunk.dataColumnsNames.toString(), generatePlaceholders(chunk.insert.parameters.size()));
                // Validness of the insert statement is outlined by inserted columns and key columns existance also
                // because we have to prevent unexpected inserts in any joined table.
                // In this case inserts will be valid only if they include at least one key column per table.
                // Another case is single table per Insert instance.
                // So, we can avoid unexpected inserts in a transaction.
                // It's considered that keyless inserts are easy to obtain with manual (dml flag) queries.
                // So avoid keys in select columns list for a table to avoid unexpected inserts in that table! 
                chunk.insert.valid = !chunk.insert.parameters.isEmpty() && (!chunk.keysColumnsNames.isEmpty() || inserts.size() == 1);
            }
        }
    }

    protected class UpdateChunk {

        public StatementsLogEntry update;
        public StringBuilder columnsClause;
        public List<ChangeValue> keys;
        public List<ChangeValue> data;
    }

    @Override
    public void visit(Update aChange) throws Exception {
        if (!aChange.consumed) {
            Map<String, UpdateChunk> updates = new HashMap<>();
            // data
            for (ChangeValue data : aChange.getData()) {
                Field field = entitiesHost.resolveField(aChange.entityName, data.name);
                if (field != null) {
                    data.type = field.getTypeInfo();
                    UpdateChunk chunk = updates.get(field.getTableName());
                    if (chunk == null) {
                        chunk = new UpdateChunk();
                        updates.put(field.getTableName(), chunk);
                        chunk.update = new StatementsLogEntry();
                        // Adding here is strongly needed. Because of order in with other and this statememts are added
                        // to the log and therefore applied into a database during a transaction.
                        logEntries.add(chunk.update);
                        chunk.columnsClause = new StringBuilder();
                        chunk.data = new ArrayList<>();
                    }
                    if (!chunk.data.isEmpty()) {
                        chunk.columnsClause.append(", ");
                    }
                    chunk.columnsClause.append(field.getOriginalName() != null ? field.getOriginalName() : field.getName()).append(" = ?");
                    chunk.data.add(data);
                }
            }
            // keys
            for (ChangeValue key : aChange.getKeys()) {
                Field field = entitiesHost.resolveField(aChange.entityName, key.name);
                if (field != null) {
                    key.type = field.getTypeInfo();
                    UpdateChunk chunk = updates.get(field.getTableName());
                    if (chunk != null) {
                        if (chunk.keys == null) {
                            chunk.keys = new ArrayList<>();
                        }
                        chunk.keys.add(new ChangeValue(field.getOriginalName() != null ? field.getOriginalName() : field.getName(), key.value, key.type));
                    }
                }
            }
            updates.entrySet().stream().forEach((Map.Entry<String, UpdateChunk> entry) -> {
                String tableName = entry.getKey();
                UpdateChunk chunk = entry.getValue();
                if (chunk.data != null && !chunk.data.isEmpty()
                        && chunk.keys != null && !chunk.keys.isEmpty()) {
                    chunk.update.clause = String.format(UPDATE_CLAUSE, tableName, chunk.columnsClause.toString(), generateWhereClause(chunk.keys));
                    chunk.update.parameters.addAll(chunk.data);
                    chunk.update.parameters.addAll(chunk.keys);
                    chunk.update.valid = true;
                } else {
                    chunk.update.valid = false;
                }
            });
        }
    }

    @Override
    public void visit(Delete aChange) throws Exception {
        if (!aChange.consumed) {
            Map<String, StatementsLogEntry> deletes = new HashMap<>();
            for (ChangeValue key : aChange.getKeys()) {
                Field field = entitiesHost.resolveField(aChange.entityName, key.name);
                if (field != null) {
                    key.type = field.getTypeInfo();
                    StatementsLogEntry delete = deletes.get(field.getTableName());
                    if (delete == null) {
                        delete = new StatementsLogEntry();
                        deletes.put(field.getTableName(), delete);
                        // Adding here is strongly needed. Because of order in with other and this statememts are added
                        // to the log and therefore applied into a database during a transaction.
                        logEntries.add(delete);
                    }
                    delete.parameters.add(new ChangeValue(field.getOriginalName() != null ? field.getOriginalName() : field.getName(), key.value, key.type));
                }
            }
            deletes.entrySet().stream().forEach((Map.Entry<String, StatementsLogEntry> entry) -> {
                String tableName = entry.getKey();
                StatementsLogEntry delete = entry.getValue();
                delete.clause = String.format(DELETE_CLAUSE, tableName, generateWhereClause(delete.parameters));
                delete.valid = !delete.parameters.isEmpty();
            });
        }
    }

    @Override
    public void visit(Command aChange) throws Exception {
        if (!aChange.consumed) {
            StatementsLogEntry logEntry = new StatementsLogEntry();
            logEntry.clause = aChange.command;
            logEntry.parameters.addAll(aChange.getParameters());
            for (ChangeValue cv : logEntry.parameters) {
                Parameter p = entitiesHost.resolveParameter(aChange.entityName, cv.name);
                if (p != null) {
                    cv.type = p.getTypeInfo();
                }
            }
            logEntries.add(logEntry);
        }
    }
}
