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
public class MySqlTypesResolver implements TypesResolver {

    protected static final Map<String, String> rdbmsTypes2ApplicationTypes = new LinkedHashMap<>();
    protected static final int MAXIMUM_NUMBERS_PRECISION = 65;
    protected static final Set<String> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<String> jdbcTypesWithScale = new HashSet<>();
    private static final Map<String, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesDefaultSize = new HashMap<>();

    static {
        rdbmsTypes2ApplicationTypes.put("varchar", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("int", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("decimal", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("boolean", Scripts.BOOLEAN_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("timestamp", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("datetime", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("geometry", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("tinyint", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("smallint", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("mediumint", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("integer", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("bigint", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("serial", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("float", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("real", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("double", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("double precision", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("dec", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("numeric", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("bool", Scripts.BOOLEAN_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("bit", Scripts.BOOLEAN_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("char", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("tinytext", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("long varchar", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("text", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("mediumtext", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("longtext", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("date", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("time", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("year", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("enum", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("set", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("binary", null);
        rdbmsTypes2ApplicationTypes.put("varbinary", null);
        rdbmsTypes2ApplicationTypes.put("tinyblob", null);
        rdbmsTypes2ApplicationTypes.put("blob", null);
        rdbmsTypes2ApplicationTypes.put("mediumblob", null);
        rdbmsTypes2ApplicationTypes.put("longblob", null);
        rdbmsTypes2ApplicationTypes.put("long varbinary", null);
        // gis types
        rdbmsTypes2ApplicationTypes.put("point", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("linestring", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("polygon", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("multipoint", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("multilinestring", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("multipolygon", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("geometrycollection", Scripts.GEOMETRY_TYPE_NAME);

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
