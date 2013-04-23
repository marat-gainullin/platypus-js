/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.testdefine;

import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.bearsoft.rowset.metadata.ForeignKeySpec.ForeignKeyRule;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vy
 */
public class H2TestDefine extends DbTestDefine {
    
    private static final Map<String, int[]> fieldsSizes = new HashMap<>();
    private static final Map<String, int[]> fieldsScales = new HashMap<>();
    
    private static final  String[][] fieldsTypes = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {"TINYINT", "NUMBER", "numeric", "tinyint", "", "TINYINT", ""},
        {"BIGINT", "NUMBER", "numeric", "bigint", "", "BIGINT", ""},
        {"IDENTITY", "NUMBER", "numeric", "bigint", "", "BIGINT", ""},
        {"LONGVARBINARY", "BLOB", "bytea", "varbinary", "", "VARBINARY", ""},
        {"VARBINARY", "BLOB", "bytea", "varbinary", "", "VARBINARY", ""},
        {"BINARY", "BLOB", "bytea", "varbinary", "", "VARBINARY", ""},
        {"UUID", "BLOB", "bytea", "varbinary", "", "VARBINARY", ""},
        {"LONGVARCHAR", "CLOB", "text", "longtext", "", "CLOB", ""},
        {"CHAR", "CHAR", "bpchar", "char", "", "CHAR", ""},
        {"NUMERIC", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"DECIMAL", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"INTEGER", "NUMBER", "numeric", "int", "", "INTEGER", ""},
        {"SMALLINT", "NUMBER", "numeric", "smallint", "", "SMALLINT", ""},
        {"FLOAT", "FLOAT", "float8", "double", "", "DOUBLE", ""},
        {"REAL", "NUMBER", "numeric", "float", "", "REAL", ""},
        {"DOUBLE", "FLOAT", "float8", "double", "", "DOUBLE", ""},
        {"VARCHAR", "VARCHAR2", "varchar", "varchar", "", "VARCHAR", ""},
        {"VARCHAR_IGNORECASE", "VARCHAR2", "varchar", "varchar", "", "VARCHAR", ""},
        {"BOOLEAN", "NUMBER", "numeric", "int", "", "INTEGER", ""},
        {"DATE", "DATE", "date", "date", "date", "DATE", ""},
        {"TIME", "DATE", "date", "time", "", "TIME", ""},
        {"TIMESTAMP", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""},
        {"BLOB", "BLOB", "bytea", "longblob", "", "BLOB", ""},
        {"CLOB", "CLOB", "text", "longtext", "", "CLOB", ""}
        

//??????????????????????????
//        {"OTHER", "", "", "", "", "other", ""},
//        {"ARRAY", "", "", "", "", "array", ""},
//??????????????????????????
    };
    
    
    static {            //type, {original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsSizes.put("DECIMAL",  new int[]{ 4,  -22,  4,  4,  4,  4,  4});
        fieldsSizes.put("NUMERIC",   new int[]{ 5,  -22,  5,  5,  5,  5,  5});
        
        fieldsSizes.put("LONGVARBINARY",   new int[]{ 6,  -4000,  -6,  6,  6,  6,  6});
        fieldsSizes.put("VARBINARY",   new int[]{ 7,  -4000,  -7,  7,  7,  7,  7});
        fieldsSizes.put("BINARY",   new int[]{ 8,  -4000,  -8,  8,  8,  8,  8});
        fieldsSizes.put("UUID",   new int[]{ 9,  -4000,  -9,  9,  9,  9,  9});
        fieldsSizes.put("LONGVARCHAR",   new int[]{ 15,  -4000,  -15,  -15,  15,  15,  15});
        fieldsSizes.put("CHAR",   new int[]{ 25,  25,  25,  25,  25,  25,  25});
        fieldsSizes.put("VARCHAR",   new int[]{ 26,  26,  26,  26,  26,  26,  26});
        fieldsSizes.put("VARCHAR_IGNORECASE",   new int[]{ 28,  28,  28,  28,  28,  28,  28});
        
        fieldsSizes.put("BLOB",   new int[]{ 35,  -4000,  -35,  -35,  35,  35,  35});
        fieldsSizes.put("CLOB",   new int[]{ 45,  -4000,  -45,  -45,  45,  45,  45});

        //originalType, {originalValue, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsScales.put("DECIMAL",  new int[]{2, -2, 2, 2, 2, 2, 2});
        fieldsScales.put("NUMERIC",  new int[]{3, -2, 3, 3, 3, 3, 3});

//        //typeName(M,D)
//        jdbcTypesWithScale.add(Types.NUMERIC);
//        jdbcTypesWithScale.add(Types.DECIMAL);
//
//        //typeName(M)
//        jdbcTypesWithSize.add(Types.LONGVARBINARY);
//        jdbcTypesWithSize.add(Types.VARBINARY);
//        jdbcTypesWithSize.add(Types.BINARY);
//        jdbcTypesWithSize.add(Types.LONGNVARCHAR);
//        jdbcTypesWithSize.add(Types.CHAR);
//        jdbcTypesWithSize.add(Types.VARCHAR);
//        jdbcTypesWithSize.add(Types.NCHAR);
//        jdbcTypesWithSize.add(Types.NVARCHAR);
//        jdbcTypesWithSize.add(Types.OTHER);
//        jdbcTypesWithSize.add(Types.BLOB);
//        jdbcTypesWithSize.add(Types.CLOB);
        
    }
    
    
    
    private static final  ForeignKeyRule [][] fKeyUpdateRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, null, ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL},
        {ForeignKeyRule.SETDEFAULT,  null, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.NOACTION, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETDEFAULT}, 
        {ForeignKeyRule.CASCADE, null, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE}
    };        
    
    private static final  ForeignKeyRule [][] fKeyDeleteRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL},
        {ForeignKeyRule.SETDEFAULT,  ForeignKeyRule.NOACTION, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.NOACTION, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETDEFAULT}, 
        {ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE}
    };   
    
    private static final boolean [][] fKeyDeferrable = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {false, false, false, false, false, false, false},
        {true, true, true, true, true, true, true}
    };
    
    @Override
    public  Map<String, int[]> getFieldsSizes() {
        return fieldsSizes;
    }

    @Override
    public Map<String, int[]> getFieldsScales() {
        return fieldsScales;
    }

    @Override
    public  String[][] getFieldsTypes() {
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
}
