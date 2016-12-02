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
 * @author kl
 */
public class Db2TypesResolver implements TypesResolver {

    protected static final Map<String, String> rdbmsTypes2ApplicationTypes = new LinkedHashMap<>();
    protected static final Set<String> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<String> jdbcTypesWithScale = new HashSet<>();
    private static final Map<String, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesDefaultSize = new HashMap<>();

    static {
        rdbmsTypes2ApplicationTypes.put("VARCHAR", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("NUMERIC", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("DECIMAL", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("TIMESTAMP", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("INT", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("SMALLINT", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("INTEGER", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("BIGINT", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("DEC", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("NUM", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("FLOAT", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("REAL", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("DOUBLE", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("DOUBLE PRECISION", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("DECFLOAT", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("LONG VARCHAR", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("CHAR", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("CHARACTER", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("CHAR VARYING", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("CHARACTER VARYING", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("CLOB", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("CHAR LARGE OBJECT", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("CHARACTER LARGE OBJECT", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("DATE", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("TIME", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("XML", Scripts.STRING_TYPE_NAME); //?? OTHER  || SQLXML || BLOB
        rdbmsTypes2ApplicationTypes.put("BLOB", null);
        rdbmsTypes2ApplicationTypes.put("BINARY LARGE OBJECT", null);
        rdbmsTypes2ApplicationTypes.put("LONG VARCHAR FOR BIT DATA", null);
        
        jdbcTypesWithScale.add("DECIMAL");
        jdbcTypesWithScale.add("DEC");
        jdbcTypesWithScale.add("NUMERIC");
        jdbcTypesWithScale.add("NUM");

        jdbcTypesWithSize.add("DECIMAL");
        jdbcTypesWithSize.add("DEC");
        jdbcTypesWithSize.add("NUMERIC");
        jdbcTypesWithSize.add("NUM");
        jdbcTypesWithSize.add("CHAR");
        jdbcTypesWithSize.add("CHARACTER");
        jdbcTypesWithSize.add("VARCHAR");
        jdbcTypesWithSize.add("CHAR VARYING");
        jdbcTypesWithSize.add("CHARACTER VARYING");
        jdbcTypesWithSize.add("CLOB");
        jdbcTypesWithSize.add("CHAR LARGE OBJECT");
        jdbcTypesWithSize.add("CHARACTER LARGE OBJECT");
        jdbcTypesWithSize.add("BLOB");
        jdbcTypesWithSize.add("BINARY LARGE OBJECT");

        // max sizes for types
        jdbcTypesMaxSize.put("CHAR", 254);
        jdbcTypesMaxSize.put("CHARACTER", 254);
        jdbcTypesMaxSize.put("VARCHAR", 4000);
        jdbcTypesMaxSize.put("CHAR VARYING", 4000);
        jdbcTypesMaxSize.put("CHARACTER VARYING", 4000);

        // default sizes for types
        jdbcTypesDefaultSize.put("CHAR", 1);
        jdbcTypesDefaultSize.put("CHARACTER", 1);
        jdbcTypesDefaultSize.put("VARCHAR", 200);
        jdbcTypesDefaultSize.put("CHAR VARYING", 200);
        jdbcTypesDefaultSize.put("CHARACTER VARYING", 200);
        jdbcTypesDefaultSize.put("CLOB", 2147483647);
        jdbcTypesDefaultSize.put("CHAR LARGE OBJECT", 2147483647);
        jdbcTypesDefaultSize.put("CHARACTER LARGE OBJECT", 2147483647);
        jdbcTypesDefaultSize.put("BLOB", 2147483647);
        jdbcTypesDefaultSize.put("BINARY LARGE OBJECT", 2147483647);
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
