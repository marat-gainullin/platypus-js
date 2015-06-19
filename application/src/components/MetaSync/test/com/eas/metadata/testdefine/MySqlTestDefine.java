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
public class MySqlTestDefine extends DbTestDefine {

    private static final Map<String, int[]> fieldsSizes = new HashMap<>();
    private static final Map<String, int[]> fieldsScales = new HashMap<>();
    private static final String[][] fieldsTypes = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {"tinyint", "NUMBER", "int2", "tinyint", "INTEGER", "TINYINT", "tinyint"},
        {"bool", "NUMBER", "int2", "tinyint", "INTEGER", "TINYINT", "tinyint"},
        {"boolean", "NUMBER", "int2", "tinyint", "INTEGER", "TINYINT", "tinyint"},
        {"smallint", "NUMBER", "int2", "smallint", "SMALLINT", "SMALLINT", "smallint"},
        {"mediumint", "NUMBER", "int4", "mediumint", "INTEGER", "INTEGER", "int"},
        {"int", "NUMBER", "int4", "int", "INTEGER", "INTEGER", "int"},
        {"integer", "NUMBER", "int4", "int", "INTEGER", "INTEGER", "int"},
        {"bigint", "NUMBER", "int8", "bigint", "BIGINT", "BIGINT", "bigint"},
        {"serial", "NUMBER", "int8", "bigint", "BIGINT", "BIGINT", "bigint"},
        {"bit", "NUMBER", "bit", "bit", "INTEGER", "INTEGER", "bit"},
        {"float", "FLOAT", "float4", "float", "DOUBLE", "REAL", "real"},
        {"real", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"double", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"double precision", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"decimal", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "decimal"},
        {"dec", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "decimal"},
        {"numeric", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "decimal"},
        {"char", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        {"varchar", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        {"tinytext", "CLOB", "text", "tinytext", "LONG VARCHAR", "VARCHAR", "text"},
        {"text", "CLOB", "text", "text", "LONG VARCHAR", "VARCHAR", "text"},
        {"mediumtext", "CLOB", "text", "mediumtext", "LONG VARCHAR", "VARCHAR", "text"},
        {"long varchar", "CLOB", "text", "mediumtext", "LONG VARCHAR", "VARCHAR", "text"},
        {"longtext", "CLOB", "text", "longtext", "LONG VARCHAR", "VARCHAR", "text"},
        {"binary", "RAW", "bytea", "binary", "CHAR () FOR BIT DATA", "VARBINARY", "binary"},
        {"varbinary", "RAW", "bytea", "varbinary", "VARCHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
        {"tinyblob", "RAW", "bytea", "tinyblob", "VARCHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
        {"blob", "BLOB", "bytea", "blob", "LONG VARCHAR FOR BIT DATA", "VARBINARY", "image"},
        {"mediumblob", "BLOB", "bytea", "mediumblob", "LONG VARCHAR FOR BIT DATA", "VARBINARY", "image"},
        {"long varbinary", "BLOB", "bytea", "mediumblob", "LONG VARCHAR FOR BIT DATA", "VARBINARY", "image"},
        {"longblob", "BLOB", "bytea", "longblob", "LONG VARCHAR FOR BIT DATA", "VARBINARY", "image"},
        {"date", "DATE", "date", "date", "DATE", "DATE", "datetime"},
        {"time", "DATE", "time", "time", "TIME", "TIME", "datetime"},
        {"year", "DATE", "date", "year", "DATE", "DATE", "datetime"},
        {"datetime", "TIMESTAMP(6)", "timestamp", "datetime", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"timestamp", "TIMESTAMP(6)", "timestamp", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"}, //        {"point", "VARBINARY", "bytea", "point", "CHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
    //        {"linestring", "VARBINARY", "bytea", "linestring", "CHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
    //        {"polygon", "VARBINARY", "bytea", "polygon", "CHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
    //        {"geometry", "VARBINARY", "bytea", "geometry", "CHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
    //        {"multipoint", "VARBINARY", "bytea", "multipoint", "CHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
    //        {"multilinestring", "VARBINARY", "bytea", "multilinestring", "CHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
    //        {"multipolygon", "VARBINARY", "bytea", "multipolygon", "CHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
    //        {"geometrycollection", "VARBINARY", "bytea", "geometrycollection", "CHAR () FOR BIT DATA", "VARBINARY", "varbinary"}
    //        {"enum", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
    //        {"set", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"}
    };

    static {
        // отрицательное значение в fieldsSizes и fieldsScales означает, что значение отсутствует  и проверка не производится
        //type, {original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsSizes.put("float", new int[]{4, -4, -4, 4, -4, -4, -4});
        fieldsSizes.put("real", new int[]{5, -5, -5, 5, -5, -5, -5});
        fieldsSizes.put("double", new int[]{6, -6, -6, 6, -6, -6, -6});
        fieldsSizes.put("double precision", new int[]{7, -7, -7, 7, -7, -7, -7});
        fieldsSizes.put("decimal", new int[]{8, -8, 8, 8, 8, 8, -8});
        fieldsSizes.put("dec", new int[]{10, -10, 10, 10, 10, 10, -10});
        fieldsSizes.put("numeric", new int[]{12, -12, 12, 12, 12, 12, -12});
        fieldsSizes.put("char", new int[]{14, 14, 14, 14, 14, 14, 14});
        fieldsSizes.put("varchar", new int[]{15, 15, 15, 15, 15, 15, 15});
        fieldsSizes.put("binary", new int[]{16, 16, -16, 16, 16, 16, -16});
        fieldsSizes.put("varbinary", new int[]{17, 17, -17, 17, 17, 17, -17});

        //originalType, {originalValue, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsScales.put("float", new int[]{2, -2, -2, 2, -2, -2, -2});
        fieldsScales.put("real", new int[]{3, -3, -3, 3, -3, -3, -3});
        fieldsScales.put("double", new int[]{4, -4, -4, 4, -4, -4, -4});
        fieldsScales.put("double precision", new int[]{5, -5, -5, 5, -5, -5, -5});
        fieldsScales.put("decimal", new int[]{6, -6, 6, 6, 6, 6, -6});
        fieldsScales.put("dec", new int[]{7, -7, 7, 7, 7, 7, -7});
        fieldsScales.put("numeric", new int[]{8, -8, 8, 8, 8, 8, -8});
    }
    private static final ForeignKeyRule[][] fKeyUpdateRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, null, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.NOACTION, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL},
        {ForeignKeyRule.SETDEFAULT, null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.CASCADE, null, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.NOACTION, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE}
    };
    private static final ForeignKeyRule[][] fKeyDeleteRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL},
        {ForeignKeyRule.SETDEFAULT, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
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
        return !("serial".equalsIgnoreCase(aFieldName));
    }
}
