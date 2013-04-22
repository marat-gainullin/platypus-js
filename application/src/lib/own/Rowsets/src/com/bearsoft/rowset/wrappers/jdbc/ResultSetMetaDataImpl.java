/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.wrappers.jdbc;

import com.bearsoft.rowset.compacts.CompactArray;
import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.compacts.CompactRef;
import com.bearsoft.rowset.compacts.CompactRowId;
import com.bearsoft.rowset.compacts.CompactSqlXml;
import com.bearsoft.rowset.compacts.CompactStruct;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Currency;

/**
 * A jdbc wrapper for class <code>Fields</code>.
 * Supports <code>ResultSetMetaData</code> interface.
 * @see Fields
 * @see ResultSetMetaData
 */
public class ResultSetMetaDataImpl extends Object implements ResultSetMetaData {

    protected Fields delegate = null;

    /**
     * Fields class jdbc wrapper constructor.
     * @param aDelegate Fields instance to be wrapped.
     * @see Fields
     * @see ResultSetMetaData
     */
    public ResultSetMetaDataImpl(Fields aDelegate) {
        super();
        delegate = aDelegate;
    }

    /**
     * @inheritDoc
     */
    public int getColumnCount() {
        return delegate.getFieldsCount();
    }

    /**
     * @inheritDoc
     */
    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    /**
     * @inheritDoc
     */
    public boolean isCaseSensitive(int column) throws SQLException {
        return false;
    }

    /**
     * @inheritDoc
     */
    public boolean isSearchable(int column) throws SQLException {
        return true;
    }

    /**
     * @inheritDoc
     */
    public boolean isCurrency(int column) throws SQLException {
        if (column >= 1 && column <= delegate.getFieldsCount()) {
            try {
                return Currency.class.isAssignableFrom(Class.forName(delegate.get(column).getTypeInfo().getJavaClassName()));
            } catch (ClassNotFoundException ex) {
                throw new SQLException(ex);
            }
        }
        return false;
    }

    /**
     * @inheritDoc
     */
    public int getColumnDisplaySize(int column) throws SQLException {
        if (column >= 1 && column <= delegate.getFieldsCount()) {
            return delegate.get(column).getSize();
        }
        return 0;
    }

    /**
     * @inheritDoc
     */
    public String getColumnLabel(int column) throws SQLException {
        if (column >= 1 && column <= delegate.getFieldsCount()) {
            return delegate.get(column).getName();
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    public String getColumnName(int column) throws SQLException {
        if (column >= 1 && column <= delegate.getFieldsCount()) {
            return delegate.get(column).getName();
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    public String getSchemaName(int column) throws SQLException {
        if (column >= 1 && column <= delegate.getFieldsCount()) {
            return delegate.get(column).getSchemaName();
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    public String getTableName(int column) throws SQLException {
        if (column >= 1 && column <= delegate.getFieldsCount()) {
            return delegate.get(column).getTableName();
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    public String getCatalogName(int column) throws SQLException {
        return null;
    }

    /**
     * @inheritDoc
     */
    public int getColumnType(int column) throws SQLException {
        if (column >= 1 && column <= delegate.getFieldsCount()) {
            return delegate.get(column).getTypeInfo().getSqlType();
        }
        return 0;
    }

    /**
     * @inheritDoc
     */
    public String getColumnTypeName(int column) throws SQLException {
        if (column >= 1 && column <= delegate.getFieldsCount()) {
            return delegate.get(column).getTypeInfo().getSqlTypeName();
        }
        return null;
    }

    public static String convertClassName(DataTypeInfo aTypeInfo) {
        String aClassName = aTypeInfo.getJavaClassName();
        if (aClassName.equals(CompactClob.class.getName())) {
            return java.sql.Clob.class.getName();
        } else if (aClassName.equals(CompactBlob.class.getName())) {
            return java.sql.Blob.class.getName();
        } else if (aClassName.equals(CompactRowId.class.getName())) {
            return java.sql.RowId.class.getName();
        } else if (aClassName.equals(CompactRef.class.getName())) {
            return java.sql.Ref.class.getName();
        } else if (aClassName.equals(CompactSqlXml.class.getName())) {
            return java.sql.SQLXML.class.getName();
        } else if (aClassName.equals(CompactArray.class.getName())) {
            return java.sql.Array.class.getName();
        } else if (aClassName.equals(CompactStruct.class.getName())) {
            return java.sql.Struct.class.getName();
        } else if (aClassName.equals(java.util.Date.class.getName())) {
            if (aTypeInfo.getSqlType() == java.sql.Types.DATE) {
                return java.sql.Date.class.getName();
            } else if (aTypeInfo.getSqlType() == java.sql.Types.TIMESTAMP) {
                return java.sql.Timestamp.class.getName();
            } else if (aTypeInfo.getSqlType() == java.sql.Types.TIME) {
                return java.sql.Time.class.getName();
            }
        }
        return aClassName;
    }

    /**
     * @inheritDoc
     */
    public String getColumnClassName(int column) throws SQLException {
        if (column >= 1 && column <= delegate.getFieldsCount()) {
            return convertClassName(delegate.get(column).getTypeInfo());
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    public boolean isReadOnly(int column) throws SQLException {
        if (column >= 1 && column <= delegate.getFieldsCount()) {
            return delegate.get(column).isReadonly();
        }
        return false;
    }

    /**
     * @inheritDoc
     */
    public boolean isWritable(int column) throws SQLException {
        if (column >= 1 && column <= delegate.getFieldsCount()) {
            return !delegate.get(column).isReadonly();
        }
        return false;
    }

    /**
     * @inheritDoc
     */
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return isWritable(column);
    }

    /**
     * @inheritDoc
     */
    public int isNullable(int index) {
        if (delegate != null && index > 0 && index <= delegate.getFieldsCount()) {
            Field field = delegate.get(index);
            if (field != null) {
                return field.isNullable() ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;
            }
        }
        return ResultSetMetaData.columnNullableUnknown;
    }

    /**
     * @inheritDoc
     */
    public boolean isSigned(int index) {
        if (delegate != null && index > 0 && index <= delegate.getFieldsCount()) {
            Field field = delegate.get(index);
            if (field != null) {
                return field.isSigned();
            }
        }
        return false;
    }

    /**
     * @inheritDoc
     */
    public int getPrecision(int index) {
        if (delegate != null && index > 0 && index <= delegate.getFieldsCount()) {
            Field field = delegate.get(index);
            if (field != null) {
                return field.getPrecision();
            }
        }
        return -1;
    }

    /**
     * @inheritDoc
     */
    public int getScale(int index) {
        if (delegate != null && index > 0 && index <= delegate.getFieldsCount()) {
            Field field = delegate.get(index);
            if (field != null) {
                return field.getScale();
            }
        }
        return -1;
    }

    /**
     * @inheritDoc
     */
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) delegate;
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(Fields.class);
    }
}
