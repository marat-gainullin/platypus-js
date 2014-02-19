/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.eas.client.DbClient;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.sqldrivers.SqlDriver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class SqlActionsController {

    protected DbClient client;
    protected String dbId;
    protected String schema;

    public SqlActionsController(DbSchemeModel aSchemeModel) throws Exception {
        super();
        if (aSchemeModel != null) {
            client = aSchemeModel.getClient();
            dbId = aSchemeModel.getDbId();
            schema = aSchemeModel.getSchema();
        }
    }

    public String getDbId() {
        return dbId;
    }

    public String getSchema() {
        return schema;
    }

    public void setDbId(String aDbId) {
        dbId = aDbId;
    }

    public void setSchema(String aSchema) {
        schema = aSchema;
    }

    public CreateTableAction createCreateTableAction(String aTableName, String aPkFieldName) {
        return new CreateTableAction(aTableName, aPkFieldName);
    }

    public CreateIndexAction createCreateIndexAction(String aTableName, DbTableIndexSpec aIndex) {
        return new CreateIndexAction(aTableName, aIndex);
    }

    public DropIndexAction createDropIndexAction(String aTableName, DbTableIndexSpec aIndex) {
        return new DropIndexAction(aTableName, aIndex);
    }

    public DefineTableAction createDefineTableAction(String aTableName, Fields aFields) {
        return new DefineTableAction(aTableName, aFields);
    }

    public DropTableAction createDropTableAction(String aTableName) {
        return new DropTableAction(aTableName);
    }

    public CreateConstraintAction createCreateConstraintAction(ForeignKeySpec fk) {
        return new CreateConstraintAction(fk);
    }

    public DropConstraintAction createDropConstraintAction(ForeignKeySpec fk) {
        return new DropConstraintAction(fk);
    }

    public AddFieldAction createAddFieldAction(String aTableName, Field aFieldMd) {
        return new AddFieldAction(aTableName, aFieldMd);
    }

    public DropFieldAction createDropFieldAction(String aTableName, Field aFieldMd) {
        return new DropFieldAction(aTableName, aFieldMd);
    }

    public ModifyFieldAction createModifyFieldAction(String aTableName, Field aOldFieldMd, Field aNewFieldMd) {
        return new ModifyFieldAction(aTableName, aOldFieldMd, aNewFieldMd);
    }

    public RenameFieldAction createRenameFieldAction(String aTableName, String aOldFieldName, Field aNewField) {
        return new RenameFieldAction(aTableName, aOldFieldName, aNewField);
    }

    public DescribeTableAction createDescribeTableAction(String aTableName, String aDescription) {
        return new DescribeTableAction(aTableName, aDescription);
    }

    public DescribeFieldAction createDescribeFieldAction(String aTableName, String aFieldName, String aDescription) {
        return new DescribeFieldAction(aTableName, aFieldName, aDescription);
    }

    public DbClient getClient() {
        return client;
    }

    public void setClient(DbClient aClient) {
        client = aClient;
    }

    public abstract class SqlAction {

        protected String errorMessage = null;

        public String getErrorString() {
            return errorMessage;
        }

        protected abstract List<Change> doSqlWork() throws Exception;

        protected void parseException(Exception ex) {
            if (ex != null) {
                try {
                    SqlDriver driver = achiveSqlDriver();
                    errorMessage = driver.parseException(ex);
                } catch (Exception ex1) {
                    Logger.getLogger(SqlActionsController.class.getName()).log(Level.SEVERE, null, ex1);
                    errorMessage = null;
                }
            }
        }

        public boolean execute() {
            try {
                Map<String, List<Change>> logs = new HashMap<>();
                logs.put(dbId, doSqlWork());
                client.commit(logs);
            } catch (Exception ex) {
                client.rollback();
                parseException(ex);
                return false;
            }
            return true;
        }

        protected SqlDriver achiveSqlDriver() throws Exception {
            return client.getDbMetadataCache(dbId).getConnectionDriver();
        }
    }

    public class CreateConstraintAction extends SqlAction {

        protected ForeignKeySpec fk;

        CreateConstraintAction(ForeignKeySpec aFk) {
            super();
            fk = aFk;
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String sqlCreateConstraintClause = driver.getSql4CreateFkConstraint(schema, fk);
            SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sqlCreateConstraintClause);
            q.enqueueUpdate();
            return q.getFlow().getChangeLog();
        }
    }

    public class DropConstraintAction extends CreateConstraintAction {

        DropConstraintAction(ForeignKeySpec aFk) {
            super(aFk);
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String sqlDropConstraintClause = driver.getSql4DropFkConstraint(schema, fk);
            SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sqlDropConstraintClause);
            q.enqueueUpdate();
            return q.getFlow().getChangeLog();
        }
    }

    public class CreateTableAction extends SqlAction {

        protected String tableName;
        protected String pkFieldName;

        public CreateTableAction(String aTableName, String aPkFieldName) {
            super();
            tableName = aTableName;
            pkFieldName = aPkFieldName;
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String sqlCreateTableClause = driver.getSql4EmptyTableCreation(schema, tableName, pkFieldName);
            SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sqlCreateTableClause);
            q.enqueueUpdate();
            return q.getFlow().getChangeLog();
        }
    }

    public class CreateIndexAction extends SqlAction {

        protected String tableName;
        protected DbTableIndexSpec index;

        public CreateIndexAction(String aTableName, DbTableIndexSpec aIndex) {
            super();
            tableName = aTableName;
            index = aIndex;
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String sqlCreateIndexClause = driver.getSql4CreateIndex(schema, tableName, index);
            SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sqlCreateIndexClause);
            q.enqueueUpdate();
            return q.getFlow().getChangeLog();
        }
    }

    public class DropIndexAction extends SqlAction {

        protected String tableName;
        protected DbTableIndexSpec index;

        public DropIndexAction(String aTableName, DbTableIndexSpec aIndex) {
            super();
            tableName = aTableName;
            index = aIndex;
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String sqlDropIndexClause = driver.getSql4DropIndex(schema, tableName, index.getName());
            SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sqlDropIndexClause);
            q.enqueueUpdate();
            return q.getFlow().getChangeLog();
        }
    }

    public class DefineTableAction extends SqlAction {

        protected String tableName;
        protected Fields fields;

        public DefineTableAction(String aTableName, Fields aFields) {
            super();
            tableName = aTableName;
            fields = aFields;
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String fullName = tableName;
            if (schema != null && !schema.isEmpty()) {
                fullName = schema + "." + fullName;
            }

            String sqlCreateTableClause = "create table " + fullName + " (";
            for (int i = 1; i <= fields.getFieldsCount(); i++) {
                Field fmd = fields.get(i);
                sqlCreateTableClause += driver.getSql4FieldDefinition(fmd);
                if (i < fields.getFieldsCount()) {
                    sqlCreateTableClause += " , ";
                }
            }
            sqlCreateTableClause += " )";
            SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sqlCreateTableClause);
            q.enqueueUpdate();
            return q.getFlow().getChangeLog();
        }
    }

    public class DropTableAction extends SqlAction {

        protected String tableName;

        public DropTableAction(String aTableName) {
            super();
            tableName = aTableName;
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String sqlDropTable = driver.getSql4DropTable(schema, tableName);
            SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sqlDropTable);
            q.enqueueUpdate();
            return q.getFlow().getChangeLog();
        }
    }

    public abstract class FieldAction extends SqlAction {

        protected String tableName = null;
        protected Field fieldMd = null;

        public FieldAction(String aTableName, Field aFieldMd) {
            super();
            tableName = aTableName;
            fieldMd = aFieldMd;
        }
    }

    public class AddFieldAction extends FieldAction {

        public AddFieldAction(String aTableName, Field aFieldMd) {
            super(aTableName, aFieldMd);
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String[] sqls = driver.getSqls4AddingField(schema, tableName, fieldMd);
            List<Change> commonLog = new ArrayList<>();
            for (int i = 0; i < sqls.length; i++) {
                SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sqls[i]);
                q.enqueueUpdate();
                commonLog.addAll(q.getFlow().getChangeLog());
            }
            return commonLog;
        }
    }

    public class DropFieldAction extends FieldAction {

        public DropFieldAction(String aTableName, Field aFieldMd) {
            super(aTableName, aFieldMd);
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String[] sql4DropField = driver.getSql4DroppingField(schema, tableName, fieldMd.getName());
            List<Change> commonLog = new ArrayList<>();
            for (String sql : sql4DropField) {
                SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sql);
                q.enqueueUpdate();
                commonLog.addAll(q.getFlow().getChangeLog());
            }
            return commonLog;
        }
    }

    public class ModifyFieldAction extends FieldAction {

        protected Field newFieldMd = null;

        public ModifyFieldAction(String aTableName, Field aFieldMd, Field aNewFieldMd) {
            super(aTableName, aFieldMd);
            newFieldMd = aNewFieldMd;
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String[] sqls4ModifyField = driver.getSqls4ModifyingField(schema, tableName, fieldMd, newFieldMd);
            List<Change> commonLog = new ArrayList<>();
            for (int i = 0; i < sqls4ModifyField.length; i++) {
                SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sqls4ModifyField[i]);
                q.enqueueUpdate();
                commonLog.addAll(q.getFlow().getChangeLog());
            }
            return commonLog;
        }
    }

    public class RenameFieldAction extends SqlAction {

        protected String tableName;
        protected String oldFieldName;
        protected Field newField;

        public RenameFieldAction(String aTableName, String aOldFieldName, Field aNewField) {
            super();
            tableName = aTableName;
            oldFieldName = aOldFieldName;
            newField = aNewField;
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String[] sqls4RenamingField = driver.getSqls4RenamingField(schema, tableName, oldFieldName, newField);
            List<Change> commonLog = new ArrayList<>();
            for (int i = 0; i < sqls4RenamingField.length; i++) {
                SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sqls4RenamingField[i]);
                q.enqueueUpdate();
                commonLog.addAll(q.getFlow().getChangeLog());
            }
            return commonLog;
        }
    }

    public class DescribeFieldAction extends SqlAction {

        protected String tableName;
        protected String fieldName;
        protected String newDescription;

        public DescribeFieldAction(String aTableName, String aFieldName, String aDescription) {
            super();
            tableName = aTableName;
            fieldName = aFieldName;
            newDescription = aDescription;
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String lschema = client.getDbMetadataCache(dbId).getConnectionSchema();
            String[] sqlsText = driver.getSql4CreateColumnComment(schema != null ? schema : lschema, tableName, fieldName, newDescription);
            List<Change> commonLog = new ArrayList<>();
            for (int i = 0; i < sqlsText.length; i++) {
                SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sqlsText[i]);
                q.enqueueUpdate();
                commonLog.addAll(q.getFlow().getChangeLog());
            }
            return commonLog;
        }
    }

    public class DescribeTableAction extends SqlAction {

        protected String tableName;
        protected String newDescription;

        public DescribeTableAction(String aTableName, String aDescription) {
            super();
            tableName = aTableName;
            newDescription = aDescription;
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String lschema = client.getDbMetadataCache(dbId).getConnectionSchema();

            String sqlText = driver.getSql4CreateTableComment(schema != null ? schema : lschema, tableName, newDescription);
            SqlCompiledQuery q = new SqlCompiledQuery(client, dbId, sqlText);
            q.enqueueUpdate();
            return q.getFlow().getChangeLog();
        }
    }
}
