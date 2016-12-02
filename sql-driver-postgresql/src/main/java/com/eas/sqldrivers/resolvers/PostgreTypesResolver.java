package com.eas.sqldrivers.resolvers;

import com.eas.client.metadata.JdbcField;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.script.Scripts;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mg
 */
public class PostgreTypesResolver implements TypesResolver {

    protected static final Map<String, String> rdbmsTypes2ApplicationTypes = new LinkedHashMap<>();
    protected static final Set<String> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<String> jdbcTypesWithScale = new HashSet<>();
    private static final Map<String, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesDefaultSize = new HashMap<>();

    static {
        rdbmsTypes2ApplicationTypes.put("character varying", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("decimal", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("boolean", Scripts.BOOLEAN_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("timestamp", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("geometry", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("int8", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("bigint", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("bigserial", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("oid", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("numeric", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("integer", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("int", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("int4", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("serial", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("smallint", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("int2", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("real", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("float4", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("double precision", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("float", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("float8", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("money", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("bool", Scripts.BOOLEAN_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("bit", Scripts.BOOLEAN_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("bpchar", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("char", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("character", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("varchar", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("name", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("text", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("date", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("time", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("timetz", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("time with time zone", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("time without time zone", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("timestamptz", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("timestamp with time zone", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("timestamp without time zone", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("bytea", null);
        // gis types
        rdbmsTypes2ApplicationTypes.put("geography", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("geometry", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("point", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("line", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("lseg", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("box", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("path", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("polygon", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("circle", Scripts.GEOMETRY_TYPE_NAME);

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
        jdbcTypesMaxSize.put("numeric", 1000);
        jdbcTypesMaxSize.put("decimal", 1000);

        // default sizes for types ??????????????????????????????????????????????
        jdbcTypesDefaultSize.put("bpchar", 1);
        jdbcTypesDefaultSize.put("char", 1);
        jdbcTypesDefaultSize.put("character", 1);
        jdbcTypesDefaultSize.put("varchar", 200);
        jdbcTypesDefaultSize.put("character varying", 200);
    }

    @Override
    public String toApplicationType(int aJdbcType, String aRDBMSType) {
        return aRDBMSType != null ? rdbmsTypes2ApplicationTypes.get(aRDBMSType.toLowerCase()) : null;
    }

    @Override
    public Set<String> getSupportedTypes() {
        return Collections.unmodifiableSet(rdbmsTypes2ApplicationTypes.keySet());
    }

    @Override
    public boolean isSized(String aRDBMSType) {
        return jdbcTypesWithSize.contains(aRDBMSType.toLowerCase());
    }

    @Override
    public boolean isScaled(String aRDBMSType) {
        return jdbcTypesWithScale.contains(aRDBMSType.toLowerCase());
    }

    @Override
    public void resolveSize(JdbcField aField) {
        String sqlTypeName = aField.getType();
        if (sqlTypeName != null) {
            sqlTypeName = sqlTypeName.toLowerCase();
            // check on max size
            int fieldSize = aField.getSize();
            Integer maxSize = jdbcTypesMaxSize.get(sqlTypeName);
            if (maxSize != null && maxSize < fieldSize) {
                aField.setSize(maxSize);
            }
            // check on default size
            if (fieldSize <= 0 && jdbcTypesDefaultSize.containsKey(sqlTypeName)) {
                aField.setSize(jdbcTypesDefaultSize.get(sqlTypeName));
            }
        }
    }
}
