/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.server.SessionRequestHandler;
import com.eas.client.SqlCompiledQuery;
import com.eas.client.SqlQuery;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.metadata.Parameters;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.security.AccessControlException;
import java.util.Set;
import java.util.function.Consumer;
import javax.security.auth.AuthPermission;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author pk, mg refactoring
 */
public class ExecuteQueryRequestHandler extends SessionRequestHandler<ExecuteQueryRequest, ExecuteQueryRequest.Response> {

    private static final String PUBLIC_ACCESS_DENIED_MSG = "Public access to query %s is denied.";
    public static final String ACCESS_DENIED_MSG = "Access denied to query  %s for user %s";
    public static final String MISSING_QUERY_MSG = "Query %s not found neither in application database, nor in hand-constructed queries.";

    public ExecuteQueryRequestHandler(PlatypusServerCore aServerCore, ExecuteQueryRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    protected void handle2(Session aSession, Consumer<ExecuteQueryRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            ((LocalQueriesProxy) getServerCore().getQueries()).getQuery(getRequest().getQueryName(), (SqlQuery query) -> {
                try {
                    if (query == null || query.getEntityName() == null) {
                        throw new Exception(String.format(MISSING_QUERY_MSG, getRequest().getQueryName()));
                    }
                    if (!query.isPublicAccess()) {
                        throw new AccessControlException(String.format(PUBLIC_ACCESS_DENIED_MSG, getRequest().getQueryName()));//NOI18N
                    }
                    Set<String> rolesAllowed = query.getReadRoles();
                    if (rolesAllowed != null && !PlatypusPrincipal.getInstance().hasAnyRole(rolesAllowed)) {
                        throw new AccessControlException(String.format(ACCESS_DENIED_MSG, query.getEntityName(), PlatypusPrincipal.getInstance().getName()), PlatypusPrincipal.getInstance() instanceof AnonymousPlatypusPrincipal ? new AuthPermission("*") : null);
                    }
                    handleQuery(query.copy(), (JSObject rowset) -> {
                        if (onSuccess != null) {
                            onSuccess.accept(new ExecuteQueryRequest.Response(rowset, 0));
                        }
                    }, onFailure);
                } catch (Exception ex) {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                }
            }, onFailure);
        } catch (Exception ex) {
            if (onFailure != null) {
                onFailure.accept(ex);
            }
        }
    }

    public void handleQuery(SqlQuery aQuery, Consumer<JSObject> onSuccess, Consumer<Exception> onFailure) throws Exception  {
        Parameters queryParams = aQuery.getParameters();
        assert queryParams.getParametersCount() == getRequest().getParams().getParametersCount();
        for (int i = 1; i <= queryParams.getParametersCount(); i++) {
            queryParams.get(i).setValue(getRequest().getParams().get(i).getValue());
        }
        SqlCompiledQuery compiledQuery = aQuery.compile();
        // SqlCompiledQuery.executeUpdate/Client.enqueueUpdate is prohibited here, because no security check is performed in it.
        compiledQuery.executeQuery(onSuccess, onFailure);
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
