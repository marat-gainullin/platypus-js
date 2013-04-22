/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.eas.client.SQLUtils;
import com.vividsolutions.jts.geom.Geometry;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class OracleTypesResolver implements TypesResolver {

    protected static final Map<Integer, String> jdbcTypes2RdbmsTypes = new HashMap<>();
    protected static final Map<String, Integer> rdbmsTypes2JdbcTypes = new HashMap<>();
    protected static final Set<String> gisTypes = new HashSet<>();
    protected static final Set<Integer> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<Integer> jdbcTypesWithScale = new HashSet<>();

    static {

        // gis types
        gisTypes.add("GEOMETRY");
        gisTypes.add("CURVE");
        gisTypes.add("POLYGON");
        gisTypes.add("LINESTRING");
        gisTypes.add("POINT");
        gisTypes.add("SURFACE");

        // rdbms -> jdbc
        rdbmsTypes2JdbcTypes.put("NUMBER", Types.NUMERIC);    // 2
        rdbmsTypes2JdbcTypes.put("INTEGER", Types.INTEGER);   // 4
        rdbmsTypes2JdbcTypes.put("FLOAT", Types.DOUBLE);      // 6
        rdbmsTypes2JdbcTypes.put("REAL", Types.REAL);         // 7
        rdbmsTypes2JdbcTypes.put("VARCHAR2", Types.VARCHAR);  // 12
        rdbmsTypes2JdbcTypes.put("NVARCHAR2", Types.NVARCHAR);
        // 93
        rdbmsTypes2JdbcTypes.put("DATE", Types.DATE);  // ??? 92 
        rdbmsTypes2JdbcTypes.put("TIMESTAMP", Types.TIMESTAMP);
        rdbmsTypes2JdbcTypes.put("TIMESTAMP(6)", Types.TIMESTAMP);
        rdbmsTypes2JdbcTypes.put("TIMESTAMP WITH TIME ZONE", Types.TIMESTAMP); // ??? -101 -> 93
        rdbmsTypes2JdbcTypes.put("TIMESTAMP WITH LOCAL TIME ZONE", Types.TIMESTAMP); // ??? -102 -> 93
        rdbmsTypes2JdbcTypes.put("TIMESTAMP(6) WITH TIME ZONE", Types.TIMESTAMP); // ??? -101 -> 93
        rdbmsTypes2JdbcTypes.put("TIMESTAMP(6) WITH LOCAL TIME ZONE", Types.TIMESTAMP); // ??? -102 -> 93

        rdbmsTypes2JdbcTypes.put("LONG RAW", Types.LONGVARBINARY);    // -4
        rdbmsTypes2JdbcTypes.put("RAW", Types.VARBINARY);             // -3
        rdbmsTypes2JdbcTypes.put("LONG", Types.LONGVARCHAR);          // -1
        rdbmsTypes2JdbcTypes.put("CHAR", Types.CHAR);                 // 1
        rdbmsTypes2JdbcTypes.put("BLOB", Types.BLOB);                 // 2004
        rdbmsTypes2JdbcTypes.put("CLOB", Types.CLOB);                 // 2005
        rdbmsTypes2JdbcTypes.put("NCLOB", Types.NCLOB);               // 2011
        rdbmsTypes2JdbcTypes.put("MDSYS.SDO_GEOMETRY", Types.STRUCT); // 2002 
        rdbmsTypes2JdbcTypes.put("NVARCHAR2", Types.NVARCHAR);        // -9
        rdbmsTypes2JdbcTypes.put("NCHAR", Types.NCHAR);               // -15


        // jdbc -> rdbms
        jdbcTypes2RdbmsTypes.put(Types.BIT, "NUMBER");
        jdbcTypes2RdbmsTypes.put(Types.TINYINT, "NUMBER");
        jdbcTypes2RdbmsTypes.put(Types.SMALLINT, "SMALLINT");
        jdbcTypes2RdbmsTypes.put(Types.INTEGER, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.BIGINT, "NUMBER");
        jdbcTypes2RdbmsTypes.put(Types.FLOAT, "FLOAT");
        jdbcTypes2RdbmsTypes.put(Types.REAL, "REAL");
        jdbcTypes2RdbmsTypes.put(Types.DOUBLE, "DOUBLE PRECISION");
        jdbcTypes2RdbmsTypes.put(Types.NUMERIC, "NUMBER");
        jdbcTypes2RdbmsTypes.put(Types.DECIMAL, "DECIMAL");
        jdbcTypes2RdbmsTypes.put(Types.CHAR, "CHAR");
        jdbcTypes2RdbmsTypes.put(Types.VARCHAR, "VARCHAR2");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARCHAR, "LONG");
        jdbcTypes2RdbmsTypes.put(Types.DATE, "DATE");
        jdbcTypes2RdbmsTypes.put(Types.TIME, "DATE"); //??????
        jdbcTypes2RdbmsTypes.put(Types.TIMESTAMP, "TIMESTAMP");
        jdbcTypes2RdbmsTypes.put(Types.BINARY, "RAW");
        jdbcTypes2RdbmsTypes.put(Types.VARBINARY, "RAW");  //???? BLOB
        jdbcTypes2RdbmsTypes.put(Types.LONGVARBINARY, "LONG RAW");  //??? BLOB
//        jdbcTypes2RdbmsTypes.put(Types.VARBINARY, "BLOB");  //???? BLOB
//        jdbcTypes2RdbmsTypes.put(Types.LONGVARBINARY, "BLOB");  //??? BLOB
        jdbcTypes2RdbmsTypes.put(Types.BLOB, "BLOB");
        jdbcTypes2RdbmsTypes.put(Types.CLOB, "CLOB");
        jdbcTypes2RdbmsTypes.put(Types.BOOLEAN, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.ROWID, "ROWID");
        jdbcTypes2RdbmsTypes.put(Types.NCHAR, "NCHAR");
        jdbcTypes2RdbmsTypes.put(Types.NVARCHAR, "NVARCHAR2");
        jdbcTypes2RdbmsTypes.put(Types.LONGNVARCHAR, "CLOB");
        jdbcTypes2RdbmsTypes.put(Types.NCLOB, "NCLOB");

        //typeName(M,D)
        jdbcTypesWithScale.add(Types.DECIMAL);
        jdbcTypesWithScale.add(Types.NUMERIC);
        
        //typeName(M)
        jdbcTypesWithSize.add(Types.FLOAT); //???????!!!!!!!!!!!!
        jdbcTypesWithSize.add(Types.CHAR);
        jdbcTypesWithSize.add(Types.VARCHAR);
        jdbcTypesWithSize.add(Types.NCHAR);
        jdbcTypesWithSize.add(Types.NVARCHAR);
        jdbcTypesWithSize.add(Types.NUMERIC);
        jdbcTypesWithSize.add(Types.DECIMAL);
        jdbcTypesWithSize.add(Types.VARBINARY);
        
        
    }

    @Override
    public void resolve2RDBMS(Field aField) {
        DataTypeInfo typeInfo = aField.getTypeInfo();
        if (typeInfo == null) {
            typeInfo = DataTypeInfo.VARCHAR;
            Logger.getLogger(OracleTypesResolver.class.getName()).log(Level.SEVERE, "sql jdbc type {0} have no mapping to rdbms type. substituting with string type (Varchar)", new Object[]{aField.getTypeInfo().getSqlType()});
        }
        DataTypeInfo copyTypeInfo = typeInfo.copy();
        String sqlTypeName = jdbcTypes2RdbmsTypes.get(typeInfo.getSqlType());
        if (sqlTypeName != null) {
            copyTypeInfo.setSqlType(getJdbcTypeByRDBMSTypename(sqlTypeName));
            copyTypeInfo.setSqlTypeName(sqlTypeName.toUpperCase());
            copyTypeInfo.setJavaClassName(typeInfo.getJavaClassName());
        }
        aField.setTypeInfo(copyTypeInfo);

    }

    @Override
    public void resolve2Application(Field aField) {
        if (aField != null) {
            if (aField.getTypeInfo().getSqlTypeName().contains("SDO_GEOMETRY")) {
                aField.getTypeInfo().setSqlType(java.sql.Types.STRUCT);
                aField.getTypeInfo().setSqlTypeName("MDSYS.SDO_GEOMETRY");
                aField.getTypeInfo().setJavaClassName(Geometry.class.getName());
            }
            if (SQLUtils.isSameTypeGroup(aField.getTypeInfo().getSqlType(), java.sql.Types.NUMERIC)) {
                aField.getTypeInfo().setJavaClassName(BigDecimal.class.getName());
            }
            if (SQLUtils.isSameTypeGroup(aField.getTypeInfo().getSqlType(), java.sql.Types.VARCHAR)) {
                aField.getTypeInfo().setJavaClassName(String.class.getName());
            }
            if (SQLUtils.isSameTypeGroup(aField.getTypeInfo().getSqlType(), java.sql.Types.BOOLEAN)) {
                aField.getTypeInfo().setJavaClassName(Boolean.class.getName());
            }
            if (SQLUtils.isSameTypeGroup(aField.getTypeInfo().getSqlType(), java.sql.Types.DATE)) {
                aField.getTypeInfo().setJavaClassName(java.util.Date.class.getName());
            }
            if (SQLUtils.isSameTypeGroup(aField.getTypeInfo().getSqlType(), java.sql.Types.BLOB)) {
                if (aField.getTypeInfo().getSqlType() == java.sql.Types.CLOB || aField.getTypeInfo().getSqlType() == java.sql.Types.NCLOB) {
                    aField.getTypeInfo().setSqlType(java.sql.Types.CLOB);
                    aField.getTypeInfo().setJavaClassName(CompactClob.class.getName());
                } else {
                    if (aField.getTypeInfo().getSqlType() == java.sql.Types.BINARY
                            || aField.getTypeInfo().getSqlType() == java.sql.Types.VARBINARY) {
                        aField.getTypeInfo().setSqlType(java.sql.Types.VARBINARY);
                        aField.getTypeInfo().setJavaClassName(CompactBlob.class.getName());
                    } else if (aField.getTypeInfo().getSqlType() == java.sql.Types.LONGVARBINARY) {
                        aField.getTypeInfo().setSqlType(java.sql.Types.LONGVARBINARY);
                        aField.getTypeInfo().setJavaClassName(CompactBlob.class.getName());
                    } else {
                        aField.getTypeInfo().setSqlType(java.sql.Types.BLOB);
                        aField.getTypeInfo().setJavaClassName(CompactBlob.class.getName());
                    }
                }
            }
            if (aField.getTypeInfo().getSqlType() == java.sql.Types.OTHER) {
                String lTypeName = aField.getTypeInfo().getSqlTypeName();
                if (lTypeName != null && !lTypeName.isEmpty()) {
                    switch (lTypeName) {
                        case "NVARCHAR2":
                            aField.getTypeInfo().setSqlType(java.sql.Types.NVARCHAR);
                            aField.getTypeInfo().setJavaClassName(String.class.getName());
                            break;
                        case "VARCHAR2":
                            aField.getTypeInfo().setSqlType(java.sql.Types.VARCHAR);
                            aField.getTypeInfo().setJavaClassName(String.class.getName());
                            break;
                    }
                }
            }
        }
    }

    @Override
    public boolean isGeometryTypeName(String aTypeName) {
        String sqlTypeName = (aTypeName != null?aTypeName.toUpperCase():null);
        for (String gisTypeName : gisTypes) {
            if (sqlTypeName.endsWith(gisTypeName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getJdbcTypeByRDBMSTypename(String aTypeName) {
        String sqlTypeName = (aTypeName != null?aTypeName.toUpperCase():null);
        Integer jdbcType = rdbmsTypes2JdbcTypes.get(sqlTypeName);
        if (jdbcType == null) {
            jdbcType = Types.OTHER;
           
            if (isGeometryTypeName(sqlTypeName)) {
                jdbcType = Types.STRUCT;
            }
        }
        return jdbcType;
    }

    @Override
    public Set<Integer> getSupportedJdbcDataTypes() {
        Set<Integer> supportedTypes = new HashSet<>();
        supportedTypes.addAll(rdbmsTypes2JdbcTypes.values());
        return supportedTypes;
    }
    
    @Override
    public boolean isSized(Integer aSqlType)   
    {
        return jdbcTypesWithSize.contains(aSqlType);
    }        

    @Override
    public boolean isScaled(Integer aSqlType)   
    {
        return jdbcTypesWithScale.contains(aSqlType);
    }        
    
}
