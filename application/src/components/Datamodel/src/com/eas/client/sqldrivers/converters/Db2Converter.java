/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.converters;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.sqldrivers.resolvers.Db2TypesResolver;

/**
 *
 * @author kl
 */
public class Db2Converter extends PlatypusConverter {

    public Db2Converter() {
        super(new Db2TypesResolver());
    }

    @Override
    public boolean isGeometry(DataTypeInfo aTypeInfo) {
        return super.isGeometry(aTypeInfo);
    }
}
