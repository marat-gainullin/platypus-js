/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.converters;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.sqldrivers.resolvers.PostgreTypesResolver;
import com.vividsolutions.jts.geom.Geometry;
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

    @Override
    public void convert2JdbcAndAssign(Object aValue, DataTypeInfo aTypeInfo, Connection aConn, int aParameterIndex, PreparedStatement aStmt) throws RowsetException {
        try {
            if (isGeometry(aTypeInfo)) {
                if (aValue instanceof Geometry) {
                    PGgeometry geometry = JTSGeometryConverter.toPostGIS((Geometry)aValue);
                    aStmt.setObject(aParameterIndex, geometry);
                } else {
                    aStmt.setObject(aParameterIndex, aValue);
                }
            } else if (aTypeInfo.getSqlType() == Types.LONGNVARCHAR
                    || aTypeInfo.getSqlType() == Types.LONGVARCHAR
                    || aTypeInfo.getSqlType() == Types.CLOB) {
                String casted = null;
                if (aValue instanceof String) {
                    casted = (String) aValue;
                } else if (aValue instanceof CompactClob) {
                    casted = ((CompactClob) aValue).getData();
                }
                if (casted == null) {
                    aStmt.setNull(aParameterIndex, Types.LONGVARCHAR);
                    checkDataLoss(aValue);
                } else {
                    aStmt.setString(aParameterIndex, casted);
                }
            } else if (aTypeInfo.getSqlType() == Types.LONGVARBINARY
                    || aTypeInfo.getSqlType() == Types.VARBINARY
                    || aTypeInfo.getSqlType() == Types.BINARY
                    || aTypeInfo.getSqlType() == Types.BLOB) {
                byte[] casted = null;
                if (aValue instanceof byte[]) {
                    casted = (byte[]) aValue;
                } else if (aValue instanceof CompactBlob) {
                    casted = ((CompactBlob) aValue).getData();
                }
                if (casted == null) {
                    aStmt.setNull(aParameterIndex, Types.BINARY);
                    checkDataLoss(aValue);
                } else {
                    aStmt.setBytes(aParameterIndex, casted);
                }
            } else if (aTypeInfo.getSqlType() == Types.CHAR
                    || aTypeInfo.getSqlType() == Types.VARCHAR
                    || aTypeInfo.getSqlType() == Types.LONGVARCHAR
                    || aTypeInfo.getSqlType() == Types.NCHAR
                    || aTypeInfo.getSqlType() == Types.NVARCHAR
                    || aTypeInfo.getSqlType() == Types.LONGNVARCHAR) {
                // target type - string
                Object oConverted = super.convert2JdbcCompatible(aValue, aTypeInfo);
                String castedString = (String) oConverted;
                if (castedString != null) {
                    aStmt.setString(aParameterIndex, castedString);
                } else {
                    aStmt.setNull(aParameterIndex, Types.VARCHAR);
                    checkDataLoss(aValue);
                }
            } else {
                super.convert2JdbcAndAssign(aValue, aTypeInfo, aConn, aParameterIndex, aStmt);
            }
        } catch (SQLException ex) {
            throw new RowsetException(ex);
        }
    }

    @Override
    public Object getFromJdbcAndConvert2RowsetCompatible(ResultSet aRs, int aColIndex, DataTypeInfo aTypeInfo) throws RowsetException {
        try {
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
                } else {
                    return super.getFromJdbcAndConvert2RowsetCompatible(aRs, aColIndex, aTypeInfo);
                }
            } else {
                return super.getFromJdbcAndConvert2RowsetCompatible(aRs, aColIndex, aTypeInfo);
            }
        } catch (SQLException ex) {
            throw new RowsetException(ex);
        }
    }
}
