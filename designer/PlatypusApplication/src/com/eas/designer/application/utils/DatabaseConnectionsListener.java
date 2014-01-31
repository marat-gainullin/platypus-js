/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.utils;

import org.netbeans.api.db.explorer.DatabaseConnection;

/**
 * The listener interface for receiving events about NetBeans database connections. 
 * @author vv
 */
public interface DatabaseConnectionsListener {

    void connected(DatabaseConnection aConnection);

    void disconnected(DatabaseConnection aConnection);
}
