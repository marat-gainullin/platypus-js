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
import com.eas.script.Scripts;
import com.eas.util.BinaryUtils;
import com.eas.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
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
            JdbcReader reader = obtainJdbcReader();
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

    protected abstract JdbcReader obtainJdbcReader();

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

    public static int assumeJdbcType(Object aValue) {
        int jdbcType;
        if (aValue instanceof CharSequence) {
            jdbcType = Types.VARCHAR;
        } else if (aValue instanceof Number) {
            jdbcType = Types.DOUBLE;
        } else if (aValue instanceof java.util.Date) {
            jdbcType = Types.TIMESTAMP;
        } else if (aValue instanceof Boolean) {
            jdbcType = Types.BOOLEAN;
        } else {
            jdbcType = Types.VARCHAR;
        }
        return jdbcType;
    }

    // Ms sql non jdbc string types
    public static final int NON_JDBC_LONG_STRING = 258;
    public static final int NON_JDBC_MEDIUM_STRING = 259;
    public static final int NON_JDBC_MEMO_STRING = 260;
    public static final int NON_JDBC_SHORT_STRING = 261;

    private static BigDecimal number2BigDecimal(Number aNumber) {
        if (aNumber instanceof Float || aNumber instanceof Double) {
            return new BigDecimal(aNumber.doubleValue());
        } else if (aNumber instanceof BigInteger) {
            return new BigDecimal((BigInteger) aNumber);
        } else if (aNumber instanceof BigDecimal) {
            return (BigDecimal) aNumber;
        } else {
            return new BigDecimal(aNumber.longValue());
        }
    }

    public static Object get(Wrapper aRs, int aColumnIndex) throws SQLException {
        try {
            int sqlType = aRs instanceof ResultSet ? ((ResultSet) aRs).getMetaData().getColumnType(aColumnIndex) : ((CallableStatement) aRs).getParameterMetaData().getParameterType(aColumnIndex);
            Object value = null;
            switch (sqlType) {
                case Types.JAVA_OBJECT:
                case Types.DATALINK:
                case Types.DISTINCT:
                case Types.NULL:
                case Types.ROWID:
                case Types.REF:
                case Types.SQLXML:
                case Types.ARRAY:
                case Types.STRUCT:
                case Types.OTHER:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getString(aColumnIndex) : ((CallableStatement) aRs).getString(aColumnIndex);
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getBytes(aColumnIndex) : ((CallableStatement) aRs).getBytes(aColumnIndex);
                    break;
                case Types.BLOB:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getBlob(aColumnIndex) : ((CallableStatement) aRs).getBlob(aColumnIndex);
                    if (value != null) {
                        try (InputStream is = ((Blob) value).getBinaryStream()) {
                            value = BinaryUtils.readStream(is, -1);
                        }
                    }
                    break;
                // clobs
                case Types.CLOB:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getClob(aColumnIndex) : ((CallableStatement) aRs).getClob(aColumnIndex);
                    if (value != null) {
                        try (Reader reader = ((Clob) value).getCharacterStream()) {
                            value = StringUtils.readReader(reader, -1);
                        }
                    }
                    break;
                case Types.NCLOB:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getNClob(aColumnIndex) : ((CallableStatement) aRs).getNClob(aColumnIndex);
                    if (value != null) {
                        try (Reader reader = ((NClob) value).getCharacterStream()) {
                            value = StringUtils.readReader(reader, -1);
                        }
                    }
                    break;
                // numbers
                case Types.DECIMAL:
                case Types.NUMERIC:
                    // target type - BigDecimal
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getBigDecimal(aColumnIndex) : ((CallableStatement) aRs).getBigDecimal(aColumnIndex);
                    break;
                case Types.BIGINT:
                    // target type - BigInteger
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getBigDecimal(aColumnIndex) : ((CallableStatement) aRs).getBigDecimal(aColumnIndex);
                    if (value != null) {
                        value = ((BigDecimal) value).toBigInteger();
                    }
                    break;
                case Types.SMALLINT:
                    // target type - Short
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getShort(aColumnIndex) : ((CallableStatement) aRs).getShort(aColumnIndex);
                    break;
                case Types.TINYINT:
                case Types.INTEGER:
                    // target type - Int
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getInt(aColumnIndex) : ((CallableStatement) aRs).getInt(aColumnIndex);
                    break;
                case Types.REAL:
                case Types.FLOAT:
                    // target type - Float
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getFloat(aColumnIndex) : ((CallableStatement) aRs).getFloat(aColumnIndex);
                    break;
                case Types.DOUBLE:
                    // target type - Double
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getDouble(aColumnIndex) : ((CallableStatement) aRs).getDouble(aColumnIndex);
                    break;
                // strings
                case Types.CHAR:
                case Types.NCHAR:
                case Types.VARCHAR:
                case Types.NVARCHAR:
                case Types.LONGVARCHAR:
                case Types.LONGNVARCHAR:
                case NON_JDBC_LONG_STRING:
                case NON_JDBC_MEDIUM_STRING:
                case NON_JDBC_MEMO_STRING:
                case NON_JDBC_SHORT_STRING:
                    // target type - string
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getString(aColumnIndex) : ((CallableStatement) aRs).getString(aColumnIndex);
                    break;
                // booleans
                case Types.BOOLEAN:
                case Types.BIT:
                    // target type - Boolean
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getBoolean(aColumnIndex) : ((CallableStatement) aRs).getBoolean(aColumnIndex);
                    break;
                // dates, times
                case Types.DATE:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getDate(aColumnIndex) : ((CallableStatement) aRs).getDate(aColumnIndex);
                    break;
                case Types.TIMESTAMP:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getTimestamp(aColumnIndex) : ((CallableStatement) aRs).getTimestamp(aColumnIndex);
                    break;
                case Types.TIME:
                    value = aRs instanceof ResultSet ? ((ResultSet) aRs).getTime(aColumnIndex) : ((CallableStatement) aRs).getTime(aColumnIndex);
                    break;
            }
            if (aRs instanceof ResultSet ? ((ResultSet) aRs).wasNull() : ((CallableStatement) aRs).wasNull()) {
                value = null;
            }
            return value;
        } catch (SQLException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new SQLException(ex);
        }
    }

    public static void assign(Object aValue, int aParameterIndex, PreparedStatement aStmt, int aParameterSqlType, String aParameterSqlTypeName) throws SQLException {
        if (aValue != null) {
            /*
             if (aValue instanceof JSObject) {
             aValue = aSpace.toJava(aValue);
             }
             */
            switch (aParameterSqlType) {
                // Some strange types. No one knows how to work with them.
                case Types.JAVA_OBJECT:
                case Types.DATALINK:
                case Types.DISTINCT:
                case Types.NULL:
                case Types.ROWID:
                case Types.REF:
                case Types.SQLXML:
                case Types.ARRAY:
                case Types.OTHER:
                    try {
                        aStmt.setObject(aParameterIndex, aValue, aParameterSqlType);
                    } catch (Exception ex) {
                        aStmt.setNull(aParameterIndex, aParameterSqlType, aParameterSqlTypeName);
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.STRUCT:
                    try {
                        aStmt.setObject(aParameterIndex, aValue, Types.STRUCT);
                    } catch (Exception ex) {
                        aStmt.setNull(aParameterIndex, aParameterSqlType, aParameterSqlTypeName);
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                    // target type - byte[]
                    if (aValue instanceof byte[]) {
                        aStmt.setBytes(aParameterIndex, (byte[]) aValue);
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.BLOB:
                    // target type - java.sql.Blob
                    if (aValue instanceof Blob) {
                        aStmt.setBlob(aParameterIndex, (Blob) aValue);
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.CLOB:
                    // target type - java.sql.Clob
                    if (aValue instanceof Clob) {
                        aStmt.setClob(aParameterIndex, (Clob) aValue);
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.NCLOB:
                    // target type - java.sql.NClob
                    if (aValue instanceof NClob) {
                        aStmt.setNClob(aParameterIndex, (NClob) aValue);
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.DECIMAL:
                case Types.NUMERIC:
                    // target type - BigDecimal
                    // target type - BigDecimal
                    BigDecimal castedDecimal = null;
                    if (aValue instanceof Number) {
                        castedDecimal = number2BigDecimal((Number) aValue);
                    } else if (aValue instanceof String) {
                        castedDecimal = new BigDecimal((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        castedDecimal = new BigDecimal(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof java.util.Date) {
                        castedDecimal = new BigDecimal(((java.util.Date) aValue).getTime());
                    }
                    if (castedDecimal != null) {
                        aStmt.setBigDecimal(aParameterIndex, castedDecimal);
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.BIGINT:
                    // target type - BigInteger
                    BigInteger castedInteger = null;
                    if (aValue instanceof Number) {
                        castedInteger = BigInteger.valueOf(((Number) aValue).longValue());
                    } else if (aValue instanceof String) {
                        castedInteger = new BigInteger((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        castedInteger = BigInteger.valueOf(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof java.util.Date) {
                        castedInteger = BigInteger.valueOf(((java.util.Date) aValue).getTime());
                    }
                    if (castedInteger != null) {
                        aStmt.setBigDecimal(aParameterIndex, new BigDecimal(castedInteger));
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.SMALLINT:
                    // target type - Short
                    // target type - Short
                    Short castedShort = null;
                    if (aValue instanceof Number) {
                        castedShort = ((Number) aValue).shortValue();
                    } else if (aValue instanceof String) {
                        castedShort = Double.valueOf((String) aValue).shortValue();
                    } else if (aValue instanceof Boolean) {
                        castedShort = Integer.valueOf(((Boolean) aValue) ? 1 : 0).shortValue();
                    } else if (aValue instanceof java.util.Date) {
                        castedShort = Integer.valueOf((int) ((java.util.Date) aValue).getTime()).shortValue();
                    }
                    if (castedShort != null) {
                        aStmt.setShort(aParameterIndex, castedShort);
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.TINYINT:
                case Types.INTEGER:
                    // target type - Integer
                    Integer castedInt = null;
                    if (aValue instanceof Number) {
                        castedInt = ((Number) aValue).intValue();
                    } else if (aValue instanceof String) {
                        castedInt = Double.valueOf((String) aValue).intValue();
                    } else if (aValue instanceof Boolean) {
                        castedInt = (Boolean) aValue ? 1 : 0;
                    } else if (aValue instanceof java.util.Date) {
                        castedInt = (int) ((java.util.Date) aValue).getTime();
                    }
                    if (castedInt != null) {
                        aStmt.setInt(aParameterIndex, castedInt);
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.REAL:
                case Types.FLOAT:
                    // target type - Float
                    Float castedFloat = null;
                    if (aValue instanceof Number) {
                        castedFloat = ((Number) aValue).floatValue();
                    } else if (aValue instanceof String) {
                        castedFloat = Float.valueOf((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        castedFloat = Float.valueOf(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof java.util.Date) {
                        castedFloat = (float) ((java.util.Date) aValue).getTime();
                    }
                    if (castedFloat != null) {
                        aStmt.setFloat(aParameterIndex, castedFloat);
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.DOUBLE:
                    // target type - Double
                    Double castedDouble = null;
                    if (aValue instanceof Number) {
                        castedDouble = ((Number) aValue).doubleValue();
                    } else if (aValue instanceof String) {
                        castedDouble = Double.valueOf((String) aValue);
                    } else if (aValue instanceof Boolean) {
                        castedDouble = Double.valueOf(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof java.util.Date) {
                        castedDouble = (double) ((java.util.Date) aValue).getTime();
                    }
                    if (castedDouble != null) {
                        aStmt.setDouble(aParameterIndex, castedDouble);
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                case Types.NCHAR:
                case Types.NVARCHAR:
                case Types.LONGNVARCHAR:
                    // target type - string
                    // target type - string
                    String castedString = null;
                    if (aValue instanceof Number) {
                        castedString = ((Number) aValue).toString();
                    } else if (aValue instanceof String) {
                        castedString = (String) aValue;
                    } else if (aValue instanceof Boolean) {
                        castedString = ((Boolean) aValue) ? ((Boolean) aValue).toString() : "";
                    } else if (aValue instanceof java.util.Date) {
                        castedString = String.valueOf(((java.util.Date) aValue).getTime());
                    } else if (aValue instanceof Clob) {
                        castedString = ((Clob) aValue).getSubString(1, (int) ((Clob) aValue).length());
                    }
                    if (castedString != null) {
                        if (aParameterSqlType == Types.NCHAR || aParameterSqlType == Types.NVARCHAR || aParameterSqlType == Types.LONGNVARCHAR) {
                            aStmt.setNString(aParameterIndex, castedString);
                        } else {
                            aStmt.setString(aParameterIndex, castedString);
                        }
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.BOOLEAN:
                case Types.BIT:
                    // target type - Boolean
                    Boolean castedBoolean = null;
                    if (aValue instanceof Number) {
                        castedBoolean = !(((Number) aValue).intValue() == 0);
                    } else if (aValue instanceof String || aValue instanceof Clob) {
                        String s;
                        if (aValue instanceof String) {
                            s = (String) aValue;
                        } else {
                            s = ((Clob) aValue).getSubString(1, (int) ((Clob) aValue).length());
                        }
                        castedBoolean = !s.isEmpty();
                    } else if (aValue instanceof Boolean) {
                        castedBoolean = (Boolean) aValue;
                    } else if (aValue instanceof java.util.Date) {
                        castedBoolean = !((java.util.Date) aValue).equals(new java.util.Date(0));
                    }
                    if (castedBoolean != null) {
                        aStmt.setBoolean(aParameterIndex, castedBoolean);
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
                case Types.DATE:
                case Types.TIMESTAMP:
                case Types.TIME:
                    // target type - date
                    java.util.Date castedDate = null;
                    if (aValue instanceof Number) {
                        castedDate = new java.util.Date(((Number) aValue).longValue());
                    } else if (aValue instanceof String) {
                        castedDate = new java.util.Date(Long.valueOf((String) aValue));
                    } else if (aValue instanceof Boolean) {
                        castedDate = new java.util.Date(((Boolean) aValue) ? 1 : 0);
                    } else if (aValue instanceof java.util.Date) {
                        castedDate = ((java.util.Date) aValue);
                    }
                    if (castedDate != null) {
                        if (aParameterSqlType == Types.DATE) {
                            aStmt.setDate(aParameterIndex, new java.sql.Date(castedDate.getTime()));
                        } else if (aParameterSqlType == Types.TIMESTAMP) {
                            aStmt.setTimestamp(aParameterIndex, new java.sql.Timestamp(castedDate.getTime()));
                        } else if (aParameterSqlType == Types.TIME) {
                            aStmt.setTime(aParameterIndex, new java.sql.Time(castedDate.getTime()));
                        } else {
                            assert false;
                        }
                    } else {
                        Logger.getLogger(JdbcFlowProvider.class.getName()).log(Level.WARNING, FALLED_TO_NULL_MSG, aValue.getClass().getName());
                    }
                    break;
            }
        } else {
            try {
                aStmt.setNull(aParameterIndex, aParameterSqlType);
            } catch (SQLException ex) {
                aStmt.setNull(aParameterIndex, aParameterSqlType, aParameterSqlTypeName);
            }
        }
    }

    protected static final String FALLED_TO_NULL_MSG = "Some value falled to null while tranferring to a database. May be it''s class is unsupported: {0}";

    /**
     * @inheritDoc
     */
    @Override
    public Collection<Map<String, Object>> refresh(Parameters aParams, Consumer<Collection<Map<String, Object>>> onSuccess, Consumer<Exception> onFailure) throws Exception {
        return select(aParams, (ResultSet rs) -> {
            if (rs != null) {
                JdbcReader reader = obtainJdbcReader();
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
                                    assignParameter(param, stmt, i, connection);
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
                                        acceptOutParameter(param, cStmt, i, connection);
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

    protected void acceptOutParameter(Parameter aParameter, CallableStatement aStatement, int aParameterIndex, Connection aConnection) throws SQLException {
        if (aParameter.getMode() == ParameterMetaData.parameterModeOut
                || aParameter.getMode() == ParameterMetaData.parameterModeInOut) {
            try {
                Object outedParamValue = get(aStatement, aParameterIndex);
                aParameter.setValue(outedParamValue);
            } catch (SQLException ex) {
                String pType = aParameter.getType();
                if (pType != null) {
                    switch (pType) {
                        case Scripts.STRING_TYPE_NAME:
                            aParameter.setValue(aStatement.getString(aParameterIndex));
                            break;
                        case Scripts.NUMBER_TYPE_NAME:
                            aParameter.setValue(aStatement.getDouble(aParameterIndex));
                            break;
                        case Scripts.DATE_TYPE_NAME:
                            aParameter.setValue(aStatement.getDate(aParameterIndex));
                            break;
                        case Scripts.BOOLEAN_TYPE_NAME:
                            aParameter.setValue(aStatement.getBoolean(aParameterIndex));
                            break;
                        default:
                            aParameter.setValue(aStatement.getObject(aParameterIndex));
                    }
                } else {
                    aParameter.setValue(aStatement.getObject(aParameterIndex));
                }
            }
        }
    }

    protected void assignParameter(Parameter aParameter, PreparedStatement aStatement, int aParameterIndex, Connection aConnection) throws SQLException {
        Object paramValue = aParameter.getValue();
        int jdbcType;
        String sqlTypeName;
        try {
            jdbcType = aStatement.getParameterMetaData().getParameterType(aParameterIndex);
            sqlTypeName = aStatement.getParameterMetaData().getParameterTypeName(aParameterIndex);
        } catch (SQLException ex) {
            jdbcType = assumeJdbcType(paramValue);
            sqlTypeName = null;
        }
        assign(paramValue, aParameterIndex, aStatement, jdbcType, sqlTypeName);
        checkOutParameter(aParameter, aStatement, aParameterIndex, jdbcType);
    }

    protected void checkOutParameter(Parameter param, PreparedStatement stmt, int aParameterIndex, int jdbcType) throws SQLException {
        if (procedure && (param.getMode() == ParameterMetaData.parameterModeOut
                || param.getMode() == ParameterMetaData.parameterModeInOut)) {
            assert stmt instanceof CallableStatement;
            CallableStatement cStmt = (CallableStatement) stmt;
            cStmt.registerOutParameter(aParameterIndex, jdbcType);
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
