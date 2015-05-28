/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata;

import com.eas.client.ClientConstants;
import com.eas.client.DatabaseMdCache;
import com.eas.client.DatabasesClient;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.changes.Change;
import com.eas.client.dataflow.ColumnsIndicies;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.ForeignKeySpec.ForeignKeyRule;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.Db2SqlDriver;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vy
 */
public class MetadataMerger {

    private boolean noDropTables;
    private Set<String> listTables = new HashSet<>();
    private boolean noExecuteSQL;
    private DatabasesClient client;
    private Map<String, TableStructure> srcMD;
    private Map<String, TableStructure> destMD;
    private String srcDialect;
    private String destDialect;
    private boolean oneDialect;
    private SqlDriver driver;
    private String dSchema;
    private String sqlCommandEndChars = ";";
    private String sqlCommentChars = "-- ";
    private Logger systemLogger;
    private Logger sqlLogger;
    private Logger errorLogger;
    private Map<String, List<ForeignKeySpec>> pKeysMap = new HashMap<>(); // relation for pkey name -> fkey in destination (!! tableName->fkeys)
    private Set<String> removedFKeys = new HashSet<>();  // for removed  Foreign keys
    private String temporaryPKFieldName = "ID";
    private long numSql = 1L;
    private Map<String, Map<String, Set<String>>> indexedColumnsMap = new HashMap<>(); // tableName->columnName->indexName
    private Set<String> removedIndexes = new HashSet<>();  // for removed indexes
    private Map<String, Set<String>> changedColumnsMap = new HashMap<>();
    private Map<String, Set<String>> droppedColumnsMap = new HashMap<>();
    private List<String> sqlsList;

    /**
     * merge metadata databases
     *
     * @param aClient connection to database
     * @param srcMetadata metadata for source
     * @param destMetadata metadata for destination
     * @param aNoExecuteSQL if true then no execute generated sql
     * @param aNoDropTables if true then no execute drop table
     * @param aListTables list tables for work
     */
    public MetadataMerger(DatabasesClient aClient, String destSchema, DBStructure srcMetadata, DBStructure destMetadata, boolean aNoExecuteSQL, boolean aNoDropTables, Set<String> aListTables, Logger aSystemLogger, Logger aSqlLogger, Logger aErrorLogger, boolean createSqlsList) throws Exception {
        super();
        client = aClient;
        assert client != null;
        if (srcMetadata != null) {
            srcMD = srcMetadata.getTablesStructure();
            srcDialect = srcMetadata.getDatabaseDialect();
        }
        if (destMetadata != null) {
            destMD = destMetadata.getTablesStructure();
            destDialect = destMetadata.getDatabaseDialect();
        }
        if (srcDialect != null && !srcDialect.isEmpty()) {
            oneDialect = srcDialect.equalsIgnoreCase(destDialect);
        }

        noExecuteSQL = aNoExecuteSQL;
        noDropTables = aNoDropTables;
        if (aListTables != null) {
            listTables = aListTables;
        }

        DatabaseMdCache mdCache = client.getDbMetadataCache(null);
        driver = mdCache.getConnectionDriver();
        dSchema = destSchema;

        systemLogger = aSystemLogger;
        sqlLogger = aSqlLogger;
        errorLogger = aErrorLogger;
        if (createSqlsList) {
            sqlsList = new ArrayList<>();
        }
    }

    /**
     * runner for merge database structure
     */
    public void run() {

        TypesResolver dTypesResolver = driver.getTypesResolver();

        // step 1 - prepare all data
        // get relation for pkey -> fkey, table -> field -> index
        log(Level.INFO, "Merge metadata: step 1 of 10 - prepare");
        for (String tableNameUpper : destMD.keySet()) {
            TableStructure dTableStructure = destMD.get(tableNameUpper);
            assert dTableStructure != null;
            // pkey -> fkey
            Map<String, List<ForeignKeySpec>> dTableFKeySpecs = dTableStructure.getTableFKeySpecs();
            if (dTableFKeySpecs != null) {
                // each fkey spec
                for (String fKeyCName : dTableFKeySpecs.keySet()) {
                    for (ForeignKeySpec fkSpec : dTableFKeySpecs.get(fKeyCName)) {
                        String pkTableName = fkSpec.getReferee().getTable().toUpperCase();
                        List<ForeignKeySpec> fks = pKeysMap.get(pkTableName);
                        if (fks == null) {
                            fks = new ArrayList<>();
                            pKeysMap.put(pkTableName, fks);  //pkey -> fk spec
                        }
                        fks.add(fkSpec);
                    }
                }
            }
            // table -> field -> index
            Map<String, DbTableIndexSpec> tableIndexes = dTableStructure.getTableIndexSpecs();
            if (tableIndexes != null && !tableIndexes.isEmpty()) {
                Map<String, Set<String>> tableIndexedColumns = new HashMap<>();
                // each index
                for (String indexName : tableIndexes.keySet()) {
                    DbTableIndexSpec indexSpec = tableIndexes.get(indexName);
                    String fKeyName = indexSpec.getFKeyName();
                    if (!indexSpec.isPKey() && (fKeyName == null || fKeyName.isEmpty())) {
                        for (DbTableIndexColumnSpec column : indexSpec.getColumns()) {
                            String columnName = column.getColumnName().toUpperCase();
                            Set<String> indexNames = tableIndexedColumns.get(columnName);
                            if (indexNames == null) {
                                indexNames = new HashSet<>();
                            }
                            indexNames.add(indexName);
                            tableIndexedColumns.put(columnName, indexNames);
                        }
                    }
                }
                indexedColumnsMap.put(tableNameUpper, tableIndexedColumns);
            }
        }
        temporaryPKFieldName = temporaryPKFieldName.toUpperCase();   // for check on create new table

        // step 2 - drop all not existed tables
        if (noDropTables == false) {
            log(Level.INFO, "Merge metadata: step 2 of 10 - drop all not existed tables");
            for (String tableNameUpper : destMD.keySet()) {
                if (listTables.isEmpty() || listTables.contains(tableNameUpper)) {
                    if (!srcMD.containsKey(tableNameUpper)) {
                        int cntErrors = 0;
                        // table not exists
                        TableStructure dTableStructure = destMD.get(tableNameUpper);
                        List<PrimaryKeySpec> dTablePKeySpecs = dTableStructure.getTablePKeySpecs();
                        if (dTablePKeySpecs != null) {
                            for (int i = 0; i < dTablePKeySpecs.size(); i++) {
                                String pKeyTableName = dTablePKeySpecs.get(i).getTable();
                                cntErrors += dropAllLinkedFKeys(pKeyTableName, "step 2.1 - drop linked keys");
                                // primary keys === 1
                                break;
                            }
                        }
                        // drop table
                        String tableName = dTableStructure.getTableName();
                        String sql4DropTable = driver.getSql4DropTable(dSchema, tableName);
                        cntErrors += executeSQL(sql4DropTable, "step 2.2 - drop table");
                        // mark all foreign keys as deleleted
                        Map<String, List<ForeignKeySpec>> tableFKeySpecs = dTableStructure.getTableFKeySpecs();
                        if (tableFKeySpecs != null) {
                            for (String cFKName : tableFKeySpecs.keySet()) {
                                String fKeyNameUpper = cFKName.toUpperCase();
                                if (!removedFKeys.contains(fKeyNameUpper)) {
                                    removedFKeys.add(fKeyNameUpper);
                                }
                            }
                        }
                        printLog(tableNameUpper, cntErrors);
                    }
                }
            }
        }
        // step 3 - create new table, add new fields, prepare drop fields
        log(Level.INFO, "Merge metadata: step 3 of 10 - create new table and fields");
        for (String tableNameUpper : srcMD.keySet()) {
            if (listTables.isEmpty() || listTables.contains(tableNameUpper)) {
                int cntErrors = 0;
                boolean processed = false;
                TableStructure sTableStructure = srcMD.get(tableNameUpper);
                assert sTableStructure != null;
                TableStructure dTableStructure = destMD.get(tableNameUpper);

                Fields sFields = sTableStructure.getTableFields();
                assert sFields != null;
                Fields dFields = null;
                String pkFieldName = "";
                String sTableName = sTableStructure.getTableName();
                String dTableName = sTableName;   // default names are equal
                Set<String> changedFieldNames = new HashSet<>();
                Set<String> droppedFieldNames = new HashSet<>();

                if (dTableStructure == null) {
                    // create new table
                    // generate unique name pk column (id,id0,id1,..)
                    int iPK = 0;
                    pkFieldName = temporaryPKFieldName;
                    // generated unique pkfield name
                    while (sTableStructure.getOriginalFieldName(pkFieldName) != null) {
                        pkFieldName = temporaryPKFieldName + iPK;
                        iPK++;
                    }
                    String sql4EmptyTableCreation = driver.getSql4EmptyTableCreation(dSchema, sTableName, pkFieldName);
                    cntErrors += executeSQL(sql4EmptyTableCreation, "step 3.1 - create table");
                    // drop temporary PKey
                    cntErrors += dropPKey(dSchema, sTableName, pkFieldName, sTableName + SqlDriver.PKEY_NAME_SUFFIX, "step 3.2 - drop temporary primary key");
                    processed = true;
                } else {
                    dFields = dTableStructure.getTableFields();
                    dTableName = dTableStructure.getTableName();
                }

                // for all fields source
                for (int i = 1; i <= sFields.getFieldsCount(); i++) {
                    Field sField = sFields.get(i);
                    String sFieldName = sField.getName();
                    String fieldNameUpper = sFieldName.toUpperCase();

                    Field dField = null;
                    if (dFields != null) {
                        dField = dFields.get(fieldNameUpper);
                    }
                    // field not exist
                    if (dField == null) {
                        // add new field
                        sField.setSchemaName(dSchema);
                        sField.setTableName(dTableName);
                        sField.setFk(null);
                        sField.setPk(false);
                        dField = sField.copy();
                        dField.setNullable(true);

                        String[] sqls4AddingField = driver.getSqls4AddingField(dSchema, dTableName, dField);
                        cntErrors += executeSQL(sqls4AddingField, "step 3.3 - add new column");
                        // set not null
                        if (!sField.isNullable()) {
                            String[] sqls4ModifyingField = driver.getSqls4ModifyingField(dSchema, dTableName, dField, sField);
                            cntErrors += executeSQL(sqls4ModifyingField, "step 3.4 - alter new column: set not null");
                        }
                        processed = true;
                    } else {
                        // check for modify field
                        String sqlTypeName = dField.getTypeInfo().getSqlTypeName();
                        Field sFieldCopy = sField.copy();
                        dTypesResolver.resolve2RDBMS(sFieldCopy);
                        if (!MetadataUtils.isSameField(sFieldCopy, dField, dTypesResolver.isSized(sqlTypeName), dTypesResolver.isScaled(sqlTypeName), oneDialect)) {
                            changedFieldNames.add(fieldNameUpper);
                        }
                    }
                }
                // drop temporary id
                if (!pkFieldName.isEmpty()) {
                    String[] sql4DroppingField = driver.getSql4DroppingField(dSchema, dTableName, pkFieldName);
                    cntErrors += executeSQL(sql4DroppingField, "step 3.5 - drop temporary pkey column");
                }
                //drop fields
                if (dFields != null) {
                    assert dTableStructure != null;
                    // for all fields destination
                    for (int i = 1; i <= dFields.getFieldsCount(); i++) {
                        Field dField = dFields.get(i);
                        String dFieldName = dField.getName();
                        String fieldNameUpper = dFieldName.toUpperCase();
                        // not exist in source
                        if (sFields.get(fieldNameUpper) == null) {
                            // drop pkey
                            if (dField.isPk()) {
                                String pKeyTableName = dTableStructure.getTableName().toUpperCase();
                                if (pKeyTableName != null && !pKeyTableName.isEmpty()) {
                                    cntErrors += dropAllLinkedFKeys(pKeyTableName, "step 3.6 - drop linked foreing key for removed column");
                                    List<PrimaryKeySpec> pKeySpecs = dTableStructure.getTablePKeySpecs();
                                    if (pKeySpecs != null && !pKeySpecs.isEmpty()) {
                                        cntErrors += dropPKey(dSchema, dTableName, dFieldName, pKeySpecs.get(0).getCName(), "step 3.7 - drop primary key for removed column");
                                        dTableStructure.setTablePKeySpecs(null);
                                    }
                                }
                            }
                            // drop fkeys
                            Map<String, List<ForeignKeySpec>> tableFKeySpecs = dTableStructure.getTableFKeySpecs();
                            if (tableFKeySpecs != null && !tableFKeySpecs.isEmpty()) {
                                for (String fkeyName : tableFKeySpecs.keySet()) {
                                    for (ForeignKeySpec fkeySpec : tableFKeySpecs.get(fkeyName)) {
                                        if (fieldNameUpper.equalsIgnoreCase(fkeySpec.getField())) {
                                            removedFKeys.add(fkeyName.toUpperCase());
                                            //????????????????????? удалить FK ???????
                                            //  String sql4DropFkConstraint = driver.getSql4DropFkConstraint(dSchema, fkeySpec);
                                            //  cntErrors += executeSQL(sql4DropFkConstraint, "step 3 - 5.5");
                                            break;
                                        }
                                    }
                                }
                            }
                            droppedFieldNames.add(fieldNameUpper);
                        }
                    }
                }
                if (!changedFieldNames.isEmpty()) {
                    changedColumnsMap.put(tableNameUpper, changedFieldNames);
                }
                if (!droppedFieldNames.isEmpty()) {
                    droppedColumnsMap.put(tableNameUpper, droppedFieldNames);
                }

                if (processed) {
                    printLog(tableNameUpper, cntErrors);
                }
            }
        }

        // step 4 - drop foreign keys
        log(Level.INFO, "Merge metadata: step 4 of 10 - drop foreign keys");
        for (String tableNameUpper : srcMD.keySet()) {
            if (listTables.isEmpty() || listTables.contains(tableNameUpper)) {
                int cntErrors = 0;
                boolean processed = false;
                TableStructure sTableStructure = srcMD.get(tableNameUpper);
                assert sTableStructure != null;
                TableStructure dTableStructure = destMD.get(tableNameUpper);
                if (dTableStructure != null) {
                    Set<String> changedColumns = changedColumnsMap.get(tableNameUpper);
                    // drop  fkeys
                    Map<String, List<ForeignKeySpec>> sFKeySpecs = sTableStructure.getTableFKeySpecs();
                    Map<String, List<ForeignKeySpec>> dFKeySpecs = dTableStructure.getTableFKeySpecs();
                    if (dFKeySpecs != null && !dFKeySpecs.isEmpty()) {
                        //for each fkey spec
                        for (String dFKeyName : dFKeySpecs.keySet()) {
                            List<ForeignKeySpec> dFKeyColumns = dFKeySpecs.get(dFKeyName);
                            assert dFKeyColumns != null;
                            assert !dFKeyColumns.isEmpty();
                            List<ForeignKeySpec> sFKeyColumns = null;
                            if (sFKeySpecs != null) {
                                sFKeyColumns = sFKeySpecs.get(sTableStructure.getOriginalFKeyName(dFKeyName.toUpperCase()));
                            }
                            if (!MetadataUtils.isSameFKeys(dFKeyColumns, sFKeyColumns, oneDialect)) {
                                cntErrors += dropFKey(dFKeyColumns.get(0), "step 4.1 - drop foreign key");
                                processed = true;
                            } else {
                                // check for change columns
                                if (changedColumns != null && !changedColumns.isEmpty()) {
                                    for (ForeignKeySpec fkey : dFKeyColumns) {
                                        String fieldName = fkey.getField();
                                        if (changedColumns.contains(fieldName.toUpperCase())) {
                                            cntErrors += dropFKey(dFKeyColumns.get(0), "step 4.2 - drop foreign key");
                                            processed = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (processed) {
                    printLog(tableNameUpper, cntErrors);
                }
            }
        }

        // reread indexes
        Set<String> tableNames = new HashSet<>();
        Set<String> tableNamesUpper = new HashSet<>();
        if (!listTables.isEmpty()) {
            tableNamesUpper.addAll(listTables);
        } else {
            tableNamesUpper.addAll(destMD.keySet());
        }
        for (String tableNameUpper : tableNamesUpper) {
            TableStructure tbl = destMD.get(tableNameUpper);
            if (tbl != null) {
                tableNames.add(tbl.getTableName());
            }
        }
        Map<String, Set<String>> newIndexState = readIndexNames(dSchema, tableNames);

        // step 5 - drop  indexes
        log(Level.INFO, "Merge metadata: step 5 of 10 - drop indexes");
        for (String tableNameUpper : newIndexState.keySet()) {
            int cntErrors = 0;
            boolean processed = false;
            TableStructure sTableStructure = srcMD.get(tableNameUpper);
            TableStructure dTableStructure = destMD.get(tableNameUpper);
            assert sTableStructure != null;
            assert dTableStructure != null;
            Map<String, DbTableIndexSpec> sIndexSpecs = sTableStructure.getTableIndexSpecs();
            Map<String, DbTableIndexSpec> dIndexSpecs = dTableStructure.getTableIndexSpecs();
            assert dIndexSpecs != null;
            Set<String> changedColumns = changedColumnsMap.get(tableNameUpper);
            for (String indexNameUpper : newIndexState.get(tableNameUpper)) {
                DbTableIndexSpec dIndex = dIndexSpecs.get(dTableStructure.getOriginalIndexName(indexNameUpper));
                DbTableIndexSpec sIndex = null;
                if (sIndexSpecs != null) {
                    sIndex = sIndexSpecs.get(sTableStructure.getOriginalIndexName(indexNameUpper));
                }
                if (!MetadataUtils.isSameIndex(dIndex, sIndex, oneDialect)) {
                    cntErrors += dropIndex(dSchema, dTableStructure.getTableName(), dTableStructure.getOriginalIndexName(indexNameUpper), "step 5.1 - drop index");
                    processed = true;
                } else {
                    // check for change columns
                    if (changedColumns != null && !changedColumns.isEmpty()) {
                        for (DbTableIndexColumnSpec column : dIndex.getColumns()) {
                            String columnName = column.getColumnName();
                            if (changedColumns.contains(columnName.toUpperCase())) {
                                cntErrors += dropIndex(dSchema, dTableStructure.getTableName(), dTableStructure.getOriginalIndexName(indexNameUpper), "step 5.2 - drop index");
                                processed = true;
                            }
                        }
                    }
                }
            }

            if (processed) {
                printLog(tableNameUpper, cntErrors);
            }
        }

        // step 6 - drop  fields
        log(Level.INFO, "Merge metadata: step 6 of 10 - drop columns");
        for (String tableNameUpper : droppedColumnsMap.keySet()) {
            int cntErrors = 0;
            TableStructure dTableStructure = destMD.get(tableNameUpper);
            assert dTableStructure != null;
            String tableName = dTableStructure.getTableName();
            Map<String, Set<String>> indexedColumns = indexedColumnsMap.get(tableNameUpper);
            for (String columnNameUpper : droppedColumnsMap.get(tableNameUpper)) {
                // check linked indexes and drop
                if (indexedColumns != null) {
                    Set<String> indexes = indexedColumns.get(columnNameUpper);
                    if (indexes != null) {
                        for (String indexName : indexes) {
                            dropIndex(dSchema, tableName, dTableStructure.getOriginalIndexName(indexName.toUpperCase()), "step 6.1 - drop index for removed column");
                        }
                    }
                }
                // drop field
                String[] sql4DroppingField = driver.getSql4DroppingField(dSchema, tableName, dTableStructure.getOriginalFieldName(columnNameUpper));
                cntErrors += executeSQL(sql4DroppingField, "step 6.2 - drop column");
            }
            printLog(tableNameUpper, cntErrors);
        }

        // step 7 - modify columns, create and modify primary key
        log(Level.INFO, "Merge metadata: step 7 of 10 - modify columns");
        for (String tableNameUpper : srcMD.keySet()) {
            if (listTables.isEmpty() || listTables.contains(tableNameUpper)) {
                int cntErrors = 0;
                boolean processed = false;
                TableStructure sTableStructure = srcMD.get(tableNameUpper);
                TableStructure dTableStructure = destMD.get(tableNameUpper);
                assert sTableStructure != null;
                List<PrimaryKeySpec> sPKeySpecs = sTableStructure.getTablePKeySpecs();
                List<PrimaryKeySpec> dPKeySpecs = null;
                String dTableName = sTableStructure.getTableName(); // default is equals
                if (dTableStructure != null) {
                    dPKeySpecs = dTableStructure.getTablePKeySpecs();
                    dTableName = dTableStructure.getTableName();
                    if (changedColumnsMap.containsKey(tableNameUpper)) {
                        Set<String> changedColumns = changedColumnsMap.get(tableNameUpper);
                        Fields sFields = sTableStructure.getTableFields();
                        Fields dFields = dTableStructure.getTableFields();
                        assert sFields != null;
                        assert dFields != null;
                        for (String columnsNameUpper : changedColumns) {
                            String sFieldName = sTableStructure.getOriginalFieldName(columnsNameUpper);
                            String dFieldName = dTableStructure.getOriginalFieldName(columnsNameUpper);
                            Field sField = sFields.get(sFieldName);
                            Field dField = dFields.get(dFieldName);
                            assert sField != null;
                            assert dField != null;
                            // drop pKey
                            if (dField.isPk() && dPKeySpecs != null) {
                                cntErrors += dropAllLinkedFKeys(tableNameUpper, "step 7.1 - drop linked foreign key");
                                cntErrors += dropPKey(dSchema, dTableName, dFieldName, dPKeySpecs.get(0).getCName(), "step 7.2 - drop primary key");
                                dPKeySpecs = null;
                            }
                            sField.setTableName(dTableName);
                            sField.setSchemaName(dSchema);
                            sField.setPk(false);
                            sField.setFk(null);
                            String[] sqls4ModifyingField = driver.getSqls4ModifyingField(dSchema, dTableName, dField, sField);
                            cntErrors += executeSQL(sqls4ModifyingField, "step 7.3 - modify columns");

                            //!!!!!!!!!!!!!!!!!!!! только в MySQL и H2 !!!!!!!!!!!!!!!!!!!!
                            //!!!!!!!!!!!!!!!!! пока нет default value !!!!!!!!!!!!!!!
                            if ("MySql".equalsIgnoreCase(destDialect) || "H2".equalsIgnoreCase(destDialect)) {
                                for (int i = 1; i <= dFields.getLength(); i++) {
                                    Field fld = dFields.get(i);
                                    if (dFieldName.equalsIgnoreCase(fld.getName())) {
                                        fld.setDescription("");
                                        break;
                                    }
                                }
                            }
                            processed = true;
                        }
                    }
                    // modify pkey (drop+create)
                    if (dPKeySpecs != null && !MetadataUtils.isSamePKeys(dPKeySpecs, sPKeySpecs, oneDialect)) {
                        PrimaryKeySpec dPKey0 = dPKeySpecs.get(0);
                        assert dPKey0 != null;
                        cntErrors += dropAllLinkedFKeys(tableNameUpper, "step 7.3 - drop linked foreign key");
                        cntErrors += dropPKey(dSchema, dTableName, dPKey0.getField(), dPKey0.getCName(), "step 7.4 - drop primary key");
                        dPKeySpecs = null;
                        processed = true;
                    }
                }
                // create pkey
                if (sPKeySpecs != null && dPKeySpecs == null) {
                    for (PrimaryKeySpec pKey : sPKeySpecs) {
                        pKey.setSchema(dSchema);
                        pKey.setTable(dTableName);
                    }
                    String[] sql4CreatePkConstraint = driver.getSql4CreatePkConstraint(dSchema, sPKeySpecs);
                    cntErrors += executeSQL(sql4CreatePkConstraint, "step 7.5 - create primary key");
                    processed = true;
                }
                if (processed) {
                    printLog(tableNameUpper, cntErrors);
                }
            }
        }

        // step 8 - create indexes and fkeys
        log(Level.INFO, "Merge metadata: step 8 of 10 - create indexes and foreign keys");
        for (String tableNameUpper : srcMD.keySet()) {
            if (listTables.isEmpty() || listTables.contains(tableNameUpper)) {
                int cntErrors = 0;
                boolean processed = false;
                TableStructure sTableStructure = srcMD.get(tableNameUpper);
                TableStructure dTableStructure = destMD.get(tableNameUpper);
                assert sTableStructure != null;
                String dTableName = sTableStructure.getTableName(); // default equal

                // fkeys
                Map<String, List<ForeignKeySpec>> sTableFKeySpecs = sTableStructure.getTableFKeySpecs();
                Map<String, List<ForeignKeySpec>> dTableFKeySpecs = null;
                if (dTableStructure != null) {
                    dTableFKeySpecs = dTableStructure.getTableFKeySpecs();
                }
                if (sTableFKeySpecs != null) {
                    //for each fkey spec
                    for (String sFKeyName : sTableFKeySpecs.keySet()) {
                        String fKeyNameUpper = sFKeyName.toUpperCase();
                        List<ForeignKeySpec> sFKeySpecs = sTableFKeySpecs.get(sFKeyName);

                        List<ForeignKeySpec> dFKeySpecs = null;
                        if (dTableFKeySpecs != null) {
                            assert dTableStructure != null;
                            String initialNameFKey = dTableStructure.getOriginalFKeyName(fKeyNameUpper);
                            if (initialNameFKey != null) {
                                dFKeySpecs = dTableFKeySpecs.get(initialNameFKey);
                            }
                        }

                        if (sFKeySpecs != null && (dFKeySpecs == null || removedFKeys.contains(sFKeySpecs.get(0).getCName().toUpperCase()))) {
                            for (int i = 0; i < sFKeySpecs.size(); i++) {
                                ForeignKeySpec fKeySpec = sFKeySpecs.get(i);
                                fKeySpec.setSchema(dSchema);
                                fKeySpec.setTable(dTableName);
                                PrimaryKeySpec referee = fKeySpec.getReferee();
                                referee.setSchema(dSchema);
                                String refereeTableName = referee.getTable();
                                TableStructure refereeTableStructure = destMD.get(refereeTableName.toUpperCase());
                                if (refereeTableStructure != null) {
                                    referee.setTable(refereeTableStructure.getTableName());
                                }
                                // !!! in Oracle not exists UpdateRule !!!!!
                                ForeignKeyRule fkUpdateRule = fKeySpec.getFkUpdateRule();
                                if (fkUpdateRule == null) {
                                    fKeySpec.setFkUpdateRule(ForeignKeyRule.NOACTION);
                                }

                            }
                            String sql4CreateFkConstraint = driver.getSql4CreateFkConstraint(dSchema, sFKeySpecs);
                            cntErrors += executeSQL(sql4CreateFkConstraint, "step 8.1 - create foreign key");
                            processed = true;
                        }
                    }
                }
                // indexes
                Map<String, DbTableIndexSpec> sIndexes = sTableStructure.getTableIndexSpecs();
                Map<String, DbTableIndexSpec> dIndexes = null;
                if (dTableStructure != null) {
                    dIndexes = dTableStructure.getTableIndexSpecs();
                    dTableName = dTableStructure.getTableName();
                }

                if (sIndexes != null) {
                    for (String indexName : sIndexes.keySet()) {
                        String indexNameUpper = indexName.toUpperCase();
                        DbTableIndexSpec sIndex = sIndexes.get(sTableStructure.getOriginalIndexName(indexNameUpper));
                        assert sIndex != null;
                        DbTableIndexSpec dIndex = null;
                        String fKeyName = sIndex.getFKeyName();
                        if (!sIndex.isPKey() && (fKeyName == null || fKeyName.isEmpty())) {
                            if (dIndexes != null) {
                                assert dTableStructure != null;
                                dIndex = dIndexes.get(dTableStructure.getOriginalIndexName(indexNameUpper));
                            }
                            if (dIndex == null || removedIndexes.contains(indexNameUpper)) {
                                String sql4CreateIndex = driver.getSql4CreateIndex(dSchema, dTableName, sIndex);
                                cntErrors += executeSQL(sql4CreateIndex, "step 8.2 - create index");
                                processed = true;
                            }
                        }
                    }
                }
                if (processed) {
                    printLog(tableNameUpper, cntErrors);
                }
            }
        }
        // step 9 - set descriptions on tables and fields
        log(Level.INFO, "Merge metadata: step 9 of 10 - set descriptions on tables and columns");
        for (String tableNameUpper : srcMD.keySet()) {
            if (listTables.isEmpty() || listTables.contains(tableNameUpper)) {
                int cntErrors = 0;
                boolean processed = false;
                TableStructure sTableStructure = srcMD.get(tableNameUpper);
                TableStructure dTableStructure = destMD.get(tableNameUpper);
                assert sTableStructure != null;
                String sTableName = sTableStructure.getTableName();
                String dTableName = sTableName;
                String sTableDescription = sTableStructure.getTableDescription();
                if (sTableDescription == null) {
                    sTableDescription = "";
                }
                String dTableDescription = "";
                Fields sFields = sTableStructure.getTableFields();
                assert sFields != null;
                Fields dFields = null;
                if (dTableStructure != null) {
                    dTableName = dTableStructure.getTableName();
                    dTableDescription = dTableStructure.getTableDescription();
                    dFields = dTableStructure.getTableFields();
                    if (dTableDescription == null) {
                        dTableDescription = "";
                    }
                }
                if (!sTableDescription.equals(dTableDescription)) {
                    // set description
                    String sql4CreateTableComment = driver.getSql4CreateTableComment(dSchema, dTableName, sTableDescription);
                    cntErrors += executeSQL(sql4CreateTableComment, "step 9.1 - set table description");
                    processed = true;
                }
                for (int i = 1; i <= sFields.getFieldsCount(); i++) {
                    Field sField = sFields.get(i);
                    String sFieldName = sField.getName();
                    String fieldNameUpper = sFieldName.toUpperCase();

                    String sFieldDescription = sField.getDescription();
                    if (sFieldDescription == null) {
                        sFieldDescription = "";
                    }
                    Field dField = null;
                    String dFieldDescription = "";
                    if (dFields != null) {
                        dField = dFields.get(fieldNameUpper);
                    }
                    // field not exist
                    if (dField != null) {
                        dFieldDescription = dField.getDescription();
                        if (dFieldDescription == null) {
                            dFieldDescription = "";
                        }
                    }
                    if (!sFieldDescription.equals(dFieldDescription)) {
                        // set description
                        String[] sql4CreateColumnComment = driver.getSql4CreateColumnComment(dSchema, dTableName, fieldNameUpper, sFieldDescription);
                        cntErrors += executeSQL(sql4CreateColumnComment, "step 9.2 - set column description");
                        processed = true;
                    }
                }
                if (processed) {
                    printLog(tableNameUpper, cntErrors);
                }
            }
        }
        // last step  - restore dropped fkeys
        log(Level.INFO, "Merge metadata: step 10 of 10 - restore dropped fkeys");
        if (!listTables.isEmpty()) {
            for (String tableNameUpper : destMD.keySet()) {
                if (!listTables.contains(tableNameUpper)) {
                    TableStructure dTableStructure = destMD.get(tableNameUpper);
                    int cntErrors = 0;
                    boolean processed = false;
                    Map<String, List<ForeignKeySpec>> fKeys = dTableStructure.getTableFKeySpecs();
                    if (fKeys != null && !fKeys.isEmpty()) {
                        for (String fKeyName : fKeys.keySet()) {
                            if (removedFKeys.contains(fKeyName.toUpperCase())) {
                                List<ForeignKeySpec> fkey = fKeys.get(fKeyName);
                                String sql4CreateFkConstraint = driver.getSql4CreateFkConstraint(dSchema, fkey);
                                cntErrors += executeSQL(sql4CreateFkConstraint, "step 10.1 - restore fkey");
                                processed = true;
                            }
                        }
                    }

                    if (processed) {
                        printLog(tableNameUpper, cntErrors);
                    }
                }
            }
        }
        //???????
        if (driver instanceof Db2SqlDriver && numSql > 0) {
            executeSQL("commit", "commit");
        }
        log(Level.INFO, "Merge metadata finished");
    }

    /**
     * drop all foreign keys linked with primary pey pKeyCName
     *
     * @param pKeyTableName table name for primary key
     */
    private int dropAllLinkedFKeys(String pKeyTableName, String aLogMessage) {
        int cntErrors = 0;
        List<ForeignKeySpec> fkSpecList = pKeysMap.get(pKeyTableName);
        if (fkSpecList != null) {
            for (ForeignKeySpec fkSpec : pKeysMap.get(pKeyTableName)) {
                String cName = fkSpec.getCName().toUpperCase();
                cntErrors += dropFKey(fkSpec, aLogMessage);
            }
        }
        return cntErrors;

    }

    private int dropFKey(ForeignKeySpec aFKey, String aLogMessage) {
        int cntErrors = 0;

        if (aFKey != null) {
            String cName = aFKey.getCName().toUpperCase();

            if (!removedFKeys.contains(cName)) {
                removedFKeys.add(cName);
                String sql4DropFkConstraint = driver.getSql4DropFkConstraint(dSchema, aFKey);
                cntErrors += executeSQL(sql4DropFkConstraint, aLogMessage);
            }
        }
        return cntErrors;
    }

    private int dropIndex(String aSchemaName, String aTableName, String aIndexName, String aLogMessage) {
        int cntErrors = 0;
        String indexNameUpper = aIndexName.toUpperCase();
        if (!removedIndexes.contains(indexNameUpper)) {
            String sql4DropIndex = driver.getSql4DropIndex(aSchemaName, aTableName, aIndexName);
            cntErrors += executeSQL(sql4DropIndex, aLogMessage);
            removedIndexes.add(indexNameUpper);
        }
        return cntErrors;
    }

    /**
     * drop primary pey pKeyCName (ONLY FOR TEMPORARY COLUMN ID !!!!!!)
     *
     */
    private int dropPKey(String aSchemaName, String aTableName, String aFieldName, String aPKeyCName, String aLogMessage) {
        int cntErrors = 0;
        PrimaryKeySpec pkSpec = new PrimaryKeySpec(aSchemaName, aTableName, aFieldName, aPKeyCName);
        String sql4DropPkConstraint = driver.getSql4DropPkConstraint(aSchemaName, pkSpec);
        return executeSQL(sql4DropPkConstraint, aLogMessage);
    }

    /**
     * execute sql with DDL
     *
     * @param aSql DDL script for execute
     * @param aName name section for debug
     */
    private int executeSQL(String aSql, String aName) {
        if (aSql != null && !aSql.isEmpty()) {
            try {
                SqlCompiledQuery q = new SqlCompiledQuery(client, null, aSql);
                if (sqlsList != null && !aSql.isEmpty()) {
                    sqlsList.add(aSql);
                }
                if (!noExecuteSQL) {
                    client.commit(Collections.singletonMap((String) null, Collections.singletonList((Change) q.prepareCommand())), null, null);
                }
                if (sqlLogger != null) {
                    sqlLogger.log(Level.CONFIG, new StringBuilder().append(sqlCommentChars).append(numSql++).append(": ").toString());
                    sqlLogger.log(Level.FINE, new StringBuilder().append(sqlCommentChars).append("(").append(aName).append(")").toString());
                    sqlLogger.log(Level.INFO, new StringBuilder().append(aSql).append(sqlCommandEndChars).toString());
                }
                return 0;
            } catch (Exception ex) {
                if (errorLogger != null) {
                    errorLogger.log(Level.CONFIG, new StringBuilder().append(sqlCommentChars).append(numSql++).append(": ").toString());
                    errorLogger.log(Level.FINE, new StringBuilder().append(sqlCommentChars).append("(").append(aName).append(")").toString());
                    errorLogger.log(Level.INFO, new StringBuilder().append(aSql).append(sqlCommandEndChars).toString());
                    errorLogger.log(Level.SEVERE, new StringBuilder().append("Exception=").append(ex.getMessage()).append("\n").toString());
                }
                return 1;
            }
        }
        return 0;
    }

    private int executeSQL(String[] aSqls, String aName) {
        int cntErrors = 0;
        if (aSqls != null) {
            for (String sql : aSqls) {
                cntErrors += executeSQL(sql, aName);
            }
        }
        return cntErrors;
    }

    /**
     * set chars for append each sql command in logs
     *
     * @param sqlEndChars chars for append each sql
     */
    public void setSqlCommandEndChars(String sqlEndChars) {
        sqlCommandEndChars = sqlEndChars;
    }

    private void printLog(String aTableName, int cntErrors) {
        String s = String.format(" %-50s \t: %s", aTableName, (cntErrors == 0 ? "Ok" : String.format("ERROR ( Total = %d)", cntErrors)));
        log(Level.INFO, s);
    }

    private Map<String, Set<String>> readIndexNames(String aSchema, Set<String> aTables) {
        Map<String, Set<String>> mapIndexes = new HashMap();
        if (aTables != null && !aTables.isEmpty()) {
            try {
                assert driver != null;
                String sql4Indexes = driver.getSql4Indexes(aSchema, aTables);
                SqlCompiledQuery queryIndexes;
                assert client != null;
                queryIndexes = new SqlCompiledQuery(client, null, sql4Indexes);
                queryIndexes.executeQuery((ResultSet rs)->{
                    ColumnsIndicies cols = new ColumnsIndicies(rs.getMetaData());
                    while(rs.next()){
                        int nCol_Idx_TableName = cols.find(ClientConstants.JDBCIDX_TABLE_NAME);
                        int nCol_Idx_Name = cols.find(ClientConstants.JDBCIDX_INDEX_NAME);
                        int nCol_Idx_PKey = cols.find(ClientConstants.JDBCIDX_PRIMARY_KEY);
                        int nCol_Idx_FKey = cols.find(ClientConstants.JDBCIDX_FOREIGN_KEY);
                        String tableName = rs.getString(nCol_Idx_TableName);
                        String tableNameUpper = tableName.toUpperCase();
                        Set<String> indexes = mapIndexes.get(tableNameUpper);
                        if (indexes == null) {
                            indexes = new HashSet<>();
                        }

                        Object oIdxName = rs.getObject(nCol_Idx_Name);
                        Object oPKey = rs.getObject(nCol_Idx_PKey);
                        Object oFKeyName = rs.getObject(nCol_Idx_FKey);

                        // if not pkey and not fkey
                        if ((oPKey == null || (oPKey instanceof Number && ((Number) oPKey).intValue() != 0))
                                && (oFKeyName == null || (oFKeyName instanceof String && ((String) oFKeyName).isEmpty()))
                                && oIdxName != null && (oIdxName instanceof String) && !((String) oIdxName).isEmpty()) {
                            indexes.add(((String) oIdxName).toUpperCase());
                        }
                        if (!indexes.isEmpty()) {
                            mapIndexes.put(tableNameUpper, indexes);
                        }
                    }
                    return null;
                }, null, null, null);
            } catch (Exception ex) {
                log(Level.SEVERE, null, ex);
            }
        }
        return mapIndexes;
    }

    /**
     * @return the sqlsList
     */
    public List<String> getSqlsList() {
        return sqlsList;
    }

    private void log(Level aLogLevel, String aMessage) {
        if (systemLogger != null) {
            systemLogger.log(aLogLevel, aMessage);
        }
    }

    private void log(Level aLogLevel, String aMessage, Throwable aThrown) {
        if (systemLogger != null) {
            systemLogger.log(aLogLevel, aMessage, aThrown);
        }
    }
}
