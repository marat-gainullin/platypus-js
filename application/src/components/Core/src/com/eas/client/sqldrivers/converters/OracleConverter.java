/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.converters;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.sqldrivers.resolvers.OracleTypesResolver;
import com.vividsolutions.jts.geom.Geometry;
import java.io.*;
import java.sql.*;
import oracle.jdbc.OracleConnection;
import oracle.sql.STRUCT;
import org.geotools.data.oracle.sdo.GeometryConverter;

/**
 *
 * @author mg
 */
public class OracleConverter extends PlatypusConverter {

    public OracleConverter() {
        super(new OracleTypesResolver());
    }

    @Override
    public void convert2JdbcAndAssign(Object aValue, DataTypeInfo aTypeInfo, Connection aConn, int aParameterIndex, PreparedStatement aStmt) throws RowsetException {
        if (aValue != null) {
            if (aTypeInfo.getSqlType() == Types.NCHAR
                    || aTypeInfo.getSqlType() == Types.CHAR
                    || aTypeInfo.getSqlType() == Types.NVARCHAR
                    || aTypeInfo.getSqlType() == Types.VARCHAR
                    || aTypeInfo.getSqlType() == Types.LONGVARCHAR // LONG
                    || aTypeInfo.getSqlType() == Types.LONGNVARCHAR) { // LONG
                try {
                    String value = (String) super.convert2JdbcCompatible(aValue, aTypeInfo);
                    aStmt.setString(aParameterIndex, value);
                } catch (SQLException ex) {
                    throw new RowsetException(ex);
                }
            } else if (aTypeInfo.getSqlType() == Types.CLOB
                    || aTypeInfo.getSqlType() == Types.NCLOB) {
                try {
                    Clob value = (Clob) super.convert2JdbcCompatible(aValue, aTypeInfo);
                    Clob newClob = createAndAssignClob(aStmt.getConnection(), (Clob) value);
                    if (newClob != null) {
                        aStmt.setClob(aParameterIndex, newClob);
                    }
                } catch (SQLException ex) {
                    throw new RowsetException(ex);
                }
            } else if (aTypeInfo.getSqlType() == Types.BLOB) {
                try {
                    Blob value = (Blob) super.convert2JdbcCompatible(aValue, aTypeInfo);
                    Blob newBlob = createAndAssignBlob(aStmt.getConnection(), (Blob) value);
                    if (newBlob != null) {
                        aStmt.setBlob(aParameterIndex, newBlob);
                    }
                } catch (SQLException ex) {
                    throw new RowsetException(ex);
                }
            } else if (aValue instanceof Geometry) {
                try {
                    Connection conn = aStmt.getConnection();
                    assert conn instanceof OracleConnection || conn.unwrap(OracleConnection.class) instanceof OracleConnection;
                    if (!(conn instanceof OracleConnection)) {
                        conn = conn.unwrap(OracleConnection.class);
                    }
                    final GeometryConverter geometryConverter = new GeometryConverter((OracleConnection) conn);
                    STRUCT struct = geometryConverter.toSDO((Geometry) aValue);
                    aStmt.setObject(aParameterIndex, struct);
                } catch (SQLException ex) {
                    throw new RowsetException(ex);
                }
            } else if (aTypeInfo.getSqlType() == Types.VARBINARY) { // RAW
                try {
                    if (aValue instanceof byte[]) {
                        aStmt.setBytes(aParameterIndex, (byte[]) aValue);
                    } else if (aValue instanceof CompactBlob) {
                        aStmt.setBytes(aParameterIndex, ((CompactBlob) aValue).getData());
                    }
                    /*
                     if (aValue instanceof byte[]) {
                     oracle.sql.RAW raw = new oracle.sql.RAW();
                     raw.setBytes((byte[]) aValue);
                     aStmt.setObject(aParameterIndex, raw);
                     } else if (aValue instanceof CompactBlob) {
                     oracle.sql.RAW raw = new oracle.sql.RAW();
                     raw.setBytes(((CompactBlob) aValue).getData());
                     aStmt.setObject(aParameterIndex, raw);
                     }
                     */
                } catch (SQLException ex) {
                    throw new RowsetException(ex);
                }
            } else if (aTypeInfo.getSqlType() == Types.LONGVARBINARY) { // LONG RAW
                try {
                    if (aValue instanceof byte[]) {
                        aStmt.setBytes(aParameterIndex, (byte[]) aValue);
                    } else if (aValue instanceof CompactBlob) {
                        aStmt.setBytes(aParameterIndex, ((CompactBlob) aValue).getData());
                    }
                } catch (SQLException ex) {
                    throw new RowsetException(ex);
                }
            } else { // general types
                super.convert2JdbcAndAssign(aValue, aTypeInfo, aConn, aParameterIndex, aStmt);
            }
        } else { // null
            super.convert2JdbcAndAssign(aValue, aTypeInfo, aConn, aParameterIndex, aStmt);
        }
    }

    @Override
    public Object getFromJdbcAndConvert2RowsetCompatible(ResultSet aRs, int aColIndex, DataTypeInfo aTypeInfo) throws RowsetException {
        try {
            Connection conn = aRs.getStatement().getConnection();
            assert conn instanceof OracleConnection || conn.unwrap(OracleConnection.class) instanceof OracleConnection;
            if (!(conn instanceof OracleConnection)) {
                conn = conn.unwrap(OracleConnection.class);
            }
            if (isGeometry(aTypeInfo)) {
                Object value = aRs.getObject(aColIndex);
                if (value instanceof oracle.sql.STRUCT) {
                    final GeometryConverter geometryConverter = new GeometryConverter((OracleConnection) conn);
                    return geometryConverter.asGeometry((STRUCT) value);
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

    @Override
    public boolean isGeometry(DataTypeInfo aTypeInfo) {
        return super.isGeometry(aTypeInfo) || (aTypeInfo.getSqlType() == Types.STRUCT && ((OracleTypesResolver) resolver).isGeometryTypeName(aTypeInfo.getSqlTypeName().toUpperCase()));
    }

    public Clob createAndAssignClob(Connection conn, Clob sourceClob) throws RowsetException {
        if (conn != null) {
            try {
                try (PreparedStatement lstmt = conn.prepareStatement("select to_clob(' ') as newClob from dual", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = lstmt.executeQuery()) {
                    if (rs.next()) {
                        Clob newClob = rs.getClob(1);
                        Reader clientReader = sourceClob.getCharacterStream();
                        Writer serverWriter = newClob.setCharacterStream(1);
                        try {
                            char[] buffer = new char[1024 * 8]; // 16Kb, NB: unicode!
                            int read = clientReader.read(buffer, 0, buffer.length);
                            while (read != -1) {
                                serverWriter.write(buffer, 0, read);
                                read = clientReader.read(buffer, 0, buffer.length);
                            }
                            serverWriter.close();
                            clientReader.close();
                        } catch (IOException ex) {
                            throw new RowsetException(ex);
                        }
                        return newClob;
                    }
                }
            } catch (SQLException ex) {
                throw new RowsetException(ex);
            }
        }
        return null;
    }

    public Blob createAndAssignBlob(Connection conn, Blob sourceBlob) throws RowsetException {
        if (conn != null) {
            try {
                try (PreparedStatement lstmt = conn.prepareStatement("select to_blob('0000') as newBlob from dual", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = lstmt.executeQuery()) {
                    if (rs.next()) {
                        Blob newBlob = rs.getBlob(1);
                        InputStream clientInput = sourceBlob.getBinaryStream();
                        OutputStream serverOutput = newBlob.setBinaryStream(1);
                        try {
                            try {
                                byte[] buffer = new byte[1024 * 16]; // 16Kb
                                int read = clientInput.read(buffer, 0, buffer.length);
                                while (read != -1) {
                                    serverOutput.write(buffer, 0, read);
                                    read = clientInput.read(buffer, 0, buffer.length);
                                }
                            } finally {
                                serverOutput.close();
                                clientInput.close();
                            }
                        } catch (IOException ex) {
                            throw new RowsetException(ex);
                        }
                        return newBlob;
                    }
                }
            } catch (SQLException ex) {
                throw new RowsetException(ex);
            }
        }
        return null;
    }
}
