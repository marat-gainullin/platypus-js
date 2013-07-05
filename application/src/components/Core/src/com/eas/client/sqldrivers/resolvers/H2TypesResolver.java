/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author vv
 */
public class H2TypesResolver extends TypesResolver {

    protected static final Map<Integer, String> jdbcTypes2RdbmsTypes = new HashMap<>();
    protected static final Map<String, Integer> rdbmsTypes2JdbcTypes = new HashMap<>();
    protected static final Set<Integer> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<Integer> jdbcTypesWithScale = new HashSet<>();
    private static final Map<Integer, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<Integer, Integer> jdbcTypesDefaultSize = new HashMap<>();
    private static final List<Integer> characterTypesOrder = new ArrayList<>();
    private static final List<Integer> binaryTypesOrder = new ArrayList<>();

    static {
        // rdbms -> jdbc
        rdbmsTypes2JdbcTypes.put("TINYINT", Types.TINYINT);
        rdbmsTypes2JdbcTypes.put("BIGINT", Types.BIGINT);
        rdbmsTypes2JdbcTypes.put("IDENTITY", Types.BIGINT);
        rdbmsTypes2JdbcTypes.put("LONGVARBINARY", Types.LONGVARBINARY);
        rdbmsTypes2JdbcTypes.put("VARBINARY", Types.VARBINARY);
        rdbmsTypes2JdbcTypes.put("BINARY", Types.BINARY);
        rdbmsTypes2JdbcTypes.put("UUID", Types.BINARY);
        rdbmsTypes2JdbcTypes.put("LONGVARCHAR", Types.LONGVARCHAR);
        rdbmsTypes2JdbcTypes.put("CHAR", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("NUMERIC", Types.NUMERIC);
        rdbmsTypes2JdbcTypes.put("DECIMAL", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("INTEGER", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("SMALLINT", Types.SMALLINT);
        rdbmsTypes2JdbcTypes.put("FLOAT", Types.FLOAT);
        rdbmsTypes2JdbcTypes.put("REAL", Types.REAL);
        rdbmsTypes2JdbcTypes.put("DOUBLE", Types.DOUBLE);
        rdbmsTypes2JdbcTypes.put("VARCHAR", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("VARCHAR_IGNORECASE", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("BOOLEAN", Types.BOOLEAN);
        rdbmsTypes2JdbcTypes.put("DATE", Types.DATE);
        rdbmsTypes2JdbcTypes.put("TIME", Types.TIME);
        rdbmsTypes2JdbcTypes.put("TIMESTAMP", Types.TIMESTAMP);
        rdbmsTypes2JdbcTypes.put("OTHER", Types.OTHER);
        rdbmsTypes2JdbcTypes.put("ARRAY", Types.ARRAY);
        rdbmsTypes2JdbcTypes.put("BLOB", Types.BLOB);
        rdbmsTypes2JdbcTypes.put("CLOB", Types.CLOB);

        // jdbc -> rdbms
        jdbcTypes2RdbmsTypes.put(Types.BIT, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.TINYINT, "TINYINT");
        jdbcTypes2RdbmsTypes.put(Types.SMALLINT, "SMALLINT");
        jdbcTypes2RdbmsTypes.put(Types.INTEGER, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.BIGINT, "BIGINT");
        jdbcTypes2RdbmsTypes.put(Types.FLOAT, "FLOAT");
        jdbcTypes2RdbmsTypes.put(Types.REAL, "REAL");
        jdbcTypes2RdbmsTypes.put(Types.DOUBLE, "DOUBLE");
        jdbcTypes2RdbmsTypes.put(Types.NUMERIC, "NUMERIC");
        jdbcTypes2RdbmsTypes.put(Types.DECIMAL, "DECIMAL");
        jdbcTypes2RdbmsTypes.put(Types.CHAR, "CHAR");
        jdbcTypes2RdbmsTypes.put(Types.VARCHAR, "VARCHAR");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARCHAR, "CLOB"); //?
        jdbcTypes2RdbmsTypes.put(Types.DATE, "DATE");
        jdbcTypes2RdbmsTypes.put(Types.TIME, "TIME");
        jdbcTypes2RdbmsTypes.put(Types.TIMESTAMP, "TIMESTAMP");
        jdbcTypes2RdbmsTypes.put(Types.BINARY, "BINARY");
        jdbcTypes2RdbmsTypes.put(Types.VARBINARY, "VARBINARY");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARBINARY, "LONGVARBINARY");
        jdbcTypes2RdbmsTypes.put(Types.BLOB, "BLOB");
        jdbcTypes2RdbmsTypes.put(Types.CLOB, "CLOB");
        jdbcTypes2RdbmsTypes.put(Types.BOOLEAN, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.NCHAR, "CHAR");
        jdbcTypes2RdbmsTypes.put(Types.NVARCHAR, "VARCHAR");
        jdbcTypes2RdbmsTypes.put(Types.LONGNVARCHAR, "CLOB");
        jdbcTypes2RdbmsTypes.put(Types.NCLOB, "CLOB");
        jdbcTypes2RdbmsTypes.put(Types.SQLXML, "CLOB");

        //typeName(M,D)
        jdbcTypesWithScale.add(Types.NUMERIC);
        jdbcTypesWithScale.add(Types.DECIMAL);

        //typeName(M)
        jdbcTypesWithSize.add(Types.LONGVARBINARY);
        jdbcTypesWithSize.add(Types.VARBINARY);
        jdbcTypesWithSize.add(Types.BINARY);
        jdbcTypesWithSize.add(Types.LONGNVARCHAR);
        jdbcTypesWithSize.add(Types.CHAR);
        jdbcTypesWithSize.add(Types.VARCHAR);
//        jdbcTypesWithSize.add(Types.NCHAR);
//        jdbcTypesWithSize.add(Types.NVARCHAR);
        jdbcTypesWithSize.add(Types.OTHER);
        jdbcTypesWithSize.add(Types.BLOB);
        jdbcTypesWithSize.add(Types.CLOB);
        
        // max sizes for types
        jdbcTypesMaxSize.put(Types.CHAR,2147483647);
        jdbcTypesMaxSize.put(Types.VARCHAR,2147483647);
        jdbcTypesMaxSize.put(Types.BINARY,2147483647);
        jdbcTypesMaxSize.put(Types.VARBINARY,2147483647);
        
        // default sizes for types ??????????????????????????????????????????????
        jdbcTypesDefaultSize.put(Types.CHAR,1);
        jdbcTypesDefaultSize.put(Types.VARCHAR,200);
        jdbcTypesDefaultSize.put(Types.BINARY,1);
        jdbcTypesDefaultSize.put(Types.VARBINARY,200);

        // порядок замены символьных типов, если требуется размер больше исходного
        characterTypesOrder.add(Types.CHAR);
        characterTypesOrder.add(Types.VARCHAR);
        characterTypesOrder.add(Types.CLOB);
        
        binaryTypesOrder.add(Types.BINARY);
        binaryTypesOrder.add(Types.VARBINARY);
        binaryTypesOrder.add(Types.BLOB);
        
        
    }

    @Override
    public void resolve2Application(Field aField) {
        if (isGeometryTypeName(aField.getTypeInfo().getSqlTypeName())) {
            aField.setTypeInfo(DataTypeInfo.GEOMETRY.copy());
        }else
            super.resolve2Application(aField);
    }

    @Override
    public boolean isGeometryTypeName(String aTypeName) {
        return false;
    }

    @Override
    public int getJdbcTypeByRDBMSTypename(String aTypeName) {
        Integer jdbcType = (aTypeName != null ? rdbmsTypes2JdbcTypes.get(aTypeName.toUpperCase()) : null);
        if (jdbcType == null) {
            jdbcType = Types.OTHER;
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
    public boolean isSized(Integer aSqlType) {
        return jdbcTypesWithSize.contains(aSqlType);
    }

    @Override
    public boolean isScaled(Integer aSqlType) {
        return jdbcTypesWithScale.contains(aSqlType);
    }
    
    @Override
    public Map<Integer, String> getJdbcTypes2RdbmsTypes() {
        return jdbcTypes2RdbmsTypes;
    }

    @Override
    public Map<Integer, Integer> getJdbcTypesMaxSize() {
        return jdbcTypesMaxSize;
    }

    @Override
    public Map<Integer, Integer> getJdbcTypesDefaultSize() {
        return jdbcTypesDefaultSize;
    }

    @Override
    public List<Integer> getCharacterTypesOrder() {
        return characterTypesOrder;
    }
    
    @Override
    public  List<Integer> getBinaryTypesOrder() {
        return binaryTypesOrder;
    }

}
