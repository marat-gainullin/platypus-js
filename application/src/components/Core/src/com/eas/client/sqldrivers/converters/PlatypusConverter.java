/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.converters;

import com.bearsoft.rowset.RowsetConverter;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.sqldrivers.resolvers.TypesResolver;

/**
 * Base class for all platypus converters
 *
 * @author mg
 */
public abstract class PlatypusConverter extends RowsetConverter {

    protected TypesResolver resolver;

    public PlatypusConverter(TypesResolver aResolver) {
        resolver = aResolver;
    }

    public boolean isGeometry(DataTypeInfo aTypeInfo){
        return DataTypeInfo.GEOMETRY.equals(aTypeInfo);
    }
}
