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
public class MsSqlTypesResolver implements TypesResolver {

    protected static final Map<String, String> rdbmsTypes2ApplicationTypes = new LinkedHashMap<>();
    protected static final Set<String> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<String> jdbcTypesWithScale = new HashSet<>();
    private static final Map<String, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesDefaultSize = new HashMap<>();

    static {
        rdbmsTypes2ApplicationTypes.put("varchar", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("numeric", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("decimal", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("money", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("bit", Scripts.BOOLEAN_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("datetime", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("int", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("smallint", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("tinyint", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("bigint", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("real", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("float", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("smallmoney", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("tinyint identity", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("bigint identity", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("numeric identity", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("decimal identity", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("int identity", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("smallint identity", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("nvarchar", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("char", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("nchar", Scripts.STRING_TYPE_NAME);

        rdbmsTypes2ApplicationTypes.put("smalldatetime", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("datetime2", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("date", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("time", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("text", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("ntext", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("uniqueidentifier", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("sysname", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("xml", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("image", null);
        rdbmsTypes2ApplicationTypes.put("sql_variant", null);
        rdbmsTypes2ApplicationTypes.put("varbinary", null);
        rdbmsTypes2ApplicationTypes.put("binary", null);

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