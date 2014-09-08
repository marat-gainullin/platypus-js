/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.sqldrivers.converters;

import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.sqldrivers.resolvers.H2TypesResolver;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

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

    @Override
    public Object getFromJdbcAndConvert2RowsetCompatible(ResultSet aRs, int aColIndex, DataTypeInfo aTypeInfo) throws RowsetException {
        try {
            Object value = null;
            switch (aTypeInfo.getSqlType()) {
                // Some strange types. No one knows how to work with them.
                case Types.JAVA_OBJECT:
                case Types.DATALINK:
                case Types.DISTINCT:
                case Types.ROWID:
                    value = aRs.getRowId(aColIndex);
                    break;
                case Types.REF:
                    value = aRs.getRef(aColIndex);
                    break;
                case Types.SQLXML:
                    value = aRs.getSQLXML(aColIndex);
                    break;
                case Types.ARRAY:
                    value = aRs.getArray(aColIndex);
                    break;
                case Types.STRUCT:
                    value = null;
                    break;
                case Types.OTHER:
                    value = null;
                    break;
                case Types.NULL: // Let's return null on NULL type
                    value = null;
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                    value = aRs.getBytes(aColIndex);
                    break;
                case Types.BLOB:
                    value = aRs.getBlob(aColIndex);
                    break;
                // clobs
                case Types.CLOB:
                    value = aRs.getClob(aColIndex);
                    break;
                case Types.NCLOB:
                    value = aRs.getNClob(aColIndex);
                    break;
                // numbers
                case Types.DECIMAL:
                case Types.NUMERIC:
                    // target type - BigDecimal
                    value = aRs.getBigDecimal(aColIndex);
                    break;
                case Types.BIGINT:
                    // target type - BigInteger
                    value = aRs.getBigDecimal(aColIndex);
                    if (value != null) {
                        value = ((BigDecimal) value).toBigInteger();
                    }
                    break;
                case Types.SMALLINT:
                    // target type - Short
                    value = aRs.getShort(aColIndex);
                    break;
                case Types.TINYINT:
                case Types.INTEGER:
                    // target type - Int
                    value = aRs.getInt(aColIndex);
                    break;
                case Types.REAL:
                case Types.FLOAT:
                    // target type - Float
                    value = aRs.getFloat(aColIndex);
                    break;
                case Types.DOUBLE:
                    // target type - Double
                    value = aRs.getDouble(aColIndex);
                    break;
                // strings
                case Types.CHAR:
                case Types.NCHAR:
                case Types.VARCHAR:
                case Types.NVARCHAR:
                case Types.LONGVARCHAR:
                case Types.LONGNVARCHAR:
                    // target type - string
                    value = aRs.getString(aColIndex);
                    break;
                // booleans
                case Types.BOOLEAN:
                case Types.BIT:
                    // target type - Boolean
                    value = aRs.getBoolean(aColIndex);
                    break;
                // dates, times
                case Types.DATE:
                    value = aRs.getDate(aColIndex);
                    break;
                case Types.TIMESTAMP:
                    value = aRs.getTimestamp(aColIndex);
                    break;
                case Types.TIME:
                    value = aRs.getTime(aColIndex);
                    break;
            }
            if (aRs.wasNull()) {
                value = null;
            }
            return convert2RowsetCompatible(value, aTypeInfo);
        } catch (SQLException ex) {
            throw new RowsetException(ex);
        }
    }
}
