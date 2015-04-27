/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.converters;

import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.sqldrivers.resolvers.OracleTypesResolver;
import java.io.*;
import java.sql.*;

/**
 *
 * @author mg
 */
public class OracleConverter extends PlatypusConverter {

    public OracleConverter() {
        super(new OracleTypesResolver());
    }

    @Override
    public boolean isGeometry(DataTypeInfo aTypeInfo) {
        return super.isGeometry(aTypeInfo) || (aTypeInfo.getSqlType() == Types.STRUCT && ((OracleTypesResolver) resolver).isGeometryTypeName(aTypeInfo.getSqlTypeName().toUpperCase()));
    }
    /*
            if (isGeometry(aTypeInfo)) {
                Connection conn = aRs.getStatement().getConnection();
                assert conn instanceof OracleConnection || conn.unwrap(OracleConnection.class) instanceof OracleConnection;
                if (!(conn instanceof OracleConnection)) {
                    conn = conn.unwrap(OracleConnection.class);
                }
                Object value = aRs.getObject(aColIndex);
                if (value instanceof oracle.sql.STRUCT) {
                    final GeometryConverter geometryConverter = new GeometryConverter((OracleConnection) conn);
                    return geometryConverter.asGeometry((STRUCT) value);
                } else {
                    return super.getFromJdbcAndConvert2RowsetCompatible(aRs, aColIndex, aTypeInfo);
                }
            }
    */
    /*
            if (aValue instanceof Geometry) {
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
            }
    */

    public Clob createAndAssignClob(Connection conn, Clob sourceClob) throws SQLException {
        if (conn != null) {
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
                            throw new SQLException(ex);
                        }
                        return newClob;
                    }
                }
        }
        return null;
    }

    public Blob createAndAssignBlob(Connection conn, Blob sourceBlob) throws SQLException {
        if (conn != null) {
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
                            throw new SQLException(ex);
                        }
                        return newBlob;
                    }
                }
        }
        return null;
    }
}
