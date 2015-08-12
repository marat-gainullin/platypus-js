/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure;

import com.eas.client.DatabasesClient;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.changes.Change;
import com.eas.client.changes.Command;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.JdbcField;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.sqldrivers.SqlDriver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class SqlActionsController {

    protected DatabasesClient basesProxy;
    protected String datasourceName;
    protected String schema;

    public SqlActionsController(DbSchemeModel aModel) throws Exception {
        super();
        if (aModel != null) {
            basesProxy = aModel.getBasesProxy();
            datasourceName = aModel.getDatasourceName();
            schema = aModel.getSchema();
        }
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public String getSchema() {
        return schema;
    }

    public void setDatasourceName(String aValue) {
        datasourceName = aValue;
    }

    public void setSchema(String aSchema) {
        schema = aSchema;
    }

    public void tableChanged(String aTable) throws Exception{
        basesProxy.tableChanged(datasourceName, schema != null && !schema.isEmpty() ? schema + "." + aTable : aTable);
    }
    
    public void tableAdded(String aTable) throws Exception{
        basesProxy.tableAdded(datasourceName, schema != null && !schema.isEmpty() ? schema + "." + aTable : aTable);
    }
    
    public void tableRemoved(String aTable) throws Exception{
        basesProxy.tableRemoved(datasourceName, schema != null && !schema.isEmpty() ? schema + "." + aTable : aTable);
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

    public AddFieldAction createAddFieldAction(String aTableName, JdbcField aFieldMd) {
        return new AddFieldAction(aTableName, aFieldMd);
    }

    public DropFieldAction createDropFieldAction(String aTableName, JdbcField aFieldMd) {
        return new DropFieldAction(aTableName, aFieldMd);
    }

    public ModifyFieldAction createModifyFieldAction(String aTableName, JdbcField aOldFieldMd, JdbcField aNewFieldMd) {
        return new ModifyFieldAction(aTableName, aOldFieldMd, aNewFieldMd);
    }

    public RenameFieldAction createRenameFieldAction(String aTableName, String aOldFieldName, JdbcField aNewField) {
        return new RenameFieldAction(aTableName, aOldFieldName, aNewField);
    }

    public DescribeTableAction createDescribeTableAction(String aTableName, String aDescription) {
        return new DescribeTableAction(aTableName, aDescription);
    }

    public DescribeFieldAction createDescribeFieldAction(String aTableName, String aFieldName, String aDescription) {
        return new DescribeFieldAction(aTableName, aFieldName, aDescription);
    }

    public DatabasesClient getBasesProxy() {
        return basesProxy;
    }

    public void setBasesProxy(DatabasesClient aBasesProxy) {
        basesProxy = aBasesProxy;
    }

    public abstract class SqlAction {

        protected String errorMessage;

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
                List<Change> changes = doSqlWork();
                for(Change change : changes){
                    if(change instanceof Command){
                        Command command = (Command)change;
                        if(command.getCommand() == null){
                            throw new SQLException("Unsupported operation. No Platypus sql driver found.");
                        }
                    }
                }
                basesProxy.commit(Collections.singletonMap(datasourceName, changes), null, null);
            } catch (Exception ex) {
                parseException(ex);
                return false;
            }
            return true;
        }

        protected SqlDriver achiveSqlDriver() throws Exception {
            return basesProxy.getMetadataCache(datasourceName).getDatasourceSqlDriver();
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
            SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sqlCreateConstraintClause);
            return Collections.singletonList(q.prepareCommand());
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
            SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sqlDropConstraintClause);
            return Collections.singletonList(q.prepareCommand());
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
            SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sqlCreateTableClause);
            return Collections.singletonList(q.prepareCommand());
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
            SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sqlCreateIndexClause);
            return Collections.singletonList(q.prepareCommand());
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
            SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sqlDropIndexClause);
            return Collections.singletonList(q.prepareCommand());
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
                JdbcField fmd = (JdbcField)fields.get(i);
                sqlCreateTableClause += driver.getSql4FieldDefinition(fmd);
                if (i < fields.getFieldsCount()) {
                    sqlCreateTableClause += " , ";
                }
            }
            sqlCreateTableClause += " )";
            SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sqlCreateTableClause);
            return Collections.singletonList(q.prepareCommand());
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
            SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sqlDropTable);
            return Collections.singletonList(q.prepareCommand());
        }
    }

    public abstract class FieldAction extends SqlAction {

        protected String tableName;
        protected JdbcField fieldMd;

        public FieldAction(String aTableName, JdbcField aFieldMd) {
            super();
            tableName = aTableName;
            fieldMd = aFieldMd;
        }
    }

    public class AddFieldAction extends FieldAction {

        public AddFieldAction(String aTableName, JdbcField aFieldMd) {
            super(aTableName, aFieldMd);
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String[] sqls = driver.getSqls4AddingField(schema, tableName, fieldMd);
            List<Change> commonLog = new ArrayList<>();
            for (String sql : sqls) {
                SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sql);
                commonLog.add(q.prepareCommand());
            }
            return commonLog;
        }
    }

    public class DropFieldAction extends FieldAction {

        public DropFieldAction(String aTableName, JdbcField aFieldMd) {
            super(aTableName, aFieldMd);
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String[] sql4DropField = driver.getSql4DroppingField(schema, tableName, fieldMd.getName());
            List<Change> commonLog = new ArrayList<>();
            for (String sql : sql4DropField) {
                SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sql);
                commonLog.add(q.prepareCommand());
            }
            return commonLog;
        }
    }

    public class ModifyFieldAction extends FieldAction {

        protected JdbcField newFieldMd;

        public ModifyFieldAction(String aTableName, JdbcField aFieldMd, JdbcField aNewFieldMd) {
            super(aTableName, aFieldMd);
            newFieldMd = aNewFieldMd;
        }

        @Override
        protected List<Change> doSqlWork() throws Exception {
            SqlDriver driver = achiveSqlDriver();
            String[] sqls4ModifyField = driver.getSqls4ModifyingField(schema, tableName, fieldMd, newFieldMd);
            List<Change> commonLog = new ArrayList<>();
            for (String sqls4ModifyField1 : sqls4ModifyField) {
                SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sqls4ModifyField1);
                commonLog.add(q.prepareCommand());
            }
            return commonLog;
        }
    }

    public class RenameFieldAction extends SqlAction {

        protected String tableName;
        protected String oldFieldName;
        protected JdbcField newField;

        public RenameFieldAction(String aTableName, String aOldFieldName, JdbcField aNewField) {
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
            for (String sqls4RenamingField1 : sqls4RenamingField) {
                SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sqls4RenamingField1);
                commonLog.add(q.prepareCommand());
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
            String lschema = basesProxy.getMetadataCache(datasourceName).getDatasourceSchema();
            String[] sqlsText = driver.getSql4CreateColumnComment(schema != null ? schema : lschema, tableName, fieldName, newDescription);
            List<Change> commonLog = new ArrayList<>();
            for (String sqlsText1 : sqlsText) {
                SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sqlsText1);
                commonLog.add(q.prepareCommand());
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
            String lschema = basesProxy.getMetadataCache(datasourceName).getDatasourceSchema();

            String sqlText = driver.getSql4CreateTableComment(schema != null ? schema : lschema, tableName, newDescription);
            SqlCompiledQuery q = new SqlCompiledQuery(basesProxy, datasourceName, sqlText);
            return Collections.singletonList(q.prepareCommand());
        }
    }
}
