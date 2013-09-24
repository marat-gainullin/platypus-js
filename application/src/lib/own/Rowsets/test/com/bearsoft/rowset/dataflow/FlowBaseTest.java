/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.dataflow;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.resourcepool.BearCallableStatement;
import com.bearsoft.rowset.resourcepool.BearDatabaseConnection;
import com.bearsoft.rowset.resourcepool.BearPreparedStatement;
import com.bearsoft.rowset.resourcepool.BearResourcePool;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import javax.sql.DataSource;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class FlowBaseTest {

    @BeforeClass
    public static void registerDrivers() throws SQLException {
        try {
            Class oraDriverClass = Class.forName("oracle.jdbc.OracleDriver");
            DriverManager.registerDriver((Driver) oraDriverClass.newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            throw new SQLException(ex);
        }
    }
    protected static final String tableName = "access_list";
    protected static final String selectClause = "select * from " + tableName + " where KEY_WORD = ? or (KEY_WORD is null and ? is null)";
    protected static final String selectClause4Paging = "select * from " + tableName;
    protected static final String paramDataValue = "Passing";
    protected static final String paramNullValue = null;

    public static String makeTestConnectionDescription(Properties props) throws SQLException {
        String url = "jdbc:oracle:thin:@asvr:1521:adb";
        props.put("schema", "eas");
        props.put("user", "eas");
        props.put("password", "eas");
        return url;
    }

    @Test
    public void dummyTest() {
    }

    public class JdbcFlowProviderAdapter<JKT> extends JdbcFlowProvider<JKT> {

        protected List<Change> changeLog = new ArrayList<>();
        protected Set<TransactionListener> listeners = new HashSet<>();
        
        public JdbcFlowProviderAdapter(JKT aJdbcSourceTag, DataSource aDataSource, Converter aConverter, String aClause) {
            super(aJdbcSourceTag, null, aDataSource, aConverter, aClause, null);
        }

        @Override
        protected void prepareConnection(Connection aConnection) throws Exception {
        }

        @Override
        protected void unprepareConnection(Connection aConnection) throws Exception {
        }

        @Override
        public String getEntityId() {
            return "testEntity";
        }

        @Override
        public List<Change> getChangeLog() {
            return changeLog;
        }

        public void commit(Connection aConnection) throws Exception
        {
        }

        @Override
        public TransactionListener.Registration addTransactionListener(TransactionListener aListener) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    protected class ObservingResourcesProvider extends BearResourcePool<BearDatabaseConnection> implements DataSource {

        protected String url;
        protected Properties props;
        protected int connectionsAchieved = 0;
        protected int statementsAchieved = 0;
        protected int callsAchieved = 0;
        protected int connectionsReturned = 0;
        protected int statementsReturned = 0;
        protected int callsReturned = 0;
        protected int statementsCount = 0;
        protected int connectionsCount = 0;
        protected int callsCount = 0;

        public ObservingResourcesProvider(String aUrl, Properties aProps, int aConnectionsCount, int aStatementsCount, int aCallsCount) {
            super(BearResourcePool.DEFAULT_MAXIMUM_SIZE);
            url = aUrl;
            props = aProps;
            connectionsCount = aConnectionsCount;
            statementsCount = aStatementsCount;
            callsCount = aCallsCount;
        }

        public void testResources() {
            assertEquals(connectionsAchieved, connectionsCount);
            assertEquals(statementsAchieved, statementsCount);
            assertEquals(callsAchieved, callsCount);

            assertEquals(connectionsReturned, connectionsCount);
            assertEquals(statementsReturned, statementsCount);
            assertEquals(callsReturned, callsCount);

            assertEquals(connectionsAchieved, connectionsReturned);
            assertEquals(statementsAchieved, statementsReturned);
            assertEquals(callsAchieved, callsReturned);
        }

        @Override
        protected BearDatabaseConnection createResource() throws Exception {
            try {
                return new BearDatabaseConnection(BearResourcePool.DEFAULT_MAXIMUM_SIZE, DriverManager.getConnection(url, props), this) {

                    @Override
                    public void close() throws SQLException {
                        connectionsReturned++;
                        super.close();
                    }

                    @Override
                    protected BearCallableStatement wrapPrearedCall(String aSql, CallableStatement aCall) {
                        callsAchieved++;
                        return new BearCallableStatement(aSql, aCall, this) {

                            @Override
                            public void close() throws SQLException {
                                callsReturned++;
                                super.close();
                            }
                        };
                    }

                    @Override
                    protected BearPreparedStatement wrapPreparedStatement(String aSql, PreparedStatement aStatement) throws SQLException {
                        statementsAchieved++;
                        return new BearPreparedStatement(aSql, aStatement, this) {

                            @Override
                            public void close() throws SQLException {
                                statementsReturned++;
                                super.close();
                            }
                        };
                    }
                };
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }

        @Override
        public Connection getConnection() throws SQLException {
            try {
                connectionsAchieved++;
                return achieveResource();
            } catch (Exception ex) {
                throw new SQLException(ex);
            }
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return false;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
