/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.utils;

import java.util.HashSet;
import java.util.Set;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;

/**
 *
 * @author mg
 */
public class DatabaseConnectionListModel implements ListModel<DatabaseConnection> {

    protected Set<ListDataListener> listeners = new HashSet<>();
    
    @Override
    public int getSize() {
        DatabaseConnection[] connections = ConnectionManager.getDefault().getConnections();
        return connections != null ? connections.length : 0;
    }

    @Override
    public DatabaseConnection getElementAt(int index) {
        DatabaseConnection[] connections = ConnectionManager.getDefault().getConnections();
        return connections != null && index >= 0 && index < connections.length ? connections[index] : null;
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

}
