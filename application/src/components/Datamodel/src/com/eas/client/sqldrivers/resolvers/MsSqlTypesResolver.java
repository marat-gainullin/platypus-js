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
public class MsSqlTypesResolver extends TypesResolver {

    protected static final Map<Integer, DataTypeInfo> jdbcToRDBMS = new HashMap<>();
    protected static final Map<Integer, String> jdbcTypes2RdbmsTypes = new HashMap<>();
    protected static final Map<String, Integer> rdbmsTypes2JdbcTypes = new HashMap<>();
    public static final int NON_JDBC_LONG_STRING = 258;
    public static final int NON_JDBC_MEDIUM_STRING = 259;
    public static final int NON_JDBC_MEMO_STRING = 260;
    public static final int NON_JDBC_SHORT_STRING = 261;
    protected static final Set<String> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<String> jdbcTypesWithScale = new HashSet<>();
    private static final Map<String, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesDefaultSize = new HashMap<>();

    static {
        // supported types at whole. This is inverse mapping, see EasMsSqlSqlDriver for straight mapping.
        jdbcTypes2RdbmsTypes.put(Types.BLOB, "image");
        jdbcTypes2RdbmsTypes.put(Types.CLOB, "text");
        jdbcTypes2RdbmsTypes.put(Types.SMALLINT, "smallint");
        jdbcTypes2RdbmsTypes.put(Types.TINYINT, "tinyint");
        jdbcTypes2RdbmsTypes.put(Types.BIGINT, "bigint");
        jdbcTypes2RdbmsTypes.put(Types.INTEGER, "int");
        jdbcTypes2RdbmsTypes.put(Types.REAL, "real");
        jdbcTypes2RdbmsTypes.put(Types.TIMESTAMP, "datetime");
        jdbcTypes2RdbmsTypes.put(Types.FLOAT, "float");
        jdbcTypes2RdbmsTypes.put(Types.OTHER, "sql_variant");
        jdbcTypes2RdbmsTypes.put(Types.BIT, "bit");
        jdbcTypes2RdbmsTypes.put(Types.DECIMAL, "decimal");
        jdbcTypes2RdbmsTypes.put(Types.NUMERIC, "numeric");
        jdbcTypes2RdbmsTypes.put(Types.VARBINARY, "varbinary");
        jdbcTypes2RdbmsTypes.put(Types.VARCHAR, "varchar");
        jdbcTypes2RdbmsTypes.put(Types.NVARCHAR, "nvarchar");
        jdbcTypes2RdbmsTypes.put(Types.NCHAR, "nchar");
        jdbcTypes2RdbmsTypes.put(NON_JDBC_LONG_STRING, "nvarchar");
        jdbcTypes2RdbmsTypes.put(NON_JDBC_MEDIUM_STRING, "nvarchar");
        jdbcTypes2RdbmsTypes.put(NON_JDBC_SHORT_STRING, "nvarchar");
        jdbcTypes2RdbmsTypes.put(NON_JDBC_MEMO_STRING, "text");
        jdbcTypes2RdbmsTypes.put(Types.BINARY, "binary");
        jdbcTypes2RdbmsTypes.put(Types.CHAR, "char");


        jdbcTypes2RdbmsTypes.put(Types.DOUBLE, "float");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARCHAR, "text");
        jdbcTypes2RdbmsTypes.put(Types.DATE, "datetime");
        jdbcTypes2RdbmsTypes.put(Types.TIME, "datetime");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARBINARY, "image");
        jdbcTypes2RdbmsTypes.put(Types.BOOLEAN, "int");
        jdbcTypes2RdbmsTypes.put(Types.LONGNVARCHAR, "text");
        jdbcTypes2RdbmsTypes.put(Types.NCLOB, "ntext");
        jdbcTypes2RdbmsTypes.put(Types.SQLXML, "text");

        // supported types at whole. see MsSqlTypesResolver for inverse mapping
        rdbmsTypes2JdbcTypes.put("image", Types.BLOB);
        rdbmsTypes2JdbcTypes.put("text", Types.CLOB);
        rdbmsTypes2JdbcTypes.put("ntext", Types.NCLOB);
        rdbmsTypes2JdbcTypes.put("uniqueidentifier", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("smallint", Types.SMALLINT);
        rdbmsTypes2JdbcTypes.put("tinyint", Types.TINYINT);
        rdbmsTypes2JdbcTypes.put("bigint", Types.BIGINT);
        rdbmsTypes2JdbcTypes.put("int", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("smalldatetime", Types.TIMESTAMP);
        rdbmsTypes2JdbcTypes.put("real", Types.REAL);
        rdbmsTypes2JdbcTypes.put("datetime", Types.TIMESTAMP);
        rdbmsTypes2JdbcTypes.put("float", Types.FLOAT);
        rdbmsTypes2JdbcTypes.put("money", Types.DECIMAL); //??? было other
        rdbmsTypes2JdbcTypes.put("smallmoney", Types.DECIMAL); //??? было other
        rdbmsTypes2JdbcTypes.put("sql_variant", Types.OTHER);
        rdbmsTypes2JdbcTypes.put("bit", Types.BIT);
        rdbmsTypes2JdbcTypes.put("decimal", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("numeric", Types.NUMERIC);
        rdbmsTypes2JdbcTypes.put("varbinary", Types.VARBINARY);
        rdbmsTypes2JdbcTypes.put("varchar", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("nvarchar", Types.NVARCHAR);

        rdbmsTypes2JdbcTypes.put("tinyint identity", Types.TINYINT);
        rdbmsTypes2JdbcTypes.put("bigint identity", Types.BIGINT);
        rdbmsTypes2JdbcTypes.put("binary", Types.BINARY);
//        rdbmsTypes2JdbcTypes.put("timestamp", Types.BINARY); //????????!!!!!!
        rdbmsTypes2JdbcTypes.put("char", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("nchar", Types.NCHAR);
        rdbmsTypes2JdbcTypes.put("numeric identity", Types.NUMERIC);
        rdbmsTypes2JdbcTypes.put("decimal identity", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("int identity", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("smallint identity", Types.SMALLINT);
        rdbmsTypes2JdbcTypes.put("sysname", Types.NVARCHAR);
        rdbmsTypes2JdbcTypes.put("xml", Types.CLOB);

        //typeName(M,D)
        //jdbcTypesWithScale.add(Types.DECIMAL);

        //typeName(M)
        jdbcTypesWithSize.add("char");
        jdbcTypesWithSize.add("varchar");
        jdbcTypesWithSize.add("nchar");
        jdbcTypesWithSize.add("nvarchar");
        jdbcTypesWithSize.add("binary");
        jdbcTypesWithSize.add("varbinary");


        // max sizes for types
        jdbcTypesMaxSize.put("char", 8000);
        jdbcTypesMaxSize.put("nchar", 4000);
        jdbcTypesMaxSize.put("varchar", 8000);
        jdbcTypesMaxSize.put("nvarchar", 4000);
        jdbcTypesMaxSize.put("binary", 8000);
        jdbcTypesMaxSize.put("varbinary", 8000);

        // default sizes for types ??????????????????????????????????????????????
        jdbcTypesDefaultSize.put("char", 1);
        jdbcTypesDefaultSize.put("nchar", 1);
        jdbcTypesDefaultSize.put("varchar", 200);
        jdbcTypesDefaultSize.put("nvarchar", 200);
        jdbcTypesDefaultSize.put("binary", 1);
        jdbcTypesDefaultSize.put("varbinary", 200);

    }

    @Override
    public void resolve2Application(Field aField) {
        if (aField.getTypeInfo().getSqlType() == NON_JDBC_LONG_STRING
                || aField.getTypeInfo().getSqlType() == NON_JDBC_MEDIUM_STRING
                || aField.getTypeInfo().getSqlType() == NON_JDBC_SHORT_STRING
                || aField.getTypeInfo().getSqlType() == NON_JDBC_MEMO_STRING) {
            aField.setTypeInfo(DataTypeInfo.NVARCHAR.copy());
        } else if (isGeometryTypeName(aField.getTypeInfo().getSqlTypeName())) {
            aField.setTypeInfo(DataTypeInfo.GEOMETRY.copy());
        } else {
            super.resolve2Application(aField);
        }
    }

    @Override
    public int getJdbcTypeByRDBMSTypename(String aTypeName) {
        Integer jdbcType = (aTypeName != null ? rdbmsTypes2JdbcTypes.get(aTypeName.toLowerCase()) : null);
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
    public boolean isGeometryTypeName(String aTypeName) {
        return false;
    }

    @Override
    public boolean isSized(String aSqlTypeName) {
        String sqlTypeName = aSqlTypeName.toLowerCase();
        return jdbcTypesWithSize.contains(sqlTypeName);
    }

    @Override
    public boolean isScaled(String aSqlTypeName) {
        String sqlTypeName = aSqlTypeName.toLowerCase();
        return jdbcTypesWithScale.contains(sqlTypeName);
    }

    @Override
    public Map<Integer, String> getJdbcTypes2RdbmsTypes() {
        return jdbcTypes2RdbmsTypes;
    }

    @Override
    public boolean containsRDBMSTypename(String aTypeName) {
        assert aTypeName != null;
        return rdbmsTypes2JdbcTypes.containsKey(aTypeName.toLowerCase());
    }

    @Override
    public void resolveFieldSize(Field aField) {
        DataTypeInfo typeInfo = aField.getTypeInfo();
        int sqlType = typeInfo.getSqlType();
        String sqlTypeName = typeInfo.getSqlTypeName();
        sqlTypeName = sqlTypeName.toLowerCase();
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