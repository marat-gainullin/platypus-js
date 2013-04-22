/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.wrappers.jdbc;

import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import java.sql.ParameterMetaData;
import java.sql.SQLException;

/**
 * A jdbc wrapper for <code>Parameters</code> class.
 * Supports <code>ParameterMetaData</code> interface.
 * @see Parameters
 * @see ParameterMetaData
 */
public class ParameterMetaDataImpl extends Object implements ParameterMetaData {

    protected Parameters delegate = null;

    /**
     * Constructor of the Parameters jdbc wrapper.
     * @param aDelegate Parameters instance to be wrapped.
     * @see Parameters
     * @see ParameterMetaData
     */
    public ParameterMetaDataImpl(Parameters aDelegate)
    {
        super();
        delegate = aDelegate;
    }

    /**
     * @inheritDoc
     */
    public int getParameterType(int index) throws SQLException {
        Field field = delegate.get(index);
        if (field != null) {
            return field.getTypeInfo().getSqlType();
        } else {
            throw new SQLException("bad col index" + String.valueOf(index));
        }
    }

    /**
     * @inheritDoc
     */
    public String getParameterTypeName(int index) throws SQLException {
        Field field = delegate.get(index);
        if (field != null) {
            return field.getTypeInfo().getSqlTypeName();
        } else {
            throw new SQLException("bad col index" + String.valueOf(index));
        }
    }

    /**
     * @inheritDoc
     */
    public String getParameterClassName(int index) throws SQLException {
        Field field = delegate.get(index);
        if (field != null) {
            if (field instanceof Parameter) {
                return ResultSetMetaDataImpl.convertClassName(((Parameter) field).getTypeInfo());
            } else {
                throw new SQLException("bad internal field found. It is field instead of parameter at index: " + String.valueOf(index));
            }
        } else {
            throw new SQLException("bad col index" + String.valueOf(index));
        }
    }

    /**
     * @inheritDoc
     */
    public int getParameterMode(int index) throws SQLException {
        Field field = delegate.get(index);
        if (field != null) {
            if (field instanceof Parameter) {
                return ParameterMetaData.parameterModeIn;//((Parameter) field).getMode();
            } else {
                throw new SQLException("bad internal field found. It is field instead of parameter at index: " + String.valueOf(index));
            }
        } else {
            throw new SQLException("bad col index" + String.valueOf(index));
        }
    }

    public int getParameterCount() throws SQLException {
        return delegate.getParametersCount();
    }

    /**
     * @inheritDoc
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if(isWrapperFor(iface))
            return (T) delegate;
        return null;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(Parameters.class);
    }

    /**
     * @inheritDoc
     */
    public int isNullable(int index) {
        if (delegate != null && index >= 1 && index <= delegate.getFieldsCount()) {
            Field field = delegate.get(index);
            if (field != null) {
                return field.isNullable() ? ParameterMetaData.parameterNullable : ParameterMetaData.parameterNoNulls;
            }
        }
        return ParameterMetaData.parameterNullableUnknown;
    }

    /**
     * @inheritDoc
     */
    public boolean isSigned(int index) {
        if (delegate != null && index >= 1 && index <= delegate.getFieldsCount()) {
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
        if (delegate != null && index >= 1 && index <= delegate.getFieldsCount()) {
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
        if (delegate != null && index >= 1 && index <= delegate.getFieldsCount()) {
            Field field = delegate.get(index);
            if (field != null) {
                return field.getScale();
            }
        }
        return -1;
    }
}