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
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import java.sql.*;

/**
 *
 * @author mg
 */
public class PostgreConverter extends PlatypusConverter {

    protected WKBWriter wkbGeometryWriter = new WKBWriter();
    protected WKTReader wktGeometryReader = new WKTReader();
    protected WKBReader wkbGeometryReader = new WKBReader();

    public PostgreConverter()
    {
        super(new PostgreTypesResolver());
    }
    
    @Override
    public boolean isGeometry(DataTypeInfo aTypeInfo) {
        return aTypeInfo.getSqlType() == Types.OTHER && ((PostgreTypesResolver)resolver).isGeometryTypeName(aTypeInfo.getSqlTypeName().toLowerCase());
    }

    @Override
    public void convert2JdbcAndAssign(Object aValue, DataTypeInfo aTypeInfo, Connection aConn, int aParameterIndex, PreparedStatement aStmt) throws RowsetException {
        try {
            if (isGeometry(aTypeInfo)) {
                if (aValue instanceof Geometry) {
                    byte[] geomBytes = wkbGeometryWriter.write((Geometry) aValue);
                    aStmt.setBytes(aParameterIndex, geomBytes);
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
                String castedString = (String)oConverted;
                if (castedString != null) {
                    aStmt.setString(aParameterIndex, castedString);
                } else {
                    aStmt.setNull(aParameterIndex, Types.VARCHAR);
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
                        if (value instanceof String) {
                            return wktGeometryReader.read((String) value);
                        } else if (value instanceof byte[]) {
                            byte[] bValue = (byte[]) value;
                            return wkbGeometryReader.read(bValue);
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
