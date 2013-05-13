/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

/**
 * Data source JNDI resource.
 * @author vv
 */
public class DataSourceResource extends Resource {
    public static final String DATA_SOURCE_RESOURCE_TYPE_NAME = "javax.sql.DataSource";//NOI18N
    private String driverClassName;
}
