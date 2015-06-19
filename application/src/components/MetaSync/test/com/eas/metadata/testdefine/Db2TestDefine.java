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
public class Db2TestDefine extends DbTestDefine {

    private static final Map<String, int[]> fieldsSizes = new HashMap<>();
    private static final Map<String, int[]> fieldsScales = new HashMap<>();
    private static final String[][] fieldsTypes = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {"SMALLINT", "NUMBER", "int2", "smallint", "SMALLINT", "SMALLINT", "smallint"},
        {"INTEGER", "NUMBER", "int4", "int", "INTEGER", "INTEGER", "int"},
        {"INT", "NUMBER", "int4", "int", "INTEGER", "INTEGER", "int"},
        {"BIGINT", "NUMBER", "int8", "bigint", "BIGINT", "BIGINT", "bigint"},
        {"DECIMAL", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "decimal"},
        {"DEC", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "decimal"},
        {"NUMERIC", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "decimal"},
        {"NUM", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "decimal"},
        {"FLOAT", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"REAL", "FLOAT", "float4", "float", "REAL", "REAL", "real"},
        {"DOUBLE", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"DOUBLE PRECISION", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"CHAR", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        {"CHARACTER", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        {"VARCHAR", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        {"CHAR VARYING", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        {"CHARACTER VARYING", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        {"CHAR () FOR BIT DATA", "RAW", "bytea", "binary", "CHAR () FOR BIT DATA", "VARBINARY", "binary"},
        {"CHARACTER () FOR BIT DATA", "RAW", "bytea", "binary", "CHAR () FOR BIT DATA", "VARBINARY", "binary"},
        {"CHAR VARYING () FOR BIT DATA", "RAW", "bytea", "varbinary", "VARCHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
        {"VARCHAR () FOR BIT DATA", "RAW", "bytea", "varbinary", "VARCHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
        {"CHARACTER VARYING () FOR BIT DATA", "RAW", "bytea", "varbinary", "VARCHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
        {"LONG VARCHAR", "CLOB", "text", "mediumtext", "LONG VARCHAR", "VARCHAR", "text"},
        {"LONG VARCHAR FOR BIT DATA", "BLOB", "bytea", "longblob", "LONG VARCHAR FOR BIT DATA", "VARBINARY", "image"},
        {"CLOB", "CLOB", "text", "longtext", "CLOB", "CLOB", "text"},
        {"CHAR LARGE OBJECT", "CLOB", "text", "longtext", "CLOB", "CLOB", "text"},
        {"CHARACTER LARGE OBJECT", "CLOB", "text", "longtext", "CLOB", "CLOB", "text"},
        // !!! not supported for this database !!!
        //        {"GRAPHIC", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        //        {"VARGRAPHIC", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        //        {"LONG VARGRAPHIC", "LONG", "text", "longtext", "LONG VARCHAR", "CLOB", "clob"},
        //        {"DBCLOB", "CLOB", "text", "longtext", "CLOB", "CLOB", "clob"},
        //        {"NCHAR", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        //        {"NATIONAL CHAR", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        //        {"NATIONAL CHARACTER", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        //        {"NVARCHAR", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        //        {"NATIONAL CHARACTER VARYING", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        //        {"NATIONAL CHAR VARYING", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        //        {"NCHAR VARYING", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        //        {"NCLOB", "CLOB", "text", "longtext", "CLOB", "CLOB", "clob"},
        //        {"NCHAR LARGE OBJECT", "CLOB", "text", "longtext", "CLOB", "CLOB", "clob"},
        //        {"NATIONAL CHARACTER LARGE OBJECT", "CLOB", "text", "longtext", "CLOB", "CLOB", "clob"},
        // !!! not supported for this database !!!
        {"BLOB", "BLOB", "bytea", "longblob", "BLOB", "BLOB", "image"},
        {"BINARY LARGE OBJECT", "BLOB", "bytea", "longblob", "BLOB", "BLOB", "image"},
        {"DATE", "DATE", "date", "date", "DATE", "DATE", "datetime"},
        {"TIME", "DATE", "time", "time", "TIME", "TIME", "datetime"},
        {"TIMESTAMP", "TIMESTAMP(6)", "timestamp", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"XML", "BLOB", "bytea", "longblob", "XML", "BLOB", "image"},};

    static {
        // отрицательное значение в fieldsSizes и fieldsScales означает, что значение отсутствует  и проверка не производится
        //type, {original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsSizes.put("DECIMAL", new int[]{4, -4, 4, 4, 4, 4, -4});
        fieldsSizes.put("DEC", new int[]{5, -5, 5, 5, 5, 5, -5});
        fieldsSizes.put("NUMERIC", new int[]{6, -6, 6, 6, 6, 6, -6});
        fieldsSizes.put("CHAR", new int[]{7, 7, 7, 7, 7, 7, 7});
        fieldsSizes.put("CHARACTER", new int[]{8, 8, 8, 8, 8, 8, 8});
        fieldsSizes.put("GRAPHIC", new int[]{9, 9, 9, 9, 9, 9, 9});
        fieldsSizes.put("NCHAR", new int[]{10, 10, 10, 10, 10, 10, 10});
        fieldsSizes.put("NATIONAL CHAR", new int[]{11, 11, 11, 11, 11, 11, 11});
        fieldsSizes.put("NATIONAL CHARACTER", new int[]{12, 12, 12, 12, 12, 12, 12});

        fieldsSizes.put("VARCHAR", new int[]{13, 13, 13, 13, 13, 13, 13});
        fieldsSizes.put("CHAR VARYING", new int[]{14, 14, 14, 14, 14, 14, 14});
        fieldsSizes.put("CHARACTER VARYING", new int[]{15, 15, 15, 15, 15, 15, 15});
        fieldsSizes.put("VARGRAPHIC", new int[]{16, 16, 16, 16, 16, 16, 16});
        fieldsSizes.put("NVARCHAR", new int[]{17, 17, 17, 17, 17, 17, 17});
        fieldsSizes.put("NCHAR VARYING", new int[]{18, 18, 18, 18, 18, 18, 18});
        fieldsSizes.put("NATIONAL CHAR VARYING", new int[]{19, 19, 19, 19, 19, 19, 19});
        fieldsSizes.put("NATIONAL CHARACTER VARYING", new int[]{20, 20, 20, 20, 20, 20, 20});

        fieldsSizes.put("CHAR () FOR BIT DATA", new int[]{21, 21, -21, 21, 21, 21, -21});
        fieldsSizes.put("CHARACTER () FOR BIT DATA", new int[]{22, 22, -22, 22, 22, 22, -22});

        fieldsSizes.put("CHAR VARYING () FOR BIT DATA", new int[]{23, 23, -23, 23, 23, 23, -23});
        fieldsSizes.put("VARCHAR () FOR BIT DATA", new int[]{24, 24, -24, 24, 24, 24, -24});
        fieldsSizes.put("CHARACTER VARYING () FOR BIT DATA", new int[]{25, 25, -25, 25, 25, 25, -25});

//        //originalType, {originalValue, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsScales.put("DECIMAL", new int[]{2, -2, 2, 2, 2, 2, -2});
        fieldsScales.put("DEC", new int[]{3, -3, 3, 3, 3, 3, -3});
        fieldsScales.put("NUMERIC", new int[]{4, -4, 4, 4, 4, 4, -4});
    }
    private static final ForeignKeyRule[][] fKeyUpdateRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETDEFAULT, null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.CASCADE, null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},};
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
        return true;
    }
}
