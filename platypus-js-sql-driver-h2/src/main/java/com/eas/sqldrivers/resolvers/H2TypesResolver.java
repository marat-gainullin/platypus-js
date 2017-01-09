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
 * @author vv
 */
public class H2TypesResolver implements TypesResolver {

    protected static final Map<String, String> rdbmsTypes2ApplicationTypes = new LinkedHashMap<>();
    protected static final Set<String> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<String> jdbcTypesWithScale = new HashSet<>();
    private static final Map<String, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesDefaultSize = new HashMap<>();

    static {
        rdbmsTypes2ApplicationTypes.put("VARCHAR", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("NUMERIC", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("DECIMAL", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("BOOLEAN", Scripts.BOOLEAN_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("TIMESTAMP", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("TINYINT", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("BIGINT", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("IDENTITY", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("INTEGER", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("SMALLINT", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("FLOAT", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("REAL", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("DOUBLE", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("LONGVARCHAR", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("CHAR", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("VARCHAR_IGNORECASE", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("DATE", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("TIME", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("CLOB", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("LONGVARBINARY", null);
        rdbmsTypes2ApplicationTypes.put("VARBINARY", null);
        rdbmsTypes2ApplicationTypes.put("BINARY", null);
        rdbmsTypes2ApplicationTypes.put("UUID", null);
        rdbmsTypes2ApplicationTypes.put("OTHER", null);
        rdbmsTypes2ApplicationTypes.put("ARRAY", null);
        rdbmsTypes2ApplicationTypes.put("BLOB", null);

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
    public String toApplicationType(int aJdbcType, String aRDBMSType) {
        return aRDBMSType != null ? rdbmsTypes2ApplicationTypes.get(aRDBMSType.toUpperCase()) : null;
    }
    
    @Override
    public Set<String> getSupportedTypes() {
        return Collections.unmodifiableSet(rdbmsTypes2ApplicationTypes.keySet());
    }

    @Override
    public boolean isSized(String aRDBMSType) {
        return jdbcTypesWithSize.contains(aRDBMSType.toUpperCase());
    }

    @Override
    public boolean isScaled(String aRDBMSType) {
        return jdbcTypesWithScale.contains(aRDBMSType.toUpperCase());
    }

    @Override
    public void resolveSize(JdbcField aField) {
        String sqlTypeName = aField.getType();
        if (sqlTypeName != null) {
            sqlTypeName = sqlTypeName.toUpperCase();
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
