/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.dataflow;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.FlowProviderFailedException;
import com.bearsoft.rowset.exceptions.FlowProviderNotPagedException;
import com.bearsoft.rowset.jdbc.JdbcReader;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.resourcepool.BearDatabaseConnection;
import com.bearsoft.rowset.utils.RowsetUtils;
import java.sql.*;
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
    protected Converter converter;
    protected Fields expectedFields;
    protected ResultSet lowLevelResults = null;
    protected Connection lowLevelConnection = null;
    protected PreparedStatement lowLevelStatement = null;
    protected boolean procedure = false;

    /**
     * A flow dataSource, intended to support jdbc data sources.
     *
     * @param aJdbcSourceTag Jdbc source key value. It may be long number or
     * string identifier.
     * @param aDataSource A DataSource instance, that would supply resources for
     * use them by flow dataSource in single operations, like retriving data of
     * applying data changes.
     * @param aClause A sql clause, dataSource should use to achieve
     * PreparedStatement instance to use it in the result set querying process.
     * @param aExpectedFields
     * @param aConverter A converter to be used while reading from and write to
     * a jdbc datasource.
     * @see DataSource
     */
    public JdbcFlowProvider(JKT aJdbcSourceTag, DataSource aDataSource, Converter aConverter, String aClause, Fields aExpectedFields) {
        super(aJdbcSourceTag, aClause);
        dataSource = aDataSource;
        converter = aConverter;
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
    public Rowset nextPage() throws Exception {
        if (!isPaged() || lowLevelResults == null) {
            throw new FlowProviderNotPagedException(BAD_NEXTPAGE_REFRESH_CHAIN_MSG);
        } else if (converter != null) {
            try {
                try {
                    JdbcReader reader = new JdbcReader(converter, expectedFields);
                    return reader.readRowset(lowLevelResults, pageSize);
                } finally {
                    if (lowLevelResults.isAfterLast()) {
                        endPaging();
                    }
                }
            } catch (SQLException ex) {
                throw new FlowProviderFailedException(ex);
            }
        } else {
            throw new FlowProviderFailedException(CONVERTER_MISSING_MSG);
        }
    }

    private void endPaging() throws SQLException {
        assert isPaged();
        assert lowLevelResults != null;
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

    protected boolean isUndefinedInParameters(Parameters aParams) {
        for (Field param : aParams.toCollection()) {
            assert param instanceof Parameter;
            if (((Parameter) param).getValue() == RowsetUtils.UNDEFINED_SQL_VALUE) {
                return true;
            }
        }
        return false;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Rowset refresh(Parameters aParams) throws Exception {
        if (lowLevelResults != null) {
            assert isPaged();
            // Let's abort paging process
            endPaging();
            //throw new FlowProviderPagedException(BAD_REFRESH_NEXTPAGE_CHAIN_MSG);
        }
        if (converter != null) {
            String sqlClause = clause;
            if (isUndefinedInParameters(aParams)) {
                sqlClause = RowsetUtils.makeQueryMetadataQuery(sqlClause);
            }
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
                                    if (paramValue == RowsetUtils.UNDEFINED_SQL_VALUE) {
                                        paramValue = null;
                                    }
                                    if (converter != null) {
                                        if (paramValue != null) {
                                            converter.convert2JdbcAndAssign(paramValue, param.getTypeInfo(), null, i, stmt);
                                        } else {
                                            try {
                                                stmt.setNull(i, param.getTypeInfo().getSqlType());
                                            } catch (SQLException ex) {
                                                stmt.setNull(i, param.getTypeInfo().getSqlType(), param.getTypeInfo().getSqlTypeName());
                                            }
                                        }
                                    } else {
                                        stmt.setObject(i, paramValue, param.getTypeInfo().getSqlType());
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
                                            Object outedParamValue = converter.convert2RowsetCompatible(cStmt.getObject(i), param.getTypeInfo());
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
                                        JdbcReader reader = new JdbcReader(converter, expectedFields);
                                        return reader.readRowset(rs, pageSize);
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
                                    return new Rowset(new Fields());
                                }
                            } catch (SQLException ex) {
                                try {
                                    connection.rollback();
                                    throw new FlowProviderFailedException(ex);
                                } catch (SQLException ex1) {
                                    throw new FlowProviderFailedException(ex);
                                }
                            } finally {
                                assert dataSource != null; // since we've aquired a statement, dataSource must present.
                                unprepareConnection(connection);
                            }
                        } finally {
                            stmt.close();
                        }
                    }
                } finally {
                    if (!isPaged()) {
                        // Paged connections can't be closed, because of ResultSet-s existance.
                        connection.close();
                    } else if (connection instanceof BearDatabaseConnection) {// Paged case
                        // We no, that Bear database connection pool's underlying native jdbc connection
                        // is never closed and so we know, that in the case we can safely close bear connection,
                        // returning the resource into the resource pool.
                        // Unfortunately, we can't do like this with J2EE pools.
                        // A little hacky, but it's needed by paging process in designers.
                        lowLevelStatement = null;
                        connection.close();
                        lowLevelConnection = null;
                    }
                }
            }
            return null;
        } else {
            throw new FlowProviderFailedException(CONVERTER_MISSING_MSG);
        }
    }

    protected abstract void prepareConnection(Connection aConnection) throws Exception;

    protected abstract void unprepareConnection(Connection aConnection) throws Exception;

    /**
     * Returns converter instance, used by this flow provider.
     *
     * @return Converter instance, used by this flow provider.
     * @see Converter
     */
    public Converter getConverter() {
        return converter;
    }

    /**
     * Returns PreparedStatement instance. Let's consider some caching system.
     * It will provide some prepared statement instance, according to passed sql
     * clause.
     *
     * @param aConnection java.sql.Connection instance to be used.
     * @param aClause Sql clause to process.
     * @return StatementResourceDescriptor instance, provided according to sql
     * clause.
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
