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
public class OracleTypesResolver implements TypesResolver {

    protected static final Map<String, String> rdbmsTypes2ApplicationTypes = new LinkedHashMap<>();
    protected static final Set<String> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<String> jdbcTypesWithScale = new HashSet<>();
    private static final Map<String, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesDefaultSize = new HashMap<>();

    static {
        rdbmsTypes2ApplicationTypes.put("VARCHAR2", Scripts.STRING_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("DECIMAL", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("NUMBER", Scripts.NUMBER_TYPE_NAME); 
        rdbmsTypes2ApplicationTypes.put("TIMESTAMP", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("MDSYS.SDO_GEOMETRY", Scripts.GEOMETRY_TYPE_NAME); 
        rdbmsTypes2ApplicationTypes.put("INTEGER", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("FLOAT", Scripts.NUMBER_TYPE_NAME);  
        rdbmsTypes2ApplicationTypes.put("REAL", Scripts.NUMBER_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("DATE", Scripts.DATE_TYPE_NAME);  
        rdbmsTypes2ApplicationTypes.put("NVARCHAR2",Scripts.STRING_TYPE_NAME);        
        rdbmsTypes2ApplicationTypes.put("NCHAR", Scripts.STRING_TYPE_NAME);               
        rdbmsTypes2ApplicationTypes.put("TIMESTAMP(6)", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("TIMESTAMP WITH TIME ZONE", Scripts.DATE_TYPE_NAME); 
        rdbmsTypes2ApplicationTypes.put("TIMESTAMP WITH LOCAL TIME ZONE", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("TIMESTAMP(6) WITH TIME ZONE", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("TIMESTAMP(6) WITH LOCAL TIME ZONE", Scripts.DATE_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("LONG RAW", Scripts.STRING_TYPE_NAME);    
        rdbmsTypes2ApplicationTypes.put("RAW", Scripts.STRING_TYPE_NAME);             
        rdbmsTypes2ApplicationTypes.put("LONG", Scripts.STRING_TYPE_NAME);          
        rdbmsTypes2ApplicationTypes.put("CHAR", Scripts.STRING_TYPE_NAME);                 
        rdbmsTypes2ApplicationTypes.put("CLOB", Scripts.STRING_TYPE_NAME);                 
        rdbmsTypes2ApplicationTypes.put("NCLOB", Scripts.STRING_TYPE_NAME);               
        rdbmsTypes2ApplicationTypes.put("GEOMETRY", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("CURVE", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("POLYGON", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("LINESTRING", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("POINT", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("SURFACE", Scripts.GEOMETRY_TYPE_NAME);
        rdbmsTypes2ApplicationTypes.put("SDO_GEOMETRY", Scripts.GEOMETRY_TYPE_NAME); 
        rdbmsTypes2ApplicationTypes.put("BLOB", null);

        //typeName(M,D)
        jdbcTypesWithScale.add("DECIMAL"); //?????
        jdbcTypesWithScale.add("NUMBER");

        //typeName(M)
        jdbcTypesWithSize.add("FLOAT"); //???????!!!!!!!!!!!!
        jdbcTypesWithSize.add("CHAR");
        jdbcTypesWithSize.add("VARCHAR2");
        jdbcTypesWithSize.add("NCHAR");
        jdbcTypesWithSize.add("NVARCHAR2");
        jdbcTypesWithSize.add("NUMBER");
        jdbcTypesWithSize.add("DECIMAL");//????
        jdbcTypesWithSize.add("RAW");

        // max sizes for types
        jdbcTypesMaxSize.put("CHAR", 255);
        jdbcTypesMaxSize.put("VARCHAR2", 4000);
        jdbcTypesMaxSize.put("NCHAR", 255);
        jdbcTypesMaxSize.put("NVARCHAR2", 4000);
        jdbcTypesMaxSize.put("NUMBER", 38);
        jdbcTypesMaxSize.put("DECIMAL", 38);
        jdbcTypesMaxSize.put("RAW", 2000);

        // default sizes for types ??????????????????????????????????????????????
        jdbcTypesDefaultSize.put("CHAR", 1);
        jdbcTypesDefaultSize.put("VARCHAR2", 200);
        jdbcTypesDefaultSize.put("NCHAR2", 1);
        jdbcTypesDefaultSize.put("NVARCHAR2", 200);
        jdbcTypesDefaultSize.put("RAW", 1);
        jdbcTypesDefaultSize.put("NUMBER", 38);
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
