/* Datamodel license
 * Exclusive rights on this code in any form
 * are belong to it's athor.
 * This code was developed for commercial purposes only
 * For any questions and any actions with this code in any form
 * you have to contact it's athor.
 * All rights reserved
 */
package com.eas.client.settings;

import com.eas.client.resourcepool.BearResourcePool;
import java.util.Properties;

/**
 *
 * @author mg
 */
public class DbConnectionSettings extends ConnectionSettings {

    protected String user;
    protected String password;
    protected int maxConnections = BearResourcePool.DEFAULT_MAXIMUM_SIZE;
    protected int maxStatements = BearResourcePool.DEFAULT_MAXIMUM_SIZE * 5;
    protected String schema;
    protected Properties props = new Properties();

    public DbConnectionSettings() {
        super();
    }

    public DbConnectionSettings(String anUrl, String anUser, String aPassword) throws Exception {
        this(anUrl, anUser, aPassword, null, null);
    }

    public DbConnectionSettings(String anUrl, String anUser, String aPassword, String aSchema) throws Exception {
        this(anUrl, anUser, aPassword, aSchema, null);
    }

    public DbConnectionSettings(String anUrl, String anUser, String aPassword, String aSchema, int aMaxConnections) throws Exception {
        this(anUrl, anUser, aPassword, aSchema);
        maxConnections = aMaxConnections;
    }

    public DbConnectionSettings(String anUrl, String anUser, String aPassword, String aSchema, int aMaxConnections, int aMaxStatements) throws Exception {
        this(anUrl, anUser, aPassword, aSchema, aMaxConnections);
        maxStatements = aMaxStatements;
    }
    
    public DbConnectionSettings(String anUrl, String anUser, String aPassword, String aSchema, Properties aProperties) throws Exception {
        this();
        url = anUrl;
        user = anUser;
        password = aPassword;
        schema = aSchema;
        if (aProperties != null) {
            props.putAll(aProperties);
        }
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
