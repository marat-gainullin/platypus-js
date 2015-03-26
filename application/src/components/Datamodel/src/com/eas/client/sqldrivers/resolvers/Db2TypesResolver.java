/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import java.sql.Types;
import java.util.*;

/**
 *
 * @author kl
 */
public class Db2TypesResolver extends TypesResolver {

    protected static final Map<Integer, String> jdbcTypes2RdbmsTypes = new HashMap<>();
    protected static final Map<String, Integer> rdbmsTypes2JdbcTypes = new HashMap<>();
    protected static final Set<String> gisTypes = new HashSet<>();
    protected static final Set<String> jdbcTypesWithSize = new HashSet<>();
    protected static final Set<String> jdbcTypesWithScale = new HashSet<>();
    protected static final Map<String, String> jdbcTypesLeftPartName = new HashMap<>();
    protected static final Map<String, String> jdbcTypesRightPartName = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesMaxSize = new HashMap<>();
    private static final Map<String, Integer> jdbcTypesDefaultSize = new HashMap<>();

    static {
        // rdbms -> jdbc
        rdbmsTypes2JdbcTypes.put("SMALLINT", Types.SMALLINT);
        rdbmsTypes2JdbcTypes.put("INTEGER", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("INT", Types.INTEGER);
        rdbmsTypes2JdbcTypes.put("BIGINT", Types.BIGINT);
        rdbmsTypes2JdbcTypes.put("DECIMAL", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("DEC", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("NUMERIC", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("NUM", Types.DECIMAL);
        rdbmsTypes2JdbcTypes.put("FLOAT", Types.REAL);
        rdbmsTypes2JdbcTypes.put("REAL", Types.REAL);
        rdbmsTypes2JdbcTypes.put("DOUBLE", Types.DOUBLE);
        rdbmsTypes2JdbcTypes.put("DOUBLE PRECISION", Types.DOUBLE);
        rdbmsTypes2JdbcTypes.put("DECFLOAT", Types.OTHER);//????? float !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        rdbmsTypes2JdbcTypes.put("CHAR", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("CHARACTER", Types.CHAR);
        rdbmsTypes2JdbcTypes.put("VARCHAR", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("CHAR VARYING", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("CHARACTER VARYING", Types.VARCHAR);
        rdbmsTypes2JdbcTypes.put("CHAR () FOR BIT DATA", Types.BINARY);
        rdbmsTypes2JdbcTypes.put("CHARACTER () FOR BIT DATA", Types.BINARY);
        rdbmsTypes2JdbcTypes.put("CHAR VARYING () FOR BIT DATA", Types.VARBINARY);
        rdbmsTypes2JdbcTypes.put("VARCHAR () FOR BIT DATA", Types.VARBINARY);
        rdbmsTypes2JdbcTypes.put("CHARACTER VARYING () FOR BIT DATA", Types.VARBINARY);
        rdbmsTypes2JdbcTypes.put("LONG VARCHAR", Types.LONGVARCHAR);
        rdbmsTypes2JdbcTypes.put("LONG VARCHAR FOR BIT DATA", Types.LONGVARBINARY);
        rdbmsTypes2JdbcTypes.put("CLOB", Types.CLOB);
        rdbmsTypes2JdbcTypes.put("CHAR LARGE OBJECT", Types.CLOB);
        rdbmsTypes2JdbcTypes.put("CHARACTER LARGE OBJECT", Types.CLOB);

        // !!! not supported for this database !!!
        //rdbmsTypes2JdbcTypes.put("GRAPHIC", Types.CHAR);//???varchar
        //rdbmsTypes2JdbcTypes.put("VARGRAPHIC", Types.VARCHAR);
        //rdbmsTypes2JdbcTypes.put("LONG VARGRAPHIC", Types.LONGVARCHAR);
        //rdbmsTypes2JdbcTypes.put("DBCLOB", Types.CLOB);
        // !!! not supported for this database !!!

        // !!! supported only for a Unicode database !!!
        //rdbmsTypes2JdbcTypes.put("NCHAR", Types.CHAR);
        //rdbmsTypes2JdbcTypes.put("NATIONAL CHAR", Types.CHAR);
        //rdbmsTypes2JdbcTypes.put("NATIONAL CHARACTER", Types.CHAR);
        //rdbmsTypes2JdbcTypes.put("NVARCHAR", Types.VARCHAR);
        //rdbmsTypes2JdbcTypes.put("NCHAR VARYING", Types.VARCHAR);
        //rdbmsTypes2JdbcTypes.put("NATIONAL CHAR VARYING", Types.VARCHAR);
        //rdbmsTypes2JdbcTypes.put("NATIONAL CHARACTER VARYING", Types.VARCHAR);
        //rdbmsTypes2JdbcTypes.put("NCLOB", Types.CLOB);
        //rdbmsTypes2JdbcTypes.put("NCHAR LARGE OBJECT", Types.CLOB);
        //rdbmsTypes2JdbcTypes.put("NATIONAL CHARACTER LARGE OBJECT", Types.CLOB);
        // !!! supported only for a Unicode database !!!
        rdbmsTypes2JdbcTypes.put("BLOB", Types.BLOB);
        rdbmsTypes2JdbcTypes.put("BINARY LARGE OBJECT", Types.BLOB);
        rdbmsTypes2JdbcTypes.put("DATE", Types.DATE);
        rdbmsTypes2JdbcTypes.put("TIME", Types.TIME);
        rdbmsTypes2JdbcTypes.put("TIMESTAMP", Types.TIMESTAMP);
        rdbmsTypes2JdbcTypes.put("XML", Types.BLOB); //?? OTHER  || SQLXML || BLOB
        //??rdbmsTypes2JdbcTypes.put("DATALINK", Types.VARCHAR);

        // jdbc -> rdbms
        jdbcTypes2RdbmsTypes.put(Types.BIT, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.TINYINT, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.SMALLINT, "SMALLINT");
        jdbcTypes2RdbmsTypes.put(Types.INTEGER, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.BIGINT, "BIGINT");
        jdbcTypes2RdbmsTypes.put(Types.FLOAT, "REAL");
        jdbcTypes2RdbmsTypes.put(Types.REAL, "REAL");
        jdbcTypes2RdbmsTypes.put(Types.DOUBLE, "DOUBLE");
        jdbcTypes2RdbmsTypes.put(Types.NUMERIC, "DECIMAL");
        jdbcTypes2RdbmsTypes.put(Types.DECIMAL, "DECIMAL");
        jdbcTypes2RdbmsTypes.put(Types.CHAR, "CHAR");
        jdbcTypes2RdbmsTypes.put(Types.VARCHAR, "VARCHAR");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARCHAR, "LONG VARCHAR");
        jdbcTypes2RdbmsTypes.put(Types.DATE, "DATE");
        jdbcTypes2RdbmsTypes.put(Types.TIME, "TIME");
        jdbcTypes2RdbmsTypes.put(Types.TIMESTAMP, "TIMESTAMP");
        jdbcTypes2RdbmsTypes.put(Types.BINARY, "CHAR () FOR BIT DATA");
        jdbcTypes2RdbmsTypes.put(Types.VARBINARY, "VARCHAR () FOR BIT DATA");
        jdbcTypes2RdbmsTypes.put(Types.LONGVARBINARY, "LONG VARCHAR FOR BIT DATA");
        jdbcTypes2RdbmsTypes.put(Types.BLOB, "BLOB");
        jdbcTypes2RdbmsTypes.put(Types.CLOB, "CLOB");
        jdbcTypes2RdbmsTypes.put(Types.BOOLEAN, "INTEGER");
        jdbcTypes2RdbmsTypes.put(Types.NCHAR, "CHAR");
        jdbcTypes2RdbmsTypes.put(Types.NVARCHAR, "VARCHAR");
        jdbcTypes2RdbmsTypes.put(Types.LONGNVARCHAR, "LONG VARCHAR");
        jdbcTypes2RdbmsTypes.put(Types.NCLOB, "CLOB");

        //typeName(M,D)
        //Types.DECIMAL (M,D)
        jdbcTypesWithScale.add("DECIMAL");
        jdbcTypesWithScale.add("DEC");
        jdbcTypesWithScale.add("NUMERIC");
        jdbcTypesWithScale.add("NUM");

        //typeName(M)
        //Types.DECIMAL (M,D)
        jdbcTypesWithSize.add("DECIMAL");
        jdbcTypesWithSize.add("DEC");
        jdbcTypesWithSize.add("NUMERIC");
        jdbcTypesWithSize.add("NUM");
        //Types.CHAR (M)
        jdbcTypesWithSize.add("CHAR");
        jdbcTypesWithSize.add("CHARACTER");
        // jdbcTypesWithSize.add("GRAPHIC");
        // jdbcTypesWithSize.add("NCHAR");
        // jdbcTypesWithSize.add("NATIONAL CHAR");
        // jdbcTypesWithSize.add("NATIONAL CHARACTER");
        //Types.VARCHAR (M)
        jdbcTypesWithSize.add("VARCHAR");
        jdbcTypesWithSize.add("CHAR VARYING");
        jdbcTypesWithSize.add("CHARACTER VARYING");
        // jdbcTypesWithSize.add("VARGRAPHIC");
        // jdbcTypesWithSize.add("NVARCHAR");
        // jdbcTypesWithSize.add("NCHAR VARYING");
        // jdbcTypesWithSize.add("NATIONAL CHAR VARYING");
        // jdbcTypesWithSize.add("NATIONAL CHARACTER VARYING");
        //Types.BINARY (M)
        jdbcTypesWithSize.add("CHAR () FOR BIT DATA");
        jdbcTypesWithSize.add("CHARACTER () FOR BIT DATA");
        //Types.VARBINARY (M)
        jdbcTypesWithSize.add("CHAR VARYING () FOR BIT DATA");
        jdbcTypesWithSize.add("VARCHAR () FOR BIT DATA");
        jdbcTypesWithSize.add("CHARACTER VARYING () FOR BIT DATA");
        //Types.CLOB (M)
        jdbcTypesWithSize.add("CLOB");
        jdbcTypesWithSize.add("CHAR LARGE OBJECT");
        jdbcTypesWithSize.add("CHARACTER LARGE OBJECT");
        // jdbcTypesWithSize.add("DBCLOB");
        // jdbcTypesWithSize.add("NCLOB");
        // jdbcTypesWithSize.add("NCHAR LARGE OBJECT");
        // jdbcTypesWithSize.add("NATIONAL CHARACTER LARGE OBJECT");
        //Types.BLOB (M)
        jdbcTypesWithSize.add("BLOB");
        jdbcTypesWithSize.add("BINARY LARGE OBJECT");

        // max sizes for types
        //Types.CHAR
        jdbcTypesMaxSize.put("CHAR", 254);
        jdbcTypesMaxSize.put("CHARACTER", 254);
        // jdbcTypesMaxSize.put("GRAPHIC",254);
        // jdbcTypesMaxSize.put("NCHAR",254);
        // jdbcTypesMaxSize.put("NATIONAL CHAR",254);
        // jdbcTypesMaxSize.put("NATIONAL CHARACTER",254);
        //Types.VARCHAR 
        jdbcTypesMaxSize.put("VARCHAR", 4000);
        jdbcTypesMaxSize.put("CHAR VARYING", 4000);
        jdbcTypesMaxSize.put("CHARACTER VARYING", 4000);
        // jdbcTypesMaxSize.put("VARGRAPHIC",4000);
        // jdbcTypesMaxSize.put("NVARCHAR",4000);
        // jdbcTypesMaxSize.put("NCHAR VARYING",4000);
        // jdbcTypesMaxSize.put("NATIONAL CHAR VARYING",4000);
        // jdbcTypesMaxSize.put("NATIONAL CHARACTER VARYING",4000);
        //Types.BINARY
        jdbcTypesMaxSize.put("CHAR () FOR BIT DATA", 254);
        jdbcTypesMaxSize.put("CHARACTER () FOR BIT DATA", 254);
        //Types.VARBINARY - зависит от установленного размера страницы до 32762 ?
        jdbcTypesMaxSize.put("CHAR VARYING () FOR BIT DATA", 4000);
        jdbcTypesMaxSize.put("VARCHAR () FOR BIT DATA", 4000);
        jdbcTypesMaxSize.put("CHARACTER VARYING () FOR BIT DATA", 4000);

        // default sizes for types ??????????????????????????????????????????????
        //Types.CHAR
        jdbcTypesDefaultSize.put("CHAR", 1);
        jdbcTypesDefaultSize.put("CHARACTER", 1);
        // jdbcTypesDefaultSize.put("GRAPHIC",1);
        // jdbcTypesDefaultSize.put("NCHAR",1);
        // jdbcTypesDefaultSize.put("NATIONAL CHAR",1);
        // jdbcTypesDefaultSize.put("NATIONAL CHARACTER",1);
        //Types.VARCHAR 
        jdbcTypesDefaultSize.put("VARCHAR", 200);
        jdbcTypesDefaultSize.put("CHAR VARYING", 200);
        jdbcTypesDefaultSize.put("CHARACTER VARYING", 200);
        // jdbcTypesDefaultSize.put("VARGRAPHIC",200);
        // jdbcTypesDefaultSize.put("NVARCHAR",200);
        // jdbcTypesDefaultSize.put("NCHAR VARYING",200);
        // jdbcTypesDefaultSize.put("NATIONAL CHAR VARYING",200);
        // jdbcTypesDefaultSize.put("NATIONAL CHARACTER VARYING",200);
        //Types.BINARY
        jdbcTypesDefaultSize.put("CHAR () FOR BIT DATA", 1);
        jdbcTypesDefaultSize.put("CHARACTER () FOR BIT DATA", 1);
        //Types.VARBINARY
        jdbcTypesDefaultSize.put("CHAR VARYING () FOR BIT DATA", 200);
        jdbcTypesDefaultSize.put("VARCHAR () FOR BIT DATA", 200);
        jdbcTypesDefaultSize.put("CHARACTER VARYING () FOR BIT DATA", 200);
        //Types.CLOB (M)
        jdbcTypesDefaultSize.put("CLOB", 2147483647);
        jdbcTypesDefaultSize.put("CHAR LARGE OBJECT", 2147483647);
        jdbcTypesDefaultSize.put("CHARACTER LARGE OBJECT", 2147483647);
        // jdbcTypesDefaultSize.put("DBCLOB", 2147483647);
        // jdbcTypesDefaultSize.put("NCLOB", 2147483647);
        // jdbcTypesDefaultSize.put("NCHAR LARGE OBJECT", 2147483647);
        // jdbcTypesDefaultSize.put("NATIONAL CHARACTER LARGE OBJECT", 2147483647);
        //Types.BLOB (M)
        jdbcTypesDefaultSize.put("BLOB", 2147483647);
        jdbcTypesDefaultSize.put("BINARY LARGE OBJECT", 2147483647);

        // для полей, где размер задается в середине имени типа
        jdbcTypesLeftPartName.put("CHAR () FOR BIT DATA", "CHAR");
        jdbcTypesLeftPartName.put("CHARACTER () FOR BIT DATA", "CHARACTER");
        jdbcTypesLeftPartName.put("CHAR VARYING () FOR BIT DATA", "CHAR VARYING");
        jdbcTypesLeftPartName.put("VARCHAR () FOR BIT DATA", "VARCHAR");
        jdbcTypesLeftPartName.put("CHARACTER VARYING () FOR BIT DATA", "CHARACTER VARYING");

        jdbcTypesRightPartName.put("CHAR () FOR BIT DATA", "FOR BIT DATA");
        jdbcTypesRightPartName.put("CHARACTER () FOR BIT DATA", "FOR BIT DATA");
        jdbcTypesRightPartName.put("CHAR VARYING () FOR BIT DATA", "FOR BIT DATA");
        jdbcTypesRightPartName.put("VARCHAR () FOR BIT DATA", "FOR BIT DATA");
        jdbcTypesRightPartName.put("CHARACTER VARYING () FOR BIT DATA", "FOR BIT DATA");

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
        return false;
    }

    private String parseRDBMSTypename(String aTypeName) {
        assert aTypeName != null;
        String sqlTypeName = aTypeName.toUpperCase();
        // убрать (size) из имени типа
        int leftIndex = sqlTypeName.indexOf("(");
        if (leftIndex > 0) {
            int rightIndex = sqlTypeName.indexOf(")");
            if (rightIndex > 0) {
                sqlTypeName = sqlTypeName.substring(0, leftIndex) + "() " + sqlTypeName.substring(rightIndex + 1);
            }
        }
        StringTokenizer st = new StringTokenizer(sqlTypeName, " ", false);
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            String part = st.nextToken();
            if (part != null && !part.isEmpty()) {
                sb.append(part).append(" ");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public int getJdbcTypeByRDBMSTypename(String aTypeName) {
        String sqlTypeName = (aTypeName != null ? parseRDBMSTypename(aTypeName) : null);
        Integer jdbcType = (sqlTypeName != null ? rdbmsTypes2JdbcTypes.get(sqlTypeName) : null);
        if (jdbcType == null) {
            jdbcType = Types.OTHER;
            if (isGeometryTypeName(sqlTypeName)) {
                jdbcType = Types.STRUCT;
            }
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
        return jdbcTypesWithSize.contains(aSqlTypeName.toUpperCase());
    }

    @Override
    public boolean isScaled(String aSqlTypeName) {
        return jdbcTypesWithScale.contains(aSqlTypeName.toUpperCase());
    }

    public String getLeftPartNameType(String aSqlTypeName) {
        String sqlTypeName = aSqlTypeName.toUpperCase();
        return (jdbcTypesLeftPartName.containsKey(sqlTypeName) ? jdbcTypesLeftPartName.get(sqlTypeName) : sqlTypeName);
    }

    public String getRightPartNameType(String aSqlTypeName) {
        return jdbcTypesRightPartName.get(aSqlTypeName.toUpperCase());
    }

    @Override
    public Map<Integer, String> getJdbcTypes2RdbmsTypes() {
        return jdbcTypes2RdbmsTypes;
    }

    @Override
    public boolean containsRDBMSTypename(String aTypeName) {
        assert aTypeName != null;
        return rdbmsTypes2JdbcTypes.containsKey(parseRDBMSTypename(aTypeName));
    }

    @Override
    public void resolveFieldSize(Field aField) {
        DataTypeInfo typeInfo = aField.getTypeInfo();
        int sqlType = typeInfo.getSqlType();
        String sqlTypeName = typeInfo.getSqlTypeName();
        sqlTypeName = sqlTypeName.toUpperCase();
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
