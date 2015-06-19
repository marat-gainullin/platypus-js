/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.testdefine;

import com.eas.client.metadata.ForeignKeySpec;
import com.eas.client.metadata.ForeignKeySpec.ForeignKeyRule;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vy
 */
public class H2TestDefine extends DbTestDefine {

    private static final Map<String, int[]> fieldsSizes = new HashMap<>();
    private static final Map<String, int[]> fieldsScales = new HashMap<>();
    private static final String[][] fieldsTypes = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {"TINYINT", "NUMBER", "int2", "tinyint", "INTEGER", "TINYINT", "tinyint"},
        {"BIGINT", "NUMBER", "int8", "bigint", "BIGINT", "BIGINT", "bigint"},
        {"IDENTITY", "NUMBER", "int8", "bigint", "BIGINT", "BIGINT", "bigint"},
        {"LONGVARBINARY", "RAW", "bytea", "varbinary", "VARCHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
        {"VARBINARY", "RAW", "bytea", "varbinary", "VARCHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
        {"BINARY", "RAW", "bytea", "varbinary", "VARCHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
        {"UUID", "RAW", "bytea", "binary", "CHAR () FOR BIT DATA", "UUID", "binary"},
        {"LONGVARCHAR", "CLOB", "text", "longtext", "CLOB", "VARCHAR", "text"},
        {"CHAR", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        {"NUMERIC", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "decimal"},
        {"DECIMAL", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "decimal"},
        {"INTEGER", "NUMBER", "int4", "int", "INTEGER", "INTEGER", "int"},
        {"SMALLINT", "NUMBER", "int2", "smallint", "SMALLINT", "SMALLINT", "smallint"},
        {"FLOAT", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"REAL", "FLOAT", "float4", "float", "REAL", "REAL", "real"},
        {"DOUBLE", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"VARCHAR", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        {"VARCHAR_IGNORECASE", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR_IGNORECASE", "varchar"},
        {"BOOLEAN", "NUMBER", "bool", "int", "INTEGER", "BOOLEAN", "int"},
        {"DATE", "DATE", "date", "date", "DATE", "DATE", "datetime"},
        {"TIME", "DATE", "time", "time", "TIME", "TIME", "datetime"},
        {"TIMESTAMP", "TIMESTAMP(6)", "timestamp", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"BLOB", "BLOB", "bytea", "longblob", "BLOB", "BLOB", "image"},
        {"CLOB", "CLOB", "text", "longtext", "CLOB", "CLOB", "text"}
//??????????????????????????
//        {"OTHER", "", "", "", "", "other", ""},
//        {"ARRAY", "", "", "", "", "array", ""},
//??????????????????????????
    };

    static {
        // отрицательное значение в fieldsSizes и fieldsScales означает, что значение отсутствует  и проверка не производится
        //type, {original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsSizes.put("DECIMAL", new int[]{4, -22, 4, 4, 4, 4, -4});
        fieldsSizes.put("NUMERIC", new int[]{5, -22, 5, 5, 5, 5, -5});

        fieldsSizes.put("LONGVARBINARY", new int[]{6, -4000, -6, 6, 6, 6, -6});
        fieldsSizes.put("VARBINARY", new int[]{7, -4000, -7, 7, 7, 7, -7});
        fieldsSizes.put("BINARY", new int[]{8, -4000, -8, 8, 8, 8, -8});
        fieldsSizes.put("UUID", new int[]{9, -4000, -9, 9, 9, 9, -9});
//        fieldsSizes.put("LONGVARCHAR",   new int[]{ 15,  -4000,  -15,  -15,  -15,  15,  15});
        fieldsSizes.put("CHAR", new int[]{25, 25, 25, 25, 25, 25, 25});
        fieldsSizes.put("VARCHAR", new int[]{26, 26, 26, 26, 26, 26, 26});
        fieldsSizes.put("VARCHAR_IGNORECASE", new int[]{28, 28, 28, 28, 28, 28, 28});

//        fieldsSizes.put("BLOB",   new int[]{ 35,  -4000,  -35,  -35,  -35,  35,  35});
//        fieldsSizes.put("CLOB",   new int[]{ 45,  -4000,  -45,  -45,  -45,  45,  45});

        //originalType, {originalValue, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsScales.put("DECIMAL", new int[]{2, -2, 2, 2, 2, 2, -2});
        fieldsScales.put("NUMERIC", new int[]{3, -2, 3, 3, 3, 3, -3});

    }
    private static final ForeignKeyRule[][] fKeyUpdateRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, null, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.NOACTION, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL},
        {ForeignKeyRule.SETDEFAULT, null, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETNULL},
        {ForeignKeyRule.CASCADE, null, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.NOACTION, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE}
    };
    private static final ForeignKeyRule[][] fKeyDeleteRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL},
        {ForeignKeyRule.SETDEFAULT, ForeignKeyRule.NOACTION, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETNULL},
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
    public ForeignKeySpec.ForeignKeyRule[][] getFKeyDeleteRules() {
        return fKeyDeleteRules;
    }

    @Override
    public ForeignKeySpec.ForeignKeyRule[][] getFKeyUpdateRules() {
        return fKeyUpdateRules;
    }

    @Override
    public boolean[][] getFKeyDeferrables() {
        return fKeyDeferrable;
    }

    @Override
    public boolean enabledSetNull(String aFieldName) {
        return !("IDENTITY".equalsIgnoreCase(aFieldName));
    }
}
