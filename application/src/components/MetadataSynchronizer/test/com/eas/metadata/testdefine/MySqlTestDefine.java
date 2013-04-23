/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.testdefine;

import com.bearsoft.rowset.metadata.ForeignKeySpec.ForeignKeyRule;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vy
 */
public class MySqlTestDefine extends DbTestDefine {
    
    private static final Map<String, int[]> fieldsSizes = new HashMap<>();
    private static final Map<String, int[]> fieldsScales = new HashMap<>();
    
    private static final  String[][] fieldsTypes = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {"tinyint", "NUMBER", "numeric", "tinyint", "", "TINYINT", ""},
        {"bool", "NUMBER", "numeric", "tinyint", "", "TINYINT", ""},
        {"boolean", "NUMBER", "numeric", "tinyint", "", "TINYINT", ""},
        {"smallint", "NUMBER", "numeric", "smallint", "", "SMALLINT", ""},
        {"mediumint", "NUMBER", "numeric", "int", "", "INTEGER", ""},
        {"int", "NUMBER", "numeric", "int", "", "INTEGER", ""},
        {"integer", "NUMBER", "numeric", "int", "", "INTEGER", ""},
        {"bigint", "NUMBER", "numeric", "bigint", "", "BIGINT", ""},
        {"serial", "NUMBER", "numeric", "bigint", "", "BIGINT", ""},
        {"bit", "NUMBER", "bit", "bit", "", "INTEGER", ""},
        {"float", "NUMBER", "numeric", "float", "", "REAL", ""},
        {"real", "FLOAT", "float8", "double", "", "DOUBLE", ""},
        {"double", "FLOAT", "float8", "double", "", "DOUBLE", ""},
        {"double precision", "FLOAT", "float8", "double", "", "DOUBLE", ""},
        {"decimal", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"dec", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"numeric", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"char", "CHAR", "bpchar", "char", "", "CHAR", ""},
        {"varchar", "VARCHAR2", "varchar", "varchar", "", "VARCHAR", ""},
        {"tinytext", "LONG", "text", "longtext", "", "CLOB", ""},
        {"text", "LONG", "text", "longtext", "", "CLOB", ""},
        {"mediumtext", "LONG", "text", "longtext", "", "CLOB", ""},
        {"longtext", "LONG", "text", "longtext", "", "CLOB", ""},
        {"binary", "VARBINARY", "bytea", "binary", "", "VARBINARY", ""},
        {"varbinary", "BLOB", "bytea", "varbinary", "", "VARBINARY", ""},
        {"tinyblob", "BLOB", "bytea", "varbinary", "", "VARBINARY", ""},
        {"blob", "BLOB", "bytea", "longblob", "", "VARBINARY", ""},
        {"mediumblob", "BLOB", "bytea", "longblob", "", "VARBINARY", ""},
        {"longblob", "BLOB", "bytea", "longblob", "", "VARBINARY", ""},
        {"date", "DATE", "date", "date", "", "DATE", ""},
        {"time", "DATE", "date", "time", "", "TIME", ""},
        {"year", "DATE", "date", "date", "", "DATE", ""},
        {"datetime", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""},
        {"timestamp", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""},
        {"point", "VARBINARY", "bytea", "binary", "", "VARBINARY", ""},
        {"linestring", "VARBINARY", "bytea", "binary", "", "VARBINARY", ""},
        {"polygon", "VARBINARY", "bytea", "binary", "", "VARBINARY", ""},
        {"geometry", "VARBINARY", "bytea", "binary", "", "VARBINARY", ""},
        {"multipoint", "VARBINARY", "bytea", "binary", "", "VARBINARY", ""},
        {"multilinestring", "VARBINARY", "bytea", "binary", "", "VARBINARY", ""},
        {"multipolygon", "VARBINARY", "bytea", "binary", "", "VARBINARY", ""},
        {"geometrycollection", "VARBINARY", "bytea", "binary", "", "VARBINARY", ""},
        {"enum", "CHAR", "bpchar", "char", "", "CHAR", ""},
        {"set", "CHAR", "bpchar", "char", "", "CHAR", ""},
    };
    
    static {            
        //type, {original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsSizes.put("float",  new int[]{ 4,  4,  4,  4,  4,  -4,  4});
        fieldsSizes.put("real",   new int[]{ 5,  -5,  -5,  5,  5,  -5,  5});
        fieldsSizes.put("double",    new int[]{ 6,  -6,  -6,  6,  6,  -6,  6});
        fieldsSizes.put("double precision",     new int[]{ 7,  -7,  -7,  7,  7,  -7,  7});
        fieldsSizes.put("decimal", new int[]{ 8,  -8,  8,  8,  8,  8,  8});
        fieldsSizes.put("dec",    new int[]{10, -10, 10, 10, 10, 10, 10});
        fieldsSizes.put("numeric",new int[]{12, -12, 12, 12, 12, 12, 12});
        fieldsSizes.put("char",  new int[]{ 14,  14,  14,  14,  14,  14,  14});
        fieldsSizes.put("varchar",   new int[]{ 15,  15,  15,  15,  15,  15,  15});
        fieldsSizes.put("binary",    new int[]{ 16,  16,  -16,  16,  16,  16,  16});
        fieldsSizes.put("varbinary",     new int[]{ 17,  -17,  -17,  17,  17,  17,  17});
        fieldsSizes.put("tinyblob",     new int[]{ 18,  -18,  -18,  18,  18,  18,  18});
        fieldsSizes.put("point",     new int[]{ 20, 20, -20, 20, 20, 20, 20});
        fieldsSizes.put("linestring",     new int[]{ 21, 21, -21, 21, 21, 21, 21});
        fieldsSizes.put("polygon",     new int[]{ 22, 22, -22, 22, 22, 22, 22});
        fieldsSizes.put("geometry",     new int[]{ 23, 23, -23, 23, 23, 23, 23});
        fieldsSizes.put("multipoint",     new int[]{ 24, 24, -24, 24, 24, 24, 24});
        fieldsSizes.put("multilinestring",     new int[]{ 25, 25, -25, 25, 25, 25, 25});
        fieldsSizes.put("multipolygon",     new int[]{ 26, 26, -26, 26, 26, 26, 26});
        fieldsSizes.put("geometrycollection",     new int[]{ 27, 27, -27, 27, 27, 27, 27});
        fieldsSizes.put("enum",     new int[]{ 28, 28, 28, 28, 28, 28, 28});
        fieldsSizes.put("set",     new int[]{ 29, 29, 29, 29, 29, 29, 29});

        //originalType, {originalValue, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsScales.put("float",  new int[]{2, 2, 2, 2, 2, -2, 2});
        fieldsScales.put("real",   new int[]{3, -3, -3, 3, 3, -3, 3});
        fieldsScales.put("double",   new int[]{4, -4, -4, 4, 4, -4, 4});
        fieldsScales.put("double precision",   new int[]{5, -5, -5, 5, 5, -5, 5});
        fieldsScales.put("decimal",   new int[]{6, -6, 6, 6, 6, 6, 6});
        fieldsScales.put("dec",   new int[]{7, -7, 7, 7, 7, 7, 7});
        fieldsScales.put("numeric",   new int[]{8, -8, 8, 8, 8, 8, 8});
    }
    
    
    
    private static final  ForeignKeyRule [][] fKeyUpdateRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, null, ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL},
        {ForeignKeyRule.SETDEFAULT,  null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.SETDEFAULT}, 
        {ForeignKeyRule.CASCADE, null, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE}
    };        
    
    private static final  ForeignKeyRule [][] fKeyDeleteRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL},
        {ForeignKeyRule.SETDEFAULT,  ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.NOACTION, ForeignKeyRule.SETDEFAULT}, 
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
}
