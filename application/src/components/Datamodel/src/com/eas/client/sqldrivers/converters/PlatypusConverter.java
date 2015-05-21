/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.converters;

import com.eas.client.dataflow.Converter;
import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.sqldrivers.resolvers.TypesResolver;

/**
 * Base class for all platypus converters.
 * It converts some RDBMS-specific data to abstract application form while reading and
 * to RDBMS-specific while applying data.
 *
 * @author mg
 */
public abstract class PlatypusConverter extends Converter {

    protected TypesResolver resolver;

    public PlatypusConverter(TypesResolver aResolver) {
        resolver = aResolver;
    }

    /**
     * Determines if aTypeInfo is about a geometry type.
     * This method is used in from RDBMS and to RDBMS data flow processes.
     * @param aTypeInfo
     * @return 
     */
    public boolean isGeometry(DataTypeInfo aTypeInfo){
        return DataTypeInfo.GEOMETRY.equals(aTypeInfo);
    }
}
