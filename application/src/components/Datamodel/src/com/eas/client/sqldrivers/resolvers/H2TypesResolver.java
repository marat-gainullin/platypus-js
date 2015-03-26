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
 * @author vv
 */
public class H2TypesResolver extends TypesResolver {

    protected static final Map<Integer, String> jdbcTypes2RdbmsTypes = new HashMap<>();
    protected static final Map<String, Integer> rdbmsTypes2JdbcTypes = new HashMap<>();
    protected static final Set<String> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<String> jdbcTypesWithScale = new HashSet<>();
    private static final Map<String, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesDefaultSize = new HashMap<>();

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
        jdbcTypes2RdbmsTypes.put(Types.LONGVARCHAR, "LONGVARCHAR");//???VARCHAR"); //?CLOB
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
        jdbcTypesWithScale.add("NUMERIC");
        jdbcTypesWithScale.add("DECIMAL");

        //typeName(M)
        jdbcTypesWithSize.add("LONGVARBINARY");
        jdbcTypesWithSize.add("VARBINARY");
        jdbcTypesWithSize.add("BINARY");
        jdbcTypesWithSize.add("UUID");
        jdbcTypesWithSize.add("LONGVARCHAR");
        jdbcTypesWithSize.add("CHAR");
        jdbcTypesWithSize.add("VARCHAR");
        jdbcTypesWithSize.add("VARCHAR_IGNORECASE");
        jdbcTypesWithSize.add("OTHER");
        jdbcTypesWithSize.add("BLOB");
        jdbcTypesWithSize.add("CLOB");

        // max sizes for types
        jdbcTypesMaxSize.put("CHAR", 2147483647);
        jdbcTypesMaxSize.put("VARCHAR", 2147483647);
        jdbcTypesMaxSize.put("VARCHAR_IGNORECASE", 2147483647);
        jdbcTypesMaxSize.put("BINARY", 2147483647);
        jdbcTypesMaxSize.put("UUID", 2147483647);
        jdbcTypesMaxSize.put("VARBINARY", 2147483647);

        // default sizes for types ??????????????????????????????????????????????
        jdbcTypesDefaultSize.put("CHAR", 1);
        jdbcTypesDefaultSize.put("VARCHAR", 200);
        jdbcTypesDefaultSize.put("VARCHAR_IGNORECASE", 200);
        jdbcTypesDefaultSize.put("BINARY", 1);
        jdbcTypesDefaultSize.put("UUID", 1);
        jdbcTypesDefaultSize.put("VARBINARY", 200);

    }

    @Override
    public void resolve2Application(Field aField) {
        if (isGeometryTypeName(aField.getTypeInfo().getSqlTypeName())) {
            aField.setTypeInfo(DataTypeInfo.GEOMETRY.copy());
        } else {
            super.resolve2Application(aField);
        }
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
