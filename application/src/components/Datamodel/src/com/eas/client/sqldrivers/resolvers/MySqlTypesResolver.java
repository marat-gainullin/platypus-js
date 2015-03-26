/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.eas.client.SQLUtils;
import java.sql.Types;
import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mg
 */
public class MySqlTypesResolver extends TypesResolver {

    protected static final Map<Integer, String> jdbcTypes2RdbmsTypes = new HashMap<>();
    protected static final Map<String, Integer> rdbmsTypes2JdbcTypes = new HashMap<>();
    protected static final Set<String> gisTypes = new HashSet<>();
    protected static final int MAXIMUM_NUMBERS_PRECISION = 65;
    protected static final Set<String> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<String> jdbcTypesWithScale = new HashSet<>();
    private static final Map<String, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesDefaultSize = new HashMap<>();

    static {

        // gis types
        gisTypes.add("point");
        gisTypes.add("linestring");
        gisTypes.add("polygon");
        gisTypes.add("geometry");
        gisTypes.add("multipoint");
        gisTypes.add("multilinestring");
        gisTypes.add("multipolygon");
        gisTypes.add("geometrycollection");

        // rdbms -> jdbc
        rdbmsTypes2JdbcTypes.put("tinyint", Types.TINYINT);
        rdbmsTypes2JdbcTypes.put("bool", Types.TINYINT);
        rdbmsTypes2JdbcTypes.put("boolean", Types.TINYINT);
        rdbmsTypes2JdbcTypes.put("smallint", Types.SMALLINT);
        rdbmsTypes2JdbcTypes.put("mediumint", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("int", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("integer", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("bigint", Types.BIGINT);
        rdbmsTypes2JdbcTypes.put("serial", Types.BIGINT);
        rdbmsTypes2JdbcTypes.put("bit", Types.BIT);
        rdbmsTypes2JdbcTypes.put("float", Types.REAL);
        rdbmsTypes2JdbcTypes.put("real", Types.DOUBLE);
        rdbmsTypes2JdbcTypes.put("double", Types.DOUBLE);
        rdbmsTypes2JdbcTypes.put("double precision", Types.DOUBLE);
        rdbmsTypes2JdbcTypes.put("decimal", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("dec", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("numeric", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("char", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("varchar", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("tinytext", Types.LONGVARCHAR);
        rdbmsTypes2JdbcTypes.put("long varchar", Types.LONGVARCHAR);
        rdbmsTypes2JdbcTypes.put("text", Types.LONGVARCHAR);
        rdbmsTypes2JdbcTypes.put("mediumtext", Types.LONGVARCHAR);
        rdbmsTypes2JdbcTypes.put("longtext", Types.LONGVARCHAR);
        rdbmsTypes2JdbcTypes.put("binary", Types.BINARY);
        rdbmsTypes2JdbcTypes.put("varbinary", Types.VARBINARY);
        rdbmsTypes2JdbcTypes.put("tinyblob", Types.VARBINARY);
        rdbmsTypes2JdbcTypes.put("blob", Types.LONGVARBINARY);
        rdbmsTypes2JdbcTypes.put("mediumblob", Types.LONGVARBINARY);
        rdbmsTypes2JdbcTypes.put("longblob", Types.LONGVARBINARY);
        rdbmsTypes2JdbcTypes.put("long varbinary", Types.LONGVARBINARY);
        rdbmsTypes2JdbcTypes.put("date", Types.DATE);
        rdbmsTypes2JdbcTypes.put("time", Types.TIME);
        rdbmsTypes2JdbcTypes.put("year", Types.DATE);
        rdbmsTypes2JdbcTypes.put("datetime", Types.TIMESTAMP);
        rdbmsTypes2JdbcTypes.put("timestamp", Types.TIMESTAMP);
        rdbmsTypes2JdbcTypes.put("point", Types.OTHER);
        rdbmsTypes2JdbcTypes.put("linestring", Types.OTHER);
        rdbmsTypes2JdbcTypes.put("polygon", Types.OTHER);
        rdbmsTypes2JdbcTypes.put("geometry", Types.OTHER);
        rdbmsTypes2JdbcTypes.put("multipoint", Types.OTHER);
        rdbmsTypes2JdbcTypes.put("multilinestring", Types.OTHER);
        rdbmsTypes2JdbcTypes.put("multipolygon", Types.OTHER);
        rdbmsTypes2JdbcTypes.put("geometrycollection", Types.OTHER);
        rdbmsTypes2JdbcTypes.put("enum", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("set", Types.CHAR);

        // jdbc -> rdbms
        jdbcTypes2RdbmsTypes.put(Types.BIT, "bit");
        jdbcTypes2RdbmsTypes.put(Types.TINYINT, "tinyint");
        jdbcTypes2RdbmsTypes.put(Types.SMALLINT, "smallint");
        jdbcTypes2RdbmsTypes.put(Types.INTEGER, "int");
        jdbcTypes2RdbmsTypes.put(Types.BIGINT, "bigint");
        jdbcTypes2RdbmsTypes.put(Types.FLOAT, "float");
        jdbcTypes2RdbmsTypes.put(Types.REAL, "float");
        jdbcTypes2RdbmsTypes.put(Types.DOUBLE, "double");
        jdbcTypes2RdbmsTypes.put(Types.NUMERIC, "decimal");
        jdbcTypes2RdbmsTypes.put(Types.DECIMAL, "decimal");
        jdbcTypes2RdbmsTypes.put(Types.CHAR, "char");
        jdbcTypes2RdbmsTypes.put(Types.VARCHAR, "varchar");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARCHAR, "longtext");
        jdbcTypes2RdbmsTypes.put(Types.DATE, "date");
        jdbcTypes2RdbmsTypes.put(Types.TIME, "time");
        jdbcTypes2RdbmsTypes.put(Types.TIMESTAMP, "timestamp");
        jdbcTypes2RdbmsTypes.put(Types.BINARY, "binary");
        jdbcTypes2RdbmsTypes.put(Types.VARBINARY, "varbinary");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARBINARY, "longblob");
        jdbcTypes2RdbmsTypes.put(Types.BLOB, "longblob");
        jdbcTypes2RdbmsTypes.put(Types.CLOB, "longtext");
        jdbcTypes2RdbmsTypes.put(Types.BOOLEAN, "int");
        jdbcTypes2RdbmsTypes.put(Types.NCHAR, "char");
        jdbcTypes2RdbmsTypes.put(Types.NVARCHAR, "varchar");
        jdbcTypes2RdbmsTypes.put(Types.LONGNVARCHAR, "longtext");
        jdbcTypes2RdbmsTypes.put(Types.NCLOB, "longtext");
        jdbcTypes2RdbmsTypes.put(Types.SQLXML, "longtext");

        //typeName(M,D)
        jdbcTypesWithScale.add("float");
        jdbcTypesWithScale.add("real");
        jdbcTypesWithScale.add("double");
        jdbcTypesWithScale.add("double precision");
        jdbcTypesWithScale.add("numeric");
        jdbcTypesWithScale.add("decimal");
        jdbcTypesWithScale.add("dec");

        //typeName(M)
        jdbcTypesWithSize.add("float");
        jdbcTypesWithSize.add("real");
        jdbcTypesWithSize.add("double");
        jdbcTypesWithSize.add("double precision");
        jdbcTypesWithSize.add("numeric");
        jdbcTypesWithSize.add("decimal");
        jdbcTypesWithSize.add("dec");
        jdbcTypesWithSize.add("char");
        jdbcTypesWithSize.add("varchar");
        jdbcTypesWithSize.add("binary");
        jdbcTypesWithSize.add("varbinary");

        // max sizes for types // ??? numeric ????
        jdbcTypesMaxSize.put("char", 255);
        jdbcTypesMaxSize.put("varchar", 65535);
        jdbcTypesMaxSize.put("tinytext", 255);
        jdbcTypesMaxSize.put("text", 65535);
        jdbcTypesMaxSize.put("mediumtext", 16777215);

        jdbcTypesMaxSize.put("longvarchar", 16777215);

        jdbcTypesMaxSize.put("longtext", 2147483647);
        jdbcTypesMaxSize.put("binary", 255);
        jdbcTypesMaxSize.put("varbinary", 255);

        jdbcTypesMaxSize.put("long varbinary", 16777215);
        jdbcTypesMaxSize.put("tinyblob", 255);
        jdbcTypesMaxSize.put("blob", 65535);
        jdbcTypesMaxSize.put("mediumblob", 16777215);
        jdbcTypesMaxSize.put("longblob", 2147483647);
//????? изменить Field.setSize(Long)        
//?????        jdbcTypesMaxSize.put(Types.VARCHAR,4294967295);
//?????        jdbcTypesMaxSize.put(Types.NVARCHAR,4294967295);
//?????        jdbcTypesMaxSize.put(Types.VARBINARY,4294967295);

        // default sizes for types ??????????????????????????????????????????????
        jdbcTypesDefaultSize.put("char", 1);
        jdbcTypesDefaultSize.put("varchar", 200);
        jdbcTypesDefaultSize.put("binary", 1);
        jdbcTypesDefaultSize.put("varbinary", 200);
    }

    @Override
    public void resolve2RDBMS(Field aField) {
        super.resolve2RDBMS(aField);
        if (SQLUtils.getTypeGroup(aField.getTypeInfo().getSqlType()) == SQLUtils.TypesGroup.NUMBERS && aField.getSize() > MAXIMUM_NUMBERS_PRECISION) {// MySql treats size as precision in error messages
            aField.setSize(MAXIMUM_NUMBERS_PRECISION);
        }
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
        return (aTypeName != null ? gisTypes.contains(aTypeName.toLowerCase()) : false);
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
