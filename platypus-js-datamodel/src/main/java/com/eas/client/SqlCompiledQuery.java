/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.changes.ChangeValue;
import com.eas.client.changes.Command;
import com.eas.client.dataflow.FlowProvider;
import com.eas.client.metadata.Fields;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.concurrent.CallableConsumer;
import com.eas.script.Scripts;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 * A compiled SQL query.
 *
 * <p>An instance of this class contains JDBC-compliant SQL query text with "?"
 * placeholders for parameters and all parameters values.</p>
 *
 * @author pk, mg
 */
public class SqlCompiledQuery {

    protected DatabasesClient basesProxy;
    protected String datasourceName;
    protected String entityName;
    protected String sqlClause;
    protected Parameters parameters;// 1 - Based    
    protected Fields expectedFields;// 1 - Based
    protected boolean procedure;
    private int pageSize = FlowProvider.NO_PAGING_PAGE_SIZE;

    /**
     * Creates an instance of compiled query.
     *
     * @param aClient
     * @param aSqlClause the SQL query text
     * @throws java.lang.Exception
     */
    public SqlCompiledQuery(DatabasesClient aClient, String aSqlClause) throws Exception {
        super();
        sqlClause = aSqlClause;
        parameters = new Parameters();
        basesProxy = aClient;
    }

    /**
     * Creates an instance of compiled SQL query.
     *
     * <p>
     * This constructor is used when creating a compiled query out of SqlQuery,
     * providing parameters values.</p>
     *
     * @param sql the SQL query text.
     * @param params parameters' values vector.
     */
    SqlCompiledQuery(DatabasesClient aClient, String aSqlClause, Parameters aParams) throws Exception {
        super();
        sqlClause = aSqlClause;
        parameters = aParams;
        basesProxy = aClient;
    }

    SqlCompiledQuery(DatabasesClient aClient, String aDatasourceName, String aSqlClause, Parameters aParams) throws Exception {
        super();
        sqlClause = aSqlClause;
        parameters = aParams;
        datasourceName = aDatasourceName;
        basesProxy = aClient;
    }

    public SqlCompiledQuery(DatabasesClient aClient, String aDatasourceName, String aSqlClause, Parameters aParams, Fields aExpectedFields) throws Exception {
        super();
        sqlClause = aSqlClause;
        parameters = aParams;
        datasourceName = aDatasourceName;
        expectedFields = aExpectedFields;
        basesProxy = aClient;
    }

    public SqlCompiledQuery(DatabasesClient aClient, String aDatasourceName, String aEntityName, String aSqlClause, Parameters aParams, Fields aExpectedFields) throws Exception {
        super();
        sqlClause = aSqlClause;
        parameters = aParams;
        datasourceName = aDatasourceName;
        entityName = aEntityName;
        expectedFields = aExpectedFields;
        basesProxy = aClient;
    }

    /**
     * Creates an instance of compiled query.
     *
     * @param aClient
     * @param aDatasourceName Database identifier.
     * @param aSqlClause the SQL query text
     * @throws java.lang.Exception
     */
    public SqlCompiledQuery(DatabasesClient aClient, String aDatasourceName, String aSqlClause) throws Exception {
        super();
        datasourceName = aDatasourceName;
        sqlClause = aSqlClause;
        parameters = new Parameters();
        basesProxy = aClient;
    }

    public boolean isProcedure() {
        return procedure;
    }

    public void setProcedure(boolean aValue) {
        procedure = aValue;
    }

    public void setParameters(Parameters aValue) {
        parameters = aValue;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int aPageSize) throws Exception {
        pageSize = aPageSize;
    }

    /**
     * Executes query and returns results whatever setted in procedure flag.
     *
     * @param <T>
     * @param aResultSetProcessor
     * @param aCallbacksExecutor
     * @param onSuccess
     * @param onFailure
     * @return Rowset insatance, representing query results.
     * @throws Exception
     */
    public <T> T executeQuery(CallableConsumer<T, ResultSet> aResultSetProcessor, Executor aCallbacksExecutor, Consumer<T> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (basesProxy != null) {
            PlatypusJdbcFlowProvider flow = basesProxy.createFlowProvider(datasourceName, entityName, sqlClause, expectedFields);
            flow.setPageSize(pageSize);
            flow.setProcedure(procedure);
            return flow.<T>select(parameters, aResultSetProcessor, onSuccess != null ? (T t) -> {
                aCallbacksExecutor.execute(() -> {
                    onSuccess.accept(t);
                });
            } : null, onFailure != null ? (Exception ex) -> {
                aCallbacksExecutor.execute(() -> {
                    onFailure.accept(ex);
                });
            } : null);
        } else {
            return null;
        }
    }

    public JSObject executeQuery(Consumer<JSObject> onSuccess, Consumer<Exception> onFailure, Scripts.Space aSpace) throws Exception {
        if (basesProxy != null) {
            PlatypusJdbcFlowProvider flow = basesProxy.createFlowProvider(datasourceName, entityName, sqlClause, expectedFields);
            flow.setPageSize(pageSize);
            flow.setProcedure(procedure);
            Collection<Map<String, Object>> data = flow.refresh(parameters, onSuccess != null ? (Collection<Map<String, Object>> aData) -> {
                aSpace.process(() -> {
                    JSObject aJsData = aSpace.readJsArray(aData);
                    onSuccess.accept(aJsData);
                });
            } : null, onFailure != null ? (Exception ex) -> {
                aSpace.process(() -> {
                    onFailure.accept(ex);
                });
            } : null);
            return data != null ? aSpace.readJsArray(data) : null;
        } else {
            return null;
        }
    }

    public FlowProvider getFlowProvider() throws Exception {
        PlatypusJdbcFlowProvider flow = basesProxy.createFlowProvider(datasourceName, entityName, sqlClause, expectedFields);
        flow.setPageSize(pageSize);
        flow.setProcedure(procedure);
        return flow;
    }

    public Command prepareCommand() {
        Command command = new Command(entityName);
        command.command = sqlClause;
        for (int i = 0; i < parameters.getParametersCount(); i++) {
            Parameter param = parameters.get(i + 1);
            command.getParameters().add(new ChangeValue(param.getName(), param.getValue()));
        }
        return command;
    }

    /**
     * Returns the SQL query text.
     *
     * @return the SQL query text.
     */
    public String getSqlClause() {
        return sqlClause;
    }

    public void setSqlClause(String aValue) throws Exception {
        sqlClause = aValue;
    }

    /**
     * Returns the vector of parameters' values.
     *
     * @return the vector of parameters' values.
     */
    public Parameters getParameters() {
        return parameters;
    }

    /**
     * @return the databaseId
     */
    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String aValue) throws Exception {
        datasourceName = aValue;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String aValue) throws Exception {
        entityName = aValue;
    }
}
