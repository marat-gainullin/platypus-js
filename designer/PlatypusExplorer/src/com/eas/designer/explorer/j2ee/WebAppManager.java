/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee;

/**
 * Sets up an web application do be deployed on specific target server.
 *
 * @author vv
 */
public interface WebAppManager {

    /**
     * Deploys JDBC drivers to specific server.
     *
     * Use this method to perform simplified JDBC drivers deployment.
     */
    void deployJdbcDrivers();

    /**
     * Perform the configuration process.
     */
    void configure();
}
