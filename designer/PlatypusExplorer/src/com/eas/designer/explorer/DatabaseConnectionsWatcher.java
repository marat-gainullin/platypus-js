/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer;

import com.eas.util.ListenerRegistration;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.util.RequestProcessor;

/**
 * Watches for NetBeans <code>DatabaseConnection</code> connections states. 
 * @author vv
 */
public class DatabaseConnectionsWatcher {

    private static final int DELAY = 1000;

    private static DatabaseConnectionsWatcher connectionsWatcher;
    private static final RequestProcessor RP = new RequestProcessor(DatabaseConnectionsWatcher.class);
    private static RequestProcessor.Task watchTask;
    private final Set<DatabaseConnectionsListener> listeners = new HashSet<>();
    private final Set<String> connected = new HashSet<>();

    private DatabaseConnectionsWatcher() {
    }

    public static synchronized DatabaseConnectionsWatcher getDefault() {
        if (connectionsWatcher == null) {
            connectionsWatcher = new DatabaseConnectionsWatcher();
            startWatch();
        }
        return connectionsWatcher;
    }

    public synchronized ListenerRegistration addListener(final DatabaseConnectionsListener aListener) {
        listeners.add(aListener);
        return new ListenerRegistration() {

            @Override
            public void remove() {
                synchronized (DatabaseConnectionsWatcher.this) {
                    listeners.remove(aListener);
                }
            }
        };
    }

    private static void startWatch() {
        watchTask = RP.create(new Runnable() {

            @Override
            public void run() {
                assert connectionsWatcher != null;
                connectionsWatcher.watch();
                watchTask.schedule(DELAY);
            }
        });
        watchTask.schedule(DELAY);
    }

    private void watch() {
        for (DatabaseConnection conn : ConnectionManager.getDefault().getConnections()) {
            Connection jdbcConn = conn.getJDBCConnection(false);
            if (jdbcConn == null) {
                if (wasConnected(conn)) {
                    for (DatabaseConnectionsListener listener : listeners) {
                        listener.disconnected(conn);
                    }
                }
                removeFromConnected(conn);
            } else {
                if (!wasConnected(conn)) {
                    for (DatabaseConnectionsListener listener : listeners) {
                        listener.connected(conn);
                    }
                }
                addToConnected(conn);
            }
        }
    }

    private boolean wasConnected(DatabaseConnection conn) {
        return connected.contains(conn.getName());
    }

    private void addToConnected(DatabaseConnection conn) {
        connected.add(conn.getName());
    }

    private void removeFromConnected(DatabaseConnection conn) {
        connected.remove(conn.getName());
    }
}
