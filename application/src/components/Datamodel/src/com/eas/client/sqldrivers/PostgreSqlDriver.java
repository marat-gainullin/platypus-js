/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers;

import com.eas.client.ClientConstants;
import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.DbTableIndexColumnSpec;
import com.eas.client.metadata.DbTableIndexSpec;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.PrimaryKeySpec;
import com.eas.client.sqldrivers.resolvers.PostgreTypesResolver;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.util.StringUtils;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mg
 */
public class PostgreSqlDriver extends SqlDriver {

    // настройка экранирования наименования объектов БД
    private final TwinString[] charsForWrap = {new TwinString("\"", "\"")};
    private final char[] restrictedChars = {' ', ',', '\'', '"'};

    protected static final PostgreTypesResolver resolver = new PostgreTypesResolver();
    protected static final String SET_SCHEMA_CLAUSE = "set search_path = %s,public";
    protected static final String GET_SCHEMA_CLAUSE = "select current_schema()";
    protected static final String CREATE_SCHEMA_CLAUSE = "CREATE SCHEMA %s";
    protected static final String DEF_OTHER_TYPE_NAME = "point";
    protected static final String RENAME_FIELD_SQL_PREFIX = "alter table %s rename column %s to %s";
    protected static final String MODIFY_FIELD_SQL_PREFIX = "alter table %s alter ";
    public static final String SQL_TABLES = ""
            + "select"
            + " cl.relname TABLE_NAME,"
            + " nsp.nspname TABLE_SCHEM,"
            + " (case cl.relkind when 'v' then '" + ClientConstants.JDBCPKS_TABLE_TYPE_VIEW + "' else '" + ClientConstants.JDBCPKS_TABLE_TYPE_TABLE + "' end) " + ClientConstants.JDBCPKS_TABLE_TYPE_FIELD_NAME + ","
            + " d.description as " + ClientConstants.JDBCCOLS_REMARKS + " "
            + "from pg_catalog.pg_namespace nsp"
            + " inner join pg_catalog.pg_class cl on (cl.relnamespace = nsp.oid)"
            + " left outer join pg_catalog.pg_am am on (cl.relam = am.oid) "
            + " left outer join pg_description d on (d.ObjOID = cl.OID and ObjSubID = 0)" //(cl.oid = d.objoid) " 
            + "where am.amname is null "
            + " and cl.relkind in ('v','r') ";

    public static final String SQL_SCHEMA_TABLES = ""
            + SQL_TABLES
            + "and nsp.nspname = '%s' "
            + "order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM + "," + ClientConstants.JDBCCOLS_TABLE_NAME;
    public static final String SQL_SCHEMAS = ""
            + "select nsp.nspname as " + ClientConstants.JDBCCOLS_TABLE_SCHEM + " from pg_catalog.pg_namespace nsp "
            + "order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM;
    protected static final String SQL_COLUMNS = ""
            + "SELECT"
            + " table_catalog,"
            + " table_schema AS " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " table_name AS " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " column_name AS " + ClientConstants.JDBCCOLS_COLUMN_NAME + ","
            + " udt_name AS " + ClientConstants.JDBCCOLS_TYPE_NAME + ","
            + " NULL AS " + ClientConstants.JDBCCOLS_DATA_TYPE + ","
            + " (CASE WHEN numeric_precision IS NOT NULL THEN numeric_precision ELSE character_maximum_length END) AS " + ClientConstants.JDBCCOLS_COLUMN_SIZE + ","
            + " (CASE WHEN is_nullable = 'YES' THEN 1 ELSE 0 END) AS " + ClientConstants.JDBCCOLS_NULLABLE + ","
            + " numeric_scale AS " + ClientConstants.JDBCCOLS_DECIMAL_DIGITS + ","
            + " 10 AS " + ClientConstants.JDBCCOLS_NUM_PREC_RADIX + ","
            + " ordinal_position AS " + ClientConstants.JDBCIDX_ORDINAL_POSITION + ","
            + " dsc.description as " + ClientConstants.JDBCCOLS_REMARKS + ","
            + " column_default "
            + "FROM information_schema.COLUMNS clmns"
            + " JOIN pg_catalog.pg_namespace n ON (n.nspname = clmns.table_schema)"
            + " JOIN pg_catalog.pg_class c ON (c.relnamespace = n.oid AND c.relname = table_name)"
            + " JOIN pg_catalog.pg_attribute a ON (a.attrelid=c.oid AND a.attnum > 0 AND NOT a.attisdropped AND a.attname = column_name)"
            + " JOIN pg_catalog.pg_type t ON (a.atttypid = t.oid)"
            + " LEFT JOIN pg_catalog.pg_attrdef def ON (a.attrelid=def.adrelid AND a.attnum = def.adnum)"
            + " LEFT JOIN pg_catalog.pg_description dsc ON (c.oid=dsc.objoid AND a.attnum = dsc.objsubid)"
            //            + " LEFT JOIN pg_catalog.pg_class dc ON (dc.oid=dsc.classoid AND dc.relname='pg_class')"
            //            + " LEFT JOIN pg_catalog.pg_namespace dn ON (dc.relnamespace=dn.oid AND dn.nspname='pg_catalog') "
            + "WHERE table_schema = '%s' AND Lower(table_name) IN (%s) "
            + "ORDER BY table_schema, table_name, ordinal_position";
    protected static final String SQL_PRIMARY_KEYS = ""
            + "SELECT"
            + " NULL AS TABLE_CAT,"
            + " n.nspname AS " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " ct.relname AS " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " a.attname AS " + ClientConstants.JDBCCOLS_COLUMN_NAME + ","
            + " a.attnum AS KEY_SEQ,"
            + " ci.relname AS " + ClientConstants.JDBCPKS_CONSTRAINT_NAME + " "
            + "FROM pg_catalog.pg_namespace n, pg_catalog.pg_class ct, pg_catalog.pg_class ci, pg_catalog.pg_attribute a, pg_catalog.pg_index i "
            + "WHERE ct.oid=i.indrelid AND ci.oid=i.indexrelid  AND a.attrelid=ci.oid AND i.indisprimary"
            + " AND ct.relnamespace = n.oid"
            + " AND n.nspname = '%s'"
            + " AND Lower(ct.relname) in (%s) "
            + "ORDER BY table_name, pk_name, key_seq";
    protected static final String SQL_FOREIGN_KEYS = "SELECT"
            + " NULL::text AS PKTABLE_CAT,"
            + " pkn.nspname AS " + ClientConstants.JDBCFKS_FKPKTABLE_SCHEM + ","
            + " pkc.relname AS " + ClientConstants.JDBCFKS_FKPKTABLE_NAME + ","
            + " pka.attname AS " + ClientConstants.JDBCFKS_FKPKCOLUMN_NAME + ","
            + " NULL::text AS FKTABLE_CAT,"
            + " fkn.nspname AS " + ClientConstants.JDBCFKS_FKTABLE_SCHEM + ","
            + " fkc.relname AS " + ClientConstants.JDBCFKS_FKTABLE_NAME + ","
            + " fka.attname AS " + ClientConstants.JDBCFKS_FKCOLUMN_NAME + ","
            + " pos.n AS KEY_SEQ,"
            + " CASE con.confupdtype WHEN 'c' THEN 0 WHEN 'n' THEN 2 WHEN 'd' THEN 4 WHEN 'r' THEN 1 WHEN 'a' THEN 3 ELSE NULL END AS " + ClientConstants.JDBCFKS_FKUPDATE_RULE + ","
            + " CASE con.confdeltype  WHEN 'c' THEN 0 WHEN 'n' THEN 2 WHEN 'd' THEN 4 WHEN 'r' THEN 1 WHEN 'a' THEN 3 ELSE NULL END AS " + ClientConstants.JDBCFKS_FKDELETE_RULE + ","
            + " con.conname AS " + ClientConstants.JDBCFKS_FK_NAME + ","
            + " pkic.relname AS " + ClientConstants.JDBCFKS_FKPK_NAME + ","
            + " CASE  WHEN con.condeferrable AND con.condeferred THEN 5 WHEN con.condeferrable THEN 6 ELSE 7 END AS " + ClientConstants.JDBCFKS_FKDEFERRABILITY + " "
            + "FROM  pg_catalog.pg_namespace pkn, pg_catalog.pg_class pkc, pg_catalog.pg_attribute pka,"
            + " pg_catalog.pg_namespace fkn, pg_catalog.pg_class fkc, pg_catalog.pg_attribute fka,  pg_catalog.pg_constraint con,"
            + " pg_catalog.generate_series(1, 32) pos(n),  pg_catalog.pg_depend dep, pg_catalog.pg_class pkic "
            + "WHERE pkn.oid = pkc.relnamespace AND pkc.oid = pka.attrelid AND pka.attnum = con.confkey[pos.n]"
            + " AND con.confrelid = pkc.oid  AND fkn.oid = fkc.relnamespace AND fkc.oid = fka.attrelid AND fka.attnum = con.conkey[pos.n]"
            + " AND con.conrelid = fkc.oid  AND con.contype = 'f' AND con.oid = dep.objid AND pkic.oid = dep.refobjid"
            + " AND pkic.relkind = 'i' AND dep.classid = 'pg_constraint'::regclass::oid AND dep.refclassid = 'pg_class'::regclass::oid"
            + " AND fkn.nspname = '%s'  AND Lower(fkc.relname) in (%s) "
            + "ORDER BY pkn.nspname,pkc.relname,pos.n";
    protected static final String SQL_INDEX_KEYS = ""
            + "SELECT"
            + " NULL AS TABLE_CAT,"
            + " n.nspname AS " + ClientConstants.JDBCIDX_TABLE_SCHEM + ","
            + " ct.relname AS " + ClientConstants.JDBCIDX_TABLE_NAME + ","
            + " CASE WHEN i.indisunique = true then 0 else 1 end AS " + ClientConstants.JDBCIDX_NON_UNIQUE + ","
            + " NULL AS " + ClientConstants.JDBCIDX_INDEX_QUALIFIER + ","
            + " ci.relname AS " + ClientConstants.JDBCIDX_INDEX_NAME + ","
            + " CASE i.indisclustered  WHEN true THEN 1 ELSE CASE am.amname WHEN 'hash' THEN 2 ELSE 3 END  END AS " + ClientConstants.JDBCIDX_TYPE + ","
            + " a.attnum AS " + ClientConstants.JDBCIDX_ORDINAL_POSITION + ","
            + " CASE WHEN i.indexprs IS NULL THEN a.attname ELSE pg_get_indexdef(ci.oid,a.attnum,false) END AS " + ClientConstants.JDBCIDX_COLUMN_NAME + ","
            + " NULL AS " + ClientConstants.JDBCIDX_ASC_OR_DESC + ","
            + " ci.reltuples AS CARDINALITY,"
            + " ci.relpages AS PAGES,"
            + " NULL AS FILTER_CONDITION,"
            + " (CASE WHEN i.indisprimary = 't' THEN 0 ELSE 1 END) AS " + ClientConstants.JDBCIDX_PRIMARY_KEY + ","
            + " NULL AS " + ClientConstants.JDBCIDX_FOREIGN_KEY + " "
            + "FROM pg_catalog.pg_namespace n, pg_catalog.pg_class ct, pg_catalog.pg_class ci,"
            + " pg_catalog.pg_attribute a, pg_catalog.pg_am am, pg_catalog.pg_index i "
            + "WHERE ct.oid=i.indrelid AND ci.oid=i.indexrelid AND a.attrelid=ci.oid AND ci.relam=am.oid"
            + " AND n.oid = ct.relnamespace AND n.nspname = '%s' AND Lower(ct.relname) in (%s) "
            + "ORDER BY NON_UNIQUE, TYPE, INDEX_NAME, ORDINAL_POSITION";
    protected static final String SQL_TABLE_COMMENTS = ""
            + "SELECT"
            + " n.nspname as  " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " c.relname as  " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " d.description as " + ClientConstants.JDBCCOLS_TABLE_DESC + " "
            + "from pg_class c"
            + " left outer join pg_namespace n on (c.relowner = n.nspowner)"
            + " left outer join pg_description d on (c.oid = d.objoid)"
            + "where d.objsubid = 0 and Lower(n.nspname) = Lower('%s') and Lower(relname) in (%s)";
    protected static final String SQL_ALL_OWNER_TABLES_COMMENTS = ""
            + "SELECT"
            + " n.nspname as  " + ClientConstants.JDBCCOLS_TABLE_SCHEM + ","
            + " c.relname as  " + ClientConstants.JDBCCOLS_TABLE_NAME + ","
            + " d.description as " + ClientConstants.JDBCCOLS_TABLE_DESC + " "
            + "from pg_class c"
            + " inner join pg_catalog.pg_namespace n on (n.oid = c.relnamespace  and relam = 0)"
            + " left outer join pg_description d on (c.oid = d.objoid)"
            + "where n.nspname = '%s'";
    protected static final String SQL_PARENTS_LIST = ""
            + "with recursive parents(mdent_id,"
            + " mdent_name,"
            + " mdent_type,"
            + " mdent_content_txt,"
            + " mdent_content_data,"
            + " tag1,"
            + " tag2,"
            + " tag3,"
            + " mdent_parent_id,"
            + " mdent_order,"
            + " mdent_content_txt_size,"
            + " mdent_content_txt_crc32) as "
            + "( "
            + "select m1.* from mtd_entities m1 where m1.mdent_id = :%s "
            + "    union all "
            + "select m2.* from parents p, mtd_entities m2 where m2.mdent_id = p.mdent_parent_id "
            + ") "
            + "select * from parents ";
    protected static final String SQL_CHILDREN_LIST = ""
            + "with recursive children(mdent_id,"
            + " mdent_name,"
            + " mdent_type,"
            + " mdent_content_txt,"
            + " mdent_content_data,"
            + " tag1,"
            + " tag2,"
            + " tag3,"
            + " mdent_parent_id,"
            + " mdent_order,"
            + " mdent_content_txt_size,"
            + " mdent_content_txt_crc32) as "
            + "( "
            + "select m1.* from mtd_entities m1 where m1.mdent_id = :%s "
            + "    union all "
            + "select m2.* from children c, mtd_entities m2 where c.mdent_id = m2.mdent_parent_id "
            + ") "
            + "select * from children ";

    @Override
    public TypesResolver getTypesResolver() {
        return resolver;
    }

    @Override
    public String getUsersSpaceInitResourceName() {
        return "/" + PostgreSqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/PostgreInitUsersSpace.sql";
    }

    @Override
    public String getVersionInitResourceName() {
        return "/" + PostgreSqlDriver.class.getPackage().getName().replace(".", "/") + "/sqlscripts/PostgreInitVersion.sql";
    }

    @Override
    public Set<Integer> getSupportedJdbcDataTypes() {
        return resolver.getSupportedJdbcDataTypes();
    }

    @Override
    public String getSql4GetConnectionContext() {
        return GET_SCHEMA_CLAUSE;
    }

    @Override
    public void applyContextToConnection(Connection aConnection, String aSchema) throws Exception {
        if (aSchema != null && !aSchema.isEmpty()) {
            try (Statement stmt = aConnection.createStatement()) {
                stmt.execute(String.format(SET_SCHEMA_CLAUSE, wrapNameIfRequired(aSchema)));
            }
        }
    }

    @Override
    public String getSql4TableColumns(String aOwnerName, Set<String> aTableNames) {
        return String.format(SQL_COLUMNS, prepareName(aOwnerName), constructIn(aTableNames).toLowerCase());
    }

    @Override
    public String getSql4TablePrimaryKeys(String aOwnerName, Set<String> aTableNames) {
        return String.format(SQL_PRIMARY_KEYS, prepareName(aOwnerName), constructIn(aTableNames).toLowerCase());
    }

    @Override
    public String getSql4TableForeignKeys(String aOwnerName, Set<String> aTableNames) {
        return String.format(SQL_FOREIGN_KEYS, prepareName(aOwnerName), constructIn(aTableNames).toLowerCase());
    }

    @Override
    public String getSql4Indexes(String aOwnerName, Set<String> aTableNames) {
        if (aTableNames != null && !aTableNames.isEmpty()) {
            return String.format(SQL_INDEX_KEYS, prepareName(aOwnerName), constructIn(aTableNames).toLowerCase());
        } else {
            return null;
        }
    }

    @Override
    public String[] getSql4CreateColumnComment(String aOwnerName, String aTableName, String aFieldName, String aDescription) {
        String ownerName = wrapNameIfRequired(aOwnerName);
        String tableName = wrapNameIfRequired(aTableName);
        String fieldName = wrapNameIfRequired(aFieldName);
        String sqlText = aOwnerName == null ? StringUtils.join(".", tableName, fieldName) : StringUtils.join(".", ownerName, tableName, fieldName);
        if (aDescription == null) {
            aDescription = "";
        }
        return new String[]{String.format("comment on column %s is '%s'", sqlText, aDescription.replaceAll("'", "''"))};
    }

    @Override
    public String getSql4CreateTableComment(String aOwnerName, String aTableName, String aDescription) {
        String sqlText = StringUtils.join(".", wrapNameIfRequired(aOwnerName), wrapNameIfRequired(aTableName));
        if (aDescription == null) {
            aDescription = "";
        }
        return String.format("comment on table %s is '%s'", sqlText, aDescription.replaceAll("'", "''"));
    }

    @Override
    public String getSql4DropTable(String aSchemaName, String aTableName) {
        return "drop table " + makeFullName(aSchemaName, aTableName) + " cascade";
    }

    @Override
    public String getSql4DropIndex(String aSchemaName, String aTableName, String aIndexName) {
        return "drop index " + makeFullName(aSchemaName, aIndexName);
    }

    @Override
    public String getSql4CreateIndex(String aSchemaName, String aTableName, DbTableIndexSpec aIndex) {
        assert aIndex.getColumns().size() > 0 : "index definition must consist of at least 1 column";
        String indexName = wrapNameIfRequired(aIndex.getName());
        String tableName = makeFullName(aSchemaName, aTableName);
        String modifier = "";
        if (aIndex.isUnique()) {
            modifier = "unique";
        }
        String methodClause = "";
        if (aIndex.isHashed()) {
            methodClause = " using hash ";
        }
        String fieldsList = "";
        for (int i = 0; i < aIndex.getColumns().size(); i++) {
            DbTableIndexColumnSpec column = aIndex.getColumns().get(i);
            fieldsList += wrapNameIfRequired(column.getColumnName());
            if (i != aIndex.getColumns().size() - 1) {
                fieldsList += ", ";
            }
        }
        return "create " + modifier + " index " + indexName + " on " + tableName + " " + methodClause + " ( " + fieldsList + " )";
    }

    @Override
    public String getSql4DropFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        String constraintName = wrapNameIfRequired(aFk.getCName());
        String tableName = makeFullName(aSchemaName, aFk.getTable());
        return "alter table " + tableName + " drop constraint " + constraintName + " cascade";
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, ForeignKeySpec aFk) {
        List<ForeignKeySpec> fkList = new ArrayList<>();
        fkList.add(aFk);
        return getSql4CreateFkConstraint(aSchemaName, fkList);
    }

    @Override
    public String getSql4EmptyTableCreation(String aSchemaName, String aTableName, String aPkFieldName) {
        String fullName = makeFullName(aSchemaName, aTableName);
        String pkFieldName = wrapNameIfRequired(aPkFieldName);
        return "CREATE TABLE " + fullName + " ("
                + pkFieldName + " NUMERIC NOT NULL,"
                + "CONSTRAINT " + wrapNameIfRequired(generatePkName(aTableName, PKEY_NAME_SUFFIX)) + " PRIMARY KEY (" + pkFieldName + "))";
    }

    private String getFieldTypeDefinition(Field aField) {
        resolver.resolve2RDBMS(aField);
        String typeDefine = "";
        String sqlTypeName = aField.getTypeInfo().getSqlTypeName().toLowerCase();
        // field length
        int size = aField.getSize();
        int scale = aField.getScale();

        typeDefine += sqlTypeName;
        if (resolver.isScaled(sqlTypeName) && resolver.isSized(sqlTypeName) && size > 0) {
            typeDefine += "(" + String.valueOf(size) + "," + String.valueOf(scale) + ")";
        } else {
            if (resolver.isSized(sqlTypeName) && size > 0) {
                typeDefine += "(" + String.valueOf(size) + ")";
            }
        }
        return typeDefine;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSql4FieldDefinition(Field aField) {
        String fieldName = wrapNameIfRequired(aField.getName());
        String fieldDefinition = fieldName + " " + getFieldTypeDefinition(aField);

        if (!aField.isNullable()) {
            fieldDefinition += " not null";
        } else {
            fieldDefinition += " null";
        }
        return fieldDefinition;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String[] getSqls4ModifyingField(String aSchemaName, String aTableName, Field aOldFieldMd, Field aNewFieldMd) {
        List<String> sqls = new ArrayList<>();
        Field newFieldMd = aNewFieldMd.copy();
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String fieldName = wrapNameIfRequired(aOldFieldMd.getName());
        String updateDefinition = String.format(MODIFY_FIELD_SQL_PREFIX, fullTableName) + fieldName + " ";
        String fieldDefination = getFieldTypeDefinition(newFieldMd);

        DataTypeInfo newTypeInfo = newFieldMd.getTypeInfo();
        String newSqlTypeName = newTypeInfo.getSqlTypeName();
        if (newSqlTypeName == null) {
            newSqlTypeName = "";
        }
        int newScale = newFieldMd.getScale();
        int newSize = newFieldMd.getSize();
        boolean newNullable = newFieldMd.isNullable();

        DataTypeInfo oldTypeInfo = aOldFieldMd.getTypeInfo();
        String oldSqlTypeName = oldTypeInfo.getSqlTypeName();
        if (oldSqlTypeName == null) {
            oldSqlTypeName = "";
        }
        int oldScale = aOldFieldMd.getScale();
        int oldSize = aOldFieldMd.getSize();
        boolean oldNullable = aOldFieldMd.isNullable();

        if (!newSqlTypeName.equalsIgnoreCase(oldSqlTypeName)
                || (resolver.isSized(newSqlTypeName) && newSize != oldSize)
                || (resolver.isScaled(newSqlTypeName) && newScale != oldScale)) {
            sqls.add(updateDefinition + " type " + fieldDefination + " using " + fieldName + "::" + newSqlTypeName);
        }
        if (oldNullable != newNullable) {
            sqls.add(updateDefinition + (newNullable ? " drop not null" : " set not null"));
        }
        return (String[]) sqls.toArray(new String[sqls.size()]);
    }

    @Override
    public String[] getSqls4RenamingField(String aSchemaName, String aTableName, String aOldFieldName, Field aNewFieldMd) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        String sqlText = String.format(RENAME_FIELD_SQL_PREFIX, fullTableName, wrapNameIfRequired(aOldFieldName), wrapNameIfRequired(aNewFieldMd.getName()));
        return new String[]{
            sqlText
        };
    }

    @Override
    public String parseException(Exception ex) {
        return ex.getMessage();
    }

    @Override
    public Integer getJdbcTypeByRDBMSTypename(String aLowLevelTypeName) {
        return resolver.getJdbcTypeByRDBMSTypename(aLowLevelTypeName);
    }

    @Override
    public String getSql4TablesEnumeration(String schema4Sql) {
        if (schema4Sql == null || schema4Sql.isEmpty()) {
            return SQL_TABLES
                    + "order by " + ClientConstants.JDBCCOLS_TABLE_SCHEM + "," + ClientConstants.JDBCCOLS_TABLE_NAME;
        } else {
            return String.format(SQL_SCHEMA_TABLES, prepareName(schema4Sql));
        }
    }

    @Override
    public String getSql4SchemasEnumeration() {
        return SQL_SCHEMAS;
    }

    @Override
    public String getSql4CreateSchema(String aSchemaName, String aPassword) {
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            return String.format(CREATE_SCHEMA_CLAUSE, aSchemaName);
        }
        throw new IllegalArgumentException("Schema name is null or empty.");
    }

    @Override
    public String getSql4DropPkConstraint(String aSchemaName, PrimaryKeySpec aPk) {
        String constraintName = wrapNameIfRequired(aPk.getCName());
        String tableFullName = makeFullName(aSchemaName, aPk.getTable());
        return "alter table " + tableFullName + " drop constraint " + constraintName;
    }

    @Override
    public String getSql4CreateFkConstraint(String aSchemaName, List<ForeignKeySpec> listFk) {
        if (listFk != null && listFk.size() > 0) {
            ForeignKeySpec fk = listFk.get(0);
            String fkTableName = makeFullName(aSchemaName, fk.getTable());
            String fkName = fk.getCName();
            String fkColumnName = wrapNameIfRequired(fk.getField());

            PrimaryKeySpec pk = fk.getReferee();
            String pkSchemaName = pk.getSchema();
            String pkTableName = makeFullName(aSchemaName, pk.getTable());
            String pkColumnName = wrapNameIfRequired(pk.getField());

            for (int i = 1; i < listFk.size(); i++) {
                fk = listFk.get(i);
                pk = fk.getReferee();
                fkColumnName += ", " + wrapNameIfRequired(fk.getField());
                pkColumnName += ", " + wrapNameIfRequired(pk.getField());
            }

            String fkRule = "";
            switch (fk.getFkUpdateRule()) {
                case CASCADE:
                    fkRule += " ON UPDATE CASCADE ";
                    break;
                case NOACTION:
                    fkRule += " ON UPDATE no action";
                    break;
                case SETDEFAULT:
                    fkRule += " ON UPDATE set default";
                    break;
                case SETNULL:
                    fkRule += " ON UPDATE set null";
                    break;
            }
            switch (fk.getFkDeleteRule()) {
                case CASCADE:
                    fkRule += " ON DELETE CASCADE ";
                    break;
                case NOACTION:
                    fkRule += " ON DELETE no action ";
                    break;
                case SETDEFAULT:
                    fkRule += " ON DELETE set default ";
                    break;
                case SETNULL:
                    fkRule += " ON DELETE set null ";
                    break;
            }
            if (fk.getFkDeferrable()) {
                fkRule += " DEFERRABLE INITIALLY DEFERRED";
            }
            return String.format("ALTER TABLE %s ADD CONSTRAINT %s"
                    + " FOREIGN KEY (%s) REFERENCES %s (%s) %s", fkTableName, fkName.isEmpty() ? "" : wrapNameIfRequired(fkName), fkColumnName, pkTableName, pkColumnName, fkRule);
        }
        return null;
    }

    @Override
    public String[] getSql4CreatePkConstraint(String aSchemaName, List<PrimaryKeySpec> listPk) {
        if (listPk != null && listPk.size() > 0) {
            PrimaryKeySpec pk = listPk.get(0);
            String tableName = pk.getTable();
            String pkTableName = makeFullName(aSchemaName, tableName);
            String pkName = wrapNameIfRequired(generatePkName(tableName, PKEY_NAME_SUFFIX));
            String pkColumnName = wrapNameIfRequired(pk.getField());
            for (int i = 1; i < listPk.size(); i++) {
                pk = listPk.get(i);
                pkColumnName += ", " + wrapNameIfRequired(pk.getField());
            }
            return new String[]{
                String.format("ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s)", pkTableName, pkName, pkColumnName)
            };
        };
        return null;
    }

    @Override
    public boolean isConstraintsDeferrable() {
        return true;
    }

    @Override
    public String[] getSqls4AddingField(String aSchemaName, String aTableName, Field aField) {
        String fullTableName = makeFullName(aSchemaName, aTableName);
        return new String[]{
            String.format(SqlDriver.ADD_FIELD_SQL_PREFIX, fullTableName) + getSql4FieldDefinition(aField)
        };
    }

    @Override
    public String[] getSql4DroppingField(String aSchemaName, String aTableName, String aFieldName) {
        String fullTableName = wrapNameIfRequired(aTableName);
        if (aSchemaName != null && !aSchemaName.isEmpty()) {
            fullTableName = wrapNameIfRequired(aSchemaName) + "." + fullTableName;
        }
        return new String[]{
            String.format(DROP_FIELD_SQL_PREFIX, fullTableName) + wrapNameIfRequired(aFieldName) + " cascade"
        };
    }

    @Override
    public TwinString[] getCharsForWrap() {
        return charsForWrap;
    }

    @Override
    public char[] getRestrictedChars() {
        return restrictedChars;
    }

    @Override
    public boolean isHadWrapped(String aName) {
        return isHaveUpperCase(aName);
    }

    private String prepareName(String aName) {
        return (isWrappedName(aName) ? unwrapName(aName) : aName.toLowerCase());
    }
}
