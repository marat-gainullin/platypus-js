/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.RowsetResponse;
import com.eas.script.JsDoc;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;
import java.security.AccessControlException;
import java.util.Set;

/**
 *
 * @author pk, mg refactoring
 */
public class ExecuteQueryRequestHandler extends SessionRequestHandler<ExecuteQueryRequest> {

    public static final String ACCESS_DENIED_MSG = "Access denied to query  %s for user %s";
    public static final String MISSING_QUERY_MSG = "Query %s not found neither in application database, nor in hand-constructed queries.";
    
    public ExecuteQueryRequestHandler(PlatypusServerCore server, Session session, ExecuteQueryRequest rq) {
        super(server, session, rq);
    }

    @Override
    public RowsetResponse handle2() throws Exception {
        SqlQuery query = getServerCore().getDatabasesClient().getAppQuery(getRequest().getQueryId());
        if (query == null || query.getEntityId() == null) {
            throw new Exception(String.format(MISSING_QUERY_MSG, getRequest().getQueryId()));
        }
        if (!query.isPublicAccess()) {
            throw new AccessControlException(String.format("Public access to query %s is denied.", getRequest().getQueryId()));//NOI18N
        }
        Set<String> rolesAllowed = query.getReadRoles();
        if (rolesAllowed != null && !getSession().getPrincipal().hasAnyRole(rolesAllowed)) {
            throw new AccessControlException(String.format(ACCESS_DENIED_MSG, query.getEntityId(), getSession().getPrincipal().getName()));
        }
        RowsetResponse response = handleQuery(query);
        return response;
    }

    public RowsetResponse handleQuery(SqlQuery aQuery) throws Exception, RowsetException {
        Parameters queryParams = aQuery.getParameters();
        assert queryParams.getParametersCount() == getRequest().getParams().getParametersCount();
        for (int i = 1; i <= queryParams.getParametersCount(); i++) {
            queryParams.get(i).setValue(getRequest().getParams().get(i).getValue());
        }
        int updateCount = 0;
        SqlCompiledQuery compiledQuery = aQuery.compile();
        if (aQuery.isManual()) {
            compiledQuery.setSessionId(getSession().getId());
            getServerCore().getDatabasesClient().enqueueUpdate(compiledQuery);
            return new RowsetResponse(getRequest().getID(), null, updateCount);
        } else {
            Rowset rowset = compiledQuery.executeQuery();
            return new RowsetResponse(getRequest().getID(), rowset, updateCount);
        }
        // SqlCompiledQuery.executeUpdate is prohibited here, because no security check is performed in it.
        // Stored procedures can't be called directly from three-tier clients for security reasons
        // and out parameters can't pass through the network.
        /*
         if (aQuery.isProcedure()) {
         for (int i = 1; i <= queryParams.getParametersCount(); i++) {
         Parameter queryParam = queryParams.get(i);
         if (queryParam.getMode() == ParameterMetaData.parameterModeOut || queryParam.getMode() == ParameterMetaData.parameterModeInOut) {
         Parameter executedParam = compiledQuery.getParameters().get(queryParam.getName());
         if (executedParam != null) {
         queryParam.setValue(executedParam.getValue());
         }
         }
         }
         }
         */
    }
}
