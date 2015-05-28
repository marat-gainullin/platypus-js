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
public class PostgreTestDefine extends DbTestDefine {

    private static final Map<String, int[]> fieldsSizes = new HashMap<>();
    private static final Map<String, int[]> fieldsScales = new HashMap<>();
    private static final String[][] fieldsTypes = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {"decimal", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "numeric"}, //??????????????????????driver????????

        {"bit", "NUMBER", "bit", "bit", "INTEGER", "INTEGER", "bit"},
        {"bool", "NUMBER", "bool", "int", "INTEGER", "INTEGER", "int"},
        {"boolean", "NUMBER", "bool", "int", "INTEGER", "INTEGER", "int"},
        {"int8", "NUMBER", "int8", "bigint", "BIGINT", "BIGINT", "bigint"},
        {"bigint", "NUMBER", "int8", "bigint", "BIGINT", "BIGINT", "bigint"},
        {"bigserial", "NUMBER", "int8", "bigint", "BIGINT", "BIGINT", "bigint"},
        {"oid", "NUMBER", "oid", "bigint", "BIGINT", "BIGINT", "bigint"},
        {"bytea", "BLOB", "bytea", "longblob", "BLOB", "BLOB", "image"},
        {"bpchar", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        {"char", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        {"character", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        {"numeric", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "numeric"},
        {"integer", "NUMBER", "int4", "int", "INTEGER", "INTEGER", "int"},
        {"int", "NUMBER", "int4", "int", "INTEGER", "INTEGER", "int"},
        {"int4", "NUMBER", "int4", "int", "INTEGER", "INTEGER", "int"},
        {"serial", "NUMBER", "int4", "int", "INTEGER", "INTEGER", "int"},
        {"smallint", "NUMBER", "int2", "smallint", "SMALLINT", "SMALLINT", "smallint"},
        {"int2", "NUMBER", "int2", "smallint", "SMALLINT", "SMALLINT", "smallint"},
        {"real", "FLOAT", "float4", "float", "REAL", "REAL", "real"},
        {"float4", "FLOAT", "float4", "float", "REAL", "REAL", "real"},
        {"double precision", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"float", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"float8", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"money", "FLOAT", "money", "double", "DOUBLE", "DOUBLE", "float"},
        {"varchar", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        {"character varying", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        {"name", "VARCHAR2", "name", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        {"text", "CLOB", "text", "longtext", "CLOB", "CLOB", "text"},
        {"date", "DATE", "date", "date", "DATE", "DATE", "datetime"},
        {"time", "DATE", "time", "time", "TIME", "TIME", "datetime"},
        {"timetz", "DATE", "timetz", "time", "TIME", "TIME", "datetime"},
        {"time with time zone", "DATE", "timetz", "time", "TIME", "TIME", "datetime"},
        {"time without time zone", "DATE", "time", "time", "TIME", "TIME", "datetime"},
        {"timestamp", "TIMESTAMP(6)", "timestamp", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"timestamptz", "TIMESTAMP(6)", "timestamptz", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"timestamp with time zone", "TIMESTAMP(6)", "timestamptz", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"timestamp without time zone", "TIMESTAMP(6)", "timestamp", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"}
//GIS        {"", "", "", "", "", "", ""},
    };

    static {
        // отрицательное значение в fieldsSizes и fieldsScales означает, что значение отсутствует  и проверка не производится
        //type, {original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsSizes.put("numeric", new int[]{3, 3, 3, 3, 3, 3, -3});
        fieldsSizes.put("decimal", new int[]{4, 4, 4, 4, 4, 4, -4});
        fieldsSizes.put("char", new int[]{5, 5, 5, 5, 5, 5, -5});
        fieldsSizes.put("varchar", new int[]{7, 7, 7, 7, 7, 7, 7});
        fieldsSizes.put("character varying", new int[]{9, 9, 9, 9, 9, 9, 9});

        //originalType, {originalValue, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsScales.put("numeric", new int[]{1, 1, 1, 1, 1, 1, -1});
        fieldsScales.put("decimal", new int[]{2, 2, 2, 2, 2, 2, -2});
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
        return !("bigserial".equalsIgnoreCase(aFieldName) || "serial".equalsIgnoreCase(aFieldName));
    }
}
