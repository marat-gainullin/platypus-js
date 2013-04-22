/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.converters;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.sqldrivers.resolvers.MySqlTypesResolver;
import java.sql.Types;

/**
 *
 */
public class MySqlConverter extends PlatypusConverter {

    public MySqlConverter()
    {
        super(new MySqlTypesResolver());
    }

    @Override
    public boolean isGeometry(DataTypeInfo aTypeInfo) {
        return aTypeInfo.getSqlType() == Types.BINARY && ((MySqlTypesResolver)resolver).isGeometryTypeName(aTypeInfo.getSqlTypeName().toLowerCase());        
    }

}
