/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.DbClient;
import com.eas.client.DbMetadataCache;
import com.eas.client.sqldrivers.SqlDriver;
import java.util.HashSet;
import java.util.Set;

/**
 * A compiled SQL query.
 *
 * <p>An instance of this class contains JDBC-compliant SQL query text with "?"
 * placeholders for parameters and all parameters values.</p>
 *
 * @author pk, mg
 */
public class SqlCompiledQuery {

    protected DbClient client;
    protected String databaseId;
    protected String sessionId;
    protected String entityId;
    protected String sqlClause;
    protected Parameters parameters;// 1 - Based    
    protected Fields expectedFields;// 1 - Based
    protected FlowProvider flow;
    protected boolean procedure = false;
    protected Set<String> readRoles = new HashSet<>();
    protected Set<String> writeRoles = new HashSet<>();
    private int pageSize = FlowProvider.NO_PAGING_PAGE_SIZE;

    /**
     * Creates an instance of compiled query.
     *
     * @param aSqlClause the SQL query text
     */
    public SqlCompiledQuery(DbClient aClient, String aSqlClause) throws Exception {
        super();
        sqlClause = aSqlClause;
        parameters = new Parameters();
        client = aClient;
        createFlow();
    }

    /**
     * Creates an instance of compiled SQL query.
     *
     * <p>This constructor is used when creating a compiled query out of
     * SqlQuery, providing parameters values.</p>
     *
     * @param sql the SQL query text.
     * @param params parameters' values vector.
     */
    SqlCompiledQuery(DbClient aClient, String aSqlClause, Parameters aParams) throws Exception {
        super();
        sqlClause = aSqlClause;
        parameters = aParams;
        client = aClient;
        createFlow();
    }

    SqlCompiledQuery(DbClient aClient, String aDbId, String aSqlClause, Parameters aParams) throws Exception {
        super();
        sqlClause = aSqlClause;
        parameters = aParams;
        databaseId = aDbId;
        client = aClient;
        createFlow();
    }

    public SqlCompiledQuery(DbClient aClient, String aDbId, String aSqlClause, Parameters aParams, Fields aExpectedFields, Set<String> aReadRoles, Set<String> aWriteRoles) throws Exception {
        super();
        sqlClause = aSqlClause;
        parameters = aParams;
        databaseId = aDbId;
        expectedFields = aExpectedFields;
        client = aClient;
        readRoles = aReadRoles;
        writeRoles = aWriteRoles;
        createFlow();
    }

    /**
     * Creates an instance of compiled query.
     *
     * @param aDbId Database identifier.
     * @param aSqlClause the SQL query text
     */
    public SqlCompiledQuery(DbClient aClient, String aDbId, String aSqlClause) throws Exception {
        super();
        databaseId = aDbId;
        sqlClause = aSqlClause;
        parameters = new Parameters();
        client = aClient;
        createFlow();
    }

    public boolean isProcedure() {
        return procedure;
    }

    public void setProcedure(boolean aValue) {
        procedure = aValue;
        if (flow != null) {
            flow.setProcedure(procedure);
        }
    }

    public Set<String> getReadRoles() {
        return readRoles;
    }

    public Set<String> getWriteRoles() {
        return writeRoles;
    }

    public void setParameters(Parameters aValue) {
        parameters = aValue;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int aPageSize) throws Exception {
        pageSize = aPageSize;
        createFlow();
    }

    /**
     *
     * @param destFields поля полученные из базы данных.
     * @param sourceFields поля предсказанные Platypus.
     */
    protected void mergeFields(Fields destFields, Fields sourceFields) {
        if (sourceFields != null && destFields != null) {
            for (int i = 1; i <= sourceFields.getFieldsCount(); i++) {
                Field srcField = sourceFields.get(i);
                Field rowsetField = destFields.get(srcField.getName());
                if (rowsetField != null) {
                    rowsetField.assignFrom(srcField);
                }
            }
        }
    }

    private void createFlow() throws Exception {
        if (client != null) {
            flow = client.createFlowProvider(databaseId, sessionId, entityId, sqlClause, readRoles, writeRoles);
            flow.setPageSize(pageSize);
            flow.setProcedure(procedure);
            
        }
    }

    /**
     * Executes query and returns results whatever setted in procedure flag.
     *
     * @return Rowset insatance, representing query results.
     * @throws RowsetException
     * @see Rowset
     */
    public Rowset executeQuery() throws Exception {
        Rowset rs = new Rowset(flow);
        rs.refresh(parameters);
        refineFields(rs);
        return rs;
    }

    /**
     * Executes or enqueue an update to database or platypus server. Enqueueing
     * occurs when procedure flag is false and when it is true direct executing
     * is performed and commit is called. If procedure flag is not setted and so
     * enqueueing is performed, affected rows count is returned in subsequent
     * call to commit().
     *
     * @return Affected rows count. If updating is enqueued, than return value
     * is 0. Otherwise return value reflects rows count that were inserted,
     * updated or deleted.
     * @throws Exception
     */
    public void enqueueUpdate() throws Exception {
        client.enqueueUpdate(this);
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
        createFlow();
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
        return databaseId;
    }

    public void setDatabaseId(String aValue) throws Exception {
        databaseId = aValue;
        createFlow();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String aSessionId) {
        sessionId = aSessionId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String aEntityId) throws Exception {
        entityId = aEntityId;
        createFlow();
    }

    /**
     * Checks if expectedFields property is setted and if it is, applies
     * information about expected fields to rowset's fields.
     *
     * @param aRowset Rowset, which is to be processed.
     * @throws RowsetException
     */
    protected void refineFields(Rowset aRowset) throws RowsetException {
        try {
            assert aRowset != null;
            DbMetadataCache cache = client.getDbMetadataCache(databaseId);
            Fields fields = aRowset.getFields();
            mergeFields(fields, expectedFields);
            SqlDriver driver = cache.getConnectionDriver();
            for (Field field : fields.toCollection()) {
                driver.getTypesResolver().resolve2Application(field);
            }
        } catch (Exception ex) {
            throw new RowsetException(ex);
        }
    }
}
