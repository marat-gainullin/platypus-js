/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.resolvers;

import com.bearsoft.rowset.metadata.Field;
import java.util.Set;

/**
 * Resolver incapsulates functionality, involved in fields types resolving from/to RDBMS friendly form.
 * @author mg
 */
public interface TypesResolver {

    /**
     * Resovles field's sql type, sql type name and java class name to RDBMS friendly form.
     * I.e. it corrects field type information.
     * For example, oracle geometry has type name MDSYS.SDO_GEOMETRY (correct only with schema name).
     * Nevetheless, oracle returns type name in the ResultSetMetaData as SDO_GEOMETRY only.
     * @param aField Field instance data type info to be resolved in.
     * @see java.sql.ResultSetMetaData
     */
    public void resolve2RDBMS(Field aField);

    /**
     * Resovles field's sql type, sql type name and java class name to application friendly form
     * @param aField Field instance data type info to be resolved in.
     */
    public void resolve2Application(Field aField);
    
    public boolean isGeometryTypeName(String aTypeName);

    public int getJdbcTypeByRDBMSTypename(String aTypeName);

    public Set<Integer> getSupportedJdbcDataTypes();
    
    public boolean isSized(Integer aSqlType);   

    public boolean isScaled(Integer aSqlType);   
    
}
