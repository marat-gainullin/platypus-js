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
public class PostgreTestDefine extends DbTestDefine {
    
    private static final Map<String, int[]> fieldsSizes = new HashMap<>();
    private static final Map<String, int[]> fieldsScales = new HashMap<>();
    
    private static final  String[][] fieldsTypes = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {"decimal", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""}, //??????????????????????driver????????

        {"bit", "NUMBER", "bit", "bit", "", "INTEGER", ""},
        {"bool", "NUMBER", "bool", "int", "", "INTEGER", ""},
        {"boolean", "NUMBER", "bool", "int", "", "INTEGER", ""},
        {"int8", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"bigint", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"bigserial", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"oid", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"bytea", "BLOB", "bytea", "longblob", "", "BLOB", ""},
        {"bpchar", "CHAR", "bpchar", "char", "", "CHAR", ""},
        {"char", "CHAR", "bpchar", "char", "", "CHAR", ""},
        {"character", "CHAR", "bpchar", "char", "", "CHAR", ""},
        {"numeric", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"integer", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"int", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"int4", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"serial", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"smallint", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"int2", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"real", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"float4", "NUMBER", "numeric", "decimal", "", "DECIMAL", ""},
        {"double precision", "FLOAT", "float8", "double", "", "DOUBLE", ""},
        {"float", "FLOAT", "float8", "double", "", "DOUBLE", ""},
        {"float8", "FLOAT", "float8", "double", "", "DOUBLE", ""},
        {"money", "FLOAT", "float8", "double", "", "DOUBLE", ""},
        {"varchar", "VARCHAR2", "varchar", "varchar", "", "VARCHAR", ""},
//!!!!!! Oracle-size default        
        {"character varying", "VARCHAR2", "varchar", "varchar", "", "VARCHAR", ""},
        {"name", "VARCHAR2", "varchar", "varchar", "", "VARCHAR", ""},
//!!!!!! Oracle-size default        
        
        {"text", "CLOB", "text", "longtext", "", "CLOB", ""},
        {"date", "DATE", "date", "date", "", "DATE", ""},
        {"time", "DATE", "date", "date", "", "DATE", ""},
        {"timetz", "DATE", "date", "date", "", "DATE", ""},
        {"time with time zone", "DATE", "date", "date", "", "DATE", ""},
        {"time without time zone", "DATE", "date", "date", "", "DATE", ""},
        {"timestamp", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""},
        {"timestamptz", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""},
        {"timestamp with time zone", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""},
        {"timestamp without time zone", "TIMESTAMP(6)", "timestamp", "timestamp", "", "TIMESTAMP", ""}
//GIS        {"", "", "", "", "", "", ""},
    };
    
    static {            //type, {original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsSizes.put("decimal",  new int[]{ 4,  4,  4,  4,  4,  4,  4});
        fieldsSizes.put("char",   new int[]{ 5,  5,  5,  5,  5,  5,  5});
        fieldsSizes.put("varchar",     new int[]{ 7,  7,  7,  7,  7,  7,  7});

//????????!!!!!!!!!!size oracle
        fieldsSizes.put("character varying",     new int[]{ 9, 9,  9,  9,  9,  9,  9});
        fieldsSizes.put("name",     new int[]{ 9, 9,  9,  9,  9,  9,  9});
        
        //originalType, {originalValue, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsScales.put("decimal",  new int[]{2, 2, 2, 2, 2, 2, 2});
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
