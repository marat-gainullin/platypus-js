/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.jdbc;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.changes.*;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
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
     * Stores short living information about statements, ti be executed while
     * jdbc update process. Performs parameterized statements execution.
     */
    public static class StatementsLogEntry {

        protected static final Logger queriesLogger = Logger.getLogger(StatementsLogEntry.class.getName());
        protected Converter converter;
        public String clause;
        public List<Change.Value> parameters = new ArrayList<>();
        public boolean valid = true;

        public StatementsLogEntry(Converter aConverter) {
            super();
            converter = aConverter;
        }

        public int apply(Connection aConnection) throws Exception {
            if (valid) {
                try (PreparedStatement stmt = aConnection.prepareStatement(clause)) {
                    for (int i = 1; i <= parameters.size(); i++) {
                        Change.Value v = parameters.get(i - 1);
                        converter.convert2JdbcAndAssign(v.value, v.type, aConnection, i, stmt);
                    }
                    if (queriesLogger.isLoggable(Level.FINE)) {
                        queriesLogger.log(Level.FINE, "Executing sql: {0}, with parameters ({1}).", new Object[]{clause, parameters.size()});
                    }
                    return stmt.executeUpdate();
                }
            } else {
                Logger.getLogger(StatementsLogEntry.class.getName()).log(Level.WARNING, "Invalid StatementsLogEntry occured!");
                return 0;
            }
        }
    }
    protected static final String INSERT_CLAUSE = "insert into %s (%s) values (%s)";
    protected static final String DELETE_CLAUSE = "delete from %s where %s";
    protected static final String UPDATE_CLAUSE = "update %s set %s where %s";
    protected List<StatementsLogEntry> logEntries = new ArrayList<>();
    protected Converter converter;
    protected EntitiesHost entitiesHost;
    protected String schemaContextFieldName;
    protected String schemaContext;

    public StatementsGenerator(Converter aConverter, EntitiesHost aEntitiesHost, String aSchemaContextFieldName, String aSchemaContext) {
        super();
        converter = aConverter;
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
     */
    protected String generateWhereClause(List<Change.Value> aKeys) {
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
        entitiesHost.checkRights(aChange.entityId);
        Map<String, InsertChunk> inserts = new HashMap<>();
        for (int i = 0; i < aChange.data.length; i++) {
            Field field = entitiesHost.resolveField(aChange.entityId, aChange.data[i].name);
            if (field != null) {
                InsertChunk chunk = inserts.get(field.getTableName());
                if (chunk == null) {
                    chunk = new InsertChunk();
                    inserts.put(field.getTableName(), chunk);
                    chunk.insert = new StatementsLogEntry(converter);
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
                    if (aChange.data[i] == null) {
                        chunk.insert.parameters.add(new Change.Value(schemaContextFieldName, schemaContext, DataTypeInfo.VARCHAR));
                    } else {
                        chunk.insert.parameters.add(aChange.data[i]);
                    }
                } else {
                    //
                    chunk.insert.parameters.add(aChange.data[i]);
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
                    && schemaContextFieldName != null && schemaContextFieldName.isEmpty()
                    && !chunk.contexted) {
                Field contextField = entitiesHost.resolveField(tableName, schemaContextFieldName);
                if (contextField != null) {
                    if (!chunk.insert.parameters.isEmpty()) {
                        chunk.dataColumnsNames.append(", ");
                    }
                    chunk.dataColumnsNames.append(schemaContextFieldName);
                    chunk.insert.parameters.add(new Change.Value(schemaContextFieldName, schemaContext, DataTypeInfo.VARCHAR));
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

    protected class UpdateChunk {

        public StatementsLogEntry update;
        public StringBuilder columnsClause;
        public List<Change.Value> keys;
        public List<Change.Value> data;
    }

    @Override
    public void visit(Update aChange) throws Exception {
        entitiesHost.checkRights(aChange.entityId);
        Map<String, UpdateChunk> updates = new HashMap<>();
        // data
        for (int i = 0; i < aChange.data.length; i++) {
            Field field = entitiesHost.resolveField(aChange.entityId, aChange.data[i].name);
            if (field != null) {
                UpdateChunk chunk = updates.get(field.getTableName());
                if (chunk == null) {
                    chunk = new UpdateChunk();
                    updates.put(field.getTableName(), chunk);
                    chunk.update = new StatementsLogEntry(converter);
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
                chunk.data.add(aChange.data[i]);
            }
        }
        // keys
        for (int i = 0; i < aChange.keys.length; i++) {
            Field field = entitiesHost.resolveField(aChange.entityId, aChange.keys[i].name);
            if (field != null) {
                UpdateChunk chunk = updates.get(field.getTableName());
                if (chunk != null) {
                    if (chunk.keys == null) {
                        chunk.keys = new ArrayList<>();
                    }
                    chunk.keys.add(new Change.Value(field.getOriginalName() != null ? field.getOriginalName() : field.getName(), aChange.keys[i].value, aChange.keys[i].type));
                }
            }
        }
        for (String tableName : updates.keySet()) {
            UpdateChunk chunk = updates.get(tableName);
            if (chunk.data != null && !chunk.data.isEmpty()
                    && chunk.keys != null) {
                chunk.update.clause = String.format(UPDATE_CLAUSE, tableName, chunk.columnsClause.toString(), generateWhereClause(chunk.keys));
                chunk.update.parameters.addAll(chunk.data);
                chunk.update.parameters.addAll(chunk.keys);
            }
            chunk.update.valid = chunk.data != null && !chunk.data.isEmpty()
                    && chunk.keys != null && !chunk.keys.isEmpty();
        }
    }

    @Override
    public void visit(Delete aChange) throws Exception {
        entitiesHost.checkRights(aChange.entityId);
        Map<String, StatementsLogEntry> deletes = new HashMap<>();
        for (int i = 0; i < aChange.keys.length; i++) {
            Field field = entitiesHost.resolveField(aChange.entityId, aChange.keys[i].name);
            if (field != null) {
                StatementsLogEntry delete = deletes.get(field.getTableName());
                if (delete == null) {
                    delete = new StatementsLogEntry(converter);
                    deletes.put(field.getTableName(), delete);
                    // Adding here is strongly needed. Because of order in with other and this statememts are added
                    // to the log and therefore applied into a database during a transaction.
                    logEntries.add(delete);
                }
                delete.parameters.add(new Change.Value(field.getOriginalName() != null ? field.getOriginalName() : field.getName(), aChange.keys[i].value, aChange.keys[i].type));
            }
        }
        for (String tableName : deletes.keySet()) {
            StatementsLogEntry delete = deletes.get(tableName);
            delete.clause = String.format(DELETE_CLAUSE, tableName, generateWhereClause(delete.parameters));
            delete.valid = !delete.parameters.isEmpty();
        }
    }

    @Override
    public void visit(Command aChange) throws Exception {
        entitiesHost.checkRights(aChange.entityId);
        StatementsLogEntry logEntry = new StatementsLogEntry(converter);
        logEntry.clause = aChange.command;
        logEntry.parameters.addAll(Arrays.asList(aChange.parameters));
        logEntries.add(logEntry);
    }
}
