/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import com.eas.client.SQLUtils;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resolver incapsulates functionality, involved in fields types resolving
 * from/to RDBMS friendly form.
 *
 * @author mg
 */
public abstract class TypesResolver {

    private static final List<Integer> characterTypesOrder = new ArrayList<>();
    private static final List<Integer> binaryTypesOrder = new ArrayList<>();

    static {
        // порядок замены символьных типов, если требуется размер больше исходного
        characterTypesOrder.add(Types.CHAR);
        characterTypesOrder.add(Types.VARCHAR);
        characterTypesOrder.add(Types.LONGVARCHAR);
        characterTypesOrder.add(Types.CLOB);

        // порядок замены бинарных типов, если требуется размер больше исходного
        binaryTypesOrder.add(Types.BINARY);
        binaryTypesOrder.add(Types.VARBINARY);
        binaryTypesOrder.add(Types.LONGVARBINARY);
        binaryTypesOrder.add(Types.BLOB);

    }

    /**
     * Resovles field's sql type, sql type name and java class name to RDBMS
     * friendly form. I.e. it corrects field type information. For example,
     * oracle geometry has type name MDSYS.SDO_GEOMETRY (correct only with
     * schema name). Nevetheless, oracle returns type name in the
     * ResultSetMetaData as SDO_GEOMETRY only. this method is used in sql driver for field
     * definiion generation. It is NOT used prior to applying data changes in a database.
     * Converter is responsible for a whole conversion in data applying process.
     *
     * @param aField Field instance data type info to be resolved in.
     * @see java.sql.ResultSetMetaData
     */
    public void resolve2RDBMS(Field aField) {
        assert aField != null;
        DataTypeInfo typeInfo = aField.getTypeInfo();
        if (typeInfo == null) {
            typeInfo = DataTypeInfo.VARCHAR;
            Logger.getLogger(TypesResolver.class.getName()).log(Level.SEVERE, "sql jdbc type {0} have no mapping to rdbms type. substituting with string type (Varchar)", new Object[]{aField.getTypeInfo().getSqlType()});
        }
        Map<Integer, String> jdbcTypes2RdbmsTypes = getJdbcTypes2RdbmsTypes();
        assert jdbcTypes2RdbmsTypes != null;
        int sqlType = typeInfo.getSqlType();
        String sqlTypeName = typeInfo.getSqlTypeName();
        // check on different rdbms
        if (sqlTypeName == null || !containsRDBMSTypename(sqlTypeName) || sqlType != getJdbcTypeByRDBMSTypename(sqlTypeName)) {
            // ??????? !!!!!!!
            if (jdbcTypes2RdbmsTypes.containsKey(sqlType)) {
                sqlTypeName = jdbcTypes2RdbmsTypes.get(sqlType);
            }    
        }
        aField.setTypeInfo(new DataTypeInfo(sqlType, sqlTypeName, typeInfo.getJavaClassName()));
        resolveFieldSize(aField);
    }

    public abstract Map<Integer, String> getJdbcTypes2RdbmsTypes();

    /**
     * Resovles field's sql type, sql type name and java class name to
     * application friendly form
     *
     * @param aField Field instance data type info to be resolved in.
     */
    public void resolve2Application(Field aField) {
        int jdbcType = getJdbcTypeByRDBMSTypename(aField.getTypeInfo().getSqlTypeName());
        if (jdbcType == Types.OTHER && SQLUtils.isTypeSupported(aField.getTypeInfo().getSqlType())) {
            // Resolver can't resolve aField's type to application, but such type is supported anyway.
            // We have to correct such situation.
            // Falling back to old-style implementation            
            SQLUtils.TypesGroup tg = SQLUtils.getTypeGroup(aField.getTypeInfo().getSqlType());
            if (tg != null) {
                jdbcType = tg.toJdbcAnalog();
            }
        }
        aField.setTypeInfo(DataTypeInfo.valueOf(jdbcType).copy());
    }

    public abstract boolean isGeometryTypeName(String aTypeName);

    public abstract int getJdbcTypeByRDBMSTypename(String aTypeName);

    public abstract Set<Integer> getSupportedJdbcDataTypes();

    public abstract boolean isSized(String aSqlTypeName);

    public abstract boolean isScaled(String aSqlTypeName);

    public abstract boolean containsRDBMSTypename(String aTypeName);

    public abstract void resolveFieldSize(Field aField);

    protected List<Integer> getTypesOrder(int aType) {
        if (characterTypesOrder.contains(aType)) {
            return characterTypesOrder;
        }
        if (binaryTypesOrder.contains(aType)) {
            return binaryTypesOrder;
        }
        return null;
    }
}
