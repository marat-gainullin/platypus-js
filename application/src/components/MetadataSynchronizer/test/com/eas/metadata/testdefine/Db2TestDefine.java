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
public class Db2TestDefine extends DbTestDefine {
    
    private static final Map<String, int[]> fieldsSizes = new HashMap<>();
    private static final Map<String, int[]> fieldsScales = new HashMap<>();
    
    private static final  String[][] fieldsTypes = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {"SMALLINT", "", "", "", "SMALLINT", "", ""},
        {"INTEGER", "", "", "", "INTEGER", "", ""},
        {"INT", "", "", "", "INTEGER", "", ""},
        {"BIGINT", "", "", "", "BIGINT", "", ""},
        {"DECIMAL", "", "", "", "DECIMAL", "", ""},
        {"DEC", "", "", "", "DECIMAL", "", ""},
        {"NUMERIC", "", "", "", "DECIMAL", "", ""},
        {"NUM", "", "", "", "DECIMAL", "", ""},
        {"FLOAT", "", "", "", "REAL", "", ""},
        {"REAL", "", "", "", "REAL", "", ""},
        {"DOUBLE", "", "", "", "DOUBLE", "", ""},
        {"DOUBLE PRECISION", "", "", "", "DOUBLE", "", ""},
        {"CHAR", "", "", "", "CHAR", "", ""},
        {"CHARACTER", "", "", "", "CHAR", "", ""},
        {"VARCHAR", "", "", "", "VARCHAR", "", ""},
        {"CHAR VARYING", "", "", "", "VARCHAR", "", ""},
        {"CHARACTER VARYING", "", "", "", "VARCHAR", "", ""},
        {"CHAR () FOR BIT DATA", "", "", "", "CHAR () FOR BIT DATA", "", ""},
        {"CHARACTER () FOR BIT DATA", "", "", "", "CHAR () FOR BIT DATA", "", ""},
        {"CHAR VARYING () FOR BIT DATA", "", "", "", "VARCHAR () FOR BIT DATA", "", ""},
        {"VARCHAR () FOR BIT DATA", "", "", "", "VARCHAR () FOR BIT DATA", "", ""},
        {"CHARACTER VARYING () FOR BIT DATA", "", "", "", "VARCHAR () FOR BIT DATA", "", ""},
        {"LONG VARCHAR", "", "", "", "LONG VARCHAR", "", ""},
        {"LONG VARCHAR FOR BIT DATA", "", "", "", "LONG VARCHAR FOR BIT DATA", "", ""},
        {"CLOB", "", "", "", "CLOB", "", ""},
        {"CHAR LARGE OBJECT", "", "", "", "CLOB", "", ""},
        {"CHARACTER LARGE OBJECT", "", "", "", "CLOB", "", ""},
        {"GRAPHIC", "", "", "", "CHAR", "", ""},
        {"VARGRAPHIC", "", "", "", "VARCHAR", "", ""},
        {"LONG VARGRAPHIC", "", "", "", "LONG VARCHAR", "", ""},
        {"DBCLOB", "", "", "", "CLOB", "", ""},
        {"NCHAR", "", "", "", "CHAR", "", ""},
        {"NATIONAL CHAR", "", "", "", "CHAR", "", ""},
        {"NATIONAL CHARACTER", "", "", "", "CHAR", "", ""},
        {"NVARCHAR", "", "", "", "VARCHAR", "", ""},
        {"NATIONAL CHARACTER VARYING", "", "", "", "VARCHAR", "", ""},
        {"NATIONAL CHAR VARYING", "", "", "", "VARCHAR", "", ""},
        {"NCHAR VARYING", "", "", "", "VARCHAR", "", ""},
        {"NCLOB", "", "", "", "CLOB", "", ""},
        {"NCHAR LARGE OBJECT", "", "", "", "CLOB", "", ""},
        {"NATIONAL CHARACTER LARGE OBJECT", "", "", "", "CLOB", "", ""},
        {"BLOB", "", "", "", "BLOB", "", ""},
        {"BINARY LARGE OBJECT", "", "", "", "BLOB", "", ""},
        {"DATE", "", "", "", "DATE", "", ""},
        {"TIME", "", "", "", "TIME", "", ""},
        {"TIMESTAMP", "", "", "", "TIMESTAMP", "", ""},
        {"XML", "", "", "", "BLOB", "", ""},
        
        
//????        rdbmsTypes2JdbcTypes.put("DECFLOAT", Types.OTHER);//????? float !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        
    };
    
    static {            
//        //type, {original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsSizes.put("DECIMAL",  new int[]{ 4,  4,  4,  4,  4,  4,  4});
        fieldsSizes.put("DEC",  new int[]{ 5,  5,  5,  5,  5,  5,  5});
        fieldsSizes.put("NUMERIC",  new int[]{ 6,  6,  6,  6,  6,  6,  6});
        fieldsSizes.put("CHAR",     new int[]{ 7,  7,  7,  7,  7,  7,  7});
        fieldsSizes.put("CHARACTER",     new int[]{ 8,  8,  8,  8,  8,  8,  8});
        fieldsSizes.put("GRAPHIC",     new int[]{ 9, 9, 9, 9, 9, 9, 9});
        fieldsSizes.put("NCHAR",     new int[]{ 10, 10, 10, 10, 10, 10, 10});
        fieldsSizes.put("NATIONAL CHAR",     new int[]{ 11, 11, 11, 11, 11, 11, 11});
        fieldsSizes.put("NATIONAL CHARACTER",     new int[]{ 12, 12, 12, 12, 12, 12, 12});

        fieldsSizes.put("VARCHAR", new int[]{ 13, 13, 13, 13, 13, 13, 13});
        fieldsSizes.put("CHAR VARYING",     new int[]{ 14, 14, 14, 14, 14, 14, 14});
        fieldsSizes.put("CHARACTER VARYING",     new int[]{ 15, 15, 15, 15, 15, 15, 15});
        fieldsSizes.put("VARGRAPHIC",     new int[]{ 16, 16, 16, 16, 16, 16, 16});
        fieldsSizes.put("NVARCHAR",     new int[]{ 17, 17, 17, 17, 17, 17, 17});
        fieldsSizes.put("NCHAR VARYING",     new int[]{ 18, 18, 18, 18, 18, 18, 18});
        fieldsSizes.put("NATIONAL CHAR VARYING",     new int[]{ 19, 19, 19, 19, 19, 19, 19});
        fieldsSizes.put("NATIONAL CHARACTER VARYING",     new int[]{ 20, 20, 20, 20, 20, 20, 20});
        
        fieldsSizes.put("CHAR () FOR BIT DATA",     new int[]{ 21, 21, 21, 21, 21, 21, 21});
        fieldsSizes.put("CHARACTER () FOR BIT DATA",     new int[]{ 22, 22, 22, 22, 22, 22, 22});
        
        fieldsSizes.put("CHAR VARYING () FOR BIT DATA",     new int[]{ 23, 23, 23, 23, 23, 23, 23});
        fieldsSizes.put("VARCHAR () FOR BIT DATA",     new int[]{ 24, 24, 24, 24, 24, 24, 24});
        fieldsSizes.put("CHARACTER VARYING () FOR BIT DATA",     new int[]{ 25, 25, 25, 25, 25, 25, 25});

        fieldsSizes.put("CLOB",     new int[]{ 26, 26, 26, 26, 26, 26, 26});
        fieldsSizes.put("CHAR LARGE OBJECT",     new int[]{ 27, 27, 27, 27, 27, 27, 27});
        fieldsSizes.put("CHARACTER LARGE OBJECT",     new int[]{ 28, 28, 28, 28, 28, 28, 28});
        fieldsSizes.put("DBCLOB",     new int[]{ 29, 29, 29, 29, 29, 29, 29});
        fieldsSizes.put("NCLOB",     new int[]{ 30, 30, 30, 30, 30, 30, 30});
        fieldsSizes.put("NCHAR LARGE OBJECT",     new int[]{ 31, 31, 31, 31, 31, 31, 31});
        fieldsSizes.put("NATIONAL CHARACTER LARGE OBJECT",     new int[]{ 32, 32, 32, 32, 32, 32, 32});
        
        fieldsSizes.put("BLOB",     new int[]{ 33, 33, 33, 33, 33, 33, 33});
        fieldsSizes.put("BINARY LARGE OBJECT",     new int[]{ 34, 34, 34, 34, 34, 34, 34});
        fieldsSizes.put("XML",     new int[]{ 35, 35, 35, 35, 35, 35, 35});

//        //originalType, {originalValue, Oracle, PostgreSQL, MySql, DB2, H2, MsSql}    
        fieldsScales.put("DECIMAL",  new int[]{2, 2, 2, 2, 2, 2, 2});
        fieldsScales.put("DEC",   new int[]{3, 3, 3, 3, 3, 3, 3});
        fieldsScales.put("NUMERIC",   new int[]{4, 4, 4, 4, 4, 4, 4});
    }
    
    
    
    private static final  ForeignKeyRule [][] fKeyUpdateRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, null, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, null, ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.NOACTION,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL},
        {ForeignKeyRule.SETDEFAULT,  null, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.NOACTION, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETDEFAULT}, 
        {ForeignKeyRule.CASCADE, null, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE, ForeignKeyRule.NOACTION, ForeignKeyRule.CASCADE, ForeignKeyRule.CASCADE}
    };        
    
    private static final  ForeignKeyRule [][] fKeyDeleteRules = {
        // original, Oracle, PostgreSQL, MySql, DB2, H2, MsSql
        {ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION, ForeignKeyRule.NOACTION},
        {ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL, ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL,ForeignKeyRule.SETNULL},
        {ForeignKeyRule.SETDEFAULT,  ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.NOACTION, ForeignKeyRule.SETDEFAULT, ForeignKeyRule.SETDEFAULT}, 
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
