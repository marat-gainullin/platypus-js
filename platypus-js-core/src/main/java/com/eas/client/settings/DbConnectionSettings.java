package com.eas.client.settings;

import com.eas.client.resourcepool.BearResourcePool;
import java.util.Properties;

/**
 *
 * @author mg
 */
public class DbConnectionSettings extends ConnectionSettings {

    protected String user;
    protected String password = "";
    protected int maxConnections = BearResourcePool.DEFAULT_MAXIMUM_SIZE;
    protected int maxStatements = BearResourcePool.DEFAULT_MAXIMUM_SIZE * 5;
    protected String schema;
    protected Properties props = new Properties();

    public DbConnectionSettings() {
        super();
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String aValue) {
        schema = aValue;
    }

    public Properties getProperties() {
        return props;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int aMaxConnections) {
        maxConnections = aMaxConnections;
    }

    public int getMaxStatements() {
        return maxStatements;
    }

    public void setMaxStatements(int aMaxStatements) {
        maxStatements = aMaxStatements;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
