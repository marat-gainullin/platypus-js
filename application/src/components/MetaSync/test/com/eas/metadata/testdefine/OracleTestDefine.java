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
public class OracleTestDefine extends DbTestDefine {

    private static final Map<String, int[]> fieldsSizes = new HashMap<>();
    private static final Map<String, int[]> fieldsScales = new HashMap<>();
    private static final String[][] fieldsTypes = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {"NUMBER", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "numeric"},
        {"INTEGER", "NUMBER", "numeric", "decimal", "DECIMAL", "DECIMAL", "numeric"},
        {"FLOAT", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"REAL", "FLOAT", "float8", "double", "DOUBLE", "DOUBLE", "float"},
        {"VARCHAR2", "VARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        {"NVARCHAR2", "NVARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "nvarchar"},
        {"DATE", "DATE", "date", "date", "DATE", "DATE", "datetime"},
        {"TIMESTAMP", "TIMESTAMP(6)", "timestamp", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"TIMESTAMP(6)", "TIMESTAMP(6)", "timestamp", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"TIMESTAMP WITH TIME ZONE", "TIMESTAMP(6) WITH TIME ZONE", "timestamp", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"TIMESTAMP WITH LOCAL TIME ZONE", "TIMESTAMP(6) WITH LOCAL TIME ZONE", "timestamp", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"TIMESTAMP(6) WITH TIME ZONE", "TIMESTAMP(6) WITH TIME ZONE", "timestamp", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"TIMESTAMP(6) WITH LOCAL TIME ZONE", "TIMESTAMP(6) WITH LOCAL TIME ZONE", "timestamp", "timestamp", "TIMESTAMP", "TIMESTAMP", "datetime"},
        {"LONG RAW", "LONG RAW", "bytea", "longblob", "LONG VARCHAR FOR BIT DATA", "VARBINARY", "image"},
        {"RAW", "RAW", "bytea", "varbinary", "VARCHAR () FOR BIT DATA", "VARBINARY", "varbinary"},
        {"LONG", "LONG", "text", "longtext", "LONG VARCHAR", "VARCHAR", "text"},
        {"CHAR", "CHAR", "bpchar", "char", "CHAR", "CHAR", "char"},
        {"BLOB", "BLOB", "bytea", "longblob", "BLOB", "BLOB", "image"},
        {"CLOB", "CLOB", "text", "longtext", "CLOB", "CLOB", "text"},
        {"NCLOB", "NCLOB", "text", "longtext", "CLOB", "CLOB", "ntext"},
        {"NVARCHAR2", "NVARCHAR2", "varchar", "varchar", "VARCHAR", "VARCHAR", "varchar"},
        {"NCHAR", "NCHAR", "bpchar", "char", "CHAR", "CHAR", "nchar"}
//         
//    //"MDSYS.SDO_GEOMETRY"
    };

    static {
        // отрицательное значение в fieldsSizes и fieldsScales означает, что значение отсутствует  и проверка не производится
        //type, {original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsSizes.put("DECIMAL", new int[]{4, 4, 4, 4, 4, 4, 4});
        fieldsSizes.put("NUMBER", new int[]{5, 5, 5, 5, 5, 5, -5});
//        fieldsSizes.put("FLOAT",    new int[]{ 6,  6,  6,  6,  6,  6,  6});
        fieldsSizes.put("CHAR", new int[]{7, 7, 7, 7, 7, 7, 7});
        fieldsSizes.put("VARCHAR2", new int[]{8, 8, 8, 8, 8, 8, 8});
        fieldsSizes.put("NCHAR", new int[]{10, 10, 10, 10, 10, 10, 10});
        fieldsSizes.put("NVARCHAR2", new int[]{12, 12, 12, 12, 12, 12, 12});
        fieldsSizes.put("RAW", new int[]{11, 11, -11, 11, 11, 11, -11});

        //originalType, {originalValue, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsScales.put("DECIMAL", new int[]{2, 2, 2, 2, 2, 2, 2});
        fieldsScales.put("NUMBER", new int[]{3, 3, 3, 3, 3, 3, -3});
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
