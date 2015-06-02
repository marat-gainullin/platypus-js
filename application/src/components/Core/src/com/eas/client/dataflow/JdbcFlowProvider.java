/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dataflow;

import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.resourcepool.BearDatabaseConnection;
import com.eas.concurrent.CallableConsumer;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * This flow dataSource intended to support the flow process from and to jdbc
 * data sources.
 *
 * @author mg
 * @param <JKT> Jdbc source key type. It may be long number or string
 * identifier.
 * @see FlowProvider
 */
public abstract class JdbcFlowProvider<JKT> extends DatabaseFlowProvider<JKT> {

    protected static final Logger queriesLogger = Logger.getLogger(JdbcFlowProvider.class.getName());

    protected DataSource dataSource;
    protected Fields expectedFields;
    protected Consumer<Runnable> asyncDataPuller;
    protected boolean procedure;

    protected ResultSet lowLevelResults;
    protected Connection lowLevelConnection;
    protected PreparedStatement lowLevelStatement;

    /**
     * A flow dataSource, intended to support jdbc data sources.
     *
     * @param aJdbcSourceTag Jdbc source key value. It may be long number or
     * string identifier.
     * @param aDataSource A DataSource instance, that would supply resources for
     * use them by flow dataSource in single operations, like retriving data of
     * applying data changes.
     * @param aAsyncDataPuller
     * @param aClause A sql clause, dataSource should use to achieve
     * PreparedStatement instance to use it in the result set querying process.
     * @param aExpectedFields
     * @see DataSource
     */
    public JdbcFlowProvider(JKT aJdbcSourceTag, DataSource aDataSource, Consumer<Runnable> aAsyncDataPuller, String aClause, Fields aExpectedFields) {
        super(aJdbcSourceTag, aClause);
        dataSource = aDataSource;
        asyncDataPuller = aAsyncDataPuller;
        expectedFields = aExpectedFields;
        assert dataSource != null : "Flow provider can't exist without a data source";
        assert clause != null : "Flow provider can't exist without a selecting sql clause";
    }

    @Override
    public boolean isProcedure() {
        return procedure;
    }

    @Override
    public void setProcedure(boolean aValue) {
        procedure = aValue;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Collection<Map<String, Object>> nextPage(Consumer<Collection<Map<String, Object>>> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (!isPaged() || lowLevelResults == null) {
            throw new FlowProviderNotPagedException(BAD_NEXTPAGE_REFRESH_CHAIN_MSG);
        } else {
            JdbcReader reader = new JdbcReader(expectedFields);
            Callable<Collection<Map<String, Object>>> doWork = () -> {
                try {
                    return reader.readRowset(lowLevelResults, pageSize);
                } catch (SQLException ex) {
                    throw new FlowProviderFailedException(ex);
                } finally {
                    if (lowLevelResults.isAfterLast()) {
                        endPaging();
                    }
                }
            };
            if (onSuccess != null) {
                asyncDataPuller.accept(() -> {
                    try {
                        Collection<Map<String, Object>> rs = doWork.call();
                        try {
                            onSuccess.accept(rs);
                        } catch (Exception ex) {
                            Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (Exception ex) {
                        if (onFailure != null) {
                            onFailure.accept(ex);
                        }
                    }
                });
                return null;
            } else {
                return doWork.call();
            }
        }
    }

    private void endPaging() throws Exception {
        assert isPaged();
        close();
    }

    @Override
    public void close() throws Exception {
        if (lowLevelResults != null) {
            lowLevelResults.close();
            // See refresh method, hacky statement closing.
            if (lowLevelStatement != null) {
                lowLevelStatement.close();
            }
            // See refresh method, hacky connection closing.
            if (lowLevelConnection != null) {
                lowLevelConnection.close();
            }
            lowLevelResults = null;
            lowLevelStatement = null;
            lowLevelConnection = null;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public Collection<Map<String, Object>> refresh(Parameters aParams, Consumer<Collection<Map<String, Object>>> onSuccess, Consumer<Exception> onFailure) throws Exception {
        return select(aParams, (ResultSet rs) -> {
            if (rs != null) {
                JdbcReader reader = new JdbcReader(expectedFields);
                return reader.readRowset(rs, pageSize);
            } else {
                return new ArrayList<>();
            }
        }, onSuccess, onFailure);
    }

    public <T> T select(Parameters aParams, CallableConsumer<T, ResultSet> aResultSetProcessor, Consumer<T> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (lowLevelResults != null) {
            assert isPaged();
            // Let's abort paging process
            endPaging();
        }
        Callable<T> doWork = () -> {
            String sqlClause = clause;
            Connection connection = dataSource.getConnection();
            if (connection != null) {
                try {
                    PreparedStatement stmt = getFlowStatement(connection, sqlClause);
                    if (stmt != null) {
                        try {
                            prepareConnection(connection);
                            try {
                                for (int i = 1; i <= aParams.getParametersCount(); i++) {
                                    Parameter param = aParams.get(i);
                                    Object paramValue = param.getValue();
                                    if (paramValue != null) {
                                        Converter.convertAndAssign(paramValue, param.getTypeInfo(), null, i, stmt);
                                    } else {
                                        try {
                                            stmt.setNull(i, param.getTypeInfo().getSqlType());
                                        } catch (SQLException ex) {
                                            stmt.setNull(i, param.getTypeInfo().getSqlType(), param.getTypeInfo().getSqlTypeName());
                                        }
                                    }
                                    if (procedure && (param.getMode() == ParameterMetaData.parameterModeOut
                                            || param.getMode() == ParameterMetaData.parameterModeInOut)) {
                                        assert stmt instanceof CallableStatement;
                                        CallableStatement cStmt = (CallableStatement) stmt;
                                        cStmt.registerOutParameter(i, param.getTypeInfo().getSqlType());
                                    }
                                }

                                if (queriesLogger.isLoggable(Level.FINE)) {
                                    queriesLogger.log(Level.FINE, "Executing sql with {0} parameters:\n{1}", new Object[]{aParams.getParametersCount(), sqlClause});
                                }
                                ResultSet rs = null;
                                if (procedure) {
                                    assert stmt instanceof CallableStatement;
                                    CallableStatement cStmt = (CallableStatement) stmt;
                                    cStmt.execute();
                                    // let's return parameters
                                    for (int i = 1; i <= aParams.getParametersCount(); i++) {
                                        Parameter param = aParams.get(i);
                                        if (param.getMode() == ParameterMetaData.parameterModeOut
                                                || param.getMode() == ParameterMetaData.parameterModeInOut) {
                                            Object outedParamValue = Converter.get(cStmt, i, param.getTypeInfo());
                                            param.setValue(outedParamValue);
                                        }
                                    }
                                    // let's return a ResultSet
                                    rs = cStmt.getResultSet();
                                } else {
                                    rs = stmt.executeQuery();
                                }
                                if (rs != null) {
                                    try {
                                        return aResultSetProcessor.call(rs);
                                    } finally {
                                        if (isPaged()) {
                                            lowLevelResults = rs;
                                            lowLevelStatement = stmt;
                                            lowLevelConnection = connection;
                                        } else {
                                            rs.close();
                                        }
                                    }
                                } else {
                                    return aResultSetProcessor.call(null);
                                }
                            } catch (SQLException ex) {
                                try {
                                    connection.rollback();
                                    throw new FlowProviderFailedException(ex);
                                } catch (SQLException ex1) {
                                    throw new FlowProviderFailedException(ex);
                                }
                            } finally {
                                assert dataSource != null; // since we've got a statement, dataSource must present.
                                unprepareConnection(connection);
                            }
                        } finally {
                            stmt.close();
                        }
                    } else {
                        return null;
                    }
                } finally {
                    if (!isPaged()) {
                        // Paged connections can't be closed, because of ResultSet-s existance.
                        connection.close();
                    } else if (connection instanceof BearDatabaseConnection) {// Paged case
                        // We no, that BearDatabaseConnection pool's underlying native jdbc connection
                        // is never closed and so we know, that in the case we can safely close bear connection,
                        // returning the resource into the resource pool.
                        // Unfortunately, we can't do like this with J2EE pools.
                        // A little hacky, but it's needed by paging process in designer.
                        lowLevelStatement = null;
                        connection.close();
                        lowLevelConnection = null;
                    }
                }
            } else {
                return null;
            }
        };
        if (onSuccess != null) {
            asyncDataPuller.accept(() -> {
                try {
                    T processed = doWork.call();
                    try {
                        onSuccess.accept(processed);
                    } catch (Exception ex) {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (Exception ex) {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                }
            });
            return null;
        } else {
            return doWork.call();
        }
    }

    protected abstract void prepareConnection(Connection aConnection) throws Exception;

    protected abstract void unprepareConnection(Connection aConnection) throws Exception;

    /**
     * Returns PreparedStatement instance. Let's consider some caching system.
     * It will provide some prepared statement instance, according to passed sql
     * clause.
     *
     * @param aConnection java.sql.Connection instance to be used.
     * @param aClause Sql clause to process.
     * @return StatementResourceDescriptor instance, provided according to sql
     * clause.
     * @throws com.eas.client.dataflow.FlowProviderFailedException
     */
    protected PreparedStatement getFlowStatement(Connection aConnection, String aClause) throws FlowProviderFailedException {
        try {
            assert aConnection != null;
            if (procedure) {
                return aConnection.prepareCall(aClause);
            } else {
                return aConnection.prepareStatement(aClause);
            }
        } catch (Exception ex) {
            throw new FlowProviderFailedException(ex);
        }
    }
}
