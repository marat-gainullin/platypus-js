/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class MsSqlTypesResolver implements TypesResolver {

    protected static final Map<Integer, DataTypeInfo> jdbcToRDBMS = new HashMap<>();
    protected static final Map<Integer, String> jdbcTypes2RdbmsTypes = new HashMap<>();
    protected static final Map<String, Integer> rdbmsTypes2JdbcTypes = new HashMap<>();
    protected static final Set<Integer> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<Integer> jdbcTypesWithScale = new HashSet<>();
    public static final int NON_JDBC_LONG_STRING = 258;
    public static final int NON_JDBC_MEDIUM_STRING = 259;
    public static final int NON_JDBC_MEMO_STRING = 260;
    public static final int NON_JDBC_SHORT_STRING = 261;

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
        

        // let's reduce all jdbc types to supported jdbc types for this RDBMS
        jdbcToRDBMS.put(Types.SQLXML, DataTypeInfo.VARCHAR.copy());
        jdbcToRDBMS.put(Types.BLOB, DataTypeInfo.BLOB.copy());
        jdbcToRDBMS.put(Types.BINARY, DataTypeInfo.BINARY.copy());
        jdbcToRDBMS.put(Types.VARBINARY, DataTypeInfo.VARBINARY.copy());
        jdbcToRDBMS.put(Types.LONGVARBINARY, DataTypeInfo.VARBINARY.copy());
        jdbcToRDBMS.put(Types.CLOB, DataTypeInfo.CLOB.copy());
        jdbcToRDBMS.put(Types.NCLOB, DataTypeInfo.CLOB.copy());
        jdbcToRDBMS.put(Types.DECIMAL, DataTypeInfo.DECIMAL.copy());
        jdbcToRDBMS.put(Types.NUMERIC, DataTypeInfo.NUMERIC.copy());
        jdbcToRDBMS.put(Types.BIGINT, DataTypeInfo.BIGINT.copy());
        jdbcToRDBMS.put(Types.SMALLINT, DataTypeInfo.SMALLINT.copy());
        jdbcToRDBMS.put(Types.TINYINT, DataTypeInfo.TINYINT.copy());
        jdbcToRDBMS.put(Types.INTEGER, DataTypeInfo.INTEGER.copy());
        jdbcToRDBMS.put(Types.REAL, DataTypeInfo.REAL.copy());
        jdbcToRDBMS.put(Types.FLOAT, DataTypeInfo.FLOAT.copy());
        jdbcToRDBMS.put(Types.DOUBLE, DataTypeInfo.FLOAT.copy());
        jdbcToRDBMS.put(Types.LONGVARCHAR, DataTypeInfo.CLOB.copy());
        jdbcToRDBMS.put(Types.LONGNVARCHAR, DataTypeInfo.CLOB.copy());
        jdbcToRDBMS.put(Types.CHAR, DataTypeInfo.CHAR.copy());
        jdbcToRDBMS.put(Types.VARCHAR, DataTypeInfo.VARCHAR.copy());
        jdbcToRDBMS.put(Types.NCHAR, DataTypeInfo.CHAR.copy());
        jdbcToRDBMS.put(Types.NVARCHAR, DataTypeInfo.NVARCHAR.copy());
        jdbcToRDBMS.put(Types.BOOLEAN, DataTypeInfo.BIT.copy());
        jdbcToRDBMS.put(Types.BIT, DataTypeInfo.BIT.copy());
        
        jdbcToRDBMS.put(Types.DATE, DataTypeInfo.TIMESTAMP.copy());
        jdbcToRDBMS.put(Types.TIMESTAMP, DataTypeInfo.TIMESTAMP.copy());
        jdbcToRDBMS.put(Types.TIME, DataTypeInfo.TIMESTAMP.copy());
        jdbcToRDBMS.put(Types.OTHER, DataTypeInfo.OTHER.copy());
        // ms sql specific (non jdbc)
        jdbcToRDBMS.put(NON_JDBC_LONG_STRING, DataTypeInfo.NVARCHAR.copy());
        jdbcToRDBMS.put(NON_JDBC_MEDIUM_STRING, DataTypeInfo.NVARCHAR.copy());
        jdbcToRDBMS.put(NON_JDBC_SHORT_STRING, DataTypeInfo.NVARCHAR.copy());
        jdbcToRDBMS.put(NON_JDBC_MEMO_STRING, DataTypeInfo.NVARCHAR.copy());
        // unsupported
        jdbcToRDBMS.put(Types.ARRAY, DataTypeInfo.ARRAY.copy());
        jdbcToRDBMS.put(Types.STRUCT, DataTypeInfo.STRUCT.copy());
        jdbcToRDBMS.put(Types.JAVA_OBJECT, DataTypeInfo.JAVA_OBJECT.copy());
        jdbcToRDBMS.put(Types.DATALINK, DataTypeInfo.DATALINK.copy());
        jdbcToRDBMS.put(Types.DISTINCT, DataTypeInfo.DISTINCT.copy());
        jdbcToRDBMS.put(Types.NULL, DataTypeInfo.NULL.copy());
        jdbcToRDBMS.put(Types.ROWID, DataTypeInfo.ROWID.copy());
        jdbcToRDBMS.put(Types.REF, DataTypeInfo.REF.copy());

        // let's assign ms sql names to our DataTypeInfo's.
        for (DataTypeInfo typeInfo : jdbcToRDBMS.values()) {
            String dbmsTypeName = jdbcTypes2RdbmsTypes.get(typeInfo.getSqlType());
            typeInfo.setSqlTypeName(dbmsTypeName);
        }
        
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

    }

    @Override
    public void resolve2RDBMS(Field aField) {
        DataTypeInfo typeInfo = jdbcToRDBMS.get(aField.getTypeInfo().getSqlType());
        if (typeInfo == null) {
            typeInfo = jdbcToRDBMS.get(DataTypeInfo.VARCHAR.getSqlType());
            Logger.getLogger(MsSqlTypesResolver.class.getName()).log(Level.SEVERE, "sql jdbc type {0} have no mapping to rdbms type. substituting with string type (Varchar)", new Object[]{aField.getTypeInfo().getSqlType()});
        }
        
        DataTypeInfo copyTypeInfo = typeInfo.copy();
        String sqlTypeName = jdbcTypes2RdbmsTypes.get(typeInfo.getSqlType());
        if (sqlTypeName != null) {
            copyTypeInfo.setSqlType(getJdbcTypeByRDBMSTypename(sqlTypeName));
            copyTypeInfo.setSqlTypeName(sqlTypeName.toLowerCase());
            copyTypeInfo.setJavaClassName(typeInfo.getJavaClassName());
        }
        aField.setTypeInfo(copyTypeInfo);
    }

    @Override
    public void resolve2Application(Field aField) {
        if (aField.getTypeInfo().getSqlType() == NON_JDBC_LONG_STRING
                || aField.getTypeInfo().getSqlType() == NON_JDBC_MEDIUM_STRING
                || aField.getTypeInfo().getSqlType() == NON_JDBC_SHORT_STRING
                || aField.getTypeInfo().getSqlType() == NON_JDBC_MEMO_STRING) {
            aField.setTypeInfo(DataTypeInfo.NVARCHAR.copy());
        } else if (aField.getTypeInfo().getSqlType() == java.sql.Types.CLOB
                || aField.getTypeInfo().getSqlType() == java.sql.Types.NCLOB) {
            aField.setTypeInfo(DataTypeInfo.CLOB.copy());
        } else if (aField.getTypeInfo().getSqlType() == java.sql.Types.BLOB) {
            aField.setTypeInfo(DataTypeInfo.BLOB.copy());
        } else if (aField.getTypeInfo().getSqlType() == java.sql.Types.BINARY
                || aField.getTypeInfo().getSqlType() == java.sql.Types.VARBINARY) {
            aField.setTypeInfo(DataTypeInfo.VARBINARY.copy());
        }
    }
    
    @Override
    public int getJdbcTypeByRDBMSTypename(String aTypeName) {
        Integer jdbcType = (aTypeName != null?rdbmsTypes2JdbcTypes.get(aTypeName.toLowerCase()):null);
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
    public boolean isSized(Integer aSqlType)   
    {
        return jdbcTypesWithSize.contains(aSqlType);
    }        

    @Override
    public boolean isScaled(Integer aSqlType)   
    {
        return jdbcTypesWithScale.contains(aSqlType);
    }        
    
}