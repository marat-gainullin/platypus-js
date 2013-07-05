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
 * @author mg
 */
public class MsSqlTypesResolver extends TypesResolver {

    protected static final Map<Integer, DataTypeInfo> jdbcToRDBMS = new HashMap<>();
    protected static final Map<Integer, String> jdbcTypes2RdbmsTypes = new HashMap<>();
    protected static final Map<String, Integer> rdbmsTypes2JdbcTypes = new HashMap<>();
    protected static final Set<Integer> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<Integer> jdbcTypesWithScale = new HashSet<>();
    public static final int NON_JDBC_LONG_STRING = 258;
    public static final int NON_JDBC_MEDIUM_STRING = 259;
    public static final int NON_JDBC_MEMO_STRING = 260;
    public static final int NON_JDBC_SHORT_STRING = 261;
    private static final Map<Integer, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<Integer, Integer> jdbcTypesDefaultSize = new HashMap<>();
    private static final List<Integer> characterTypesOrder = new ArrayList<>();
    private static final List<Integer> binaryTypesOrder = new ArrayList<>();

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
        jdbcTypes2RdbmsTypes.put(Types.NCLOB, "text");
        jdbcTypes2RdbmsTypes.put(Types.SQLXML, "text");

        // supported types at whole. see MsSqlTypesResolver for inverse mapping
        rdbmsTypes2JdbcTypes.put("image", Types.BLOB);
        rdbmsTypes2JdbcTypes.put("text", Types.CLOB);
        rdbmsTypes2JdbcTypes.put("ntext", Types.CLOB);
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
        rdbmsTypes2JdbcTypes.put("timestamp", Types.BINARY); //????????!!!!!!
        rdbmsTypes2JdbcTypes.put("char", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("nchar", Types.NCHAR);
        rdbmsTypes2JdbcTypes.put("numeric() identity", Types.NUMERIC);
        rdbmsTypes2JdbcTypes.put("decimal() identity", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("int identity", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("smallint identity", Types.SMALLINT);
        rdbmsTypes2JdbcTypes.put("sysname", Types.NVARCHAR);
        rdbmsTypes2JdbcTypes.put("xml", Types.CLOB);

        //typeName(M,D)
        //jdbcTypesWithScale.add(Types.DECIMAL);

        //typeName(M)
        jdbcTypesWithSize.add(Types.CHAR);
        jdbcTypesWithSize.add(Types.VARCHAR);
        jdbcTypesWithSize.add(Types.NCHAR);
        jdbcTypesWithSize.add(Types.NVARCHAR);
        jdbcTypesWithSize.add(Types.BINARY);
        jdbcTypesWithSize.add(Types.VARBINARY);


        // max sizes for types
        jdbcTypesMaxSize.put(Types.CHAR, 8000);
        jdbcTypesMaxSize.put(Types.NCHAR, 4000);
        jdbcTypesMaxSize.put(Types.VARCHAR, 8000);
        jdbcTypesMaxSize.put(Types.NVARCHAR, 4000);
        jdbcTypesMaxSize.put(Types.BINARY, 8000);
        jdbcTypesMaxSize.put(Types.VARBINARY, 8000);

        // default sizes for types ??????????????????????????????????????????????
        jdbcTypesDefaultSize.put(Types.CHAR, 1);
        jdbcTypesDefaultSize.put(Types.NCHAR, 1);
        jdbcTypesDefaultSize.put(Types.VARCHAR, 200);
        jdbcTypesDefaultSize.put(Types.NVARCHAR, 200);
        jdbcTypesDefaultSize.put(Types.BINARY, 1);
        jdbcTypesDefaultSize.put(Types.VARBINARY, 200);

        // порядок замены символьных типов, если требуется размер больше исходного
        characterTypesOrder.add(Types.CHAR);
        characterTypesOrder.add(Types.NCHAR);
        characterTypesOrder.add(Types.VARCHAR);
        characterTypesOrder.add(Types.NVARCHAR);
        characterTypesOrder.add(Types.CLOB);

        binaryTypesOrder.add(Types.BINARY);
        binaryTypesOrder.add(Types.VARBINARY);
        binaryTypesOrder.add(Types.BLOB);
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
    public List<Integer> getBinaryTypesOrder() {
        return binaryTypesOrder;
    }
}