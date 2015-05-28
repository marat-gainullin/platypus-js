/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.testdefine;

import com.eas.client.metadata.ForeignKeySpec.ForeignKeyRule;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vy
 */
public class MsSqlTestDefine extends DbTestDefine {

    private static final Map<String, int[]> fieldsSizes = new HashMap<>();
    private static final Map<String, int[]> fieldsScales = new HashMap<>();
    private static final String[][] fieldsTypes = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {"image", "BLOB", "bytea", "longblob", "BLOB", "BLOB", "image"},
        {"text", "CLOB", "text", "longtext", "CLOB", "CLOB", "text"},
        {"ntext", "NCLOB", "text", "longtext", "CLOB", "CLOB", "ntext"},
        {"uniqueidentifier", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "uniqueidentifier"},
        {"smallint", "NUMBER", "int2", "smallint", "SMALLINT", "SMALLINT", "smallint"},
        {"tinyint", "NUMBER", "int2", "tinyint", "INTEGER", "TINYINT", "tinyint"},
        {"bigint", "NUMBER", "int8", "bigint", "BIGINT", "BIGINT", "bigint"},
        {"int", "NUMBER", "int4", "int", "INTEGER", "INTEGER", "int"},
        {"smalldatetime", "TIMESTAMP(6)", "timestamp", "timestamp", "TIMESTAMP", "TIMESTAMP", "smalldatetime"},
        {"real", "FLOAT", "float4", "float", "REAL", "REAL", "real"},
        {"datetime", "TIMESTAMP(6)", "timestamp", "datetime", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"float", "FLOAT", "float8", "float", "REAL", "DOUBLE", "float"},
        {"money", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "money"},
        {"smallmoney", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "smallmoney"},
        //???        {"sql_variant", "", "", "", "", "", ""},
        {"bit", "NUMBER", "bit", "bit", "INTEGER", "INTEGER", "bit"},
        {"decimal", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "decimal"},
        {"numeric", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "numeric"},
        {"varbinary", "RAW", "bytea", "varbinary", "VARCHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
        {"varchar", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        {"nvarchar", "NVARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "nvarchar"},
        {"tinyint identity", "NUMBER", "int2", "tinyint", "INTEGER", "TINYINT", "tinyint"},
        {"bigint identity", "NUMBER", "int8", "bigint", "BIGINT", "BIGINT", "bigint"},
        {"binary", "RAW", "bytea", "binary", "CHAR () FOR BIT DATA", "VARBINARY", "binary"},
        //        {"timestamp", "VARBINARY", "bytea", "varbinary", "VARCHAR () FOR BIT DATA", "VARBINARY", "timestamp"}, //?????????????????
        {"char", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        {"nchar", "NCHAR", "bpchar", "char", "CHAR", "CHAR", "nchar"},
        {"numeric identity", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "numeric"},
        {"decimal identity", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "decimal"},
        {"int identity", "NUMBER", "int4", "int", "INTEGER", "INTEGER", "int"},
        {"smallint identity", "NUMBER", "int2", "smallint", "SMALLINT", "SMALLINT", "smallint"},
        {"sysname", "NVARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "sysname"},
        {"xml", "CLOB", "text", "longtext", "CLOB", "CLOB", "xml"},};

    static {
        //type, {original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsSizes.put("CHAR", new int[]{7, 7, 7, 7, 7, 7, 7});
        fieldsSizes.put("VARCHAR2", new int[]{8, 8, 8, 8, 8, 8, 8});
        fieldsSizes.put("NCHAR", new int[]{10, 10, 10, 10, 10, 10, 10});
        fieldsSizes.put("NVARCHAR2", new int[]{12, 12, 12, 12, 12, 12, 12});
        fieldsSizes.put("BINARY", new int[]{20, 20, 20, 20, 20, 20, 20});
        fieldsSizes.put("VARBINARY", new int[]{22, 22, 22, 22, 22, 22, 22});
    }
    private static final ForeignKeyRule[][] fKeyUpdateRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, null, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.NOACTION, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL},
        {ForeignKeyRule.SETDEFAULT, null, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.NOACTION, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL},
        {ForeignKeyRule.CASCADE, null, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.NOACTION, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE}
    };
    private static final ForeignKeyRule[][] fKeyDeleteRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL},
        {ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL},
        {ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE}
    };
    private static final boolean[][] fKeyDeferrable = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {false, false, false, false, false, false, false},
        {true, true, true, true, true, true, true}
    };

    @Override
    public Map<String, int[]> getFieldsSizes() {
        return fieldsSizes;
    }

    @Override
    public Map<String, int[]> getFieldsScales() {
        return fieldsScales;
    }

    @Override
    public String[][] getFieldsTypes() {
        return fieldsTypes;
    }

    @Override
    public ForeignKeyRule[][] getFKeyDeleteRules() {
        return fKeyDeleteRules;
    }

    @Override
    public ForeignKeyRule[][] getFKeyUpdateRules() {
        return fKeyUpdateRules;
    }

    @Override
    public boolean[][] getFKeyDeferrables() {
        return fKeyDeferrable;
    }

    @Override
    public boolean enabledSetNull(String aFieldName) {
        return !(aFieldName.equalsIgnoreCase("uniqueidentifier") || aFieldName.contains(" identity"));
    }
}
