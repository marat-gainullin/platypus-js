/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.resourcepool;

import com.bearsoft.rowset.resourcepool.BearDatabaseConnection;
import com.bearsoft.rowset.resourcepool.BearResourcePool;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author mg
 */
public class PlatypusNativeDataSource extends BearResourcePool<BearDatabaseConnection> implements DataSource {

    protected String url;
    // connection properties, including user name and password
    protected Properties props = new Properties();
    protected int maxStatements = DEFAULT_MAXIMUM_SIZE;
    protected PrintWriter printWriter;
    protected int loginTimeout;

    public PlatypusNativeDataSource(int aMaxConnections, int aMaxStatements, int aResourceTimeout, String aUrl, String aUser, String aPassword, String aSchema, Properties aProperties) throws Exception {
        super(aMaxConnections, aResourceTimeout);
        url = aUrl;
        maxStatements = aMaxStatements;
        resourceTimeout = aResourceTimeout;
        if (aProperties != null) {
            props.putAll(aProperties);
        }
        props.put("user", aUser);
        props.put("password", aPassword);
        if (aSchema != null) {
            props.put("schema", aSchema);
        }
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Not supported yet.");
    }

    @Override
    protected BearDatabaseConnection createResource() throws Exception {
        return new BearDatabaseConnection(maxStatements, resourceTimeout, DriverManager.getConnection(url, props), this);
    }

    @Override
    public BearDatabaseConnection getConnection() throws SQLException {
        try {
            return achieveResource();
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return printWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        printWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        loginTimeout = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    void shutdown() throws SQLException {
        for (BearDatabaseConnection conn : resources) {
            conn.shutdown();
        }
        resources.clear();
    }

}
