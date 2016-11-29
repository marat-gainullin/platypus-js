/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.eas.client.metadata.JdbcField;
import com.eas.script.Scripts;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author mg
 */
public class GenericTypesResolver implements TypesResolver {

    protected static final Map<Integer, String> jdbcTypesToApplicationTypes = new HashMap<>();

    static {
        jdbcTypesToApplicationTypes.put(java.sql.Types.VARCHAR, Scripts.STRING_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.CHAR, Scripts.STRING_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.CLOB, Scripts.STRING_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.DATALINK, Scripts.STRING_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.LONGNVARCHAR, Scripts.STRING_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.LONGVARCHAR, Scripts.STRING_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.NCHAR, Scripts.STRING_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.NCLOB, Scripts.STRING_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.NVARCHAR, Scripts.STRING_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.ROWID, Scripts.STRING_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.SQLXML, Scripts.STRING_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.BIGINT, Scripts.NUMBER_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.DECIMAL, Scripts.NUMBER_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.DOUBLE, Scripts.NUMBER_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.FLOAT, Scripts.NUMBER_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.INTEGER, Scripts.NUMBER_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.NUMERIC, Scripts.NUMBER_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.REAL, Scripts.NUMBER_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.SMALLINT, Scripts.NUMBER_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.TINYINT, Scripts.NUMBER_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.DATE, Scripts.DATE_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.TIME, Scripts.DATE_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.TIMESTAMP, Scripts.DATE_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.TIMESTAMP_WITH_TIMEZONE, Scripts.DATE_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.TIME_WITH_TIMEZONE, Scripts.DATE_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.BIT, Scripts.BOOLEAN_TYPE_NAME);
        jdbcTypesToApplicationTypes.put(java.sql.Types.BOOLEAN, Scripts.BOOLEAN_TYPE_NAME);
    }

    @Override
    public String toApplicationType(int aJdbcType, String aRDBMSType) {
        return jdbcTypesToApplicationTypes.get(aJdbcType);
    }

    @Override
    public Set<String> getSupportedTypes() {
        return null;
    }

    @Override
    public boolean isSized(String aTypeName) {
        return false;
    }

    @Override
    public boolean isScaled(String aTypeName) {
        return false;
    }

    @Override
    public void resolveSize(JdbcField aField) {
    }

}
