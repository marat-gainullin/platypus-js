/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Data source JNDI resource. Only required attributes supported yet.
 *
 * @author vv
 */
public class DataSourceResource extends Resource {

    public static final String DATA_SOURCE_RESOURCE_TYPE_NAME = "javax.sql.DataSource";//NOI18N
    public static final String DRIVER_CLASS_NAME_ATTR_NAME = "driverClassName";//NOI18N
    public static final String URL_ATTR_NAME = "url";//NOI18N
    public static final String USER_NAME_ATTR_NAME = "username";//NOI18N
    public static final String PASSWORD_ATTR_NAME = "password";//NOI18N
    public static final String SCHEMA_ATTR_NAME = "schema";//NOI18N
    public static final String VALIDATION_QUERY_ATTR_NAME = "validationQuery";//NOI18N
    private static final String MSSQL_VALIDATION_QUERY = "select 1";
    private static final String MSSQL_DRIVER_PATTERN = "net.sourceforge.jtds.jdbc";

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String schema;

    /**
     * Gets the fully qualified Java class name of the JDBC driver to be used.
     *
     * @return JDBC driver name
     */
    public String getDriverClassName() {
        return driverClassName;
    }

    /**
     * Sets the fully qualified Java class name of the JDBC driver to be used.
     *
     * @param aValue JDBC driver name
     */
    public void setDriverClassName(String aValue) {
        driverClassName = aValue;
    }

    /**
     * Gets the database URL.
     *
     * @return database URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the database URL
     *
     * @param aValue database URL
     */
    public void setUrl(String aValue) {
        url = aValue;
    }

    /**
     * Gets the connection username to be passed to our JDBC driver to establish
     * a connection.
     *
     * @return connection username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the connection username to be passed to our JDBC driver to establish
     * a connection.
     *
     * @param aValue connection username
     */
    public void setUsername(String aValue) {
        username = aValue;
    }

    /**
     * Gets the connection password to be passed to our JDBC driver to establish
     * a connection.
     *
     * @return connection password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the connection password to be passed to our JDBC driver to establish
     * a connection.
     *
     * @param aValue connection password
     */
    public void setPassword(String aValue) {
        password = aValue;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String aValue) {
        if (aValue != null && aValue.isEmpty()) {
            aValue = null;
        }
        schema = aValue;
    }

    @Override
    public void load(Element tag) {
        super.load(tag);
        driverClassName = tag.getAttribute(DRIVER_CLASS_NAME_ATTR_NAME);
        url = tag.getAttribute(URL_ATTR_NAME);
        username = tag.getAttribute(USER_NAME_ATTR_NAME);
        password = tag.getAttribute(PASSWORD_ATTR_NAME);
        if (tag.hasAttribute(SCHEMA_ATTR_NAME)) {
            schema = tag.getAttribute(SCHEMA_ATTR_NAME);
        }
    }

    @Override
    public Element getElement(Document aDoc) {
        Element element = super.getElement(aDoc);
        if (driverClassName != null) {
            element.setAttribute(DRIVER_CLASS_NAME_ATTR_NAME, driverClassName);
            if(driverClassName.contains(MSSQL_DRIVER_PATTERN)){
                element.setAttribute(VALIDATION_QUERY_ATTR_NAME, MSSQL_VALIDATION_QUERY);
            }
        }
        if (url != null) {
            element.setAttribute(URL_ATTR_NAME, url);
        }
        if (username != null) {
            element.setAttribute(USER_NAME_ATTR_NAME, username);
        }
        if (password != null) {
            element.setAttribute(PASSWORD_ATTR_NAME, password);
        }
        if (schema != null) {
            element.setAttribute(SCHEMA_ATTR_NAME, schema);
        }
        return element;
    }

}
