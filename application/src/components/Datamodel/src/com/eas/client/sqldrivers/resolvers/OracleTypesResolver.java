/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mg
 */
public class OracleTypesResolver extends TypesResolver {

    private static final Map<Integer, String> jdbcTypes2RdbmsTypes = new HashMap<>();
    protected static final Map<String, Integer> rdbmsTypes2JdbcTypes = new HashMap<>();
    protected static final Set<String> gisTypes = new HashSet<>();
    protected static final Set<String> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<String> jdbcTypesWithScale = new HashSet<>();
    private static final Map<String, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesDefaultSize = new HashMap<>();

    static {

        // gis types
        gisTypes.add("GEOMETRY");
        gisTypes.add("CURVE");
        gisTypes.add("POLYGON");
        gisTypes.add("LINESTRING");
        gisTypes.add("POINT");
        gisTypes.add("SURFACE");

        // rdbms -> jdbc
        rdbmsTypes2JdbcTypes.put("DECIMAL", Types.DECIMAL);    //  ????????????!!!!!!!
        rdbmsTypes2JdbcTypes.put("NUMBER", Types.NUMERIC);    // 2
        rdbmsTypes2JdbcTypes.put("INTEGER", Types.INTEGER);   // 4
        rdbmsTypes2JdbcTypes.put("FLOAT", Types.DOUBLE);      // 6
        rdbmsTypes2JdbcTypes.put("REAL", Types.REAL);         // 7
        rdbmsTypes2JdbcTypes.put("VARCHAR2", Types.VARCHAR);  // 12
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
//        jdbcTypes2RdbmsTypes.put(Types.LONGVARCHAR, "LONG"); // deprecate
        jdbcTypes2RdbmsTypes.put(Types.LONGVARCHAR, "CLOB");
        jdbcTypes2RdbmsTypes.put(Types.DATE, "DATE");
        jdbcTypes2RdbmsTypes.put(Types.TIME, "DATE"); //??????
        jdbcTypes2RdbmsTypes.put(Types.TIMESTAMP, "TIMESTAMP");
        jdbcTypes2RdbmsTypes.put(Types.BINARY, "RAW");
        jdbcTypes2RdbmsTypes.put(Types.VARBINARY, "RAW");  //???? BLOB
        jdbcTypes2RdbmsTypes.put(Types.LONGVARBINARY, "BLOB");
//        jdbcTypes2RdbmsTypes.put(Types.LONGVARBINARY, "LONG RAW");  //deprecate
        jdbcTypes2RdbmsTypes.put(Types.BLOB, "BLOB");
        jdbcTypes2RdbmsTypes.put(Types.CLOB, "CLOB");
        jdbcTypes2RdbmsTypes.put(Types.BOOLEAN, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.ROWID, "ROWID");
        jdbcTypes2RdbmsTypes.put(Types.NCHAR, "NCHAR");
        jdbcTypes2RdbmsTypes.put(Types.NVARCHAR, "NVARCHAR2");
        jdbcTypes2RdbmsTypes.put(Types.LONGNVARCHAR, "CLOB");
        jdbcTypes2RdbmsTypes.put(Types.NCLOB, "NCLOB");

        //typeName(M,D)
        jdbcTypesWithScale.add("DECIMAL"); //?????
        jdbcTypesWithScale.add("NUMBER");

        //typeName(M)
        jdbcTypesWithSize.add("FLOAT"); //???????!!!!!!!!!!!!
        jdbcTypesWithSize.add("CHAR");
        jdbcTypesWithSize.add("VARCHAR2");
        jdbcTypesWithSize.add("NCHAR");
        jdbcTypesWithSize.add("NVARCHAR2");
        jdbcTypesWithSize.add("NUMBER");
        jdbcTypesWithSize.add("DECIMAL");//????
        jdbcTypesWithSize.add("RAW");

        // max sizes for types
        jdbcTypesMaxSize.put("CHAR", 255);
        jdbcTypesMaxSize.put("VARCHAR2", 4000);
        jdbcTypesMaxSize.put("NCHAR", 255);
        jdbcTypesMaxSize.put("NVARCHAR2", 4000);
        jdbcTypesMaxSize.put("NUMBER", 38);
        jdbcTypesMaxSize.put("DECIMAL", 38);
        jdbcTypesMaxSize.put("RAW", 2000);

        // default sizes for types ??????????????????????????????????????????????
        jdbcTypesDefaultSize.put("CHAR", 1);
        jdbcTypesDefaultSize.put("VARCHAR2", 200);
        jdbcTypesDefaultSize.put("NCHAR2", 1);
        jdbcTypesDefaultSize.put("NVARCHAR2", 200);
        jdbcTypesDefaultSize.put("RAW", 1);
        jdbcTypesDefaultSize.put("NUMBER", 38);
    }

    @Override
    public void resolve2Application(Field aField) {
        if (aField != null) {
            if (isGeometryTypeName(aField.getTypeInfo().getSqlTypeName())
                    || aField.getTypeInfo().getSqlTypeName().contains("SDO_GEOMETRY")) {
                aField.setTypeInfo(DataTypeInfo.GEOMETRY.copy());
            } else if (aField.getTypeInfo().getSqlType() == java.sql.Types.OTHER) {
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
            } else {
                super.resolve2Application(aField);
            }
        }
    }

    @Override
    public boolean isGeometryTypeName(String aTypeName) {
        if (aTypeName != null) {
            String sqlTypeName = aTypeName.toUpperCase();
            for (String gisTypeName : gisTypes) {
                if (sqlTypeName.endsWith(gisTypeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getJdbcTypeByRDBMSTypename(String aTypeName) {
        String sqlTypeName = (aTypeName != null ? aTypeName.toUpperCase() : null);
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
    public boolean isSized(String aSqlTypeName) {
        String sqlTypeName = aSqlTypeName.toUpperCase();
        return jdbcTypesWithSize.contains(sqlTypeName);
    }

    @Override
    public boolean isScaled(String aSqlTypeName) {
        String sqlTypeName = aSqlTypeName.toUpperCase();
        return jdbcTypesWithScale.contains(sqlTypeName);
    }

    @Override
    public Map<Integer, String> getJdbcTypes2RdbmsTypes() {
        return jdbcTypes2RdbmsTypes;
    }

    @Override
    public boolean containsRDBMSTypename(String aTypeName) {
        assert aTypeName != null;
        return rdbmsTypes2JdbcTypes.containsKey(aTypeName.toUpperCase());
    }

    @Override
    public void resolveFieldSize(Field aField) {
        DataTypeInfo typeInfo = aField.getTypeInfo();
        int sqlType = typeInfo.getSqlType();
        String sqlTypeName = typeInfo.getSqlTypeName();
        sqlTypeName = sqlTypeName.toUpperCase();
        // check on max size
        int fieldSize = aField.getSize();
        Integer maxSize = jdbcTypesMaxSize.get(sqlTypeName);
        if (maxSize != null && maxSize < fieldSize) {
            List<Integer> typesOrder = getTypesOrder(sqlType);
            if (typesOrder != null) {
                for (int i = typesOrder.indexOf(sqlType); i < typesOrder.size(); i++) {
                    sqlType = typesOrder.get(i);
                    sqlTypeName = jdbcTypes2RdbmsTypes.get(sqlType);
                    maxSize = jdbcTypesMaxSize.get(sqlTypeName);
                    if (maxSize != null && maxSize >= fieldSize) {
                        break;
                    }
                }
            }
            if (maxSize != null && maxSize < fieldSize) {
                aField.setSize(maxSize);
            }
        }
        aField.setTypeInfo(new DataTypeInfo(sqlType, sqlTypeName, typeInfo.getJavaClassName()));
        // check on default size
        if (fieldSize <= 0 && jdbcTypesDefaultSize.containsKey(sqlTypeName)) {
            aField.setSize(jdbcTypesDefaultSize.get(sqlTypeName));
        }
    }
}
