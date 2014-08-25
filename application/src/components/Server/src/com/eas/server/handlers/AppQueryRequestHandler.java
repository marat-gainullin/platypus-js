/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.queries.SqlQuery;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.security.AccessControlException;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class AppQueryRequestHandler extends SessionRequestHandler<AppQueryRequest, AppQueryRequest.Response> {

    public static final String ACCESS_DENIED_MSG = "Access denied to query  %s for user %s";
    public static final String MISSING_QUERY_MSG = "Query %s not found neither in application database, nor in hand-constructed queries.";

    public AppQueryRequestHandler(PlatypusServerCore aServerCore, AppQueryRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    protected void handle2(Session aSession, Consumer<AppQueryRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            getServerCore().getDatabasesClient().getAppQuery(getRequest().getQueryId(), (SqlQuery query) -> {
                try {
                    if (query == null || query.getEntityId() == null) {
                        throw new Exception(String.format(MISSING_QUERY_MSG, getRequest().getQueryId()));
                    }
                    if (!query.isPublicAccess()) {
                        throw new AccessControlException(String.format(PUBLIC_ACCESS_DENIED_MSG, getRequest().getQueryId()));//NOI18N
                    }
                    Set<String> rolesAllowed = query.getReadRoles();
                    if (rolesAllowed != null && !aSession.getPrincipal().hasAnyRole(rolesAllowed)) {
                        throw new AccessControlException(String.format(ACCESS_DENIED_MSG, query.getEntityId(), aSession.getPrincipal().getName()));
                    }
                    assert query.getEntityId().equals(getRequest().getQueryId());
                    /**
                     * this code is moved to stored query factory in order to
                     * code abstraction SqlDriver driver =
                     * getServerCore().getDatabasesClient().getDbMetadataCache(query.getDbId()).getConnectionDriver();
                     * Fields queryFields = query.getFields(); if (queryFields
                     * != null) { for (Field field : queryFields.toCollection())
                     * { driver.getTypesResolver().resolve2Application(field); }
                     * }
                     */
                } catch (Exception ex) {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                }
                if (onSuccess != null) {
                    onSuccess.accept(new AppQueryRequest.Response(query));
                }
            }, onFailure);
        } catch (Exception ex) {
            if (onFailure != null) {
                onFailure.accept(ex);
            }
        }
    }
    private static final String PUBLIC_ACCESS_DENIED_MSG = "Public access to query %s is denied.";
}
