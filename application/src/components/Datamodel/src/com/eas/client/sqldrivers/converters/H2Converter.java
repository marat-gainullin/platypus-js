/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.converters;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.sqldrivers.resolvers.H2TypesResolver;

/**
 *
 * @author vv
 */
public class H2Converter extends PlatypusConverter {

    public H2Converter() {
        super(new H2TypesResolver());
    }

    @Override
    public boolean isGeometry(DataTypeInfo aTypeInfo) {
        return super.isGeometry(aTypeInfo);
    }
}
