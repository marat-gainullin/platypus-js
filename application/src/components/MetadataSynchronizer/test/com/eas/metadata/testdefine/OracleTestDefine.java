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
public class OracleTestDefine extends DbTestDefine {
    
    private static final Map<String, int[]> fieldsSizes = new HashMap<>();
    private static final Map<String, int[]> fieldsScales = new HashMap<>();
    
    private static final  String[][] fieldsTypes = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {"NUMBER", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"INTEGER", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"FLOAT", "FLOAT", "float8", "double", "", "DOUBLE", ""},
        {"REAL", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"VARCHAR2", "VARCHAR2", "varchar", "varchar", "","VARCHAR", ""},
        {"NVARCHAR2", "NVARCHAR2", "varchar", "varchar", "", "VARCHAR", ""},
        {"DATE", "DATE", "date", "date", "", "DATE", ""},
        {"TIMESTAMP", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""},
        {"TIMESTAMP(6)", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""},
        {"TIMESTAMP WITH TIME ZONE", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""},
        {"TIMESTAMP WITH LOCAL TIME ZONE", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""},
        {"TIMESTAMP(6) WITH TIME ZONE", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""},
        {"TIMESTAMP(6) WITH LOCAL TIME ZONE", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""},
        
//??? !!!!  Map in RowsetUtils        !!!!!!!!!!!!!!
        {"LONG RAW", "LONGVARBINARY", "bytea", "longblob", "", "VARBINARY", ""},
        {"RAW", "VARBINARY", "bytea", "varbinary", "", "VARBINARY", ""},
        
        {"LONG", "LONG", "text", "longtext", "", "CLOB", ""},
        {"CHAR", "CHAR", "bpchar", "char", "", "CHAR", ""},
        {"BLOB", "BLOB", "bytea", "longblob", "", "BLOB", ""},
        {"CLOB", "CLOB", "text", "longtext", "", "CLOB", ""},
        
//?????        
        {"NCLOB", "CLOB", "text", "longtext", "", "CLOB", ""},
        {"NVARCHAR2", "NVARCHAR2", "varchar", "varchar", "", "VARCHAR", ""},
        {"NCHAR", "NCHAR", "bpchar", "char", "", "CHAR", ""}
//         
//    //"MDSYS.SDO_GEOMETRY"
    };
    
    static {            
        //type, {original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsSizes.put("DECIMAL",  new int[]{ 4,  4,  4,  4,  4,  4,  4});
        fieldsSizes.put("NUMBER",   new int[]{ 5,  5,  5,  5,  5,  5,  5});
//        fieldsSizes.put("FLOAT",    new int[]{ 6,  6,  6,  6,  6,  6,  6});
        fieldsSizes.put("CHAR",     new int[]{ 7,  7,  7,  7,  7,  7,  7});
        fieldsSizes.put("VARCHAR2", new int[]{ 8,  8,  8,  8,  8,  8,  8});
        fieldsSizes.put("NCHAR",    new int[]{10, 10, 10, 10, 10, 10, 10});
        fieldsSizes.put("NVARCHAR2",new int[]{12, 12, 12, 12, 12, 12, 12});
//!!!!!!!!!!!!!!!!!!!! добавить в драйвер        
        fieldsSizes.put("RAW",      new int[]{11, 11, -1, 11, 11, 11, 11}); 
//??????        
//        fieldsSizes.put("LONG",new Integer[]{10, 10, 10, 10, 10, 10, 10});
        
        //originalType, {originalValue, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsScales.put("DECIMAL",  new int[]{2, 2, 2, 2, 2, 2, 2});
        fieldsScales.put("NUMBER",   new int[]{3, 3, 3, 3, 3, 3, 3});
    }
    
    
    
    private static final  ForeignKeyRule [][] fKeyUpdateRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, null,  ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETDEFAULT,  null,  ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.CASCADE, null,  ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
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
