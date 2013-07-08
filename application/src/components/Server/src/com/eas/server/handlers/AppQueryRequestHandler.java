/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.queries.SqlQuery;
import com.eas.client.sqldrivers.SqlDriver;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.AppQueryResponse;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;
import java.security.AccessControlException;
import java.util.Set;

/**
 *
 * @author mg
 */
public class AppQueryRequestHandler extends SessionRequestHandler<AppQueryRequest> {

    public static final String ACCESS_DENIED_MSG = "Access denied to query  %s for user %s";
    public static final String MISSING_QUERY_MSG = "Query %s not found neither in application database, nor in hand-constructed queries.";

    public AppQueryRequestHandler(PlatypusServerCore server, Session session, AppQueryRequest aRequest) {
        super(server, session, aRequest);
    }

    @Override
    public Response handle2() throws Exception {
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
        assert query.getEntityId().equals(getRequest().getQueryId());
        Fields queryFields = query.getFields();
        if (queryFields != null) {
            SqlDriver driver = getServerCore().getDatabasesClient().getDbMetadataCache(query.getDbId()).getConnectionDriver();
            for (Field field : queryFields.toCollection()) {
                driver.getTypesResolver().resolve2Application(field);
            }
        }
        return new AppQueryResponse(getRequest().getID(), query);
    }
}
