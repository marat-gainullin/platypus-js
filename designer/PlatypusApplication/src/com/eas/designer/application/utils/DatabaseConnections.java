/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.utils;

import com.eas.client.resourcepool.BearDatabaseConnection;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.resourcepool.PlatypusNativeDataSource;
import com.eas.util.ListenerRegistration;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.openide.util.RequestProcessor;

/**
 * Watches for NetBeans <code>DatabaseConnection</code> connections states.
 *
 * @author vv
 */
public class DatabaseConnections {

    protected static class InstanceHolder {

        public static final DatabaseConnections instance = init();
    }

    private static final int DELAY = 1000;

    private static final RequestProcessor RP = new RequestProcessor(DatabaseConnections.class);
    private final Set<DatabaseConnectionsListener> listeners = new CopyOnWriteArraySet<>();
    private final Set<String> connected = new HashSet<>();

    public static DatabaseConnections getDefault() throws Exception {
        return InstanceHolder.instance;
    }

    private static DatabaseConnections init() {
        DatabaseConnections connections = new DatabaseConnections();
        RP.scheduleWithFixedDelay(() -> {
            try {
                connections.watch();
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }, DELAY, DELAY, TimeUnit.MILLISECONDS);
        return connections;
    }

    private DatabaseConnections() {
    }

    public ListenerRegistration addListener(final DatabaseConnectionsListener aListener) {
        listeners.add(aListener);
        return () -> {
            listeners.remove(aListener);
        };
    }

    private void watch() throws Exception {
        for (DatabaseConnection conn : ConnectionManager.getDefault().getConnections()) {
            Connection jdbcConn = conn.getJDBCConnection(false);
            if (jdbcConn == null) {
                if (connected.contains(conn.getDisplayName())) {
                    connected.remove(conn.getDisplayName());
                    GeneralResourceProvider.getInstance().unregisterDatasource(conn.getDisplayName());
                    listeners.stream().forEach((listener) -> {
                        listener.disconnected(conn);
                    });
                }
            } else {
                if (!connected.contains(conn.getDisplayName())) {
                    connected.add(conn.getDisplayName());
                    GeneralResourceProvider.getInstance().registerDatasource(conn.getDisplayName(), new PlatypusNativeDataSource(1, 25, conn.getDatabaseURL(), conn.getUser(), conn.getPassword(), conn.getSchema(), conn.getConnectionProperties()) {

                        @Override
                        public Connection getConnection() throws SQLException {
                            return new BearDatabaseConnection(25, conn.getJDBCConnection(), this) {

                                @Override
                                protected void shutdownDelegate() throws SQLException {
                                    // no op
                                }

                            };
                        }

                    });
                    listeners.stream().forEach((listener) -> {
                        listener.connected(conn);
                    });
                }
            }
        }
    }

    public static DatabaseConnection lookup(String aDisplayName) {
        for (DatabaseConnection conn : ConnectionManager.getDefault().getConnections()) {
            if (aDisplayName == null ? conn.getDisplayName() == null : aDisplayName.equals(conn.getDisplayName())) {
                return conn;
            }
        }
        return null;
    }
}
