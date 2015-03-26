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
import java.sql.ResultSet;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 * A compiled SQL query.
 *
 * <p>
 * An instance of this class contains JDBC-compliant SQL query text with "?"
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
     * @param onSuccess
     * @param onFailure
     * @return Rowset insatance, representing query results.
     * @throws Exception
     * @see Rowset
     */
    public <T> T executeQuery(CallableConsumer<T, ResultSet> aResultSetProcessor, Consumer<T> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (basesProxy != null) {
            PlatypusJdbcFlowProvider flow = basesProxy.createFlowProvider(datasourceName, entityName, sqlClause, expectedFields);
            flow.setPageSize(pageSize);
            flow.setProcedure(procedure);
            return flow.<T>select(parameters, aResultSetProcessor, onSuccess, onFailure);
        } else {
            return null;
        }
    }

    public JSObject executeQuery(Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (basesProxy != null) {
            PlatypusJdbcFlowProvider flow = basesProxy.createFlowProvider(datasourceName, entityName, sqlClause, expectedFields);
            flow.setPageSize(pageSize);
            flow.setProcedure(procedure);
            return flow.refresh(parameters, onSuccess, onFailure);
        } else {
            return null;
        }
    }

    public Command prepareCommand() {
        Command command = new Command(entityName);
        command.command = sqlClause;
        command.parameters = new ChangeValue[parameters.getParametersCount()];
        for (int i = 0; i < command.parameters.length; i++) {
            Parameter param = parameters.get(i + 1);
            command.parameters[i] = new ChangeValue(param.getName(), param.getValue(), param.getTypeInfo());
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
    public String getDatabaseId() {
        return datasourceName;
    }

    public void setDatabaseId(String aValue) throws Exception {
        datasourceName = aValue;
    }

    public String getEntityId() {
        return entityName;
    }

    public void setEntityId(String aEntityId) throws Exception {
        entityName = aEntityId;
    }
}
