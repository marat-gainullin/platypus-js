/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.wrappers.jdbc;

import com.bearsoft.rowset.compacts.CompactArray;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * JDBC warapper of <code>CompactArray</code> implementing <code>Array</code> imterface
 * @author mg
 * @see Array
 */
public class ArrayImpl implements Array{

    protected CompactArray delegate;
    public ArrayImpl(CompactArray aArray)
    {
        super();
        delegate = aArray;
    }

    public String getBaseTypeName() throws SQLException {
        return delegate.getBaseTypeName();
    }

    public int getBaseType() throws SQLException {
        return delegate.getBaseType();
    }

    public Object getArray() throws SQLException {
        return delegate.getArray();
    }

    public Object getArray(Map<String, Class<?>> map) throws SQLException {
        return delegate.getArray();
    }

    public Object getArray(long index, int count) throws SQLException {
        return delegate.getArray(index, count);
    }

    public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return delegate.getArray(index, count);
    }

    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    public ResultSet getResultSet(long index, int count) throws SQLException {
        return null;
    }

    public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
        return null;
    }

    public void free() throws SQLException {
    }
}