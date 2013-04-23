/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.metadata.ForeignKeySpec.ForeignKeyRule;
import com.bearsoft.rowset.metadata.PrimaryKeySpec;
import com.eas.client.DbClient;
import com.eas.client.DbMetadataCache;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vy
 */
public class MetadataMerger {

    private boolean noDropTables = false;
    private Set<String> listTables = new HashSet<>();
    private boolean noExecuteSQL = false;
    private DbClient client;
    private Map<String, TableStructure> srcMD;
    private Map<String, TableStructure> destMD;
    private String srcDialect;
    private String destDialect;
    private boolean oneDialect = false;
    private DbMetadataCache mdCache;
    private SqlDriver driver;
    private String dSchema;
    private String sqlCommandEndChars = ";";
    private Logger sqlLogger;
    private Logger errorLogger;
    private Map<String, List<ForeignKeySpec>> pKeysMap = new HashMap<>(); // relation for pkey name -> fkey in destination (!! tableName->fkeys)
    private Set<String> removedFKeys = new HashSet<>();  // for removed  Foreign keys
    private String temporaryPKFieldName = "ID";
    private String suffixPKName = "_PK";
    private long numSql = 1L;

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
    public MetadataMerger(DbClient aClient, DBStructure srcMetadata, DBStructure destMetadata, boolean aNoExecuteSQL, boolean aNoDropTables, Set<String> aListTables) throws Exception {
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

        mdCache = client.getDbMetadataCache(null);
        driver = mdCache.getConnectionDriver();
        dSchema = mdCache.getConnectionSchema();
    }

    /**
     * runner for merge database structure
     */
    public void run() {

        TypesResolver dTypesResolver = driver.getTypesResolver();

        // stages for merge:
        // 0 - prepare: get relation for primary key -> foreign key
        // 1 - drop all not existed tables (with linked foreign keys )
        // 2 - create new table with primary key, add new and modify fields and comments for tables and fields
        // 3 - drop fields (with check linked fkeys), drop and modify indexes, and fkeys from destination 
        // 4 - create new indexes and fkeys


        // pass 0 - prepare all date
        // get relation for pkey -> fkey
        Logger.getLogger(MetadataMerger.class.getName()).log(Level.INFO, "Merge metadata: start pass 0 - prepare");

        for (String tableName : destMD.keySet()) {
            TableStructure dTableStructure = destMD.get(tableName);
            Map<String, List<ForeignKeySpec>> dTableFKeySpecs = dTableStructure.getTableFKeySpecs();
            if (dTableFKeySpecs != null) {
                // each fkey spec
                for (String fKeyCName : dTableFKeySpecs.keySet()) {
                    for (ForeignKeySpec fkSpec : dTableFKeySpecs.get(fKeyCName)) {
                        String pkTableName = fkSpec.getReferee().getTable();
                        List<ForeignKeySpec> fks = pKeysMap.get(pkTableName);
                        if (fks == null) {
                            fks = new ArrayList<>();
                            pKeysMap.put(pkTableName, fks);  //pkey -> fk spec
                        }
                        fks.add(fkSpec);
                    }
                }
            }
        }
        temporaryPKFieldName = temporaryPKFieldName.toUpperCase();   // for check on create new table


        // pass1 - drop all not existed tables
        if (noDropTables == false) {
            Logger.getLogger(MetadataMerger.class.getName()).log(Level.INFO, "Merge metadata: start pass 1 - drop all not existed tables");
            for (String tableNameUpper : destMD.keySet()) {
                int cntErrors = 0;
                if (listTables.isEmpty() || listTables.contains(tableNameUpper)) {
                    if (!srcMD.containsKey(tableNameUpper)) {
                        // table not exists
                        TableStructure dTableStructure = destMD.get(tableNameUpper);
                        List<PrimaryKeySpec> dTablePKeySpecs = dTableStructure.getTablePKeySpecs();
                        if (dTablePKeySpecs != null) {
                            for (int i = 0; i < dTablePKeySpecs.size(); i++) {
                                String pKeyTableName = dTablePKeySpecs.get(i).getTable();
                                cntErrors += dropAllLinkedFKeys(pKeyTableName);
                                // primary keys === 1
                                break;
                            }
                        }
                        // drop table
                        String tableName = dTableStructure.getTableName();
                        String sql4DropTable = driver.getSql4DropTable(dSchema, tableName);
                        boolean result = executeSQL(sql4DropTable, "pass 1 - 1");
                        if (result) {
                            // mark all foreign keys as deleleted
                            Map<String, List<ForeignKeySpec>> tableFKeySpecs = dTableStructure.getTableFKeySpecs();
                            if (tableFKeySpecs != null) {
                                for (String cFKName : tableFKeySpecs.keySet()) {
                                    if (!removedFKeys.contains(cFKName)) {
                                        removedFKeys.add(cFKName);
                                    }
                                }
                            }
                        } else {
                            cntErrors++;
                        }
                    }
                }
                printLog(tableNameUpper, cntErrors);
            }
        }

        // pass 2 - create new table with primary key, add new and modify fields and comments for tables and fields
        Logger.getLogger(MetadataMerger.class.getName()).log(Level.INFO, "Merge metadata: start pass 2 - create new table with primary key, add new and modify fields and comments for tables and fields");
        for (String tableNameUpper : srcMD.keySet()) {
            int cntErrors = 0;
            if (listTables.isEmpty() || listTables.contains(tableNameUpper)) {
                TableStructure sTableStructure = srcMD.get(tableNameUpper);
                TableStructure dTableStructure = destMD.get(tableNameUpper);

                String tableDescription = sTableStructure.getTableDescription();
                if (tableDescription == null) {
                    tableDescription = "";
                }

                List<PrimaryKeySpec> sTablePKeySpecs = sTableStructure.getTablePKeySpecs();
                List<PrimaryKeySpec> dTablePKeySpecs = null;
                if (dTableStructure != null) {
                    dTablePKeySpecs = dTableStructure.getTablePKeySpecs();
                }

                Map<String, List<ForeignKeySpec>> sTableFKeySpecs = sTableStructure.getTableFKeySpecs();
                Map<String, List<ForeignKeySpec>> dTableFKeySpecs = null;
                if (dTableStructure != null) {
                    dTableFKeySpecs = dTableStructure.getTableFKeySpecs();
                }

                Fields sFields = sTableStructure.getTableFields();
                Fields dFields = null;

                String pkFieldNameForDrop = "";
                String pkFieldName = "";

                String sTableName = sTableStructure.getTableName();
                String dTableName = sTableName;   // default names are equal

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
                    // for drop columns with PK
                    pkFieldNameForDrop = pkFieldName;

                    String sql4EmptyTableCreation = driver.getSql4EmptyTableCreation(dSchema, sTableName, pkFieldName);
                    if (!executeSQL(sql4EmptyTableCreation, "pass 2 - 1")) {
                        cntErrors++;
                    }
                    // drop temporary PKey
                    cntErrors += dropPKey(dSchema, dTableName, pkFieldNameForDrop, dTableName + suffixPKName);
                } else {
                    dFields = dTableStructure.getTableFields();
                    dTableName = dTableStructure.getTableName();
                    // drop primary key
                    if (dTablePKeySpecs != null && dTablePKeySpecs.size() > 0 && !MetadataUtils.isSamePKeys(dTablePKeySpecs, sTablePKeySpecs, oneDialect)) {
                        PrimaryKeySpec dTablePKeySpec0 = dTablePKeySpecs.get(0);
                        // drop all linked fkeys
                        cntErrors += dropAllLinkedFKeys(dTablePKeySpec0.getTable());
//                        // drop  PKey
//                        cntErrors += dropPKey(dSchema, dTableName, dTablePKeySpec0.getField(), dTablePKeySpec0.getCName());
                        // drop  Keys
                        cntErrors += dropKeys(dSchema, dTableStructure);
                    }
                }
                boolean droppedPK = false;
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!                
//!!!!!!!!!!! НЕ МЕНЯТЬ ИМЯ PK ??????????????????????????????                        
String OLDCNAME="";                
//!!!!!!!!!!! НЕ МЕНЯТЬ ИМЯ PK ??????????????????????????????                        

                // for all fields source
                for (int i = 1; i <= sFields.getFieldsCount(); i++) {
                    Field sField = sFields.get(i);
                    String sFieldName = sField.getName();
                    String dFieldName = sFieldName;   // default equal

                    String fieldNameUpper = sFieldName.toUpperCase();

                    Field sFieldCopy = sField.copy();
                    sFieldCopy.setSchemaName(dSchema);

                    Field dField = null;
                    if (dFields != null) {
                        dFieldName = dTableStructure.getOriginalFieldName(fieldNameUpper);
                        dField = dFields.get(dFieldName);
                    }

                    // field not exist
                    if (dField == null) {
                        // add new field
                        dField = sFieldCopy.copy();
                        dField.setDescription("");
                        dFieldName = dField.getName();

                        dField.setNullable(true);
                        dField.setFk(null);
                        dField.setPk(false);
                        String fullTableName = driver.wrapName(dSchema) + "." + driver.wrapName(dTableName);
                        String addFieldPrefix = String.format(SqlDriver.ADD_FIELD_SQL_PREFIX, fullTableName);
                        String sql4AddField = addFieldPrefix + driver.getSql4FieldDefinition(dField);
                        if (!executeSQL(sql4AddField, "pass 2 - 2")) {
                            cntErrors++;
                        }
                    }
//                    // add comment
//                    String sDescription = sFieldCopy.getDescription();
//                    String dDescription = dField.getDescription();
//                    dField.setDescription(sDescription);
//                    if (sDescription == null) {
//                        sDescription = "";
//                    }
//                    if (dDescription == null) {
//                        dDescription = "";
//                    }
//                    if (!sDescription.equals(dDescription)) {
//                        String[] sql4CreateColumnComment = driver.getSql4CreateColumnComment(dSchema, dTableName, dFieldName, sDescription);
//                        for (String s : sql4CreateColumnComment) {
//                            if (!executeSQL(s, "pass 2 - 3")) {
//                                cntErrors++;
//                            }
//                        }
//                    }
                    Field dFieldCopy = dField.copy();
                    dField.setPk(sFieldCopy.isPk());
                    dField.setFk(sFieldCopy.getFk());
                    dField.setNullable(sFieldCopy.isNullable());

                    int sqlType = dField.getTypeInfo().getSqlType();


                    // need modify
                    if (!MetadataUtils.isSameField(sFieldCopy, dField, dTypesResolver.isSized(sqlType), dTypesResolver.isScaled(sqlType), oneDialect)) {
                        // drop PK and all linked FK
                        if (dFieldCopy.isPk()) {
                            if (dTablePKeySpecs != null && dTablePKeySpecs.size() > 0) {
                                String cTableName = dTablePKeySpecs.get(0).getTable();
                                if (!droppedPK) {
//!!!!!!!!!!! НЕ МЕНЯТЬ ИМЯ PK ??????????????????????????????                        
OLDCNAME = dTablePKeySpecs.get(0).getCName();                                    
//!!!!!!!!!!! НЕ МЕНЯТЬ ИМЯ PK ??????????????????????????????                        

//                                    cntErrors += dropPKey(dSchema, cTableName, dFieldName, dTablePKeySpecs.get(0).getCName());
                                    // drop Keys
                                    cntErrors += dropKeys(dSchema, dTableStructure);
                                    droppedPK = true;
                                }
                                cntErrors += dropAllLinkedFKeys(cTableName);
                            }
                        }
                        // drop Foreign keys
                        ForeignKeySpec fkSpec = dFieldCopy.getFk();
                        if (fkSpec != null) {
                            String cName = fkSpec.getCName();
                            if (!removedFKeys.contains(cName)) {
                                removedFKeys.add(cName);
                                String sql4DropFkConstraint = driver.getSql4DropFkConstraint(dSchema, fkSpec);
                                if (!executeSQL(sql4DropFkConstraint, "pass 2 - 4")) {
                                    cntErrors++;
                                }
                            }
                        }
                        // modify field - step 1 (all besides Nullable)
                        if (!sFieldCopy.isPk()) {
                            sFieldCopy.setNullable(true);
                        }
                        sFieldCopy.setPk(false);
                        String fullTableName = driver.wrapName(dSchema) + "." + driver.wrapName(dTableName);
                        String[] sqls4ModifyingField = driver.getSqls4ModifyingField(fullTableName, dField, sFieldCopy);

                        dFieldCopy = sFieldCopy;
                        for (String s : sqls4ModifyingField) {
                            if (!executeSQL(s, "pass 2 - 5")) {
                                cntErrors++;
                            }
                        }
                    }
                    if (!sField.isNullable() && dFieldCopy.isNullable()) {
                        // modify field - step 2 (set not null)
                        dField = dFieldCopy.copy();
                        dField.setNullable(false);
                        String fullTableName = driver.wrapName(dSchema) + "." + driver.wrapName(dTableName);
                        String[] sqls4ModifyingField = driver.getSqls4ModifyingField(fullTableName, dFieldCopy, dField);

                        for (String s : sqls4ModifyingField) {
                            if (!executeSQL(s, "pass 2 - 6")) {
                                cntErrors++;
                            }
                        }
                    }
                    // add comment
                    String sDescription = sFieldCopy.getDescription();
                    String dDescription = dField.getDescription();
                    dField.setDescription(sDescription);
                    if (sDescription == null) {
                        sDescription = "";
                    }
                    if (dDescription == null) {
                        dDescription = "";
                    }
                    if (!sDescription.equals(dDescription)) {
                        String[] sql4CreateColumnComment = driver.getSql4CreateColumnComment(dSchema, dTableName, dFieldName, sDescription);
                        for (String s : sql4CreateColumnComment) {
                            if (!executeSQL(s, "pass 2 - 3")) {
                                cntErrors++;
                            }
                        }
                    }
                    
                }
                // drop temporary id
                if (!pkFieldNameForDrop.isEmpty()) {
                    String fullTableName = driver.wrapName(dSchema) + "." + driver.wrapName(dTableName);
                    String[] sql4DroppingField = driver.getSql4DroppingField(fullTableName, pkFieldNameForDrop);
                    for (String s : sql4DroppingField) {
                        if (!executeSQL(s, "pass 2 - 7")) {
                            cntErrors++;
                        }
                    }
                }
                // add comment for table
                String dTableDescription = null;
                if (dTableStructure != null) {
                    dTableDescription = dTableStructure.getTableDescription();
                }
                if (dTableDescription == null) {
                    dTableDescription = "";
                }

                if (!tableDescription.equals(dTableDescription)) {
                    String sql4CreateTableComment = driver.getSql4CreateTableComment(dSchema, dTableName, tableDescription);
                    if (!executeSQL(sql4CreateTableComment, "pass 2 - 8")) {
                        cntErrors++;
                    }
                }
                //recreate PKey 
                if ((droppedPK || !MetadataUtils.isSamePKeys(sTablePKeySpecs, dTablePKeySpecs, oneDialect)) && sTablePKeySpecs != null && !sTablePKeySpecs.isEmpty()) {
                    for (int i = 0; i < sTablePKeySpecs.size(); i++) {
                        PrimaryKeySpec pkSpec = sTablePKeySpecs.get(i);
                        pkSpec.setSchema(dSchema);
//!!!!!!!!!!! НЕ МЕНЯТЬ ИМЯ PK ??????????????????????????????                        
pkSpec.setCName(OLDCNAME);                        
//!!!!!!!!!!! НЕ МЕНЯТЬ ИМЯ PK ??????????????????????????????                        

                    }
                    String sql4CreatePkConstraint = driver.getSql4CreatePkConstraint(dSchema, sTablePKeySpecs);
                    if (!executeSQL(sql4CreatePkConstraint, "pass 2 - 9")) {
                        cntErrors++;
                    }
                }
            }
            printLog(tableNameUpper, cntErrors);
        }

        // pass 3 - drop fields (with check linked fkeys), drop and modify indexes, fkeys from destination 
        Logger.getLogger(MetadataMerger.class.getName()).log(Level.INFO, "Merge metadata: start pass 3 - drop fields, drop and modify indexes and foreign keys");
        for (String tableNameUpper : destMD.keySet()) {
            int cntErrors = 0;
            if (listTables.isEmpty() || listTables.contains(tableNameUpper)) {
                TableStructure dTableStructure = destMD.get(tableNameUpper);
                TableStructure sTableStructure = srcMD.get(tableNameUpper);

                if (sTableStructure != null) {

                    String dTableName = dTableStructure.getTableName();

                    // for all fields destination
                    Fields dFields = dTableStructure.getTableFields();
                    Fields sFields = null;
                    if (sTableStructure != null) {
                        sFields = sTableStructure.getTableFields();
                    }

                    // for each field in destination
                    for (int i = 1; i <= dFields.getFieldsCount(); i++) {
                        Field dField = dFields.get(i);
                        String dFieldName = dField.getName();

                        // not exist in source
                        if (sFields == null || sTableStructure.getOriginalFieldName(dFieldName.toUpperCase()) == null) {
                            if (dField.isPk()) {
                                String pKeyTableName = dTableStructure.getTableName();
                                if (pKeyTableName != null && !pKeyTableName.isEmpty()) {
                                    cntErrors += dropAllLinkedFKeys(pKeyTableName);
                                }
                            }
                            ForeignKeySpec dFKSpec = dField.getFk();
                            if (dFKSpec != null) {
                                String cName = dFKSpec.getCName();
                                if (cName != null && !cName.isEmpty()) {
                                    removedFKeys.add(cName);
                                }
                            }

                            // drop field
                            String fullTableName = driver.wrapName(dSchema) + "." + driver.wrapName(dTableName);
                            String[] sql4DroppingField = driver.getSql4DroppingField(fullTableName, dField.getName());
                            for (String s : sql4DroppingField) {
                                if (!executeSQL(s, "pass 3 - 1")) {
                                    cntErrors++;
                                }
                            }
                        }
                    }

                    // drop  fkeys
                    Map<String, List<ForeignKeySpec>> dTableFKeySpecs = dTableStructure.getTableFKeySpecs();
                    Map<String, List<ForeignKeySpec>> sTableFKeySpecs = null;
                    if (sTableStructure != null) {
                        sTableFKeySpecs = sTableStructure.getTableFKeySpecs();
                    }

                    if (dTableFKeySpecs != null) {
                        //for each fkey spec
                        for (String dFKSpecName : dTableFKeySpecs.keySet()) {
                            List<ForeignKeySpec> sFKSpecs = null;
                            if (sTableFKeySpecs != null) {
                                sFKSpecs = sTableFKeySpecs.get(sTableStructure.getOriginalFKeyName(dFKSpecName.toUpperCase()));
                            }
                            List<ForeignKeySpec> dFKSpecs = dTableFKeySpecs.get(dFKSpecName);
                            ForeignKeySpec dFKSpec0 = dFKSpecs.get(0);

                            if (!MetadataUtils.isSameFKeys(sFKSpecs, dFKSpecs, oneDialect)) {
                                if (!removedFKeys.contains(dFKSpec0.getCName())) {
                                    String sql4DropFkConstraint = driver.getSql4DropFkConstraint(dSchema, dFKSpec0);
                                    if (!executeSQL(sql4DropFkConstraint, "pass 3 - 2")) {
                                        cntErrors++;
                                    }
                                }

                            }
                        }
                    }
                    // drop and modify (drop+create) indexes
                    Map<String, DbTableIndexSpec> dIndexes = dTableStructure.getTableIndexSpecs();
                    Map<String, DbTableIndexSpec> sIndexes = null;
                    if (sTableStructure != null) {
                        sIndexes = sTableStructure.getTableIndexSpecs();
                    }
                    if (dIndexes != null) {
                        // for each index
                        for (String indexName : dIndexes.keySet()) {
                            DbTableIndexSpec dIndexSpec = dIndexes.get(indexName);
                            DbTableIndexSpec sIndexSpec = null;
                            if (sIndexes != null) {
                                sIndexSpec = sIndexes.get(sTableStructure.getOriginalIndexName(indexName.toUpperCase()));
                            }
                            // no exist in source or need for modify
                            if (sIndexSpec == null || !MetadataUtils.isSameIndex(sIndexSpec, dIndexSpec, oneDialect)) {
                                // if not primary key index
                                if (isPKIndex(dIndexSpec, dTableStructure.getTablePKeySpecs()) == false) {
                                    String sql4DropIndex = driver.getSql4DropIndex(dSchema, dTableName, dTableStructure.getOriginalIndexName(indexName.toUpperCase()));
                                    if (!executeSQL(sql4DropIndex, "pass 3 - 4")) {
                                        cntErrors++;
                                    }
                                    // modify = drop+create
                                    if (sIndexSpec != null) {
                                        String sql4CreateIndex = driver.getSql4CreateIndex(dSchema, dTableName, sIndexSpec);
                                        if (!executeSQL(sql4CreateIndex, "pass 3 - 5")) {
                                            cntErrors++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            printLog(tableNameUpper, cntErrors);
        }

        // step 4 - create indexes and fkeys
        Logger.getLogger(MetadataMerger.class.getName()).log(Level.INFO, "Merge metadata: start pass 4 - create indexes and foreign keys");
        for (String tableNameUpper : srcMD.keySet()) {
            int cntErrors = 0;
            if (listTables.isEmpty() || listTables.contains(tableNameUpper)) {
                TableStructure sTableStructure = srcMD.get(tableNameUpper);
                TableStructure dTableStructure = destMD.get(tableNameUpper);
                assert sTableStructure != null;
                String dTableName = sTableStructure.getTableName();//tableNameUpper; // default equal

                // indexes
                Map<String, DbTableIndexSpec> sIndexes = sTableStructure.getTableIndexSpecs();
                Map<String, DbTableIndexSpec> dIndexes = null;
                if (dTableStructure != null) {
                    dIndexes = dTableStructure.getTableIndexSpecs();
                    dTableName = dTableStructure.getTableName();
                }

                if (sIndexes != null) {
                    for (String indexName : sIndexes.keySet()) {
                        // only for new indexes
                        if (dIndexes == null || dTableStructure.getOriginalIndexName(indexName.toUpperCase()) == null) {
                            DbTableIndexSpec sIndexSpec = sIndexes.get(indexName);
                            // if not primary key index
                            if (isPKIndex(sIndexSpec, sTableStructure.getTablePKeySpecs()) == false) {
                                String sql4CreateIndex = driver.getSql4CreateIndex(dSchema, dTableName, sIndexSpec);
                                if (!executeSQL(sql4CreateIndex, "pass 4 - 1")) {
                                    cntErrors++;
                                }
                            }
                        }
                    }
                }

                // fkeys
                Map<String, List<ForeignKeySpec>> sTableFKeySpecs = sTableStructure.getTableFKeySpecs();
                Map<String, List<ForeignKeySpec>> dTableFKeySpecs = null;
                if (dTableStructure != null) {
                    dTableFKeySpecs = dTableStructure.getTableFKeySpecs();
                }
                if (sTableFKeySpecs != null) {
                    //for each fkey spec
                    for (String sFKSpecName : sTableFKeySpecs.keySet()) {
                        List<ForeignKeySpec> sFKSpecs = sTableFKeySpecs.get(sFKSpecName);

                        List<ForeignKeySpec> dFKSpecs = null;
                        if (dTableFKeySpecs != null) {
                            String initialNameFKey = dTableStructure.getOriginalFKeyName(sFKSpecName.toUpperCase());
                            if (initialNameFKey != null) {
                                dFKSpecs = dTableFKeySpecs.get(initialNameFKey);
                            }
                        }
                        ForeignKeySpec dFKSpec0 = null;
                        if (dFKSpecs != null && dFKSpecs.size() > 0) {
                            dFKSpec0 = dFKSpecs.get(0);
                        } else {
                            dFKSpecs = new ArrayList<>();
                        }

                        if (sFKSpecs != null
                                && (!MetadataUtils.isSameFKeys(sFKSpecs, dFKSpecs, oneDialect) || dFKSpec0 == null || removedFKeys.contains(dFKSpec0.getCName()))) {
                            dFKSpecs.clear();
                            for (int i = 0; i < sFKSpecs.size(); i++) {
                                ForeignKeySpec sFkSpec = sFKSpecs.get(i);
                                ForeignKeySpec fkSpec = (ForeignKeySpec) sFkSpec.copy();
                                fkSpec.setSchema(dSchema);
                                fkSpec.getReferee().setSchema(dSchema);
                                // !!! in Oracle not exists UpdateRule !!!!!
                                ForeignKeyRule fkUpdateRule = fkSpec.getFkUpdateRule();
                                if (fkUpdateRule == null) {
                                    fkSpec.setFkUpdateRule(ForeignKeyRule.NOACTION);
                                }
                                dFKSpecs.add(fkSpec);
                            }

                            String sql4CreateFkConstraint = driver.getSql4CreateFkConstraint(dSchema, dFKSpecs);
                            if (!executeSQL(sql4CreateFkConstraint, "pass 4 - 2")) {
                                cntErrors++;
                            }
                        }
                    }
                }
            }
            printLog(tableNameUpper, cntErrors);
        }
        Logger.getLogger(MetadataMerger.class.getName()).log(Level.INFO, "Merge metadata finished");

    }

    /**
     * check columns index on entry in columns primary key
     *
     * @param indexSpec specification for index
     * @param pkSpecs specifications for primary keys
     * @return true if index created for primary key
     */
    private boolean isPKIndex(DbTableIndexSpec indexSpec, List<PrimaryKeySpec> pkSpecs) {
        if (indexSpec != null && pkSpecs != null) {
            List<DbTableIndexColumnSpec> indexColumns = indexSpec.getColumns();
            int sizeIndex = indexColumns.size();
            int sizePK = pkSpecs.size();
            if (sizePK == sizeIndex && sizePK > 0) {
                for (int i = 0; i < sizePK; i++) {
                    if (!indexColumns.get(i).getColumnName().equalsIgnoreCase(pkSpecs.get(i).getField())) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * drop all foreign keys linked with primary pey pKeyCName
     *
     * @param pKeyTableName table name for primary key
     */
    private int dropAllLinkedFKeys(String pKeyTableName) {
        int cntErrors = 0;

        List<ForeignKeySpec> fkSpecList = pKeysMap.get(pKeyTableName);
        if (fkSpecList != null) {
            for (ForeignKeySpec fkSpec : pKeysMap.get(pKeyTableName)) {
                String cName = fkSpec.getCName();

                if (!removedFKeys.contains(cName)) {
                    removedFKeys.add(cName);
                    String sql4DropFkConstraint = driver.getSql4DropFkConstraint(dSchema, fkSpec);
                    if (!executeSQL(sql4DropFkConstraint, "drop linked constraints")) {
                        cntErrors++;
                    }
                }
            }
        }
        return cntErrors;

    }

    /**
     * drop primary pey pKeyCName (ONLY FOR TEMPORARY COLUMN ID !!!!!!)
     *
     */
    private int dropPKey(String aSchemaName, String aTableName, String aFieldName, String aPKeyCName) {
        int cntErrors = 0;
//        assert aTableName != null;
//        // drop foreign key
//        if (destMD != null) {
//            TableStructure tblStructure = destMD.get(aTableName);
//            if (tblStructure != null) {
//                Map<String, List<ForeignKeySpec>> tableFKeySpecs = tblStructure.getTableFKeySpecs();
//                if (tableFKeySpecs != null) {
//                    for (List<ForeignKeySpec> fkSpecs : tableFKeySpecs.values()) {
//                        if (fkSpecs != null && fkSpecs.size() > 0) {
//                            ForeignKeySpec fkSpec = fkSpecs.get(0);
//                            assert fkSpec != null;
//                            String cName = fkSpec.getCName();
//                            if (!removedFKeys.contains(cName)) {
//                                removedFKeys.add(cName);
//                                String sql4DropFkConstraint = driver.getSql4DropFkConstraint(dSchema, fkSpec);
//                                if (!executeSQL(sql4DropFkConstraint, "drop fkey constraints")) {
//                                    cntErrors++;
//                                }
//                            }
//
//                        }
//                    }
//                }
//            }
//        }
        PrimaryKeySpec pkSpec = new PrimaryKeySpec(aSchemaName, aTableName, aFieldName, aPKeyCName);
        String sql4DropPkConstraint = driver.getSql4DropPkConstraint(aSchemaName, pkSpec);
        if (!executeSQL(sql4DropPkConstraint, "drop temp PKey")) {
            return cntErrors++;
        }
        return cntErrors;
    }

    /**
     * drop primary and foreign keys
     *
     */
    private int dropKeys(String aSchemaName,TableStructure aTblStructure) {
        int cntErrors = 0;
        assert aTblStructure != null;
        // drop foreign keys
        Map<String, List<ForeignKeySpec>> tableFKeySpecs = aTblStructure.getTableFKeySpecs();
        if (tableFKeySpecs != null) {
            for (List<ForeignKeySpec> fkSpecs : tableFKeySpecs.values()) {
                if (fkSpecs != null && fkSpecs.size() > 0) {
                    ForeignKeySpec fkSpec = fkSpecs.get(0);
                    assert fkSpec != null;
                    String cName = fkSpec.getCName();
                    if (!removedFKeys.contains(cName)) {
                        removedFKeys.add(cName);
                        String sql4DropFkConstraint = driver.getSql4DropFkConstraint(dSchema, fkSpec);
                        if (!executeSQL(sql4DropFkConstraint, "drop fkey constraints")) {
                            cntErrors++;
                        }
                    }
                }
            }
        }
        List<PrimaryKeySpec> tablePKeySpecs = aTblStructure.getTablePKeySpecs();
        if (tablePKeySpecs != null && tablePKeySpecs.size() > 0) {
            String sql4DropPkConstraint = driver.getSql4DropPkConstraint(aSchemaName, tablePKeySpecs.get(0));
            if (!executeSQL(sql4DropPkConstraint, "drop pkey constraint")) {
                return cntErrors++;
            }
        }
        return cntErrors;
    }

    /**
     * execute sql with DDL
     *
     * @param aSql DDL script for execute
     * @param aName name section for debug
     */
    private boolean executeSQL(String aSql, String aName) {
        try {
            SqlCompiledQuery q = new SqlCompiledQuery(client, null, aSql);
            if (!noExecuteSQL) {
                q.enqueueUpdate();
                client.commit(null);
            }
            if (sqlLogger != null) {
//                sqlLogger.log(Level.CONFIG, new StringBuilder().append(numSql++).append(": (").append(aName).append(")").toString());
                sqlLogger.log(Level.CONFIG, new StringBuilder().append(numSql++).append(": ").toString());
                sqlLogger.log(Level.FINE, new StringBuilder().append("(").append(aName).append(")").toString());
                sqlLogger.log(Level.INFO, new StringBuilder().append(aSql).append(sqlCommandEndChars).toString());
            }
            return true;
        } catch (Exception ex) {
            if (!noExecuteSQL) {
                client.rollback(null);
            }
            if (errorLogger != null) {
//                errorLogger.log(Level.CONFIG, new StringBuilder().append(numSql++).append(": (").append(aName).append(")").toString());
                errorLogger.log(Level.CONFIG, new StringBuilder().append(numSql++).append(": ").toString());
                errorLogger.log(Level.FINE, new StringBuilder().append("(").append(aName).append(")").toString());
                errorLogger.log(Level.INFO, new StringBuilder().append(aSql).append(sqlCommandEndChars).toString());
                errorLogger.log(Level.SEVERE, new StringBuilder().append("Exception=").append(ex.getMessage()).append("\n").toString());
            }
            return false;
        }
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
        Logger.getLogger(MetadataMerger.class.getName()).log(Level.INFO, s);
    }

    /**
     * @return the loggerSQL
     */
    public Logger getSqlLogger() {
        return sqlLogger;
    }

    /**
     * @param aLogger the loggerSql to set
     */
    public void setSqlLogger(Logger aLogger) {
        sqlLogger = aLogger;
    }

    /**
     * @return the loggerERROR
     */
    public Logger getErrorLogger() {
        return errorLogger;
    }

    /**
     * @param aLogger the loggerError to set
     */
    public void setErrorLogger(Logger aLogger) {
        errorLogger = aLogger;
    }
}
