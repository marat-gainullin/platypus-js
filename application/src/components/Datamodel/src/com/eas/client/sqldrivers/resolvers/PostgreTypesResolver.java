/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import com.eas.client.SQLUtils;
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
public class PostgreTypesResolver extends TypesResolver {

    protected static final Map<Integer, String> jdbcTypes2RdbmsTypes = new HashMap<>();
    protected static final Map<String, Integer> rdbmsTypes2JdbcTypes = new HashMap<>();
    protected static final Set<String> gisTypes = new HashSet<>();
    protected static final Set<String> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<String> jdbcTypesWithScale = new HashSet<>();
    private static final Map<String, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesDefaultSize = new HashMap<>();

    static {

        // gis types
        gisTypes.add("point");
        gisTypes.add("line");
        gisTypes.add("lseg");
        gisTypes.add("box");
        gisTypes.add("path");
        gisTypes.add("polygon");
        gisTypes.add("circle");
        gisTypes.add("geometry");


        // rdbms -> jdbc
        rdbmsTypes2JdbcTypes.put("decimal", Types.DECIMAL);
        //-7
        rdbmsTypes2JdbcTypes.put("bit", Types.BIT);
        //-7  ->  16 
        rdbmsTypes2JdbcTypes.put("bool", Types.BOOLEAN);
        rdbmsTypes2JdbcTypes.put("boolean", Types.BOOLEAN);
        //-5
        rdbmsTypes2JdbcTypes.put("int8", Types.BIGINT);
        rdbmsTypes2JdbcTypes.put("bigint", Types.BIGINT);
        rdbmsTypes2JdbcTypes.put("bigserial", Types.BIGINT);
        rdbmsTypes2JdbcTypes.put("oid", Types.BIGINT);
        //-2
        //???        rdbmsTypes2JdbcTypes.put("bytea", Types.BINARY);
        rdbmsTypes2JdbcTypes.put("bytea", Types.BLOB);   //???? LONGVARBINARY       TEXT-???????!!!!!!
        // 1
        rdbmsTypes2JdbcTypes.put("bpchar", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("char", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("character", Types.CHAR);
        // 2
        rdbmsTypes2JdbcTypes.put("numeric", Types.NUMERIC);
        // 4
        rdbmsTypes2JdbcTypes.put("integer", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("int", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("int4", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("serial", Types.INTEGER);
        // 5
        rdbmsTypes2JdbcTypes.put("smallint", Types.SMALLINT);
        rdbmsTypes2JdbcTypes.put("int2", Types.SMALLINT);
        // 7
        rdbmsTypes2JdbcTypes.put("real", Types.REAL);
        rdbmsTypes2JdbcTypes.put("float4", Types.REAL);
        // 8
        rdbmsTypes2JdbcTypes.put("double precision", Types.DOUBLE);
        rdbmsTypes2JdbcTypes.put("float", Types.DOUBLE);
        rdbmsTypes2JdbcTypes.put("float8", Types.DOUBLE);
        rdbmsTypes2JdbcTypes.put("money", Types.DOUBLE);
        // 12
        rdbmsTypes2JdbcTypes.put("varchar", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("character varying", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("name", Types.VARCHAR);
        // 12  ->  -1
//        rdbmsTypes2JdbcTypes.put("text", Types.LONGVARCHAR); //????????????!!!!!!!!
        rdbmsTypes2JdbcTypes.put("text", Types.CLOB);
        // 91
        rdbmsTypes2JdbcTypes.put("date", Types.DATE);
        // 92
        rdbmsTypes2JdbcTypes.put("time", Types.TIME);
        rdbmsTypes2JdbcTypes.put("timetz", Types.TIME);
        rdbmsTypes2JdbcTypes.put("time with time zone", Types.TIME);
        rdbmsTypes2JdbcTypes.put("time without time zone", Types.TIME);
        // 93
        rdbmsTypes2JdbcTypes.put("timestamp", Types.TIMESTAMP);
        rdbmsTypes2JdbcTypes.put("timestamptz", Types.TIMESTAMP);
        rdbmsTypes2JdbcTypes.put("timestamp with time zone", Types.TIMESTAMP);
        rdbmsTypes2JdbcTypes.put("timestamp without time zone", Types.TIMESTAMP);

        for (String typeName : gisTypes) {
            rdbmsTypes2JdbcTypes.put(typeName, Types.OTHER);
        }


        // jdbc -> rdbms
        jdbcTypes2RdbmsTypes.put(Types.BIT, "bit");
        jdbcTypes2RdbmsTypes.put(Types.TINYINT, "smallint");
        jdbcTypes2RdbmsTypes.put(Types.SMALLINT, "smallint");
        jdbcTypes2RdbmsTypes.put(Types.INTEGER, "integer");
        jdbcTypes2RdbmsTypes.put(Types.BIGINT, "bigint");
        jdbcTypes2RdbmsTypes.put(Types.FLOAT, "float");
        jdbcTypes2RdbmsTypes.put(Types.REAL, "real");
        jdbcTypes2RdbmsTypes.put(Types.DOUBLE, "double precision");
        jdbcTypes2RdbmsTypes.put(Types.NUMERIC, "numeric");
        jdbcTypes2RdbmsTypes.put(Types.DECIMAL, "decimal");
        jdbcTypes2RdbmsTypes.put(Types.CHAR, "char");
        jdbcTypes2RdbmsTypes.put(Types.VARCHAR, "varchar");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARCHAR, "text");
        jdbcTypes2RdbmsTypes.put(Types.DATE, "date");
        jdbcTypes2RdbmsTypes.put(Types.TIME, "time");
        jdbcTypes2RdbmsTypes.put(Types.TIMESTAMP, "timestamp");
        jdbcTypes2RdbmsTypes.put(Types.BINARY, "bytea");
        jdbcTypes2RdbmsTypes.put(Types.VARBINARY, "bytea");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARBINARY, "bytea");
        jdbcTypes2RdbmsTypes.put(Types.BLOB, "bytea");
        jdbcTypes2RdbmsTypes.put(Types.CLOB, "text");
        jdbcTypes2RdbmsTypes.put(Types.REF, "refcursor");
        jdbcTypes2RdbmsTypes.put(Types.BOOLEAN, "boolean");
        jdbcTypes2RdbmsTypes.put(Types.NCHAR, "char");
        jdbcTypes2RdbmsTypes.put(Types.NVARCHAR, "varchar");
        jdbcTypes2RdbmsTypes.put(Types.LONGNVARCHAR, "text");
        jdbcTypes2RdbmsTypes.put(Types.NCLOB, "text");

        //typeName(M,D)
        jdbcTypesWithScale.add("decimal");
        jdbcTypesWithScale.add("numeric");

        //typeName(M)
        jdbcTypesWithSize.add("decimal");
        jdbcTypesWithSize.add("numeric");
        jdbcTypesWithSize.add("bpchar");
        jdbcTypesWithSize.add("char");
        jdbcTypesWithSize.add("character");
        jdbcTypesWithSize.add("varchar");
        jdbcTypesWithSize.add("character varying");

        // max sizes for types
        jdbcTypesMaxSize.put("bpchar", 10485760);
        jdbcTypesMaxSize.put("char", 10485760);
        jdbcTypesMaxSize.put("character", 10485760);
        jdbcTypesMaxSize.put("varchar", 10485760);
        jdbcTypesMaxSize.put("character varying", 10485760);
        jdbcTypesMaxSize.put("name", 10485760); //????

        // default sizes for types ??????????????????????????????????????????????
        jdbcTypesDefaultSize.put("bpchar", 1);
        jdbcTypesDefaultSize.put("char", 1);
        jdbcTypesDefaultSize.put("character", 1);
        jdbcTypesDefaultSize.put("varchar", 200);
        jdbcTypesDefaultSize.put("character varying", 200);
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
    public void resolve2Application(Field aField) {
        if (aField != null) {
//            int lSize = aField.getSize();
//            int size = lSize >> 16;
//            int scale = (lSize << 16) >> 16;
            if (SQLUtils.getTypeGroup(aField.getTypeInfo().getSqlType()) == SQLUtils.TypesGroup.STRINGS) {
                super.resolve2Application(aField);
//                aField.setSize(Math.max(0, scale));
//                aField.setScale(0);
//                aField.setPrecision(0);
            } else if (isGeometryTypeName(aField.getTypeInfo().getSqlTypeName())) {
                aField.setTypeInfo(DataTypeInfo.GEOMETRY.copy());
            } else {
                super.resolve2Application(aField);
//                aField.setSize(Math.max(0, size));
//                if (scale > 0) {
//                    aField.setScale(scale);
//                    aField.setPrecision(scale);
//                } else {
//                    aField.setScale(0);
//                    aField.setPrecision(0);
//                }
            }
        }
    }

    @Override
    public boolean isGeometryTypeName(String aTypeName) {
        return (aTypeName != null ? gisTypes.contains(aTypeName.toLowerCase()) : false);
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
