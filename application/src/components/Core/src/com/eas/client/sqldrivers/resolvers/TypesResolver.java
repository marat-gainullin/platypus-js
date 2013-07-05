/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resolver incapsulates functionality, involved in fields types resolving from/to RDBMS friendly form.
 * @author mg
 */
public abstract class TypesResolver {

    /**
     * Resovles field's sql type, sql type name and java class name to RDBMS friendly form.
     * I.e. it corrects field type information.
     * For example, oracle geometry has type name MDSYS.SDO_GEOMETRY (correct only with schema name).
     * Nevetheless, oracle returns type name in the ResultSetMetaData as SDO_GEOMETRY only.
     * @param aField Field instance data type info to be resolved in.
     * @see java.sql.ResultSetMetaData
     */
//    public abstract void resolve2RDBMS(Field aField);
    public void resolve2RDBMS(Field aField) {
        assert aField != null;
        DataTypeInfo typeInfo = aField.getTypeInfo();
        if (typeInfo == null) {
            typeInfo = DataTypeInfo.VARCHAR;
            Logger.getLogger(TypesResolver.class.getName()).log(Level.SEVERE, "sql jdbc type {0} have no mapping to rdbms type. substituting with string type (Varchar)", new Object[]{aField.getTypeInfo().getSqlType()});
        }
        DataTypeInfo copyTypeInfo = typeInfo.copy();
        // проверка на максимальный размер
        int sqlType = typeInfo.getSqlType();
        int fieldSize = aField.getSize();
        Map<Integer, Integer> jdbcTypesMaxSize = getJdbcTypesMaxSize();
        List<Integer> characterTypesOrder = getCharacterTypesOrder();
        if (jdbcTypesMaxSize != null && jdbcTypesMaxSize.containsKey(sqlType)) {
            Integer maxSize = jdbcTypesMaxSize.get(sqlType);
            if (maxSize != null && maxSize < fieldSize) {
                if (characterTypesOrder != null && characterTypesOrder.contains(sqlType)) {
                    for (int i = characterTypesOrder.indexOf(sqlType)+1;i < characterTypesOrder.size(); i++) {
                        sqlType = characterTypesOrder.get(i);
                        maxSize = jdbcTypesMaxSize.get(sqlType);
                        if (maxSize != null && maxSize >= fieldSize) {
                            break;
                        }
                    }
                } else {
                    List<Integer> binaryTypesOrder = getBinaryTypesOrder();
                    if (binaryTypesOrder != null && binaryTypesOrder.contains(sqlType)) {
                        for (int i = binaryTypesOrder.indexOf(sqlType)+1;i < binaryTypesOrder.size(); i++) {
                            sqlType = binaryTypesOrder.get(i);
                            maxSize = jdbcTypesMaxSize.get(sqlType);
                            if (maxSize != null && maxSize >= fieldSize) {
                                break;
                            }
                        }
                    }    
                }
            }
        }
        Map<Integer, String> jdbcTypes2RdbmsTypes = getJdbcTypes2RdbmsTypes();
        if (jdbcTypes2RdbmsTypes != null) {
            String sqlTypeName = jdbcTypes2RdbmsTypes.get(sqlType);
            if (sqlTypeName != null) {
                copyTypeInfo.setSqlType(getJdbcTypeByRDBMSTypename(sqlTypeName));
                copyTypeInfo.setSqlTypeName(sqlTypeName.toLowerCase());
                copyTypeInfo.setJavaClassName(typeInfo.getJavaClassName());
            }
            aField.setTypeInfo(copyTypeInfo);
            if (fieldSize <= 0) {
                Map<Integer, Integer> jdbcTypesDefaultSize = getJdbcTypesDefaultSize();
                if (jdbcTypesDefaultSize != null && jdbcTypesDefaultSize.containsKey(sqlType)) {
                    aField.setSize(jdbcTypesDefaultSize.get(sqlType));
                }
            }    
        }    
    }
    
    public abstract Map<Integer, String> getJdbcTypes2RdbmsTypes();
    
    /**
     * Resovles field's sql type, sql type name and java class name to application friendly form
     * @param aField Field instance data type info to be resolved in.
     */
    public void resolve2Application(Field aField){
        int jdbcType = getJdbcTypeByRDBMSTypename(aField.getTypeInfo().getSqlTypeName());
        aField.setTypeInfo(DataTypeInfo.valueOf(jdbcType).copy());
    }
    
    public abstract boolean isGeometryTypeName(String aTypeName);

    public abstract int getJdbcTypeByRDBMSTypename(String aTypeName);

    public abstract Set<Integer> getSupportedJdbcDataTypes();
    
    public abstract boolean isSized(Integer aSqlType);   

    public abstract boolean isScaled(Integer aSqlType);   
    
    /**
     * @return the BinaryTypesOrder
     */
    public abstract List<Integer> getBinaryTypesOrder();

    /**
     * @return the jdbcTypesMaxSize
     */
    public abstract Map<Integer, Integer> getJdbcTypesMaxSize();

    /**
     * @return the jdbcTypesDefaultSize
     */
    public abstract Map<Integer, Integer> getJdbcTypesDefaultSize();

    /**
     * @return the CharacterTypesOrder
     */
    public abstract List<Integer> getCharacterTypesOrder();
    
    
}
