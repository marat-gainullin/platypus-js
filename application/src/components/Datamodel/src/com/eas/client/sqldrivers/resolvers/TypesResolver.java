/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.metadata.Field;
import com.eas.client.SQLUtils;
import java.sql.Types;
import java.util.Map;
import java.util.Set;

/**
 * Resolver incapsulates functionality, involved in fields types resolving
 * from/to RDBMS friendly form.
 *
 * @author mg
 */
public abstract class TypesResolver {

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
}
