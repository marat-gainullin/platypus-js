/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.wrappers.jdbc;

import com.bearsoft.rowset.compacts.CompactRef;
import java.sql.Ref;
import java.sql.SQLException;
import java.util.Map;

/**
 * JDBC warapper of <code>CompactRef</code> implementing <code>Ref</code> imterface
 * @author mg
 * @see Ref
 */
public class RefImpl implements Ref{

    protected CompactRef delegate;

    /**
     * JDBC Wrapper Constructor
     * @param aRef
     */
    public RefImpl(CompactRef aRef)
    {
        super();
        delegate = aRef;
    }

    public String getBaseTypeName() throws SQLException {
        return delegate.getBaseTypeName();
    }

    public Object getObject(Map<String, Class<?>> map) throws SQLException {
        return delegate.getObject();
    }

    public Object getObject() throws SQLException {
        return delegate.getObject();
    }

    public void setObject(Object value) throws SQLException {
        delegate.setObject(value);
    }
}
