/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.converters;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.sqldrivers.resolvers.PostgreTypesResolver;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgis.PGgeometry;

/**
 *
 * @author mg
 */
public class PostgreConverter extends PlatypusConverter {

    private static class JTSGeometryConverter {

        public static com.vividsolutions.jts.geom.Geometry fromPostGIS(PGgeometry aPostGISGeometry) {
            if (aPostGISGeometry != null) {
                WKTReader wktGeometryReader = new WKTReader();
                org.postgis.Geometry geom = aPostGISGeometry.getGeometry();
                try {
                    return wktGeometryReader.read(geom.getTypeString() + geom.getValue());
                } catch (ParseException ex) {
                    Logger.getLogger(PostgreConverter.class.getName()).log(Level.SEVERE, "Could not convert PostGIS to JTS geometry. Could not parse geometry wkt " + geom.getTypeString() + geom.getValue(), ex);
                }
            }
            return null;
        }

        public static PGgeometry toPostGIS(com.vividsolutions.jts.geom.Geometry aJTSGeometry) {
            if (aJTSGeometry != null) {
                try {
                    return  new PGgeometry(org.postgis.PGgeometry.geomFromString(aJTSGeometry.toText()));
                } catch (SQLException ex) {
                    Logger.getLogger(PostgreConverter.class.getName()).log(Level.SEVERE, "Could not convert JTS to PostGIS geometry. Could not parse geometry wkt " + aJTSGeometry.toString(), ex);
                }
            }
            return null;
        }
    }

    public PostgreConverter() {
        super(new PostgreTypesResolver());
    }

    @Override
    public boolean isGeometry(DataTypeInfo aTypeInfo) {
        return super.isGeometry(aTypeInfo) || (aTypeInfo.getSqlType() == Types.OTHER && ((PostgreTypesResolver) resolver).isGeometryTypeName(aTypeInfo.getSqlTypeName().toLowerCase()));
    }
    /*
            if (isGeometry(aTypeInfo)) {
                if (aValue instanceof Geometry) {
                    PGgeometry geometry = JTSGeometryConverter.toPostGIS((Geometry)aValue);
                    aStmt.setObject(aParameterIndex, geometry);
                } else {
                    aStmt.setObject(aParameterIndex, aValue);
                }
            }
    */
    /*
            if (isGeometry(aTypeInfo)) {
                Object value = aRs.getObject(aColIndex);
                if (value != null) {
                    try {
                        if (value instanceof PGgeometry) {
                            return JTSGeometryConverter.fromPostGIS((PGgeometry) value);
                        } else {
                            return value;
                        }
                    } catch (Exception ex) {
                        throw new RowsetException(ex);
                    }
                }else{
                    return null;
                }
            }
    */
}
